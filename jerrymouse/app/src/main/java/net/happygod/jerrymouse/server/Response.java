package net.happygod.jerrymouse.server;

import java.io.*;
import java.net.*;

public class Response
{
	private final BufferedOutputStream bos;
	private final ByteArrayOutputStream baos;
	private final PrintWriter pw;
	private final DataOutputStream dos;
	Response(Socket s) throws IOException
	{
		bos=new BufferedOutputStream(s.getOutputStream());
		baos=new ByteArrayOutputStream();
		pw=new PrintWriter(baos);
		dos=new DataOutputStream(baos);
	}
	public void setContentType(String contentType)
	{
		pw.println("Content-Type: "+contentType);
		//TODO set charset
	}
	public PrintWriter getWriter()
	{
		return pw;
	}
	public DataOutputStream getDataStream()
	{
		return dos;
	}
	BufferedOutputStream getStream()
	{
		return bos;
	}
	void reset() throws IOException
	{
		pw.flush();
		dos.flush();
		baos.reset();
	}
	void commit() throws IOException
	{
		baos.writeTo(bos);
		bos.flush();
	}
}
