package net.happygod.jerrymouse.server;

public abstract class Servlet
{
	private Config config=null;
	void config(Config config)
	{
		this.config=config;
	}
	public Config config()
	{
		return config;
	}
	public void init() throws Exception
	{
	}
	public void doGet(Request request,Response response) throws Exception
	{
	}
	public void doPost(Request request,Response response) throws Exception
	{
	}
}
