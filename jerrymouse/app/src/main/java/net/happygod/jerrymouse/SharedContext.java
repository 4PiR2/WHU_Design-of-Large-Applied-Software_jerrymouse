package net.happygod.jerrymouse;

import android.content.*;

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
}
