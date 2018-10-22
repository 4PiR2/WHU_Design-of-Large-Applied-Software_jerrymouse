package net.happygod.jerrymouse.server;

import java.io.*;
import java.net.*;

class Proxy extends Servlet
{
	@Override
	void doDefault(Request request,Response response) throws HTTPException
	{
		request.parse();
		int port=80;
		String requestMethod=request.getMethod(),host=request.getHeader("host");
		if(host==null)
			throw new HTTPException(400);
		String[] hostTemp=host.split(":");
		host=hostTemp[0];
		if(hostTemp.length>1)
			port=Integer.parseInt(hostTemp[1]);
		try
		{
			final Socket proxySocket=new Socket(host,port);
			final BufferedInputStream proxyInput=new BufferedInputStream(proxySocket.getInputStream());
			final BufferedOutputStream proxyOutput=new BufferedOutputStream(proxySocket.getOutputStream());
			//check permission
			if(settings().authentication!=null&&!"".equals(settings().authentication))
			{
				String authorization=request.getHeader("proxy-authorization");
				if(authorization==null||!authorization.substring(authorization.indexOf(' ')+1).equals(settings().authentication))
				{
					response.setHeader("proxy-authenticate","Basic realm=\"Connecting to Jerrymouse Proxy\"");
					throw new HTTPException(407);
				}
			}
			if("CONNECT".equals(requestMethod))
			{
				//HTTPS Connection Established
				response.commit(new HTTPException(200));
			}
			else
			{
				proxyOutput.write(request.getReadData());
				proxyOutput.flush();
			}
			Pipe.pipe(proxyInput,response.getRawStream(),request.getStream(),proxyOutput);
			proxySocket.close();
			throw new HTTPException(-1);
		}
		catch(IOException ioe)
		{
			throw new HTTPException(500,ioe);
		}
	}
}
