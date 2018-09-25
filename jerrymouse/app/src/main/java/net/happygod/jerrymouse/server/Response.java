package net.happygod.jerrymouse.server;

import java.io.*;
import java.net.*;

public class Response
{
	private final PrintWriter pw;
	private final DataOutputStream dos;
	Response(Socket s) throws IOException
	{
		pw=new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())));
		dos=new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
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
