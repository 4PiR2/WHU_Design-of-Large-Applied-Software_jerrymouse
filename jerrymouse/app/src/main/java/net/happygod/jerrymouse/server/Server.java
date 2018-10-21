package net.happygod.jerrymouse.server;

public class Server
{
	private Listener listener=null;
	private boolean isRunning=false;
	private int port=0;
	private String webroot=null;
	private int proxy;
	private String message;
	public Server()
	{
	}
	public Server(int port,String webroot,int proxy)
	{
		this();
		reset(port,webroot,proxy);
	}
	public int port()
	{
		return port;
	}
	public String webroot()
	{
		return webroot;
	}
	public int proxy()
	{
		return proxy;
	}
	public boolean isRunning()
	{
		return isRunning;
	}
	public void reset(int port,String webroot,int proxy)
	{
		message=null;
		if(isRunning)
			stop();
		this.port=port;
		this.webroot=webroot;
		this.proxy=proxy;
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
		isRunning=false;
	}
	public String getMessage()
	{
		return message;
	}
}
