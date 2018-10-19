package net.happygod.jerrymouse.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class Request
{
	private String requestMethod="", URI="", queryString="";
	private final Hashtable<String,String> headers=new Hashtable<>(), formData=new Hashtable<>();
	private final BufferedInputStream bis;
	private String data;
	Request(Socket s) throws HTTPException
	{
		try
		{
			bis=new BufferedInputStream(s.getInputStream());
		}
		catch(IOException ioe)
		{
			throw new HTTPException(400,ioe);
		}
	}

	void parse() throws HTTPException
	{
		try
		{
			BufferedReader br=new BufferedReader(new InputStreamReader(bis));
			StringBuilder sb=new StringBuilder();
			// Wait for HTTP request from the connection
			String line;
			line=br.readLine();
			sb.append(line);
			sb.append("\r\n");
			// Bail out if line is null. In case some client tries to be
			// funny and close immediately after connection.  (I am
			// looking at you, Chrome!)
			if(line==null)
			{
				throw new HTTPException(400);
			}
			//Log client's requests.
			//System.err.println("Request: "+line);
			String tokens[]=line.split(" ");
			if(!tokens[2].substring(0,4).equalsIgnoreCase("HTTP"))
				throw new HTTPException(400);
			requestMethod=tokens[0].toUpperCase();
			//Only support GET or POST
			if(!(requestMethod.equals("GET")||requestMethod.equals("POST")||requestMethod.equals("HEAD")||requestMethod.equals("CONNECT")))
			{
				throw new HTTPException(400,"The web server only understands GET or POST requests");
			}
			Matcher m=Pattern.compile("^([0-9A-Za-z]+):\\/\\/([^\\/]+)(.*)$").matcher(URI=tokens[1]);
			if(m.find())
				URI=m.group(3);
			String urlComponents[]=URI.split("\\?");
			if(urlComponents.length>1)
			{
				queryString=urlComponents[1];
			}
			// Read and parse the rest of the HTTP headers
			int idx;
			line=br.readLine();
			sb.append(line);
			sb.append("\r\n");
			while(!line.equals(""))
			{
				idx=line.indexOf(":");
				if(idx<0)
				{
					//headers=null;
					break;
				}
				else
				{
					headers.put(line.substring(0,idx).toLowerCase(),line.substring(idx+1).trim());
				}
				line=br.readLine();
				sb.append(line);
				sb.append("\r\n");
			}
			// read form data if POST
			if(requestMethod.equals("POST"))
			{
				int contentLength=getContentLength();
				final char[] data=new char[contentLength];
				for(int i=0;i<contentLength;i++)
				{
					data[i]=(char)br.read();
				}
				queryString=new String(data);
				//queryString=br.readLine();
				sb.append(queryString);
				sb.append("\r\n");
			}
			String queries[]=queryString.split("&");
			for(String query : queries)
			{
				String[] keys=query.split("=");
				String key=keys[0], value="";
				if(keys.length>1)
				{
					value=keys[1];
				}
				if(!key.equals(""))
				{
					//formData.put(key,hex2char(value));
					formData.put(key,URLDecoder.decode(value,"UTF-8"));
				}
			}
			data=sb.toString();
		}
		catch(IOException ioe)
		{
			throw new HTTPException(400,ioe);
		}
	}

	private String hex2char(String str)
	{
		str=str.replace('+',' ');
		StringBuilder sb=new StringBuilder();
		Matcher m=Pattern.compile("%[0-9A-F]{2}").matcher(str);
		int i=0,start;
		while(m.find())
		{
			start=m.start();
			sb.append(str.substring(i,start));
			i=start+3;
			sb.append((char)Integer.parseInt(str.substring(start+1,i),16));
		}
		sb.append(str.substring(i));
		return sb.toString();
	}
	public String getMethod()
	{
		return requestMethod;
	}
	public String getRequestURI()
	{
		return URI;
	}
	public void setContentType(String enc)
	{
		headers.put("content-type",enc);
	}
	public int getContentLength()
	{
		return Integer.parseInt(headers.get("content-length"));
	}
	public String getParameter(String name)
	{
		return formData.get(name);
	}
	public Enumeration<String> getParameterNames()
	{
		return formData.keys();
	}
	public String getHeader(String name)
	{
		return headers.get(name.toLowerCase());
	}
	public Enumeration<String> getHeaderNames()
	{
		return headers.keys();
	}
	public String getQueryString()
	{
		return queryString;
	}
	public BufferedInputStream getStream()
	{
		return bis;
	}
	public String getData()
	{
		return data;
	}
}
