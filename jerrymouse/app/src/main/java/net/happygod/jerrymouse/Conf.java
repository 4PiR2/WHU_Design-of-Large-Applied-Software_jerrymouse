package net.happygod.jerrymouse;

import java.io.Serializable;

class Conf implements Serializable
{
	boolean enabled,proxy;
	int port;
	String webroot;
	Conf(int port,boolean proxy,String webroot)
	{
		this.enabled=true;
		this.port=port;
		this.proxy=proxy;
		this.webroot=webroot;
	}
}
