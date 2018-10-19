package net.happygod.jerrymouse.server;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

class Listener implements Runnable
{
	private final Thread thread;
	private final ExecutorService executor=Executors.newCachedThreadPool();
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
			server.failure(e.getMessage());
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
			catch(IOException e)
			{
				server.failure("Unable to accept connection: "+server.port()+": "+e.getMessage());
				break;
			}
			//System.err.println("Connection accepted.");
			executor.execute(new Loader(socket,server));
		}
		executor.shutdownNow();
		try
		{
			serverSocket.close();
		}
		catch(IOException e)
		{
			server.failure(e.getMessage());
		}
	}
}