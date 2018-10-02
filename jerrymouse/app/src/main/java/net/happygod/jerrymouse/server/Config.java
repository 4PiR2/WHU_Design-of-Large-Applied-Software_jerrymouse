package net.happygod.jerrymouse.server;

public class Config
{
	public boolean enabled;
    private Server server=null;
    private boolean running=false;
    private int port=0;
    private String webroot=null;
    private String cacheDir=null;
    public Config(){}
    public Config(int port,String webroot,String cacheDir)
    {
	    this();
        reset(port,webroot,cacheDir);
    }
    public String webroot()
    {
        return webroot;
    }
    public int port()
    {
        return port;
    }
    public String cacheDir()
    {
        return cacheDir;
    }
	public boolean isRunning()
	{
		return running;
	}
	public void port(int port)
	{
		if(running)
			stop();
		this.port=port;
		if(running)
			start();
	}
	public void webroot(String webroot)
	{
		if(running)
			stop();
		this.webroot=webroot;
		if(running)
			start();
	}
	public void cacheDir(String cacheDir)
	{
		if(running)
			stop();
		this.cacheDir=cacheDir;
		if(running)
			start();
	}
	public void reset(int port,String webroot,String cacheDir)
	{
		if(running)
			stop();
		this.port=port;
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
