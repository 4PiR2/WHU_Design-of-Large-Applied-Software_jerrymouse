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
			case 100:
				description="Continue";
				break;
			case 101:
				description="switch";
				break;
			case 102:
				description="Processing";
				break;
			case 200:
				description="OK";
				break;
			case 201:
				description="Created";
				break;
			case 202:
				description="Accepted";
				break;
			case 203:
				description="Non-Authoritative Information";
				break;
			case 204:
				description="No Contact";
				break;
			case 205:
				description="Reset Content";
				break;
			case 206:
				description="Partial Content";
				break;
			case 207:
				description="Multi-Status";
				break;
			case 300:
				description="Multiple Choices";
				break;
			case 301:
				description="Moved permanently";
				break;
			case 302:
				description="Move temporarily";
				break;
			case 303:
				description="See Other";
				break;
			case 304:
				description="Not Modified";
				break;
			case 305:
				description="Use Proxy";
				break;
			case 306:
				description="Switch Proxy";
				break;
			case 307:
				description="Temporary Redirect";
				break;
			case 400:
				description="Bad Request";
				break;
			case 401:
				description="Unauthorized";
				break;
			case 402:
				description="Payment Required";
				break;
			case 403:
				description="Forbidden";
				break;
			case 404:
				description="Not Found";
				break;
			case 405:
				description="Method Not Allowed";
				break;
			case 406:
				description="Not Acceptable";
				break;
			case 407:
				description="Proxy Authentication Required";
				break;
			case 408:
				description="Request Timeout";
				break;
			case 409:
				description="Conflict";
				break;
			case 410:
				description="Gone";
				break;
			case 411:
				description="Length Required";
				break;
			case 412:
				description="Precondition Failed";
				break;
			case 413:
				description="Request Entity Too Large";
				break;
			case 414:
				description="Request-URL Too Long";
				break;
			case 415:
				description="Unsupported Media Type";
				break;
			case 416:
				description="Requested Range Not Satisfiable";
				break;
			case 417:
				description="Expectation Failed";
				break;
			case 421:
				description="too many connections";
				break;
			case 422:
				description="Unprocessable Entity";
				break;
			case 423:
				description="Locked";
				break;
			case 424:
				description="Failed Dependency";
				break;
			case 425:
				description="Unordered Collection";
				break;
			case 426:
				description="Upgrade Required";
				break;
			case 449:
				description="Retry With";
				break;
			case 451:
				description="Unavaliable For Legal Reasons";
				break;
			case 500:
				description="Internal Server Error";
				break;
			case 501:
				description="Not Implemented";
				break;
			case 502:
				description="Bad Gateway";
				break;
			case 503:
				description="Service Unavailable";
				break;
			case 504:
				description="Gateway Timeout";
				break;
			case 505:
				description="HTTP Version Not Supported";
				break;
			case 506:
				description="Variant Also Negotiates";
				break;
			case 507:
				description="Insufficient Storage";
				break;
			case 509:
				description="Bandwidth Limit Exceeded";
				break;
			case 510:
				description="Not Extended";
				break;
			case 600:
				description="Unparseable Response Headers";
				break;

			default:
				description="";
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
