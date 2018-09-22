package net.happygod.jerry.server;

import java.net.*;
import java.io.*;
import java.util.*;

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
        HTTPRequestParser httpRequestParser = new HTTPRequestParser(is);
        // Only support GET or POST
        String requestMethod = httpRequestParser.getRequestMethod();
        if (!(requestMethod.equals("GET") || requestMethod.equals("POST"))) {
            invalidRequestError();
            return;
        }

        String fileName = httpRequestParser.getFileName();
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
        dos.writeBytes("HTTP/1.1 200 OK\r\n");

        String queryString = httpRequestParser.getQueryString();

        if (fileName.endsWith("pl"))
        {
            Process p;
            String env = "REQUEST_METHOD=" + requestMethod + " ";

            if (requestMethod.equals("POST")) {
                env += "CONTENT_TYPE=" + httpRequestParser.getContentType() + " " +
                        "CONTENT_LENGTH=" + Integer.toString(httpRequestParser.getContentLength()) + " ";
            } else {
                env += "QUERY_STRING=" + queryString + " ";
            }

            p = Runtime.getRuntime().exec("/usr/bin/env " + env +
                    "/usr/bin/perl " + filePath);

            if (requestMethod.equals("POST"))
            {
                // Pass form data into Perl process
                DataOutputStream o = new DataOutputStream(p.getOutputStream());
                o.writeBytes(httpRequestParser.getFormData() + "\r\n");
                o.close();
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

            // Write response content
            String l;
            while ((l = br.readLine()) != null) {
                dos.writeBytes(l + "\r\n");
            }
            dos.writeBytes("\r\n");
        }
        else
        {
            staticFileRequests(filePath);
        }

        dos.flush();
    }

    private void staticFileRequests(String filePath) {
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

class HTTPRequestParser
{
    private String requestMethod, fileName, queryString, formData;
    private Hashtable<String, String> headers;
    private int[] ver;

    HTTPRequestParser(InputStream is)
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        requestMethod = "";
        fileName = "";
        queryString = "";
        formData = "";
        headers = new Hashtable<String, String>();
        try {
            // Wait for HTTP request from the connection
            String line = br.readLine();

            // Bail out if line is null. In case some client tries to be
            // funny and close immediately after connection.  (I am
            // looking at you, Chrome!)
            if (line == null) {
                return;
            }

            // Log client's requests.
            System.out.println("Request: " + line);

            String tokens[] = line.split(" ");

            requestMethod = tokens[0];

            if (tokens[1].indexOf("?") != -1)
            {
                String urlComponents[] = tokens[1].split("\\?");
                fileName = urlComponents[0];
                if (urlComponents.length > 0)
                {
                    queryString = urlComponents[1];
                }
            }
            else
            {
                fileName = tokens[1];
            }

            // Read and parse the rest of the HTTP headers
            int idx;
            line = br.readLine();
            while (!line.equals(""))
            {
                idx = line.indexOf(":");
                if (idx < 0)
                {
                    headers = null;
                    break;
                }
                else
                {
                    headers.put(line.substring(0, idx).toLowerCase(),
                            line.substring(idx+1).trim());
                }
                line = br.readLine();
            }

            // read form data if POST
            if (requestMethod.equals("POST"))
            {
                int contentLength = getContentLength();
                final char[] data = new char[contentLength];
                for (int i = 0; i < contentLength; i++)
                {
                    data[i] = (char)br.read();
                }
                formData = new String(data);
            }
        }
        catch(IOException e)
        {
            System.err.println("Unable to read/write: "  + e.getMessage());
        }
    }

    public String getRequestMethod()
    {
        return requestMethod;
    }

    public String getFileName()
    {
        return fileName;
    }

    public String getQueryString()
    {
        return queryString;
    }

    public String getContentType()
    {
        return headers.get("content-type");
    }

    public int getContentLength()
    {
        return Integer.parseInt(headers.get("content-length"));
    }

    public String getFormData()
    {
        return formData;
    }
}