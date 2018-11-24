package net.happygod.jerrymouse;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class DashboardFragment extends Fragment
{
	private Activity activity;
	private StatusUpdater su;
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_dashboard,container,false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		activity=getActivity();
		final TextView textviewIP=activity.findViewById(R.id.textviewIP);
		final TextView textviewStatus=activity.findViewById(R.id.textviewStatus);
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
				((ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE)).setText(textviewStatus.getText());
				Const.toast("Copied",Toast.LENGTH_SHORT);
			}
		});
		su=new StatusUpdater(textviewStatus,50);
	}
	@Override
	public void onPause()
	{
		super.onPause();
		su.halt();
	}
	private static class StatusUpdater extends Thread
	{
		private TextView v;
		private int interval;
		private boolean running=true;
		StatusUpdater(TextView v,int interval)
		{
			this.v=v;
			this.interval=interval;
			start();
		}
		@Override
		public void run()
		{
			while(running)
			{
				StringBuilder sb=new StringBuilder();
				for(String status : WebService.checkServer())
				{
					if(status!=null&&!"".equals(status))
						sb.append(status).append("\n");
				}
				if(sb.length()==0)
					sb.append("OFF");
				v.setText(sb);
				try
				{
					Thread.sleep(interval);
				}
				catch(InterruptedException ie)
				{}
			}
		}
		void halt()
		{
			running=false;
			try
			{
				join();
			}
			catch(InterruptedException ie)
			{}
		}
	}
}
