package net.happygod.jerrymouse;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.design.widget.FloatingActionButton;
import android.view.*;
import android.widget.*;

public class DashboardFragment extends Fragment
{
	private Activity activity;
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
		final FloatingActionButton fabRefresh=activity.findViewById(R.id.fabRefresh);
		NetworkStateReceiver.textviewIP=textviewIP;
		NetworkStateReceiver.showIP(textviewIP);
		fabRefresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				NetworkStateReceiver.showIP(textviewIP);
			}
		});
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
				for(String status : WebService.checkServer())
				{
					if(status!=null)
						sb.append(status+"\n");
				}
				if(sb.length()>0)
					sb.append("Please check your phone and try again!");
				else
					sb.append("OK");
				textviewStatus.setText(sb);
				//TODO auto-update
			}
		});
		textviewStatus.callOnClick();
	}
}
