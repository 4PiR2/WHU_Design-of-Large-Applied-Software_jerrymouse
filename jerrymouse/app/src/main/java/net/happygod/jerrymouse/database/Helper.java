package net.happygod.jerrymouse.database;

import android.content.*;
import android.database.sqlite.*;

class Helper extends SQLiteOpenHelper
{
	private static Integer version = 1;
	Helper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version)
	{
		super(context,name,factory,version);
	}
	Helper(Context context,String name,int version)
	{
		this(context,name,null,version);
	}
	Helper(Context context,String name)
	{
		this(context, name, version);
	}
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		System.out.println("create database");
		//创建了数据库并创建一个叫records的表
		//SQLite数据创建支持的数据类型： 整型数据，字符串类型，日期类型，二进制的数据类型
		//String sql="create table user(id int primary key,name varchar(200))";
		//execSQL用于执行SQL语句
		//完成数据库的创建
		//db.execSQL(sql);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		System.out.println("update to: "+newVersion);
	}
}
