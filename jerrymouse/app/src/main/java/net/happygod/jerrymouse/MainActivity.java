package net.happygod.jerrymouse;

import android.content.Intent;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.widget.*;
import java.util.*;
import net.happygod.jerrymouse.server.Config;

public class MainActivity extends AppCompatActivity
{
	Map<Conf,Config> configs=new HashMap<>();
    Setting setting;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(net.happygod.jerrymouse.R.layout.activity_main);
        intent = new Intent(this,WebService.class);
        setting=Setting.load(getFilesDir().getPath()+"/setting");
        Switch st=findViewById(R.id.switchStartServer);
       //st.setOnClickListener(new View.OnClickListener(){public void onClick(View v){}});
        st.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                    if(b)
                    {
                        startService(intent);
                        for(Conf conf:setting.confs)
                        {
	                        configs.put(conf,new Config(conf.port,conf.webroot,getCacheDir().getPath()));
                            WebService.addServer(configs.get(conf));
                        }
                        setting.save();
                        Toast.makeText(getApplicationContext(), "Started", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        stopService(intent);
                        setting.save();
                        Toast.makeText(getApplicationContext(), "Stopped", Toast.LENGTH_SHORT).show();
                    }
            }});
    }

    @Override
    public void onDestroy()
    {
        setting.save();
        super.onDestroy();
    }
}
