package net.happygod.jerrymouse.server;

import android.content.*;

public class Config
{
	private Server server=null;
	private boolean running=false;
	private int port=0;
	private boolean proxy=false;
	private String webroot=null;
	private Context context=null;
	public Config()
	{
	}
	public Config(int port,boolean proxy,String webroot,Context context)
	{
		this();
		reset(port,proxy,webroot,context);
	}
	public String webroot()
	{
		return webroot;
	}
	public int port()
	{
		return port;
	}
	public boolean proxy()
	{
		return proxy;
	}
	public Context Context()
	{
		return context;
	}
	public String cacheDir()
	{
		return context.getCacheDir().getPath();
	}
	public boolean isRunning()
	{
		return running;
	}
	public void reset(int port,boolean proxy,String webroot,Context context)
	{
		if(running)
			stop();
		this.port=port;
		this.proxy=proxy;
		this.webroot=webroot;
		this.context=context;
		if(running)
			start();
	}
	public Server start()
	{
		if(running)
			stop();
		running=true;
		server=new Server(this);
		//TODO failure
		return server;
	}
	public void stop()
	{
		if(!running)
			return;
		server.stop();
		//TODO failure
		server=null;
		running=false;
	}
}
