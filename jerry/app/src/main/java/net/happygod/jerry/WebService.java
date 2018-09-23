package net.happygod.jerry;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
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
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
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
        server1.stop();
        server2.stop();
        System.out.println("onDestroy 服务关闭时");
    }
}
