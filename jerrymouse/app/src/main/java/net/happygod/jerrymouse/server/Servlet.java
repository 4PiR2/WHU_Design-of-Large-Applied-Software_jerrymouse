package net.happygod.jerrymouse.server;

public abstract class Servlet
{
	private Server server=null;
	void server(Server server)
	{
		this.server=server;
	}
	protected Server server()
	{
		return server;
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
