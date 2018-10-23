package net.happygod.jerrymouse.server;

import dalvik.system.*;
import java.net.*;
import net.happygod.jerrymouse.*;

class Connector implements Runnable
{
	private final Server server;
	private final Socket socket;
	private Request request;
	private Response response;
	Connector(Socket socket,Server server)
	{
		this.server=server;
		this.socket=socket;
	}

	public void run()
	{
		try
		{
			request=new Request(socket);
			response=new Response(socket);
			if(server.proxy()!=0)
			{
				//proxy and reverse-proxy mode
				proxyMode();
			}
			else
			{
				while(true)
				{
					standardMode();
				}
			}
			socket.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//TODO comment it after debug
		}
	}

	private void proxyMode() throws Exception
	{
		Settings settings=new Settings(server,null);
		Container container=new Container(settings,request,response);
		try
		{
			request.setProxyMode(true);
			container.run(server.proxy()==2?Proxy.class:ReverseProxy.class);
		}
		catch(HTTPException he)
		{
			response.commit(he);
		}
	}

	private void standardMode() throws Exception
	{
		try
		{
			request.parse();
			Settings settings=new Settings(server,request.getRequestURI());
			response.setMethod(request.getMethod());
			//check permission
			if(settings.permission==0||settings.permission==1&&!socket.getInetAddress().isLoopbackAddress())
				throw new HTTPException(403,"You have no permission to access "+request.getRequestURI()+" on this server");
			if(settings.authentication!=null&&!"".equals(settings.authentication))
			{
				String authorization=request.getHeader("authorization");
				if(authorization==null||!authorization.substring(authorization.indexOf(' ')+1).equals(settings.authentication))
				{
					response.setHeader("www-authenticate","Basic realm=\"Connecting to Jerrymouse Web Server\"");
					throw new HTTPException(401);
				}
			}
			//Assume everything is OK then.  Send back a reply.
			Container container=new Container(settings,request,response);
			switch(settings.type)
			{
				case 1:
				case 2:
					response.setHeader("location",settings.path);
					throw new HTTPException(300+settings.type);
				case 3:
					String filePath=settings.path;
					String className=filePath.substring(filePath.lastIndexOf("/")+1);
					if(className.contains("."))
						className=className.substring(0,className.lastIndexOf("."));
					//Load servlet
					DexClassLoader classLoader=new DexClassLoader(filePath,SharedContext.get().getCacheDir().getPath(),null,getClass().getClassLoader());
					container.run(classLoader.loadClass(className));
					throw new HTTPException(200);
				default:
					container.run(FileManager.class);
			}
		}
		catch(HTTPException he)
		{
			response.commit(he);
		}
	}

	private class Container
	{
		private final Settings settings;
		private final Request request;
		private final Response response;
		Container(Settings settings,Request request,Response response)
		{
			this.settings=settings;
			this.request=request;
			this.response=response;
		}
		void run(Class<?> c) throws Exception
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
			if(getClass().getPackage().equals(c.getPackage())||settings.visible)
				servlet.settings(settings);
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
}
