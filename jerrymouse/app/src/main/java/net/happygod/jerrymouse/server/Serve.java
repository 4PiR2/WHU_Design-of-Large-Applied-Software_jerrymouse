package net.happygod.jerrymouse.server;

import dalvik.system.DexClassLoader;
import java.io.*;
import java.net.*;

class Serve implements Runnable
{
	private final Config config;
	private final Socket socket;

	Serve(Socket socket,Config config)
	{
		this.config=config;
		this.socket=socket;
	}

	public void run()
	{
		try
		{
			//TODO invalid method detection
			Request request=new Request(socket);
			Response response=new Response(socket);
			if(config.proxyMode())
			{
				if(config.webroot()==null||config.webroot().equals(""))
				{
					request.parse();
					proxy(request,response);
				}
				else
				{
					reverseProxy(request,response);
				}
				return;
			}
			request.parse();
			if(errorDetection(request,response))
			{
				response.commit();
				return;
			}
			// Assume everything is OK then.  Send back a reply.
			//TODO lastindexof -1
			String URI=request.getRequestURI(), extension=URI.substring(URI.lastIndexOf(".")+1);
			switch(extension)
			{
				case "dex":
				case "jar":
				case "apk":
				case "class":
					servlet(request,response);
					break;
				case "redirect":
					redirect(request,response);
					break;
				default:
					file(request,response);
			}
			response.commit();
			socket.close();
			System.out.println("Connection closed\n");
		}
		catch(IOException e)
		{
			System.out.println("Unable to read/write: "+e.getMessage());
		}
	}

	private void file(Request request,Response response) throws IOException
	{
		PrintWriter out=response.getWriter();
		DataOutputStream dos=response.getDataStream();
		String URI=request.getRequestURI(), filePath=config.webroot()+URI, extension=URI.substring(URI.lastIndexOf(".")+1);
		out.println("HTTP/1.1 200 OK");
		out.println("Server: Jerrymouse");
		String mime;
		switch(extension)
		{
			case "html":
				mime="text/html";
				break;
			case "jpg":
				mime="image/jpeg";
				break;
			case "png":
				mime="image/png";
				break;
			case "gif":
				mime="image/gif";
				break;
			case "css":
				mime="text/css";
				break;
			default:
				mime="application/octet-stream";
				//TODO add file types
		}
		out.println("Content-type: "+mime);
		out.println();
		out.flush();
		// Read the content 1KB at a time.
		File file=new File(filePath);
		if(file.isDirectory())
		{
			response.reset();
			dir(request,response);
		}
		else
		{
			BufferedInputStream bis=new BufferedInputStream(new FileInputStream(file));
			pipe(bis,dos);
			bis.close();
		}
	}

	private void dir(Request request,Response response)
	{
		PrintWriter out=response.getWriter();
		File file=new File(config.webroot()+request.getRequestURI());
		if(file.isDirectory())
		{
			out.println("HTTP/1.1 200 OK");
			out.println("Server: Jerrymouse");
			out.println("Content-type: text/html");
			out.println();
			out.println("<html><head></head><body>");
			out.println("<a href='..'>Parent</a><br />");
			for(File subFile:file.listFiles())
			{
				if(subFile.isDirectory())
					out.println("<a href='"+subFile.getName()+"/'>"+subFile.getName()+"</a><br />");
				else
					out.println("<a href='"+subFile.getName()+"'>"+subFile.getName()+"</a><br />");
			}
			out.println("</body></html>");
			out.flush();
		}
	}

	private void redirect(Request request,Response response) throws IOException
	{
		PrintWriter out=response.getWriter();
		File file=new File(config.webroot()+request.getRequestURI());
		BufferedReader redirectReader=new BufferedReader(new FileReader(file));
		out.println("HTTP/1.1 "+redirectReader.readLine());
		out.println("Server: Jerrymouse");
		out.println("Location: "+redirectReader.readLine());
		out.println();
		out.flush();
	}

