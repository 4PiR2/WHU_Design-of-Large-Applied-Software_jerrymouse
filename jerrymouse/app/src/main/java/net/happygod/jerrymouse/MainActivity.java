package net.happygod.jerrymouse;

import android.*;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.*;
import android.support.v4.content.*;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;
import java.util.*;
import net.happygod.jerrymouse.database.*;
import net.happygod.jerrymouse.server.*;

public class MainActivity extends AppCompatActivity
{
	private static final Fragment homeFragment=new HomeFragment(),
		dashboardFragment=new DashboardFragment(),
		webPageFragment=new WebPageFragment(),
		aboutFragment=new AboutFragment();
	private final FragmentManager fragmentManager=getFragmentManager();
	private Server server;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
		Const.context=getApplication();
		setTheme(Const.theme);
		setContentView(R.layout.activity_main);
		checkPermission();
		BottomNavigationView navigation=findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
		{
			@Override
			public boolean onNavigationItemSelected(MenuItem item)
			{
				FragmentTransaction transaction=fragmentManager.beginTransaction();
				switch(item.getItemId())
				{
					case R.id.navigation_home:
						transaction.replace(R.id.frame_layout,homeFragment);
						break;
					case R.id.navigation_dashboard:
						transaction.replace(R.id.frame_layout,dashboardFragment);
						break;
					case R.id.navigation_settings:
						((WebPageFragment)webPageFragment).homepage="file:///android_asset/settings/index.html";
						transaction.replace(R.id.frame_layout,webPageFragment);
						break;
					case R.id.navigation_about:
						transaction.replace(R.id.frame_layout,aboutFragment);
						break;
					default:
						return false;
				}
				transaction.commit();
				return true;
			}
		});

		FragmentTransaction transaction=fragmentManager.beginTransaction();
		transaction.replace(R.id.frame_layout,homeFragment);
		transaction.commit();

		NetworkStateReceiver networkStateReceiver=new NetworkStateReceiver();
		IntentFilter filter=new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(networkStateReceiver,filter);

		if(DBConst.SYS_DB.query("SELECT version FROM lock;").values.isEmpty())
		{
			try
			{
				Const.copyAssets(getAssets(),"settings/dynamic",getFilesDir().getPath()+"/settings");
			}
			catch(Exception e)
			{
				Const.toast("Oops! Files are damaged!\nPlease try to reinstall the APP!",Toast.LENGTH_LONG);
			}
			DBConst.SYS_DB.query("INSERT INTO lock VALUES(1);");
		}

		Result result=DBConst.SYS_DB.query("SELECT port,path,proxy FROM general WHERE port="+ServerConst.SYS_PORT+" LIMIT 1;");
		Map<String,Object> map=result.values.iterator().next();
		server=new Server((Integer)map.get("port"),(String)map.get("path"),(Integer)map.get("proxy"));
		server.start();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		//server.stop();
	}

	public void checkPermission()
	{
		//第 1 步: 检查是否有相应的权限
		String[] permissions=new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
		boolean isAllGranted=true;
		for(String permission : permissions)
		{
			if(ContextCompat.checkSelfPermission(Const.context(),permission)!=PackageManager.PERMISSION_GRANTED)
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
}
