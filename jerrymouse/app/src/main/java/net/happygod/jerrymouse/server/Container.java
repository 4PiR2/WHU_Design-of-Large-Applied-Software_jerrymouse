package net.happygod.jerrymouse.server;

class Container
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
