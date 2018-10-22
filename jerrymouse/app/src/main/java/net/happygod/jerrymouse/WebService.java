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
	private static final Database db=new Database("jerrymouse");
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
			Intent intentForeService=new Intent(SharedContext.get(),MainActivity.class);
			PendingIntent pendingIntent=PendingIntent.getActivity(SharedContext.get(),0,intentForeService,0);
			Notification notification=new NotificationCompat.Builder(SharedContext.get(),"fore_service").setContentTitle("Jerrymouse Web Server is running").setContentText("Touch for more options").setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.ic_launcher_white).setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher_white)).setContentIntent(pendingIntent).build();
			startForeground(1,notification);
			running=true;
			startServers();
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
		Result result=db.query("SELECT port,path,proxy FROM general WHERE port<>1998;");
		for(Map map:result.values)
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
