import java.io.*;
import java.util.*;

import net.happygod.jerrymouse.database.*;
import net.happygod.jerrymouse.server.*;
import org.json.JSONObject;

public class DBManager extends Servlet
{
	private String operation;
	@Override
	public void doGet(Request request,Response response)
	{
		response.setContentType("text/json; charset=utf-8");
		StringBuilder SQL=new StringBuilder();
		String message="";
		Result result=null;
		Map<String,String> control=new HashMap<>(),newValue=new HashMap<>(),oldValue=new HashMap<>();
		Enumeration<String> names=request.getParameterNames();
		while(names.hasMoreElements())
		{
			String name=names.nextElement(),value=request.getParameter(name);
			switch(name)
			{
				case "":
					break;
				case "table":
				case "database":
				case "sql":
				case "qe":
					if(value!=null&&!"".equals(value))
						control.put(name,value);
					break;
				default:
					if(name.startsWith("old_"))
						oldValue.put(name.substring(4),value);
					else
						newValue.put(name,value);
			}
		}
		String database=control.get("database"),table=control.get("table"),sql=control.get("sql"),qe=control.get("qe");
		if(database!=null&&!(sql==null&&table==null))
		{
			if(sql==null)
			{
				StringBuilder where=new StringBuilder(" WHERE 1");
				for(String key : newValue.keySet())
				{
					where.append(" AND ").append(key).append("='").append(oldValue.get(key)).append("'");
				}
				if(operation==null)
					operation="SELECT";
				SQL.append("SELECT * FROM ").append(table).append(where).append(";");
				sql=SQL.toString();
			}
			else
			{
				operation="SQL";
			}
		}
		else
		{
			operation="FAIL";
		}
		try
		{
			Database db=new Database(database);
			result=db.query(sql);
			db.close();
		}
		catch(Exception e)
		{
			operation="FAIL";
			message=e.getMessage();
		}
		Map<String,Object> json=new HashMap<>();
		json.put("operation",operation);
		json.put("message",message);
		if(result!=null)
		{
			json.put("columns",result.columns);
			json.put("values",result.values);
		}
		PrintWriter out=response.getWriter();
		out.println(new JSONObject(json).toString());
		out.flush();
	}
	@Override
	public void doPost(Request request,Response response)
	{
		StringBuilder SQL=new StringBuilder();
		Map<String,String> control=new HashMap<>(),newValue=new HashMap<>(),oldValue=new HashMap<>();
		Enumeration<String> names=request.getParameterNames();
		while(names.hasMoreElements())
		{
			String name=names.nextElement(),value=request.getParameter(name);
			switch(name)
			{
				case "":
					break;
				case "table":
				case "database":
				case "sql":
				case "qe":
					if(value!=null&&!"".equals(value))
						control.put(name,value);
					break;
				default:
					if(name.startsWith("old_"))
						oldValue.put(name.substring(4),value);
					else
						newValue.put(name,value);
			}
		}
		String database=control.get("database"),table=control.get("table"),sql=control.get("sql"),qe=control.get("qe");
		if(database!=null&&!(sql==null&&table==null))
		{
			if(sql==null)
			{
				if(oldValue.isEmpty())
				{
					if(!newValue.isEmpty())
					{
						operation="INSERT";
						StringBuilder insert=new StringBuilder(), values=new StringBuilder();
						for(String key : newValue.keySet())
						{
							insert.append(",").append(key);
							values.append(",'").append(newValue.get(key)).append("'");
						}
						insert.setCharAt(0,'(');
						values.setCharAt(0,'(');
						insert.append(")");
						values.append(")");
						SQL.append("INSERT INTO ").append(table).append(insert).append(" VALUES").append(values);
					}
					else
					{
						operation="SELECT";
						//SQL.append("SELECT * FROM ").append(table);
					}
				}
				else
				{
					StringBuilder where=new StringBuilder(" WHERE 1");
					for(String key : oldValue.keySet())
					{
						where.append(" AND ").append(key).append("='").append(oldValue.get(key)).append("'");
					}
					if(newValue.isEmpty()&&!oldValue.isEmpty())
					{
						operation="DELETE";
						SQL.append("DELETE FROM ").append(table).append(where);
					}
					else
					{
						operation="UPDATE";
						StringBuilder set=new StringBuilder();
						for(String key : newValue.keySet())
						{
							set.append(key).append("=").append(newValue.get(key)).append(",");
						}
						set.deleteCharAt(set.length()-1);
						SQL.append("UPDATE ").append(table).append(" SET ").append(set).append(where);
					}
				}
				SQL.append(";");
				sql=SQL.toString();
			}
			else
			{
				operation="SQL";
			}
		}
		else
		{
			operation="FAIL";
		}
		try
		{
			if("SQL".equals(operation)||"FAIL".equals(operation))
			{
				throw new Exception();
			}
			if(!"SELECT".equals(operation))
			{
				Database db=new Database(database);
				db.execSQL(sql);
				db.close();
			}
		}
		catch(Exception e)
		{
			response.setContentType("text/json; charset=utf-8");
			if(!"SQL".equals(operation))
			{
				operation="FAIL";
			}
			Map<String,Object> json=new HashMap<>();
			json.put("operation",operation);
			json.put("message",e.getMessage());
			PrintWriter out=response.getWriter();
			out.println(new JSONObject(json).toString());
			out.flush();
			return;
		}
		doGet(request,response);
	}
}
