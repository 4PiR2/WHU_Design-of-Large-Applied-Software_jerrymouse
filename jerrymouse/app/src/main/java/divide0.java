import java.io.*;
import java.util.*;
import net.happygod.jerrymouse.server.*;

public class test extends Servlet
{
	@Override
	public void init()
	{
		System.out.println("Servlet -- init");
	}
	@Override
	public void doGet(final Request request,final Response response)
	{
		response.setContentType("text/html");
		PrintWriter out=response.getWriter();
		out.println("<html><head>");
		out.println("<title>ServletTest</title>");
		out.println("</head><body>");

		out.println("<h2>Headers</h2>");
		Enumeration headers=request.getHeaderNames();
		while(headers.hasMoreElements())
		{
			String header=(String)headers.nextElement();
			out.println(header+" : "+request.getHeader(header)+"<br />");
		}
		out.println("<h2>Method</h2>");
		out.println(request.getMethod()+"<br />");

		out.println("<h2>Parameters</h2>");
		Enumeration parameters=request.getParameterNames();
		while(parameters.hasMoreElements())
		{
			String parameter=(String)parameters.nextElement();
			out.println(parameter+" : "+request.getParameter(parameter)+"<br />");
		}

		out.println("<h2>Query String</h2>");
		out.println(request.getQueryString()+"<br />");

		out.println("<h2>Request URI</h2>");
		out.println(request.getRequestURI()+"<br />");

		out.println("<h2>RAW</h2>");
		out.println("<pre>");
		out.write(new String(request.getReadData()));
		out.flush();
		Thread t=new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					byte[] buffer=new byte[2048];
					InputStream in=request.getStream();
					OutputStream out=response.getStream();
					out.write(buffer,0,in.read(buffer));
					out.flush();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		out.println("</pre>");
		out.println("</body></html>");
		out.flush();
	}

	@Override
	public void doPost(Request request,Response response)
	{
		doGet(request,response);
	}
}
