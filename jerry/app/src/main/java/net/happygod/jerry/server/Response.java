package net.happygod.jerry.server;

import java.io.*;

public class Response
{
	private PrintWriter out;
	Response(DataOutputStream dos)
	{
		out=new PrintWriter(dos);
	}
	public void setContentType(String contentType)
	{
		out.println("Content-Type: "+contentType);
		//TODO set charset
	}
	public PrintWriter getWriter()
	{
		return out;
	}
}
