package net.happygod.jerrymouse.server;

public class Config
{
    private Server server=null;
    private boolean running=false;
    private int port=0;
    private boolean proxy=false;
    private String webroot=null;
    private String cacheDir=null;
    public Config(){}
    public Config(int port,boolean proxy,String webroot,String cacheDir)
    {
	    this();
        reset(port,proxy,webroot,cacheDir);
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
	public String cacheDir()
    {
        return cacheDir;
    }
	public boolean isRunning()
	{
		return running;
	}
	public void reset(int port,boolean proxy,String webroot,String cacheDir)
	{
		if(running)
			stop();
		this.port=port;
		this.proxy=proxy;
		this.webroot=webroot;
		this.cacheDir=cacheDir;
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
