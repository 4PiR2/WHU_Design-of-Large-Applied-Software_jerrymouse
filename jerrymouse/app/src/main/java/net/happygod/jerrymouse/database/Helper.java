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
		if("jerrymouse".equals(getDatabaseName()))
		{
			String[] sql=new String[6];
			sql[0]="CREATE TABLE general("
			       //+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
			       //+ "enabled BOOLEAN NOT NULL DEFAULT FALSE,"
			       +"port INTEGER PRIMARY KEY,"+"proxy INTEGER NOT NULL DEFAULT 0,"+"path VARCHAR(1024) NOT NULL DEFAULT ''"+");";
			sql[1]="CREATE TABLE link("+"port INTEGER REFERENCES general(port) ON DELETE CASCADE ON UPDATE CASCADE,"+"uri VARCHAR(1024),"+"extensive BOOLEAN NOT NULL DEFAULT TRUE,"+"permission INTEGER DEFAULT NULL,"+"authentication VARCHAR(1024) DEFAULT NULL,"+"directory BOOLEAN DEFAULT NULL,"+"visible BOOLEAN DEFAULT NULL,"+"type INTEGER DEFAULT NULL,"+"path VARCHAR(1024) DEFAULT NULL,"+"pathextensive BOOLEAN DEFAULT TRUE,"+"PRIMARY KEY(port,uri)"+");";
			sql[2]="CREATE TABLE error("+"code INTEGER PRIMARY KEY,"+"path VARCHAR(1024) NOT NULL DEFAULT ''"+");";
			//TODO different error page for different port
			sql[3]="INSERT INTO general VALUES"+"(1998,0,'/storage/emulated/0'),"+"(8080,0,'/storage/emulated/0/web'),"+"(8000,2,''),"+"(8010,1,'localhost:8000'),"+"(8020,1,'localhost:8080'),"+"(8030,1,'legend-k.com')"+";";
			sql[4]="INSERT INTO link VALUES"+"(1998,'',1,1,'dXNlcm5hbWU6cGFzc3dvcmQ=',1,1,NULL,NULL,NULL),"+"(1998,'/files',1,NULL,NULL,NULL,NULL,0,'/data/user/0/net.happygod.jerrymouse',NULL),"+"(1998,'/DBManager',0,NULL,'',NULL,NULL,3,'/data/user/0/net.happygod.jerrymouse/files/settings/DBManager.dex',NULL),"+"(8080,'',1,NULL,NULL,1,1,NULL,NULL,NULL),"+"(8080,'/redirectp',1,NULL,NULL,NULL,NULL,1,'https://www.happygod.net',NULL),"+"(8080,'/redirect',1,NULL,NULL,NULL,NULL,2,'https://www.happygod.net',NULL),"+"(8080,'/test.dex',1,NULL,NULL,NULL,NULL,3,NULL,NULL),"+"(8000,'',0,NULL,'dXNlcm5hbWU6cGFzc3dvcmQ=',NULL,NULL,NULL,NULL,NULL)"+";";
			sql[5]="INSERT INTO error VALUES"+"(404,'/storage/emulated/0/web/error/404.html')"+";";
			for(String s : sql)
			{
				if(s!=null)
				{
					db.execSQL(s);
				}
			}
		}
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		//System.err.println("update to: "+newVersion);
	}
}
