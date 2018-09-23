package net.happygod.jerry.server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Request
{
	private String requestMethod="", fileName="";
	private Hashtable<String, String> headers= new Hashtable<>(),formData=new Hashtable<>();
	Request(Socket s) throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		String queryString="";
		// Wait for HTTP request from the connection
		String line = br.readLine();
		// Bail out if line is null. In case some client tries to be
		// funny and close immediately after connection.  (I am
		// looking at you, Chrome!)
		if (line == null)
		{
			return;
		}
		// Log client's requests.
		System.out.println("Request: " + line);
		String tokens[] = line.split(" ");
		requestMethod = tokens[0];
		if (tokens[1].contains("?"))
		{
			String urlComponents[] = tokens[1].split("\\?");
			fileName = urlComponents[0];
			if (urlComponents.length > 1)
			{
				queryString = urlComponents[1];
				//TODO charset
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
				headers.put(line.substring(0, idx).toLowerCase(),line.substring(idx+1).trim());
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
			queryString = new String(data);
			//TODO charset
		}
		String queries[] = queryString.split("&");
		for(String query : queries)
		{
			String[] keys=query.split("=");
			String key=keys[0],value="";
			if(keys.length>1)
			{
				value=keys[1];
			}
			if(!key.equals(""))
			{
				formData.put(key,value);
			}
		}
	}
	public String getMethod()
	{
		return requestMethod;
	}
	public String getFileName()
	{
		return fileName;
	}
	public String getContentType()
	{
		return headers.get("content-type");
	}
	public int getContentLength()
	{
		return Integer.parseInt(headers.get("content-length"));
	}
	public String getParameter(String name)
	{
		return formData.get(name);
	}
	public Enumeration<String> ParameterNames()
	{
		return formData.keys();
	}
}
