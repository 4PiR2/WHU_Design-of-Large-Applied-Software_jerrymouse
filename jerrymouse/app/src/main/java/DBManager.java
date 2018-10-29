import java.io.*;
import java.util.*;

import net.happygod.jerrymouse.server.*;
import net.happygod.jerrymouse.database.*;

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
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out=response.getWriter();
		out.println("<html><head>");
		out.println("<title>Results</title>");
		out.println("</head><body>");
		String database=request.getParameter("database");
		String type=request.getParameter("type");
		String sql=request.getParameter("sql");
		if(database==null||type==null||sql==null)
		{
			out.println("<h3>Parameters Error</h3>");
		}
		else
		{
			Database db=new Database(database);
			if("query".equals(type))
			{
				Result result=db.query(sql);
				out.println("<table border='1'>");
				out.println("<tr>");
				for(String column : result.columns)
				{
					out.println("<th>"+column+"</th>");
				}
				out.println("</tr>");
				for(Map map : result.values)
				{
					out.println("<tr>");
					for(String column : result.columns)
					{
						Object v=map.get(column);
						out.println("<td>"+(v!=null?v:"")+"</td>");
					}
					out.println("</tr>");
				}
				out.println("</table>");
			}
			else if("execute".equals(type))
			{
				db.execSQL(sql);
			}
			db.close();
		}
		out.println("</body></html>");
		out.flush();
	}
}
