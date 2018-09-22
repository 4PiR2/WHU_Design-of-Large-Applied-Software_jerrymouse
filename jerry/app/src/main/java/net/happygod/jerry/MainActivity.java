package net.happygod.jerry;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import net.happygod.jerry.server.*;

public class MainActivity extends AppCompatActivity
{
    private Server server;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        server=new Server(new Config());
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        server.stop();
    }
}
