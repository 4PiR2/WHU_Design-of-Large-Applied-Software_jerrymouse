package net.happygod.jerry.server;

//import java.lang.reflect.*;
import java.net.*;
import java.io.*;

class Service implements Runnable
{
    private Config config;
    private Socket s;
    private InputStream is;
    //private OutputStream os;
    private DataOutputStream dos;
    Service(Socket s, Config config)
    {
        this.config=config;
        this.s=s;
        new Thread(this).start();
    }
    public void run()
    {
        try
        {
            is = s.getInputStream();
            dos = new DataOutputStream(s.getOutputStream());
            handleRequests();
            s.close();
            System.out.println("Connection closed\n");
        }
        catch (IOException e)
        {
            System.err.println("Unable to read/write: "  + e.getMessage());
        }
    }
    private void handleRequests() throws IOException
    {
        Request request = new Request(is);
        // Only support GET or POST
        String requestMethod = request.getMethod();
        if (!(requestMethod.equals("GET") || requestMethod.equals("POST")))
        {
            invalidRequestError();
            return;
        }

        String fileName = request.getFileName();
        String filePath = config.webroot + fileName;
        
        File file = new File(filePath);

        // Check for file permission or not found error.
        if (!file.exists()) {
            fileNotFoundError(fileName);
            return;
        }

        if (!file.canRead()) {
            forbiddenAccessError(fileName);
            return;
        }

        // Assume everything is OK then.  Send back a reply.
        if(fileName.endsWith(".class"))
        {
            int index=fileName.lastIndexOf("/");
            String classPath=config.webroot+fileName.substring(0,index);
            String className=fileName.substring(index+1,fileName.length()-6);
            dos.writeBytes("HTTP/1.1 200 OK\r\n\r\n");
            //Load servlet
            Response response=new Response(dos);
            ServletLoader servletLoader=new ServletLoader(classPath);
            try
            {
                Class c=servletLoader.loadClass(className);
                if(c!=null)
                {
                    Servlet servlet=(Servlet)c.newInstance();
                    //Method method=c.getDeclaredMethod(doMethod,Request.class,Response.class);
                    //method.invoke(servlet,request,response);
                    if(requestMethod.equals("GET"))
                    {
                        servlet.doGet(request,response);
                    }
                    else
                    {
                        servlet.doPost(request,response);
                    }
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        else if(fileName.endsWith(".redirect"))
        {
            BufferedReader redirectReader=new BufferedReader(new FileReader(file));
            dos.writeBytes("HTTP/1.1 "+redirectReader.readLine()+"\r\n");
            dos.writeBytes("Location: "+redirectReader.readLine()+"\r\n");
        }
        else
        {
            dos.writeBytes("HTTP/1.1 200 OK\r\n");
            staticFileRequests(filePath);
        }
        dos.flush();
    }

    private void staticFileRequests(String filePath)
    {
        try
        {
            if (filePath.endsWith(".html")) {
                dos.writeBytes("Content-type: text/html\r\n");
            }
            else if (filePath.endsWith(".jpg"))
            {
                dos.writeBytes("Content-type: image/jpeg\r\n");
            }
            else if (filePath.endsWith("gif"))
            {
                dos.writeBytes("Content-type: image/gif\r\n");
            }
            else if (filePath.endsWith("css"))
            {
                dos.writeBytes("Content-type: text/css\r\n");
            }
            //TODO add file types
            dos.writeBytes("\r\n");
            // Read the content 1KB at a time.
            File file = new File(filePath);
            byte[] buffer = new byte[(int)file.length()];
            FileInputStream fis = new FileInputStream(file);
            int size = fis.read(buffer);
            while (size > 0)
            {
                dos.write(buffer, 0, size);
                size = fis.read(buffer);
            }
        }
        catch (IOException e)
        {
            System.err.println("Unable to read/write: "  + e.getMessage());
        }
    }

    private void invalidRequestError() throws IOException
    {
        String errorMessage = "The web server only understands GET or POST requests\r\n";
        dos.writeBytes("HTTP/1.1 400 Bad Request\r\n");
        dos.writeBytes("Content-length: " + errorMessage.length() + "\r\n\r\n");
        dos.writeBytes(errorMessage);
    }

    private void fileNotFoundError(String fileName) throws IOException
    {
        String errorMessage = "Unable to find " + fileName + " on this server.\r\n";
        dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
        dos.writeBytes("Content-length: " + errorMessage.length() + "\r\n\r\n");
        dos.writeBytes(errorMessage);
    }

    private void forbiddenAccessError(String fileName) throws IOException
    {
        String errorMessage = "You have no permission to access " + fileName + " on this server.\r\n";
        dos.writeBytes("HTTP/1.1 403 Forbidden\r\n");
        dos.writeBytes("Content-length: " + errorMessage.length() + "\r\n\r\n");
        dos.writeBytes(errorMessage);
    }
}
