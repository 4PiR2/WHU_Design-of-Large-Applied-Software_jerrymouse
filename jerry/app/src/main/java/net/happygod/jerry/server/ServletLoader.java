package net.happygod.jerry.server;

import java.io.*;

public class ServletLoader extends ClassLoader
{
	private String path;
	public ServletLoader(String path)
	{
		this.path=path;
	}
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException
	{
		//String fileName=getFileName(name);
		//File file=new File(path,fileName);
		File file=new File(path,name+".class");
		try
		{
			FileInputStream is=new FileInputStream(file);
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			int len=0;
			try
			{
				while((len=is.read())!=-1)
				{
					bos.write(len);
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
			byte[] data=bos.toByteArray();
			is.close();
			bos.close();

			return defineClass(name,data,0,data.length);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return super.findClass(name);
	}
}