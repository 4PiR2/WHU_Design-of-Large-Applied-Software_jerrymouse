package net.happygod.jerrymouse;

import java.io.Serializable;

class Conf implements Serializable
{
	boolean enabled;
	int port;
	String webroot;
	Object config;
	Conf(int port,String webroot)
	{
		this.enabled=true;
		this.port=port;
		this.webroot=webroot;
	}
}
