package net.happygod.jerrymouse.server;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

class Server implements Runnable
{
	private Thread thread;
	private ExecutorService executor=Executors.newCachedThreadPool();
	private boolean running;
	private final Config config;
	private ServerSocket serverSocket;
	Server(Config config)
	{
		this.config=config;
		running=true;
		thread=new Thread(this);
		thread.start();
	}
	void stop()
	{
		running=false;
		try
		{
			new Socket("localhost",config.port());
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
			serverSocket=new ServerSocket(config.port());
			System.out.println("Server listening on port "+config.port());
		}
		catch(IOException e)
		{
			System.out.println("Unable to listen on port "+config.port()+": "+e.getMessage());
		}
		Socket s;
		while(running)
		{
			try
			{
				s=serverSocket.accept();
				//TODO port in use
				s.setKeepAlive(true);
			}
			catch(IOException e)
			{
				System.out.println("Unable to accept connection: "+e.getMessage());
				break;
			}
			System.out.println("Connection accepted.");
			executor.execute(new Serve(s,config));
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