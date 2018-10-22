package net.happygod.jerrymouse.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class Request
{
	private String requestMethod="", URI="", queryString="";
	private final Hashtable<String,String> headers=new Hashtable<>();
	private final Hashtable<String,Object> parameters=new Hashtable<>();
	private final BufferedInputStream bis;
	private RequestInputStream rs;
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
			queryString="";
			headers.clear();
			parameters.clear();
			rs=new RequestInputStream(bis);
			//Wait for HTTP request from the connection
			String line;
			line=rs.readLine();
			//Bail out if line is null. In case some client tries to be
			//funny and close immediately after connection.  (I am
			//looking at you, Chrome!)
			if(line==null)
			{
				throw new HTTPException(400);
			}
			//Log client's requests.
			//System.err.println("Request: "+line);
			String tokens[]=line.split(" ");
			if(!"HTTP".equalsIgnoreCase(tokens[2].substring(0,4)))
				throw new HTTPException(400);
			requestMethod=tokens[0].toUpperCase();
			//Only support GET or POST
			if(!("GET".equals(requestMethod)||"POST".equals(requestMethod)||"HEAD".equals(requestMethod)||"CONNECT".equals(requestMethod)))
			{
				throw new HTTPException(400,"The web server only understands GET or POST requests");
			}
			Matcher m=Pattern.compile("^(([0-9A-Za-z]+):\\/\\/([^\\/]+))?([^?]*)(\\?(.*))?$").matcher(URI=tokens[1]);
			if(m.find())
			{
				URI=m.group(4);
				queryString=m.group(6);
				parseQueryString(queryString,true);
			}

			//Read and parse the rest of the HTTP headers
			line=rs.readLine();
			while(!"".equals(line))
			{
				int index=line.indexOf(":");
				if(index<0)
				{
					break;
				}
				else
				{
					headers.put(line.substring(0,index).toLowerCase(),line.substring(index+1).trim());
				}
				line=rs.readLine();
			}

			//read form formData if POST
			if("POST".equals(requestMethod))
			{
				int contentLength=getContentLength();
				byte[] bytes=new byte[contentLength];
				rs.read(bytes,0,contentLength);
				char[] chars=new char[contentLength];
				for(int i=0;i<contentLength;i++)
					chars[i]=(char)bytes[i];
				line=new String(chars);
				String contentType=getContentType();
				if(contentType.startsWith("multipart/form-data"))
				{
					String boundary="--"+contentType.split("=")[1];
					Pattern p=Pattern.compile("Content-Disposition: ?([^ ;]*); ?name=\"([^\"]*)\"(; ?filename=\"([^\"]*)\")? ?\\r?\\n( ?Content-Type: ?([^ \\r\\n]*) ?\\r?\\n)?\\r?\\n(.*)\\r?\\n"
	                      ,Pattern.MULTILINE|Pattern.DOTALL);
					for(String zone:line.split(boundary))
					{
						m=p.matcher(zone);
						if(m.find())
						{
							BinaryParameter bp=new BinaryParameter();
							bp.attr.put("Content-Disposition",m.group(1));
							bp.attr.put("name",m.group(2));
							if(m.group(4)!=null)
								bp.attr.put("filename",m.group(4));
							if(m.group(6)!=null)
								bp.attr.put("Content-Type",m.group(6));
							char[] data=m.group(7).toCharArray();
							bp.data=new byte[data.length];
							for(int i=0;i<data.length;i++)
							{
								bp.data[i]=(byte)data[i];
							}
							if(bp.attr.get("name")!=null&&!"".equals(bp.attr.get("name")))
								parameters.put(bp.attr.get("name"),bp);
						}
					}
				}
				else
				{
					//application/x-www-form-urlencoded
					//text/plain
					parseQueryString(line,contentType.startsWith("application/x-www-form-urlencoded"));
				}
			}
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

	private void parseQueryString(String queryString,boolean decode) throws UnsupportedEncodingException
	{
		if(queryString==null||"".equals(queryString))
			return;
		if(queryString.charAt(0)=='?')
			queryString=queryString.substring(1);
		if(decode)
			queryString=URLDecoder.decode(queryString,"UTF-8");//parameters.put(key,hex2char(value));
		else
			queryString=queryString.replace('+',' ');
		for(String query : queryString.split("&"))
		{
			String[] keys=query.split("=");
			String key=keys[0], value="";
			if(keys.length>1)
			{
				value=keys[1];
			}
			if(!"".equals(key))
			{
				parameters.put(key,value);
			}
		}
	}

	public String getMethod()
	{
		return requestMethod;
	}
	public String getRequestURI()
	{
		return URI;
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
		return parameters.get(name).toString();
	}
	public byte[] getBinaryParameter(String name)
	{
		return ((BinaryParameter)parameters.get(name)).data;
	}
	public Enumeration<String> getParameterNames()
	{
		return parameters.keys();
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
	public byte[] getReadData()
	{
		return rs.getReadData();
	}
	public class BinaryParameter
	{
		public final Map<String,String> attr=new HashMap<>();
		public byte[] data;
		@Override
		public String toString()
		{
			return new String(data);
		}
	}
	private class RequestInputStream extends InputStream
	{
		private final InputStream is;
		private final ByteArrayOutputStream baos=new ByteArrayOutputStream();
		RequestInputStream(InputStream is)
		{
			this.is=is;
		}
		@Override
		public int read() throws IOException
		{
			int i=is.read();
			baos.write(i);
			return i;
		}
		String readLine() throws IOException
		{
			StringWriter sw=new StringWriter();
			int i,max=100000000;
			label:
			while(true)
			{
				while((i=read())!=(int)'\r')
				{
					if(max--<=0||i==-1)
						break label;
					sw.write(i);
				}
				if((i=read())=='\n'||i==-1)
					break;
				sw.write((int)'\r');
				sw.write(i);
			}
			return sw.toString();
		}
		byte[] getReadData()
		{
			byte[] readData=baos.toByteArray();
			baos.reset();
			return readData;
		}
	}
}
