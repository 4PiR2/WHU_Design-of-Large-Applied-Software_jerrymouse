package net.happygod.jerrymouse;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.support.v7.app.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class MainActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
		setContentView(net.happygod.jerrymouse.R.layout.activity_main);
		SharedContext.set(getApplicationContext());
		checkPermission();
		final Switch switchEnable=findViewById(R.id.switchEnable);
		final Button buttonSettings=findViewById(R.id.buttonSettings);
		final TextView textviewIP=findViewById(R.id.textviewIP);
		final TextView textviewStatus=findViewById(R.id.textviewStatus);
		final Intent serviceIntent=new Intent(SharedContext.get(),WebService.class);
		switchEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton compoundButton,boolean b)
			{
				if(b)
				{
					startService(serviceIntent);
					Toast.makeText(SharedContext.get(),"Started",Toast.LENGTH_SHORT).show();
				}
				else
				{
					stopService(serviceIntent);
					Toast.makeText(SharedContext.get(),"Stopped",Toast.LENGTH_SHORT).show();
				}
			}
		});
		final Intent webIntent=new Intent(SharedContext.get(),WebPageActivity.class);
		buttonSettings.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				startActivity(webIntent);
			}
		});
		textviewIP.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				NetworkStateReceiver.showIP(textviewIP);
				//refresh when clicked
				((ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE)).setText(textviewIP.getText());
				Toast.makeText(SharedContext.get(),"Copied",Toast.LENGTH_SHORT).show();
				for(String str:WebService.checkServer())
				{
					if(str!=null)
						System.out.println(str+"\nPlease check your phone and try again!");
				}
			}
		});
		textviewStatus.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				StringBuilder sb=new StringBuilder();
				for(String status:WebService.checkServer())
				{
					if(status!=null)
						sb.append(status+"\n");
				}
				if(sb.length()>0)
					sb.append("Please check your phone and try again!");
				else
					sb.append("Status: OK");
				textviewStatus.setText(sb);
				//TODO auto-update
			}
		});
		NetworkStateReceiver networkStateReceiver=new NetworkStateReceiver(textviewIP);
		IntentFilter filter=new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(networkStateReceiver,filter);
		if(isServiceRunning(SharedContext.get(),"net.happygod.jerrymouse.WebService"))
			switchEnable.setChecked(true);
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	private void reset()
	{

	}

	public void checkPermission()
	{
		//第 1 步: 检查是否有相应的权限
		String[] permissions=new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
		boolean isAllGranted=true;
		for(String permission : permissions)
		{
			if(ContextCompat.checkSelfPermission(SharedContext.get(),permission)!=PackageManager.PERMISSION_GRANTED)
			{
				// 只要有一个权限没有被授予, 则直接返回 false
				isAllGranted=false;
				break;
			}
		}
		// 如果这3个权限全都拥有, 则直接执行读取短信代码
		if(isAllGranted)
		{
			return;
		}
		//第 2 步: 请求权限
		// 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
		ActivityCompat.requestPermissions(this,permissions,10000);
	}
	//第 3 步: 申请权限结果返回处理
	@Override
	public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode,permissions,grantResults);
		if(requestCode==10000)
		{

		}
	}

	/*
	 * 判断服务是否启动,context上下文对象 ，className服务的name
	 */
	public static boolean isServiceRunning(Context mContext,String className)
	{
		boolean isRunning=false;
		ActivityManager activityManager=(ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList=activityManager.getRunningServices(30);
		if(!(serviceList.size()>0))
		{
			return false;
		}
		for(int i=0;i<serviceList.size();i++)
		{
			System.out.println(serviceList.get(i).service.getClassName());
			if(className.equals(serviceList.get(i).service.getClassName())==true)
			{
				isRunning=true;
				break;
			}
		}
		return isRunning;
	}
}
