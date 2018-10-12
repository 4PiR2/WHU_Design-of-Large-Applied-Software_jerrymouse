package net.happygod.jerrymouse.database;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import java.util.*;

public class Database
{
	private SQLiteDatabase db;
	public Database(String name, Context context)
	{
		Helper helper = new Helper(context,name);
		db=helper.getReadableDatabase();
		//db=helper.getWritableDatabase();
	}
	public void execSQL(String sql)
	{
		db.execSQL(sql);
	}
	public Result query(String sql)
	{
		Result result=new Result();
		Cursor cursor=db.rawQuery(sql,null);
		result.columns().addAll(Arrays.asList(cursor.getColumnNames()));
		while(cursor.moveToNext())
		{
			Map<String,String> map=new HashMap<>();
			for(String column:result.columns())
			{
				map.put(column,cursor.getString(cursor.getColumnIndex(column)));
			}
			result.values().add(map);
		}
		cursor.close();
		return result;
	}
	public void close()
	{
		db.close();
	}
}
