package net.happygod.jerrymouse;

import android.content.*;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.widget.*;

public class MainActivity extends AppCompatActivity
{
	private Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(net.happygod.jerrymouse.R.layout.activity_main);
		intent=new Intent(this,WebService.class);
		Switch st=findViewById(R.id.switchEnable);
		//st.setOnClickListener(new View.OnClickListener(){public void onClick(View v){}});
		//TODO restore status
		st.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton compoundButton,boolean b)
			{
				if(b)
				{
					startService(intent);
					Toast.makeText(getApplicationContext(),"Started",Toast.LENGTH_SHORT).show();
				}
				else
				{
					stopService(intent);
					Toast.makeText(getApplicationContext(),"Stopped",Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}
}
