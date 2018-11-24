package net.happygod.jerrymouse;

import android.app.*;
import android.content.*;
import android.net.Uri;
import android.os.*;
import android.view.*;
import android.widget.*;
import net.happygod.jerrymouse.database.*;

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
		final Button buttonContact=activity.findViewById(R.id.buttonContact);
		final Button buttonUpdate=activity.findViewById(R.id.buttonUpdate);
		final Button buttonRestore=activity.findViewById(R.id.buttonRestore);
		buttonHelp.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				startActivity(webIntent);
			}
		});
		buttonContact.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				joinQQGroup("7GiBHIJ9P3KggadECzB7xBkwFeh9wKom");
			}
		});
		buttonUpdate.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				openApplicationMarket(Const.context().getPackageName());
			}
		});
		buttonRestore.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v){
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("Warning!");
				builder.setMessage("This operation will restore all your settings.\nAfter pressing OK please restart this APP to finish!");
				builder.setIcon(R.drawable.ic_launcher_colored);
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int i) {
						Const.context().deleteDatabase(DBConst.SYS_DBNAME);
						ActivityManager am = (ActivityManager) Const.context().getSystemService(Context.ACTIVITY_SERVICE);
						am.restartPackage(Const.context().getPackageName());
					}
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int i) {
						dialog.dismiss();
					}
				});
				builder.setCancelable(false).create();
				builder.show();
			}
		});
	}
	/****************
	 *
	 * 发起添加群流程。群号：大软密潜小分队(524873785) 的 key 为： 7GiBHIJ9P3KggadECzB7xBkwFeh9wKom
	 * 调用 joinQQGroup(7GiBHIJ9P3KggadECzB7xBkwFeh9wKom) 即可发起手Q客户端申请加群 大软密潜小分队(524873785)
	 *
	 * @param key 由官网生成的key
	 * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
	 ******************/
	public boolean joinQQGroup(String key) {
		Intent intent = new Intent();
		intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
		// 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		try {
			startActivity(intent);
			return true;
		} catch (Exception e) {
			// 未安装手Q或安装的版本不支持
			Const.toast("未安装手Q或安装的版本不支持", Toast.LENGTH_SHORT);
			return false;
		}
	}


	private void openApplicationMarket(String packageName) {
		try {
			String str = "market://details?id=" + packageName;
			Intent localIntent = new Intent(Intent.ACTION_VIEW);
			localIntent.setData(Uri.parse(str));
			startActivity(localIntent);
		} catch (Exception e) {
			// 打开应用商店失败 可能是没有手机没有安装应用市场
			e.printStackTrace();
			// 调用系统浏览器进入商城
			String url = "https://www.coolapk.com/";
			openLinkBySystem(url);
		}
	}

	/**
	 * 调用系统浏览器打开网页
	 *
	 * @param url 地址
	 */
	private void openLinkBySystem(String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		startActivity(intent);
	}

}
