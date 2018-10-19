package net.happygod.jerrymouse.database;

import android.database.*;
import java.util.*;

public class Result
{
	public final Collection<String> columns=new ArrayList<>();
	public final Collection<Map<String,Object>> values=new ArrayList<>();
	Result(Cursor cursor)
	{
		columns.addAll(Arrays.asList(cursor.getColumnNames()));
		while(cursor.moveToNext())
		{
			Map<String,Object> map=new HashMap<>();
			for(String column:columns)
			{
				Object value=null;
				int index=cursor.getColumnIndex(column);
				switch(cursor.getType(index))
				{
					case Cursor.FIELD_TYPE_NULL:
						value=null;
						break;
					case Cursor.FIELD_TYPE_INTEGER:
						value=cursor.getInt(index);
						//TODO Long
						break;
					case Cursor.FIELD_TYPE_FLOAT:
						value=cursor.getFloat(index);
						break;
					case Cursor.FIELD_TYPE_STRING:
						value=cursor.getString(index);
						break;
					case Cursor.FIELD_TYPE_BLOB:
						value=cursor.getBlob(index);
						break;
				}
				map.put(column,value);
			}
			values.add(map);
		}
		cursor.close();
	}
}
