import java.io.*;
import java.util.*;

import net.happygod.jerrymouse.server.*;
import net.happygod.jerrymouse.database.*;

public class DBManage extends Servlet
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
		String type=request.getParameter("type");
		String sql=request.getParameter("sql");
		Database db=new Database("jerrymouse",super.config().context());
		if(type==null||sql==null)
		{
			out.println("<h3>Parameters Error</h3>");
		}
		else if(type.equals("query"))
		{
			Result result=db.query(sql);
			out.println("<table border='1'>");
			out.println("<tr>");
			for(String column:result.columns)
			{
				out.println("<th>"+column+"</th>");
			}
			out.println("</tr>");
			for(Map map:result.values)
			{
				out.println("<tr>");
				for(String column:result.columns)
				{
					out.println("<td>"+map.get(column)+"</td>");
				}
				out.println("</tr>");
			}
			out.println("</table>");
		}
		else if(type.equals("execute"))
		{
			db.execSQL(sql);
		}
		db.close();
		out.println("</body></html>");
		out.flush();
	}
}
