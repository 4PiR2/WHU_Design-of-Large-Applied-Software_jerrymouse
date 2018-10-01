package net.happygod.jerrymouse;

import android.content.Intent;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.widget.*;
import net.happygod.jerrymouse.server.*;

public class MainActivity extends AppCompatActivity
{
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(net.happygod.jerrymouse.R.layout.activity_main);
        intent = new Intent(this,WebService.class);
        Switch st=(Switch)findViewById(R.id.switchStartServer);
	    final Config config1,config2;
	    config1=new Config(8080,"/storage/emulated/0/web",getCacheDir().getPath());
	    config2=new Config(8000,"/storage/emulated/0/AAA",getCacheDir().getPath());
       //st.setOnClickListener(new View.OnClickListener(){public void onClick(View v){}});
        st.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b)
            {
                    if(b)
                    {
                        startService(intent);
                        WebService.addServer(config1);
                        WebService.addServer(config2);
                        Toast.makeText(getApplicationContext(), "Started", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        stopService(intent);
                        Toast.makeText(getApplicationContext(), "Stopped", Toast.LENGTH_SHORT).show();
                    }
            }});
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}
