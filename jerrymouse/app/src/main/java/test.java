import net.happygod.jerrymouse.server.*;

import java.io.PrintWriter;

public class test extends Servlet
{
	@Override
	public void doGet(Request request,Response response)
	{
		PrintWriter out=response.getWriter();
		out.println("get!");
		out.flush();
	}
	
	@Override
	public void doPost(Request request,Response response)
	{
		PrintWriter out=response.getWriter();
		out.println("post!");
		out.flush();
	}
}