	private void servlet(Request request,Response response)
	{
		Servlet servlet=null;
		String filePath=config.webroot()+request.getRequestURI();
		String className=filePath.substring(filePath.lastIndexOf("/")+1,filePath.lastIndexOf("."));
		//Load servlet
		DexClassLoader classLoader=new DexClassLoader(filePath,config.cacheDir(),null,getClass().getClassLoader());
		try
		{
			Class<?> c=classLoader.loadClass(className);
			if(c!=null)
			{
				Object object=c.newInstance();
				if(object instanceof Servlet)
				{
					servlet=(Servlet)object;
				}
				//TODO if not instance of Servlet
				if(config.servletVisible())
				{
					servlet.config(config);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		if(servlet==null)
			return;
		PrintWriter out=response.getWriter();
		out.println("HTTP/1.1 200 OK");
		out.println("Server: Jerrymouse");
		out.println();
		out.flush();
		try
		{
			servlet.init();
			if(request.getMethod().equals("GET"))
			{
				servlet.doGet(request,response);
			}
			else
			{
				servlet.doPost(request,response);
			}
		}
		catch(Exception e)
		{
			//TODO 500 ByteArrayOutputStream
			e.printStackTrace(out);
			out.flush();
		}
	}

	private boolean errorDetection(Request request,Response response)
	{
		int code=200;
		String URI=request.getRequestURI(), filePath=config.webroot()+URI;
		File file=new File(filePath);
		// Only support GET or POST
		//TODO HEAD
		String requestMethod=request.getMethod();
		if(!(requestMethod.equals("GET")||requestMethod.equals("POST")||requestMethod.equals("HEAD")||requestMethod.equals("CONNECT")))
		{
			code=400;
		}
		// Check for file permission or not found error.
		else if(!file.exists())
		{
			code=404;
		}
		else if(!config.allowIndex()&&file.isDirectory()||!file.canRead())
		{
			code=403;
		}
		PrintWriter out=response.getWriter();
		String type="", message="";
		switch(code)
		{
			case 200:
				return false;
			case 400:
				type="Bad Request";
				message="The web server only understands GET or POST requests.";
				break;
			case 403:
				type="Forbidden";
				message="You have no permission to access "+URI+" on this server.";
				//TODO directory?
				break;
			case 404:
				type="Not Found";
				message="Unable to find "+URI+" on this server.";
				break;
			default:
		}
		out.println("HTTP/1.1 "+code+" "+type);
		out.println("Server: Jerrymouse");
		out.println("Content-Length: "+message.length());
		out.println();
		out.println(message);
		out.flush();
		return true;
	}

	private void proxy(final Request request,final Response response) throws IOException
	{
		int port=80;
		String requestMethod=request.getMethod(), host;
		String[] hostTemp;
		try
		{
			hostTemp=request.getHeader("host").split(":");
		}
		catch(NullPointerException e)
		{
			//TODO why
			//errorDetection(request,response);
			return;
		}
		host=hostTemp[0];
		if(hostTemp.length>1)
			port=Integer.parseInt(hostTemp[1]);
		final Socket proxySocket=new Socket(host,port);
		final BufferedInputStream proxyInput=new BufferedInputStream(proxySocket.getInputStream());
		final BufferedOutputStream proxyOutput=new BufferedOutputStream(proxySocket.getOutputStream());
		PrintWriter out=new PrintWriter(new BufferedOutputStream(response.getStream())),pout=new PrintWriter(proxyOutput);
		if(requestMethod.equals("CONNECT"))
		{//https
			out.println("HTTP/1.1 200 Connection Established");
			out.println();
			out.flush();
		}
		else
		{
			pout.println(request.getData());
			pout.flush();
		}
		Thread t=new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					pipe(request.getInput(),proxyOutput);
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		});
		t.start();
		pipe(proxyInput,response.getStream());
		try
		{
			t.join();
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		proxySocket.close();
	}

	private void reverseProxy(final Request request,final Response response) throws IOException
	{
		int port=80;
		String[] domains=config.webroot().split(":");
		String domain=domains[0];
		if(domains.length>1)
			port=Integer.parseInt(domains[1]);
		final Socket proxySocket=new Socket(domain,port);
		final BufferedInputStream proxyInput=new BufferedInputStream(proxySocket.getInputStream());
		final BufferedOutputStream proxyOutput=new BufferedOutputStream(proxySocket.getOutputStream());
		Thread t=new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					pipe(request.getInput(),proxyOutput);
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}
		});
		t.start();
		pipe(proxyInput,response.getStream());
		try
		{
			t.join();
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		proxySocket.close();
	}

	private void pipe(InputStream in,OutputStream out) throws IOException
	{
		byte[] buffer=new byte[2048];
		int size;
		while((size=in.read(buffer))>0)
		{
			out.write(buffer,0,size);
			out.flush();
		}
	}
}
