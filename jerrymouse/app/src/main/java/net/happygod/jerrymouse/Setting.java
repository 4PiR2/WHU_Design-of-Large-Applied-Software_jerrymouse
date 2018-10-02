package net.happygod.jerrymouse;

import java.io.*;
import java.util.*;

class Setting implements Serializable
{
	File file;
	boolean enabled=false;
	List<Conf> confs=new LinkedList<>();
	Setting(String path)
	{
		file=new File(path);
		confs.add(new Conf(8080,"/storage/emulated/0/web"));
		confs.add(new Conf(8000,"/storage/emulated/0/AAA"));
	}
	boolean save()
	{
		try
		{
			ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(file));
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
	static Setting load(String path)
	{
		File file=new File(path);
		if(!file.exists()||true)
			return new Setting(path);
		try
		{
			ObjectInputStream ois=new ObjectInputStream(new FileInputStream(file));
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
