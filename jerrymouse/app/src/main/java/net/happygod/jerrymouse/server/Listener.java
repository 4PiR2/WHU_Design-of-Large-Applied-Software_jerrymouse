package net.happygod.jerrymouse.server;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

class Listener implements Runnable
{
	private final Thread thread;
	private boolean running;
	private final Server server;
	private ServerSocket serverSocket;
	Listener(Server server)
	{
		this.server=server;
		running=true;
		thread=new Thread(this);
		thread.start();
	}
	void stop()
	{
		running=false;
		try
		{
			new Socket("localhost",server.port());
			thread.join();
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
	}
	public void run()
	{
		try
		{
			serverSocket=new ServerSocket(server.port());
			//System.err.println("Listener listening on port "+server.port());
		}
		catch(IOException e)
		{
			//port in use
			server.failure("Unable to listen on port "+server.port()+": "+e.getMessage());
			return;
		}
		Socket socket;
		while(running)
		{
			try
			{
				socket=serverSocket.accept();
				socket.setKeepAlive(true);
			}
			catch(IOException ioe)
			{
				continue;
			}
			//System.err.println("Connection accepted.");
			ServerConst.EXECUTOR.execute(new Connector(socket,server));
		}
		//executor.shutdownNow();
		try
		{
			serverSocket.close();
		}
		catch(IOException ioe)
		{
			//ioe.printStackTrace();
		}
	}
}
