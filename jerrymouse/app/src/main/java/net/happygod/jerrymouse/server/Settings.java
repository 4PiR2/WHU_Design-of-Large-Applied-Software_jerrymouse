package net.happygod.jerrymouse.server;

import java.util.*;
import net.happygod.jerrymouse.database.*;

public class Settings
{
	private static final Database db=new Database("jerrymouse");
	private static Map<String,Object> getValues(Settings settings,String key)
	{
		String sql="SELECT uri,"+key+" FROM link WHERE port="+settings.port+" AND (uri='"+settings.URI+"' OR uri IN("+settings.URIs+") AND extensive=1) AND "+key+" IS NOT NULL ORDER BY LENGTH(uri) DESC LIMIT 5;";
		Result result=db.query(sql);
		try
		{
			return result.values.iterator().next();
		}
		catch(RuntimeException re)
		{
			return null;
		}
	}
	private static Object getValue(Settings settings,String key)
	{
		try
		{
			return getValues(settings,key).get(key);
		}
		catch(RuntimeException re)
		{
			return null;
		}
	}
	public final int port, permission,type;
	public final boolean directory,visible;
	public final String webroot,authentication,path;
	private final String URI,URIs;
	Settings(Server server,String URI)
	{
		port=server.port();
		webroot=server.webroot();
		if(URI==null)
		{
			URI="";
			//permission=type=0;
			//directory=visible=false;
			//authentication=path=URIs=null;
			//return;
		}
		this.URI=URI;
		StringBuilder sb=new StringBuilder("'',");
		String tmp="";
		for(String s:URI.split("/"))
		{
			if("".equals(s))
				continue;
			tmp+="/"+s;
			sb.append("'"+tmp+"',");
		}
		sb.deleteCharAt(sb.length()-1);
		URIs=sb.toString();
		Object o;
		o=getValue(this,"permission");
		permission=(int)(o==null?2:o);
		o=getValue(this,"authentication");
		authentication=(String)(o==null?"":o);
		o=getValue(this,"directory");
		directory=o!=null&&(int)o!=0;
		o=getValue(this,"visible");
		visible=o!=null&&(int)o!=0;
		o=getValue(this,"type");
		type=(int)(o==null?0:o);
		Map<String,Object> map=getValues(this,"path");
		String path,uri;
		if(map==null||(path=(String)map.get("path"))==null)
			path="";
		if(map==null||(uri=(String)map.get("uri"))==null)
			uri="";
		if("".equals(path))
			path=webroot;
		else if(path.charAt(0)!='/'&&!path.startsWith("http"))
			path=webroot+'/'+path;
		Boolean pathextensive;
		if(map==null||(pathextensive=(Boolean)map.get("pathextensive"))==null||pathextensive)
			path+=URI.substring(uri.length());
		this.path=path;
	}
}
