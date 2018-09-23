package net.happygod.jerry;

import android.os.*;
import android.support.v7.app.AppCompatActivity;
import net.happygod.jerry.server.*;

public class MainActivity extends AppCompatActivity
{
    private Server server1,server2;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Config config1,config2;
        config1=new Config(8080,"/storage/emulated/0/web",getCacheDir().getPath());
        config2=new Config(8000,"/storage/emulated/0/AAA",getCacheDir().getPath());
        server1=new Server(config1);
        server2=new Server(config2);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        server1.stop();
        server2.stop();
    }
}
