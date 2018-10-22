package net.happygod.jerrymouse.server;

import net.happygod.jerrymouse.database.*;

public class Settings
{
	private static final Database db=new Database("jerrymouse");
	private static Object getValue(Settings settings,String key)
	{
		String sql="SELECT "+key+" FROM link WHERE port="+settings.port+" AND uri IN("+settings.URIs+") AND "+key+" IS NOT NULL ORDER BY LENGTH(uri) DESC LIMIT 1;";
		Result result=db.query(sql);
		try
		{
			return result.values.iterator().next().get(key);
		}
		catch(Exception e)
		{
			return null;
		}
	}
	public final int port, permission,type;
	public final boolean directory,visible;
	public final String webroot,authentication,path;
	private final String URIs;
	Settings(Server server,String URI)
	{
		port=server.port();
		webroot=server.webroot();
		if(URI==null)
		{
			URI="/";
			//permission=type=0;
			//directory=visible=false;
			//authentication=path=URIs=null;
			//return;
		}
		StringBuilder sb=new StringBuilder("'/',");
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
		o=getValue(this,"path");
		path=(String)(o==null?"":o);
	}
}
