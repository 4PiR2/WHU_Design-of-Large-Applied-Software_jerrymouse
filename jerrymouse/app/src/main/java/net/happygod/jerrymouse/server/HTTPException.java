package net.happygod.jerrymouse.server;

import java.io.*;

public class HTTPException extends Exception
{
	private final int code;
	private final String description;
	private String message="";
	public HTTPException(int code)
	{
		this.code=code;
		switch(code)
		{
			case 200:
				description="OK";
				break;
			case 400:
				description="BAD REQUEST";
				break;
			case 403:
				description="FORBIDDEN";
				break;
			case 404:
				description="NOT FOUND";
				break;
			default:
				description="";
				//TODO more error codes
		}
	}
	public HTTPException(int code,String message)
	{
		this(code);
		this.message=message;
	}
	public HTTPException(int code,Exception e)
	{
		this(code);
		if(e instanceof HTTPException)
		{
			message=((HTTPException)e).message;
		}
		else
		{
			StringWriter sw=new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			message=sw.toString();
			try
			{
				sw.close();
			}
			catch(IOException ioe)
			{
				message+="\n"+ioe.getMessage();
			}
		}
	}
	int code()
	{
		return code;
	}
	String description()
	{
		return description;
	}
	String message()
	{
		return message;
	}
}
