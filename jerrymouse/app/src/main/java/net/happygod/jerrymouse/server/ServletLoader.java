package net.happygod.jerrymouse.server;

import java.io.*;

class ServletLoader extends ClassLoader
{
	private byte[] data;
	ServletLoader(String path)
	{
		try
		{
			BufferedInputStream bis=new BufferedInputStream(new FileInputStream(path));
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			BufferedOutputStream bos=new BufferedOutputStream(baos);
			Serve.pipe(bis,bos);
			bis.close();
			data=baos.toByteArray();
			bos.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	@Override
	protected Class<?> findClass(String className)
	{
		return defineClass(className,data,0,data.length);
	}
}