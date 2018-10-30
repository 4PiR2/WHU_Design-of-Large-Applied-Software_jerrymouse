package net.happygod.jerrymouse;

import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class AboutFragment extends Fragment
{
	private Activity activity;
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_about,container,false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		activity=getActivity();
		final Button buttonHelp=activity.findViewById(R.id.buttonHelp);
		final Intent webIntent=new Intent(Const.context(),WebPageActivity.class);
		buttonHelp.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				startActivity(webIntent);
			}
		});
	}
}
