package net.happygod.jerrymouse.server;

import dalvik.system.DexClassLoader;
import java.io.*;
import java.net.*;

class Loader implements Runnable
{
	private final Server server;
	private final Socket socket;
	Loader(Socket socket,Server server)
	{
		this.server=server;
		this.socket=socket;
	}

	public void run()
	{
		Request request;
		Response response=null;
		Class<?> c=null;
		String function="doDefault";
		try
		{
			try
			{
				request=new Request(socket);
				response=new Response(socket);
				if(server.proxyMode())
				{
					//proxy and reverse-proxy mode
					if(server.webroot()==null||server.webroot().equals(""))
					{
						c=Proxy.class;
					}
					else
					{
						c=ReverseProxy.class;
					}
				}
				else
				{
					request.parse();
					String URI=request.getRequestURI();
					//TODO URI
					//GET http://localhost:8080/ HTTP/1.1
					//Host: localhost:8080
					// Only support GET or POST
					//TODO HEAD
					String requestMethod=request.getMethod();
					if(!(requestMethod.equals("GET")||requestMethod.equals("POST")||requestMethod.equals("HEAD")||requestMethod.equals("CONNECT")))
					{
						throw new HTTPException(400,"The web server only understands GET or POST requests");
					}
					// Assume everything is OK then.  Send back a reply.
					//TODO lastindexof -1
					String extension=URI.substring(URI.lastIndexOf(".")+1);
					switch(extension)
					{
						case "dex":
						case "jar":
						case "apk":
						case "class":
							String filePath=server.webroot()+request.getRequestURI();
							String className=filePath.substring(filePath.lastIndexOf("/")+1,filePath.lastIndexOf("."));
							//Load servlet
							DexClassLoader classLoader=new DexClassLoader(filePath,server.cacheDir(),null,getClass().getClassLoader());
							c=classLoader.loadClass(className);
							break;
						case "redirect":
							//redirect(request,response);
							break;
						default:
							c=FileSender.class;
					}
				}
				Servlet servlet;
				Object object=c.newInstance();
				if(object instanceof Servlet)
				{
					servlet=(Servlet)object;
				}
				else
				{
					//if not instance of Servlet
					throw new HTTPException(500,"This is not a Servlet.");
				}
				//if(server.servletVisible())
				{
					servlet.config(server);
				}
				//call servlet methods
				servlet.init();
				//c.getMethod(function,Request.class,Response.class).invoke(servlet,request,response);
				if(request.getMethod().equals("GET"))
					servlet.doGet(request,response);
				else
					servlet.doPost(request,response);
				throw new HTTPException(200);
			}
			catch(HTTPException he)
			{
				response.commit(he);
			}
			catch(Exception e)
			{
				response.commit(new HTTPException(500,e));
			}
			socket.close();
		}
		catch(IOException e)
		{
			System.err.println("Unable to read/write: "+e.getMessage());
		}
	}

	private void redirect(Request request,Response response) throws HTTPException
	{
		//TODO
		/*
		File file=new File(server.webroot()+request.getRequestURI());
		BufferedReader redirectReader=new BufferedReader(new FileReader(file));
		int code=Integer.parseInt(redirectReader.readLine());
		response.setHeader("location",redirectReader.readLine());
		throw new HTTPException(code);
		*/
	}
}