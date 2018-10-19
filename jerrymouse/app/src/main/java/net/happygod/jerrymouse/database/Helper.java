package net.happygod.jerrymouse.database;

import android.database.sqlite.*;
import net.happygod.jerrymouse.*;

class Helper extends SQLiteOpenHelper
{
	private final static Integer version = 1;
	Helper(String name,SQLiteDatabase.CursorFactory factory,int version)
	{
		super(SharedContext.get(),name,factory,version);
	}
	Helper(String name,int version)
	{
		this(name,null,version);
	}
	Helper(String name)
	{
		this(name,version);
	}
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		System.out.println("create database");
		String sql="CREATE TABLE general(id INTEGER PRIMARY KEY AUTOINCREMENT,enabled BOOLEAN NOT NULL DEFAULT FALSE,port INTEGER DEFAULT NULL,webroot VARCHAR(256) DEFAULT NULL,proxymode BOOLEAN NOT NULL DEFAULT FALSE,allowindex BOOLEAN NOT NULL DEFAULT FALSE,servletvisible BOOLEAN NOT NULL DEFAULT FALSE);";
		//ONLY database jerrymouse shall do it
		db.execSQL(sql);
		sql="INSERT INTO general VALUES(0,1,8080,'/storage/emulated/0/web',0,1,1),(1,1,8000,null,1,0,0),(2,1,8010,'localhost:8000',1,0,0),(3,1,8020,'localhost:8080',1,0,0);";
		db.execSQL(sql);
		sql="CREATE TABLE error(code INTEGER PRIMARY KEY,enabled BOOLEAN NOT NULL DEFAULT FALSE,page VARCHAR(256) DEFAULT NULL);";
		db.execSQL(sql);
		sql="INSERT INTO error VALUES(404,1,'/storage/emulated/0/web/404.html');";
		db.execSQL(sql);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		System.out.println("update to: "+newVersion);
	}
}
