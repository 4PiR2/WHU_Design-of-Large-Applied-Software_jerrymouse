package net.happygod.jerry;

import android.content.Intent;
import android.os.*;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this,WebService.class);
        //启动service服务
        startService(intent);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //Intent name = new Intent(this,WebService.class);
        //stopService(name);//name表示停止哪一个服务
    }
}
