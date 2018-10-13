package net.happygod.jerrymouse.database;

import android.content.*;
import android.database.sqlite.*;

public class Database
{
	private SQLiteDatabase db;
	public Database(String name, Context context)
	{
		db=new Helper(context,name).getReadableDatabase();
		//db=helper.getWritableDatabase();
	}
	public void execSQL(String sql)
	{
		db.execSQL(sql);
	}
	public Result query(String sql)
	{
		return new Result(db.rawQuery(sql,null));
	}
	public void close()
	{
		db.close();
	}
}
