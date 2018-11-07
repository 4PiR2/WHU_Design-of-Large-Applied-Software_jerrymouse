package net.happygod.jerrymouse;

import android.content.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class NetworkStateReceiver extends BroadcastReceiver
{
	static TextView textviewIP;
	@Override
	public void onReceive(Context context, Intent intent)
	{
		//ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		//NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		//wait for the IP to be stable
		try
		{
			Thread.sleep(400);
		}
		catch(InterruptedException ie)
		{
			//ie.printStackTrace();
		}
		if(textviewIP!=null)
			showIP(textviewIP);
	}
	static void showIP(TextView textView)
	{
		StringBuilder sb=new StringBuilder();
		try
		{
			Set<String> v4=new HashSet<>(),v6=new HashSet<>();
			Enumeration allNetInterfaces=NetworkInterface.getNetworkInterfaces();
			InetAddress ip;
			while(allNetInterfaces.hasMoreElements())
			{
				NetworkInterface netInterface=(NetworkInterface)allNetInterfaces.nextElement();
				//System.out.println(netInterface.getName());
				Enumeration<InetAddress> addresses=netInterface.getInetAddresses();
				while(addresses.hasMoreElements())
				{
					ip=addresses.nextElement();
					if(ip!=null&&!ip.isLoopbackAddress()&&!ip.isLinkLocalAddress())
					{
						if(ip instanceof Inet4Address)
						{
							v4.add(ip.getHostAddress());
						}
						else if(ip instanceof Inet6Address)
						{
							v6.add(ip.getHostAddress());
						}
					}
				}
			}
			for(String s:v4)
				sb.append(s+"\n");
			for(String s:v6)
				sb.append(s+"\n");
			int index=sb.lastIndexOf("\n");
			if(index>=0)
				sb.deleteCharAt(index);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		textView.setText(sb);
	}
}
