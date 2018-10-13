package net.happygod.jerrymouse.server;

import android.content.*;

public class Config
{
	private Server server=null;
	private boolean isRunning=false;
	private int port=0;
	private String webroot=null;
	private Context context=null;
	private boolean proxyMode=false;
	private boolean allowIndex=false;
	private boolean servletVisible=false;
	public Config()
	{
	}
	public Config(int port,String webroot,Context context,boolean proxyMode,boolean allowIndex,boolean servletVisible)
	{
		this();
		reset(port,webroot,context,proxyMode,allowIndex,servletVisible);
	}
	public int port()
	{
		return port;
	}
	public String webroot()
	{
		return webroot;
	}
	public Context context()
	{
		return context;
	}
	public boolean proxyMode()
	{
		return proxyMode;
	}
	public String cacheDir()
	{
		return context.getCacheDir().getPath();
	}
	public boolean isRunning()
	{
		return isRunning;
	}
	public boolean allowIndex()
	{
		return allowIndex;
	}
	public boolean servletVisible()
	{
		return servletVisible;
	}
	public void reset(int port,String webroot,Context context,boolean proxyMode,boolean allowIndex,boolean servletVisible)
	{
		if(isRunning)
			stop();
		this.port=port;
		this.webroot=webroot;
		this.context=context;
		this.proxyMode=proxyMode;
		this.allowIndex=allowIndex;
		this.servletVisible=servletVisible;
		if(isRunning)
			start();
	}
	public Server start()
	{
		if(isRunning)
			stop();
		isRunning=true;
		server=new Server(this);
		//TODO failure
		return server;
	}
	public void stop()
	{
		if(!isRunning)
			return;
		server.stop();
		//TODO failure
		server=null;
		isRunning=false;
	}
}
