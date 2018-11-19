package net.happygod.jerrymouse;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v7.app.*;
import android.view.*;

public class WebPageActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setTheme(Const.theme);
		setContentView(R.layout.activity_frame);
		WebPageFragment webPageFragment=new WebPageFragment();
		webPageFragment.homepage="file:///android_asset/help/index.html";
		FragmentManager fragmentManager=getFragmentManager();
		FragmentTransaction transaction=fragmentManager.beginTransaction();
		transaction.replace(R.id.frame_layout,webPageFragment);
		transaction.commit();
	}

	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event)
	{
		final Intent mainIntent=new Intent(Const.context(),MainActivity.class);
		startActivity(mainIntent);
		return true;
		/*
		Log.i("ansen","是否有上一个页面:"+webView.canGoBack());
		if(webView.canGoBack()&&keyCode==KeyEvent.KEYCODE_BACK)
		{//点击返回按钮的时候判断有没有上一页
			webView.goBack(); // goBack()表示返回webView的上一页面
			return true;
		}
		return super.onKeyDown(keyCode,event);
		*/
	}
}
