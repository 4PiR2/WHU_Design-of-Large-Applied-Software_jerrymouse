package net.happygod.jerrymouse;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.widget.*;
import java.io.*;
import net.happygod.jerrymouse.server.*;

public final class Const
{
	static Application context;
	public static Context context()
	{
		return context;
	}
	public static void toast(String str,int length)
	{
		Toast.makeText(context,str,length).show();
	}
	static boolean copyAssets(AssetManager am,String dir,String path) throws IOException, HTTPException
	{
		String[] list=am.list(dir);
		//empty directories will not be compiled to apk, no need to detect
		if(list.length==0)
			return false;
		//mkdir
		new File(path).mkdirs();
		for(String fileName : list)
		{
			//recursively call
			String newDir=dir+("".equals(dir)?"":"/")+fileName, newPath=path+"/"+fileName;
			if(!copyAssets(am,newDir,newPath))
			{
				//cp
				BufferedInputStream bis=new BufferedInputStream(am.open(newDir));
				BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(newPath));
				Pipe.pipe(bis,bos);
				bis.close();
				bos.close();
			}
		}
		return true;
	}
}
