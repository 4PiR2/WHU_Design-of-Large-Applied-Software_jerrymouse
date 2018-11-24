package net.happygod.jerrymouse.server;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import net.happygod.jerrymouse.database.*;

public class Response
{
	private final Map<String,String> headers=new HashMap<>();
	private final BufferedOutputStream bos;
	private final ByteArrayOutputStream baos;
	private final PrintWriter pw,rpw;
	private final DataOutputStream dos;
	private String method;
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
		setHeader("date",new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss zzz",Locale.US).format(new Date()));
		setHeader("Access-Control-Allow-Origin","*");
	}
	void setMethod(String method)
	{
		this.method=method.toUpperCase();
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
		if(!"HEAD".equals(method))
		{
			baos.writeTo(bos);
			baos.reset();
			bos.flush();
		}
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
	private static class ErrorPage
	{
		//TODO pretty page
		final ByteArrayOutputStream baos=new ByteArrayOutputStream();
		ErrorPage(HTTPException he)
		{
			int code=he.code();
			String description=he.description(),message=he.message();
			Result result=DBConst.SYS_DB.query("SELECT path FROM error WHERE code="+code+" LIMIT 1;");
			try
			{
				String page=(String)result.values.iterator().next().get("path");
				//String page="/storage/emulated/0/web/404.html";
				BufferedInputStream bis=new BufferedInputStream(new FileInputStream(page));
				Pipe.pipe(bis,baos);
				bis.close();
			}
			catch(Exception e)
			{
				PrintWriter pw=new PrintWriter(baos);
				pw.println("<html><head>");
				pw.println("<meta content='text/html;charset=utf-8'>");
				pw.println("<title>"+code+" "+description+"</title>");
				pw.println("</head><body>");
				pw.println("<h1>"+code+" "+description+"</h1>");
				pw.println("<hr />");
				pw.println("<pre>");
				pw.println(message);
				pw.println("</pre>");
				pw.println("<br />");
				pw.println("<p><i>Jerrymouse Web Server</i></p>");
				pw.println("</body></html>");
				pw.flush();
			}
		}
	}
}
