package net.happygod.jerrymouse;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.os.*;
import android.support.v4.app.*;
import java.util.*;
import net.happygod.jerrymouse.database.*;
import net.happygod.jerrymouse.server.*;

public class WebService extends Service
{
	private static Set<Server> servers=new HashSet<>();
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
			Intent intentForeService=new Intent(Const.context(),MainActivity.class);
			PendingIntent pendingIntent=PendingIntent.getActivity(Const.context(),0,intentForeService,0);
			Notification notification=new NotificationCompat.Builder(Const.context(),"fore_service").setContentTitle("Jerrymouse Web Server is running").setContentText("Touch for more options").setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.ic_launcher_white).setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher_white)).setContentIntent(pendingIntent).build();
			startForeground(1,notification);
		}
		running=true;
		startServers();
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
		for(Server server : servers)
		{
			server.stop();
			//removeServer(server);
		}
		running=false;
		servers.clear();
		super.onDestroy();
	}
	void startServers()
	{
		Result result=DBConst.SYS_DB.query("SELECT port,path,proxy FROM general WHERE port<>"+ServerConst.SYS_PORT+";");
		for(Map<String,Object> map:result.values)
		{
			Integer port=(Integer)map.get("port");
			String webroot=(String)map.get("path");
			Integer proxy=(Integer)map.get("proxy");
			addServer(new Server(port,webroot,proxy));
		}
	}
	static List<String> checkServer()
	{
		List<String> messages=new ArrayList<>();
		for(Server server:servers)
		{
			messages.add(server.getMessage());
		}
		return messages;
	}
	boolean addServer(Server server)
	{
		if(!running||server.isRunning())
			return false;
		server.start();
		return servers.add(server);
	}
	boolean removeServer(Server server)
	{
		if(!running||!server.isRunning())
			return false;
		server.stop();
		return servers.remove(server);
	}
}
