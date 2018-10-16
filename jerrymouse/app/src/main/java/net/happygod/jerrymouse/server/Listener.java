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
		}
		catch(Exception e)
		{
		}
		try
		{
			thread.join();
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	public void run()
	{
		try
		{
			serverSocket=new ServerSocket(server.port());
			System.out.println("Listener listening on port "+server.port());
		}
		catch(IOException e)
		{
			System.out.println("Unable to listen on port "+server.port()+": "+e.getMessage());
		}
		Socket socket;
		while(running)
		{
			try
			{
				socket=serverSocket.accept();
				//TODO port in use
				socket.setKeepAlive(true);
			}
			catch(IOException e)
			{
				System.out.println("Unable to accept connection: "+e.getMessage());
				break;
			}
			System.out.println("Connection accepted.");
			executor.execute(new Loader(socket,server));
		}
		executor.shutdownNow();
		try
		{
			serverSocket.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}