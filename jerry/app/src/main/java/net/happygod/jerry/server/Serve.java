package net.happygod.jerry.server;

import java.net.*;
import java.io.*;
import dalvik.system.DexClassLoader;

class Serve implements Runnable
{
    private final Config config;
    private final Socket s;

    Serve(Socket s, Config config)
    {
        this.config = config;
        this.s = s;
        new Thread(this).start();
    }

    public void run()
    {
        try
        {
            Request request=new Request(s.getInputStream());
            Response response=new Response(s.getOutputStream());

            //dos.writeBytes("HTTP/1.1 200 OK\r\n\r\n\r\n");dos.flush();if(true)return;
            PrintWriter out = response.getWriter();
            // Only support GET or POST
            String requestMethod = request.getMethod();
            if (!(requestMethod.equals("GET") || requestMethod.equals("POST"))) {
                error(out, 400, "");
                return;
            }
            String fileName = request.getFileName();
            String filePath = config.webroot + fileName;
            File file = new File(filePath);
            // Check for file permission or not found error.
            if (!file.exists()) {
                error(out, 404, fileName);
                return;
            }
            if (!file.canRead()) {
                error(out, 403, fileName);
                return;
            }

            // Assume everything is OK then.  Send back a reply.
            if (fileName.endsWith(".jar") || fileName.endsWith(".dex")) {
                //Load servlet
                Servlet servlet = servletLoader(filePath);
                if (servlet != null) {
                    out.println("HTTP/1.1 200 OK");
                    out.println("Server: Jerrymouse");
                    out.println();
                    out.flush();
                    if (requestMethod.equals("GET")) {
                        servlet.doGet(request, response);
                    } else {
                        servlet.doPost(request, response);
                    }
                }
            } else if (fileName.endsWith(".redirect")) {
                BufferedReader redirectReader = new BufferedReader(new FileReader(file));
                out.println("HTTP/1.1 " + redirectReader.readLine());
                out.println("Server: Jerrymouse");
                out.println("Location: " + redirectReader.readLine());
                out.println();
                out.flush();
            } else {
                out.println("HTTP/1.1 200 OK");
                out.println("Server: Jerrymouse");
                out.flush();
                staticFileRequests(response.getStream(), filePath);
            }
            s.close();
            System.out.println("Connection closed\n");
        }
        catch (IOException e)
        {
            System.out.println("Unable to read/write: " + e.getMessage());
        }
    }

    private void staticFileRequests(DataOutputStream dos, String filePath)
    {
        String extension=filePath.substring(filePath.lastIndexOf(".")+1),mime;
        try
        {
            switch (extension)
            {
                case "html": mime="text/html"; break;
                case "jpg": mime="image/jpeg"; break;
                case "gif": mime="image/gif"; break;
                case "css": mime="text/css"; break;
                default: mime="application/octet-stream";
                //TODO add file types
            }
            dos.writeBytes("Content-type: "+mime+"\r\n\r\n");
            // Read the content 1KB at a time.
            File file = new File(filePath);
            byte[] buffer = new byte[(int) file.length()];
            BufferedInputStream bis=new BufferedInputStream(new FileInputStream(file));
            int size = bis.read(buffer);
            while (size > 0)
            {
                dos.write(buffer, 0, size);
                size = bis.read(buffer);
            }
            dos.flush();
        }
        catch (IOException e)
        {
            System.out.println("Unable to read/write: " + e.getMessage());
        }
    }

    private Servlet servletLoader(String filePath)
    {
        int index = filePath.lastIndexOf("/");
        //String classPath = filePath.substring(0, index);
        String className = filePath.substring(index + 1, filePath.lastIndexOf("."));
        //Load servlet
        DexClassLoader classLoader = new DexClassLoader(filePath, config.cacheDir, null, getClass().getClassLoader());
        try {
            Class<?> c = classLoader.loadClass(className);
            if (c != null)
            {
                Object servlet = c.newInstance();
                if (servlet instanceof Servlet) {
                    return (Servlet) servlet;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void error(PrintWriter out, int code, String fileName)
    {
        String type = "", message = "";
        switch (code) {
            case 400:
                type = "Bad Request";
                message = "The web server only understands GET or POST requests.";
                break;
            case 403:
                type = "Forbidden";
                message = "You have no permission to access " + fileName + " on this server.";
                break;
            case 404:
                type = "Not Found";
                message = "Unable to find " + fileName + " on this server.";
                break;
            default:
        }
        out.println("HTTP/1.1 " + code + " " + type);
        out.println("Server: Jerrymouse");
        out.println("Content-Length: " + message.length());
        out.println();
        out.println(message);
        out.flush();
    }
}
