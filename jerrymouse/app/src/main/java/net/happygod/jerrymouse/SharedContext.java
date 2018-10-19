package net.happygod.jerrymouse;

import android.content.*;
import android.widget.*;

public class SharedContext
{
	private static Context context;
	static void set(Context context)
	{
		SharedContext.context=context;
	}
	public static Context get()
	{
		return context;
	}
	public static void toast(String str,int length)
	{
		Toast.makeText(context,str,length).show();
	}
}
