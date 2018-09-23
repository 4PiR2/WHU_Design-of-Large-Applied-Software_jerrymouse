package net.happygod.jerry;

import android.app.*;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.*;
import android.support.v4.app.NotificationCompat;

import net.happygod.jerry.server.*;

public class WebService extends Service
{
    private Server server1,server2;
    //Service启动时调用
    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("OnCreate 服务启动时调用");
        Config config1,config2;
        config1=new Config(8080,"/storage/emulated/0/web",getCacheDir().getPath());
        config2=new Config(8000,"/storage/emulated/0/AAA",getCacheDir().getPath());
        server1=new Server(config1);
        server2=new Server(config2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("fore_service", "foreground service", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
            Intent intentForeService = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentForeService, 0);
            Notification notification = new NotificationCompat.Builder(this, "fore_service")
                    .setContentTitle("Jerrymouse Web Server is running")
                    .setContentText("Touch for more options")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(1, notification);
        }
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId)
    {
        System.out.println("服务onStart");
        //flags=START_STICKY;
        return super.onStartCommand(intent,flags,startId);
    }
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    //服务被关闭时调用
    @Override
    public void onDestroy() {
        super.onDestroy();
        //server1.stop();
        //server2.stop();
        System.out.println("onDestroy 服务关闭时");
    }
}
