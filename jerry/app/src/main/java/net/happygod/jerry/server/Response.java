package net.happygod.jerry.server;

import java.io.*;

public class Response
{
	private final PrintWriter pw;
	private final DataOutputStream dos;
	Response(OutputStream os)
	{
		pw=new PrintWriter(new BufferedWriter(new OutputStreamWriter(os)));
		dos=new DataOutputStream(new BufferedOutputStream(os));
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
	public DataOutputStream getStream()
	{
		return dos;
	}
}
