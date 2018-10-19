package net.happygod.jerrymouse.server;

import dalvik.system.DexClassLoader;
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
		Response response;
		Class<?> c=null;
		boolean setConfig=true;
		try
		{
			request=new Request(socket);
			response=new Response(socket);
			if(server.proxyMode())
			{
				//proxy and reverse-proxy mode
				try
				{
					if(server.webroot()==null||server.webroot().equals(""))
					{
						c=Proxy.class;
					}
					else
					{
						c=ReverseProxy.class;
					}
					loadServlet(c,request,response,setConfig);
				}
				catch(HTTPException he)
				{
					response.commit(he);
				}
			}
			while(!server.proxyMode())
			{
				try
				{
					request.parse();
					String URI=request.getRequestURI();
					//TODO HEAD
					// Assume everything is OK then.  Send back a reply.
					//TODO
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
							setConfig=server.servletVisible();
							break;
						case "redirect":
							//redirect(request,response);
							break;
						default:
							c=FileSender.class;
					}
					loadServlet(c,request,response,setConfig);
					if(c!=FileSender.class)
						throw new HTTPException(200);
				}
				catch(HTTPException he)
				{
					response.commit(he);
				}
			}
			socket.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
	private void loadServlet(Class<?> c,Request request,Response response,boolean setConfig) throws Exception
	{
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
		if(setConfig)
		{
			servlet.config(server);
		}
		//call servlet methods
		try
		{
			servlet.init();
			switch(request.getMethod())
			{
				case "GET":
					servlet.doGet(request,response);
					break;
				case "POST":
					servlet.doPost(request,response);
					break;
				default:
					servlet.doDefault(request,response);
					break;
			}
		}
		catch(RuntimeException re)
		{
			throw new HTTPException(500,re);
		}
	}
}