package net.happygod.jerrymouse.server;

import android.content.*;
import net.happygod.jerrymouse.*;

public class Server
{
	private Listener listener=null;
	private boolean isRunning=false;
	private int port=0;
	private String webroot=null;
	private boolean proxyMode=false;
	private boolean allowIndex=false;
	private boolean servletVisible=false;
	private String message;
	public Server()
	{
	}
	public Server(int port,String webroot,boolean proxyMode,boolean allowIndex,boolean servletVisible)
	{
		this();
		reset(port,webroot,proxyMode,allowIndex,servletVisible);
	}
	public int port()
	{
		return port;
	}
	public String webroot()
	{
		return webroot;
	}
	public boolean proxyMode()
	{
		return proxyMode;
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
	public void reset(int port,String webroot,boolean proxyMode,boolean allowIndex,boolean servletVisible)
	{
		message=null;
		if(isRunning)
			stop();
		this.port=port;
		this.webroot=webroot;
		this.proxyMode=proxyMode;
		this.allowIndex=allowIndex;
		this.servletVisible=servletVisible;
		if(isRunning)
			start();
	}
	public Listener start()
	{
		if(isRunning)
			stop();
		isRunning=true;
		listener=new Listener(this);
		return listener;
	}
	public void stop()
	{
		if(!isRunning)
			return;
		listener.stop();
		listener=null;
		isRunning=false;
	}
	void failure(String message)
	{
		this.message=message;
		stop();
		isRunning=false;
	}
	public String getMessage()
	{
		return message;
	}
}
