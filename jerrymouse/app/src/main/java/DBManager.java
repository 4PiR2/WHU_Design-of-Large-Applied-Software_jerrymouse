import java.io.*;
import java.util.*;

import net.happygod.jerrymouse.server.*;
import net.happygod.jerrymouse.database.*;
import org.json.JSONObject;

public class DBManager extends Servlet
{
	@Override
	public void doGet(Request request,Response response)
	{
		doPost(request,response);
	}
	@Override
	public void doPost(Request request,Response response)
	{
		response.setContentType("text/json; charset=utf-8");
		PrintWriter out=response.getWriter();
		Map<String,String> map=new HashMap<>();
		Enumeration pNames=request.getParameterNames();
		while(pNames.hasMoreElements()){
			String name=(String)pNames.nextElement();
			String value=request.getParameter(name);
			map.put(name,value);
		}

		String database="jerrymouse";
		String table=map.get("table");
		String res_sql=new String();
		String exc_sql=new String();
		String operation=new String();
		String message="success!";

		int port;
		int proxy;
		String path;

		String uri;
		boolean extensive;
		int permission;
		String authentication;
		boolean directory;
		boolean visible;
		int type;
		boolean pathextensive;

		int code;
		int version;

		int old_port;
		int old_proxy;
		String old_path;

		String old_uri;
		boolean old_extensive;
		int old_permission;
		String old_authentication;
		boolean old_directory;
		boolean old_visible;
		int old_type;
		boolean old_pathextensive;

		int old_code;
		int old_version;

		switch(table)
		{
			case "general":
				if(map.containsKey("proxy"))
				{
					if(map.containsKey("old_proxy"))
					{
						port=Integer.parseInt(map.get("port"));
						proxy=Integer.parseInt(map.get("proxy"));
						path=map.get("path");

						old_port=Integer.parseInt(map.get("old_port"));
						old_proxy=Integer.parseInt(map.get("old_proxy"));
						old_path=map.get("old_path");

						operation="update";
						exc_sql="UPDATE general SET port="+port+", proxy="+proxy+", path='"+path+"' " +
								"WHERE port="+old_port+" AND proxy="+old_proxy+" AND path='"+old_path+"';";
						res_sql="SELECT * FROM general WHERE port="+port+" AND proxy="+proxy+" AND path='"+path+"';";
					}
					else
					{
						port=Integer.parseInt(map.get("port"));
						proxy=Integer.parseInt(map.get("proxy"));
						path=map.get("path");

						operation="insert";
						exc_sql="INSERT INTO general (port,proxy,path) VALUES ("+port+","+proxy+",'"+path+"');";
						res_sql="SELECT * FROM general WHERE port="+port+" AND proxy="+proxy+" AND path='"+path+"';";
					}
				}
				else if(map.containsKey("old_proxy"))
				{
					old_port=Integer.parseInt(map.get("old_port"));
					old_proxy=Integer.parseInt(map.get("old_proxy"));
					old_path=map.get("old_path");

					operation="delete";
					exc_sql="DELETE FROM general WHERE port="+old_port+" AND proxy="+old_proxy+" AND path='"+old_path+"';";
					res_sql="SELECT * FROM general WHERE 0;";
				}
				else
				{
					operation="select";
					exc_sql="SELECT * FROM general;";
					res_sql="SELECT * FROM general;";
				}
				break;
			case "link":
				if(map.containsKey("uri"))
				{
					if(map.containsKey("old_uri"))
					{
						port=Integer.parseInt(map.get("port"));
						uri=map.get("uri");
						extensive=Boolean.parseBoolean(map.get("extensive"));
						permission=Integer.parseInt(map.get("permission"));
						authentication=map.get("authentication");
						directory=Boolean.parseBoolean(map.get("directory"));
						visible=Boolean.parseBoolean(map.get("visible"));
						type=Integer.parseInt(map.get("type"));
						path=map.get("path");
						pathextensive=Boolean.parseBoolean(map.get("pathextensive"));

						old_port=Integer.parseInt(map.get("old_port"));
						old_path=map.get("old_path");
						old_uri=map.get("old_uri");
						old_extensive=Boolean.parseBoolean(map.get("old_extensive"));
						old_permission=Integer.parseInt(map.get("old_permission"));
						old_authentication=map.get("old_authentication");
						old_directory=Boolean.parseBoolean(map.get("old_directory"));
						old_visible=Boolean.parseBoolean(map.get("old_visible"));
						old_type=Integer.parseInt(map.get("old_type"));
						old_pathextensive=Boolean.parseBoolean(map.get("old_pathextensive"));

						operation="update";
						exc_sql="UPDATE link SET port="+port+", uri='"+uri+"', extensive="+extensive+", permission="+permission+", " +
								"authentication='"+authentication+"', directory="+directory+", visible="+visible+", type="+type+", " +
								"path='"+path+"', pathextensive="+pathextensive+" "+
								"WHERE port="+old_port+" AND uri='"+old_uri+"' AND extensive="+old_extensive+" AND " +
								"permission="+old_permission+" AND authentication='"+old_authentication+"' AND directory="+old_directory+" AND" +
								"visible="+old_visible+" AND type="+old_type+" AND path='"+old_path+"' AND pathextensive='"+old_pathextensive+"';";
						res_sql="SELECT * FROM link WHERE port="+port+", uri='"+uri+"', extensive="+extensive+", permission="+permission+", " +
								"authentication='"+authentication+"', directory="+directory+", visible="+visible+", type="+type+", " +
								"path='"+path+"', pathextensive="+pathextensive+" ;";
					}
					else
					{
						port=Integer.parseInt(map.get("port"));
						uri=map.get("uri");
						extensive=Boolean.parseBoolean(map.get("extensive"));
						permission=Integer.parseInt(map.get("permission"));
						authentication=map.get("authentication");
						directory=Boolean.parseBoolean(map.get("directory"));
						visible=Boolean.parseBoolean(map.get("visible"));
						type=Integer.parseInt(map.get("type"));
						path=map.get("path");
						pathextensive=Boolean.parseBoolean(map.get("pathextensive"));

						operation="insert";
						exc_sql="INSERT INTO link (port,uri,extensive,authentication,directory,visible,type,path,pathextensive) " +
								"VALUES ("+port+", '"+uri+"', "+extensive+", "+permission+", '"+authentication+"', "+directory+", "+visible+", "+type+", '"+path+"', "+pathextensive+") ;";
						res_sql="SELECT * FROM link WHERE port="+port+", uri='"+uri+"', extensive="+extensive+", permission="+permission+", " +
								"authentication='"+authentication+"', directory="+directory+", visible="+visible+", type="+type+", " +
								"path='"+path+"', pathextensive="+pathextensive+" ;";
					}
				}
				else if(map.containsKey("old_uri"))
				{
					old_port=Integer.parseInt(map.get("old_port"));
					old_path=map.get("old_path");
					old_uri=map.get("old_uri");
					old_extensive=Boolean.parseBoolean(map.get("old_extensive"));
					old_permission=Integer.parseInt(map.get("old_permission"));
					old_authentication=map.get("old_authentication");
					old_directory=Boolean.parseBoolean(map.get("old_directory"));
					old_visible=Boolean.parseBoolean(map.get("old_visible"));
					old_type=Integer.parseInt(map.get("old_type"));
					old_pathextensive=Boolean.parseBoolean(map.get("old_pathextensive"));

					operation="delete";
					exc_sql="DELETE FROM link WHERE port="+old_port+" AND uri='"+old_uri+"' AND extensive="+old_extensive+" AND " +
							"permission="+old_permission+" AND authentication='"+old_authentication+"' AND directory="+old_directory+" AND" +
							"visible="+old_visible+" AND type="+old_type+" AND path='"+old_path+"' AND pathextensive='"+old_pathextensive+"';";
					res_sql="SELECT * FROM link WHERE 0;";
				}
				else
				{
					operation="select";
					exc_sql="SELECT * FROM link;";
					res_sql="SELECT * FROM link;";
				}
				break;
			case "error":
				if(map.containsKey("code"))
				{
					if(map.containsKey("old_code"))
					{
						code=Integer.parseInt(map.get("code"));
						path=map.get("path");

						old_code=Integer.parseInt(map.get("old_code"));
						old_path=map.get("old_path");

						operation="update";
						exc_sql="UPDATE error SET code="+code+", path='"+path+"' " +
								"WHERE code="+old_code+" AND path='"+old_path+"';";
						res_sql="SELECT * FROM error WHERE code="+code+" AND path='"+path+"';";
					}
					else
					{
						code=Integer.parseInt(map.get("code"));
						path=map.get("path");

						operation="insert";
						exc_sql="INSERT INTO error (code,path) VALUES ("+code+",'"+path+"');";
						res_sql="SELECT * FROM error WHERE ocde="+code+" AND path='"+path+"';";
					}
				}
				else if(map.containsKey("old_code"))
				{
					old_code=Integer.parseInt(map.get("old_code"));
					old_path=map.get("old_path");

					operation="delete";
					exc_sql="DELETE FROM error WHERE code="+old_code+" AND path='"+old_path+"';";
					res_sql="SELECT * FROM error WHERE 0;";
				}
				else
				{
					operation="select";
					exc_sql="SELECT * FROM error;";
					res_sql="SELECT * FROM error;";
				}
				break;
			case "lock":
				if(map.containsKey("version"))
				{
					if(map.containsKey("old_version"))
					{
						version=Integer.parseInt(map.get("version"));

						old_version=Integer.parseInt(map.get("old_version"));

						operation="update";
						exc_sql="UPDATE lock SET version="+version+" WHERE version="+old_version+" ;";
						res_sql="SELECT * FROM lock WHERE version="+version+" ;";
					}
					else
					{
						version=Integer.parseInt(map.get("version"));

						operation="insert";
						exc_sql="INSERT INTO lock (version) VALUES ("+version+") ;";
						res_sql="SELECT * FROM lock WHERE version="+version+" ;";
					}
				}
				else if(map.containsKey("old_version"))
				{
					old_version=Integer.parseInt(map.get("old_version"));

					operation="delete";
					exc_sql="DELETE FROM lock WHERE version="+old_version+" ;";
					res_sql="SELECT * FROM lock WHERE 0;";
				}
				else
				{
					operation="select";
					exc_sql="SELECT * FROM lock;";
					res_sql="SELECT * FROM lock;";
				}
				break;
			default:
				message="table does not exist";
				break;
		}
		Database db=new Database(database);
		try
		{
			if(!operation.equals("select"))
			{
				db.execSQL(exc_sql);
			}
		}
		catch(Exception e)
		{
			message="Exception: "+e;
	    }finally
		{
			Result result=db.query(res_sql);
			Map<String, Object> js=new HashMap<>();
			js.put("operation",operation);
			js.put("message",message);
			List l=new ArrayList();
			for(String column : result.columns)
			{
				l.add(column);
			}
			js.put("columns",l);
			List newlist=new ArrayList();
			for(Map map1 : result.values)
			{
				newlist.add(map1);
			}
			js.put("values",newlist);
			JSONObject newjson = new JSONObject(js);
			out.println(newjson.toString());
		}
		db.close();
		out.flush();
	}
}
