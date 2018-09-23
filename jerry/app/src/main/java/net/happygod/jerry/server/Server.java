package net.happygod.jerry.server;

import java.net.*;
import java.io.*;

public class Server implements Runnable
{
    private boolean running;
    private Config config;
    private ServerSocket serverSocket;
    public Server(Config config)
    {
        this.config=config;
        running = true;
        new Thread(this).start();
    }
	public void stop()
    {
		running = false;
	}
	public void run()
    {
        try
        {
            serverSocket = new ServerSocket(config.port);
            System.out.println("Server listening on port " + config.port);
        }
        catch(IOException e)
        {
            System.err.println("Unable to listen on port " + config.port + ": " + e.getMessage());
        }
        Socket s;
		while(running)
        {
			try
            {
				s = serverSocket.accept();
			}
			catch(IOException e)
            {
				System.err.println("Unable to accept connection: " + e.getMessage());
				continue;
			}
			System.out.println("Connection accepted.");
			new Serve(s,config);
		}
	}
}