package net.happygod.jerrymouse.database;

import java.util.*;

public class Result
{
	private Collection<String> columns=new ArrayList<>();//HashSet<>();
	private Collection<Map<String,String>> values=new ArrayList<>();
	public Collection<String> columns()
	{
		return columns;
	}
	public Collection<Map<String,String>> values()
	{
		return values;
	}
}
