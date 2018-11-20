package net.happygod.jerrymouse;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.os.*;
import android.app.Fragment;
import android.support.design.widget.*;
import android.view.*;
import android.widget.*;
import java.util.*;

public class HomeFragment extends Fragment
{
	private Activity activity;
	private boolean isServiceOn;
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_home,container,false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		activity=getActivity();
		final Intent serviceIntent=new Intent(Const.context(),WebService.class);
		/*final Switch switchEnable=activity.findViewById(R.id.switchEnable);
		switchEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton compoundButton,boolean b)
			{
				if(b)
				{
					activity.startService(serviceIntent);
					if(!isServiceOn)
						Const.toast("Started",Toast.LENGTH_SHORT);
					isServiceOn=true;
				}
				else
				{
					activity.stopService(serviceIntent);
					if(isServiceOn)
						Const.toast("Stopped",Toast.LENGTH_SHORT);
					isServiceOn=false;
				}
			}
		});*/
		final FloatingActionButton fabEnable=activity.findViewById(R.id.fabEnable);
		fabEnable.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorDisabled)));
		fabEnable.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				if(!isServiceOn)
				{
					activity.startService(serviceIntent);
					fabEnable.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEnabled)));
					Const.toast("Started",Toast.LENGTH_SHORT);
				}
				else
				{
					activity.stopService(serviceIntent);
					fabEnable.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorDisabled)));
					Const.toast("Stopped",Toast.LENGTH_SHORT);
				}
				isServiceOn=!isServiceOn;
			}
		});
		if(isServiceRunning(Const.context(),"net.happygod.jerrymouse.WebService"))
		{
			//switchEnable.setChecked(true);
			isServiceOn=true;
			fabEnable.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorEnabled)));
		}
		final SeekBar seekbarTheme=activity.findViewById(R.id.seekbarTheme);
		seekbarTheme.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
			@Override
			public void onProgressChanged(SeekBar seekBar,int i,boolean b)
			{
				if(i<50)
				{
					Const.theme=R.style.AppTheme;
				}
				else
				{
					Const.theme=R.style.AppTheme2;
				}
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{

			}
			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				activity.getFragmentManager().beginTransaction().detach(HomeFragment.this).commit();
				activity.recreate();
			}
		});
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
			if(className.equals(serviceList.get(i).service.getClassName()))
			{
				isRunning=true;
				break;
			}
		}
		return isRunning;
	}
}
