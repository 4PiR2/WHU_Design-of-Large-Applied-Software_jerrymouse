package net.happygod.jerrymouse;

import android.app.*;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.*;
import android.support.v4.app.NotificationCompat;
import java.util.*;
import net.happygod.jerrymouse.server.Config;

public class WebService extends Service
{
	private static Set<Config> configs=new HashSet<>();
	private static boolean running=false;
	@Override
	public void onCreate()
	{
		super.onCreate();
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		{
			NotificationChannel channel=new NotificationChannel("fore_service","foreground service",NotificationManager.IMPORTANCE_HIGH);
			NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
			notificationManager.createNotificationChannel(channel);
			Intent intentForeService=new Intent(this,MainActivity.class);
			PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intentForeService,0);
			Notification notification=new NotificationCompat.Builder(this,"fore_service").setContentTitle("Jerrymouse Web Server is running").setContentText("Touch for more options").setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.ic_launcher_white).setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher_white)).setContentIntent(pendingIntent).build();
			startForeground(1,notification);
			running=true;
		}
	}
	@Override
	public int onStartCommand(Intent intent,int flags,int startId)
	{
		//flags=START_STICKY;
		return super.onStartCommand(intent,flags,startId);
	}
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	@Override
	public void onDestroy()
	{
		for(Config config : configs)
		{
			config.stop();
			//removeServer(config);
		}
		running=false;
		configs.clear();
		super.onDestroy();
	}
	static boolean addServer(Config config)
	{
		if(running||config.isRunning())
			return false;
		config.start();
		return configs.add(config);
	}
	static boolean removeServer(Config config)
	{
		if(!running)
			return false;
		config.stop();
		return configs.remove(config);
	}
}
