package net.happygod.jerrymouse.server;

import java.io.*;

public class Pipe extends Thread
{
	private final InputStream in;
	private final OutputStream out;
	private HTTPException he;
	private Pipe(InputStream in,OutputStream out)
	{
		this.in=in;
		this.out=out;
		start();
	}
	@Override
	public void run()
	{
		he=pipeCore(in,out);
	}
	private static HTTPException pipeCore(InputStream in,OutputStream out)
	{
		try
		{
			byte[] buffer=new byte[2048];
			int size;
			while((size=in.read(buffer))>0)
			{
				out.write(buffer,0,size);
				out.flush();
			}
		}
		catch(IOException ioe)
		{
			return new HTTPException(500,ioe);
		}
		return null;
	}
	public static void pipe(InputStream in,OutputStream out) throws HTTPException
	{
		HTTPException he=pipeCore(in,out);
		if(he!=null)
			throw he;
	}
	public static void pipe(InputStream in1,OutputStream out2,InputStream in2,OutputStream out1) throws HTTPException
	{
		Pipe p=new Pipe(in2,out1);
		pipe(in1,out2);
		/*try
		{
			p.join();
		}
		catch(InterruptedException ie)
		{
			throw new HTTPException(500,ie);
		}*/
		if(p.he!=null)
			throw p.he;
	}
}
