package net.happygod.jerrymouse;

import android.app.*;
import android.content.*;
import android.os.*;
import android.app.Fragment;
import android.view.*;
import android.widget.*;
import java.util.*;

public class HomeFragment extends Fragment
{
	private Activity activity;
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
		final Switch switchEnable=activity.findViewById(R.id.switchEnable);
		final Button buttonSettings=activity.findViewById(R.id.buttonSettings);
		final TextView textviewIP=activity.findViewById(R.id.textviewIP);
		final TextView textviewStatus=activity.findViewById(R.id.textviewStatus);
		final Intent serviceIntent=new Intent(Const.context(),WebService.class);
		switchEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton compoundButton,boolean b)
			{
				if(b)
				{
					activity.startService(serviceIntent);
					Const.toast("Started",Toast.LENGTH_SHORT);
				}
				else
				{
					activity.stopService(serviceIntent);
					Const.toast("Stopped",Toast.LENGTH_SHORT);
				}
			}
		});
		final Intent webIntent=new Intent(Const.context(),WebPageActivity.class);
		buttonSettings.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				startActivity(webIntent);
			}
		});
		NetworkStateReceiver.textviewIP=textviewIP;
		NetworkStateReceiver.showIP(textviewIP);
		textviewIP.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				NetworkStateReceiver.showIP(textviewIP);
				//refresh when clicked
				((ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE)).setText(textviewIP.getText());
				Const.toast("Copied",Toast.LENGTH_SHORT);
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

		//if(isServiceRunning(Const.context(),"net.happygod.jerrymouse.WebService"))
			//switchEnable.setChecked(true);
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
