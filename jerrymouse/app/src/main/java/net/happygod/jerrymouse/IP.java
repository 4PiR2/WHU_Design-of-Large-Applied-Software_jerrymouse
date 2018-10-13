package net.happygod.jerrymouse;

import java.net.*;
import java.util.*;

public class IP
{
	IP() throws Exception
	{
		Enumeration allNetInterfaces=NetworkInterface.getNetworkInterfaces();
		InetAddress ip;
		while(allNetInterfaces.hasMoreElements())
		{
			NetworkInterface netInterface=(NetworkInterface)allNetInterfaces.nextElement();
			//System.out.println(netInterface.getName());
			Enumeration addresses=netInterface.getInetAddresses();
			while(addresses.hasMoreElements())
			{
				ip=(InetAddress)addresses.nextElement();
				if(ip!=null&&ip instanceof Inet4Address)
				{
					System.out.println("本机的IPv4 = "+ip.getHostAddress()+"\n");
				}
				if(ip!=null&&ip instanceof Inet6Address)
				{
					System.out.println("本机的IPv6 = "+ip.getHostAddress()+"\n");
				}
			}
		}
	}
}
