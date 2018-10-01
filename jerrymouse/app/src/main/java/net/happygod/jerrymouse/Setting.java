package net.happygod.jerrymouse;

import java.io.*;
import java.util.*;

class Setting implements Serializable
{
	List configs=new LinkedList();
	private static final String fileName="";
	boolean save()
	{
		try
		{
			ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(fileName));
			oos.writeObject(this);
			oos.close();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	static Setting load()
	{
		try
		{
			ObjectInputStream ois=new ObjectInputStream(new FileInputStream(fileName));
			return (Setting)ois.readObject();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	boolean saveToFile()
	{return true;}
	static Setting loadFromFile()
	{return null;}
}
