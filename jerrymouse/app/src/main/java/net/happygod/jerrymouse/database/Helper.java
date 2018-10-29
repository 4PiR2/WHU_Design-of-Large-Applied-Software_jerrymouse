package net.happygod.jerrymouse.database;

import android.database.sqlite.*;
import net.happygod.jerrymouse.*;

class Helper extends SQLiteOpenHelper
{
	Helper(String name)
	{
		super(Const.context(),name,null,DBConst.SYS_DBVERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		if(DBConst.SYS_DBNAME.equals(getDatabaseName()))
			for(String s:DBConst.SYS_DBINIT)
				if(s!=null)
					db.execSQL(s);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		if(DBConst.SYS_DBNAME.equals(getDatabaseName()))
		{
			//TODO may cause DBConst_DB unavailable
			Const.context().deleteDatabase(DBConst.SYS_DBNAME);
			new Database(DBConst.SYS_DBNAME);
		}
	}
}
