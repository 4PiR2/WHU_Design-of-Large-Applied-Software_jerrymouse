package net.happygod.jerrymouse.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class Response
{
	private final Hashtable<String,String> headers=new Hashtable<>();
	private final BufferedOutputStream bos;
	private final ByteArrayOutputStream baos;
	private final PrintWriter pw,rpw;
	private final DataOutputStream dos;
	Response(Socket socket) throws IOException
	{
		bos=new BufferedOutputStream(socket.getOutputStream());
		rpw=new PrintWriter(bos);
		baos=new ByteArrayOutputStream();
		pw=new PrintWriter(baos);
		dos=new DataOutputStream(baos);
		resetHeaders();
	}
	public void setHeaders(Map<String,String> newHeaders)
	{
		for(String key:newHeaders.keySet())
		{
			headers.put(key.toLowerCase(),newHeaders.get(key));
		}
	}
	public void setHeader(final String key,final String value)
	{
		setHeaders(new HashMap<String,String>()
		{
			{
				put(key.toLowerCase(),value);
			}
		});
	}
	public void resetHeaders(Map<String,String> headers)
	{
		this.headers.clear();
		setHeaders(headers);
	}
	private void resetHeaders()
	{
		resetHeaders(new HashMap<String,String>());
		setHeader("server","Jerrymouse");
		//TODO more
	}
	public void setContentType(final String contentType)
	{
		setHeader("content-type",contentType);
	}
	public PrintWriter getWriter()
	{
		return pw;
	}
	PrintWriter getRawWriter()
	{
		return rpw;
	}
	public DataOutputStream getStream()
	{
		return dos;
	}
	BufferedOutputStream getRawStream()
	{
		return bos;
	}
	void commit(HTTPException he) throws IOException
	{
		int code=he.code();
		if(code<=0)
			return;
		pw.flush();
		dos.flush();
		if(code!=200)
		{
			if(code==500)
				resetHeaders();
			baos.reset();
			new ErrorPage(he).baos.writeTo(baos);
		}
		if(headers.get("content-length")==null)
			setHeader("content-length",baos.size()+"");
		rpw.println("HTTP/1.1 "+code+" "+he.description());
		for(String key:headers.keySet())
		{
			rpw.println(capitalize(key)+": "+headers.get(key));
		}
		rpw.println();
		rpw.flush();
		baos.writeTo(bos);
		baos.reset();
		bos.flush();
		resetHeaders();
	}
	private String capitalize(String str)
	{
		String text="0"+((str!=null)?str.trim():"")+"0";
		String[] words=text.split("[^A-Za-z]+");
		Matcher m=Pattern.compile("[^A-Za-z]+").matcher(text);
		int i=1;
		StringBuilder sb=new StringBuilder();
		while(m.find())
		{
			sb.append(m.group());
			if(i<words.length)
			{
				sb.append(words[i].substring(0,1).toUpperCase());
				sb.append(words[i].substring(1));
			}
			i++;
		}
		String result=sb.toString();
		return result.substring(1,result.length()-1);
	}
}
