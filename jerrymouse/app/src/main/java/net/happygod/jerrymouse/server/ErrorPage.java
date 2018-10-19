package net.happygod.jerrymouse.server;

import net.happygod.jerrymouse.database.*;
import java.io.*;

class ErrorPage
{
	final ByteArrayOutputStream baos=new ByteArrayOutputStream();
	ErrorPage(HTTPException he)
	{
		int code=he.code();
		String description=he.description(),message=he.message();
		Database db=new Database("jerrymouse");
		Result result=db.query("SELECT page FROM error WHERE enabled=1 AND code="+code);
		try
		{
			String page=(String)result.values.iterator().next().get("page");
			//String page="/storage/emulated/0/web/404.html";
			BufferedInputStream bis=new BufferedInputStream(new FileInputStream(page));
			Pipe.pipe(bis,baos);
			bis.close();
		}
		catch(Exception e)
		{
			PrintWriter pw=new PrintWriter(baos);
			pw.println("<html><head>");
			pw.println("<meta content='text/html;charset=utf-8'>");
			pw.println("<title>"+code+" "+description+"</title>");
			pw.println("</head><body>");
			pw.println("<h1>"+code+" "+description+"</h1>");
			pw.println("<hr />");
			pw.println("<pre>");
			pw.println(message);
			pw.println("</pre>");
			pw.println("<br />");
			pw.println("<p>Jerrymouse Web Server</p>");
			pw.println("</body></html>");
			pw.flush();
		}
	}
}
