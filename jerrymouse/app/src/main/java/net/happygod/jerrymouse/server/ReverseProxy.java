package net.happygod.jerrymouse.server;

import java.io.*;
import java.net.*;

class ReverseProxy extends Servlet
{
	@Override
	void doDefault(Request request,Response response) throws HTTPException
	{
		int port=80;
		String[] hostTemp=server().webroot().split(":");
		String host=hostTemp[0];
		if(hostTemp.length>1)
			port=Integer.parseInt(hostTemp[1]);
		try
		{
			final Socket proxySocket=new Socket(host,port);
			final BufferedInputStream proxyInput=new BufferedInputStream(proxySocket.getInputStream());
			final BufferedOutputStream proxyOutput=new BufferedOutputStream(proxySocket.getOutputStream());
			Pipe.pipe(proxyInput,response.getRawStream(),request.getStream(),proxyOutput);
			proxySocket.close();
		}
		catch(IOException ioe)
		{
			throw new HTTPException(500,ioe);
		}
		throw new HTTPException(-1);
	}
}
