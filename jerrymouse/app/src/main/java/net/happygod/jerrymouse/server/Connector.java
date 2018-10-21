package net.happygod.jerrymouse.server;

import dalvik.system.DexClassLoader;
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
			if(settings.permission==0||settings.permission==1&&!socket.getInetAddress().isLoopbackAddress())
				throw new HTTPException(403,"You have no permission to access "+request.getRequestURI()+" on this server");
			//Assume everything is OK then.  Send back a reply.
			Container container=new Container(settings,request,response);
			switch(settings.type)
			{
				case 1:
				case 2:
					response.setHeader("location",settings.path);
					throw new HTTPException(300+settings.type);
				case 3:
					String filePath=FileSender.getPath(settings,request.getRequestURI());
					String className=filePath.substring(filePath.lastIndexOf("/")+1);
					if(className.contains("."))
						className=className.substring(0,className.lastIndexOf("."));
					//Load servlet
					DexClassLoader classLoader=new DexClassLoader(filePath,SharedContext.get().getCacheDir().getPath(),null,getClass().getClassLoader());
					container.run(classLoader.loadClass(className));
					throw new HTTPException(200);
				default:
					container.run(FileSender.class);
			}
		}
		catch(HTTPException he)
		{
			response.commit(he);
		}
	}
}
