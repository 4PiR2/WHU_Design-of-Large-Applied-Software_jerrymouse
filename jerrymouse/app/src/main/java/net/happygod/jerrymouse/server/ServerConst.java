package net.happygod.jerrymouse.server;

import java.util.*;
import java.util.concurrent.*;
public final class ServerConst
{
	static final ExecutorService EXECUTOR=Executors.newCachedThreadPool();
	public static final int SYS_PORT=1998;
	static final Map<String,String> MIME=new HashMap<String,String>()
	{
		{
			put("","application/octet-stream");
			put("txt","text/plain");
			put("html","text/html; charset=utf-8");
			put("css","text/css");
			put("js","application/x-javascript");
			put("json","application/json");
			put("jpg","image/jpeg");
			put("png","image/png");
			put("bmp","image/bmp");
			put("gif","image/gif");
			put("svg","image/svg+xml");
			put("mp3","audio/mpeg");
			put("mp4","video/mp4");
			put("zip","application/zip");
		}
	};
}
