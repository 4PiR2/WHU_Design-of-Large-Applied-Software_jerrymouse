package net.happygod.jerrymouse.server;

public class Config
{
    int port;
    String webroot;
    String cacheDir;
    public Config(int port,String webroot,String cacheDir)
    {
        this.port=port;
        this.webroot=webroot;
        this.cacheDir=cacheDir;
    }
}
