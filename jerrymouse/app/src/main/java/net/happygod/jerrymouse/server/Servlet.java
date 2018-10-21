package net.happygod.jerrymouse.server;

public abstract class Servlet
{
	private Settings settings;
	void settings(Settings settings)
	{
		this.settings=settings;
	}
	protected Settings settings()
	{
		return settings;
	}
	public void init() throws HTTPException
	{
	}
	public void doGet(Request request,Response response) throws HTTPException
	{
		doDefault(request,response);
	}
	public void doPost(Request request,Response response) throws HTTPException
	{
		doDefault(request,response);
	}
	void doDefault(Request request,Response response) throws HTTPException
	{
	}
}
