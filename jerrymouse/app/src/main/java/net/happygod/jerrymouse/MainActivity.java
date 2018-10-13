package net.happygod.jerrymouse;

import android.content.*;
import android.content.res.*;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.*;

import java.io.*;

public class MainActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
		setContentView(net.happygod.jerrymouse.R.layout.activity_main);
		am=getAssets();
		try
		{
			copyAssets("",getFilesDir().getPath());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		Switch switchEnable=findViewById(R.id.switchEnable);
		Button buttonSettings=findViewById(R.id.buttonSettings);
		//TODO restore status
		final Intent serviceIntent=new Intent(this,WebService.class);
		switchEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton compoundButton,boolean b)
			{
				if(b)
				{
					startService(serviceIntent);
					Toast.makeText(getApplicationContext(),"Started",Toast.LENGTH_SHORT).show();
				}
				else
				{
					stopService(serviceIntent);
					Toast.makeText(getApplicationContext(),"Stopped",Toast.LENGTH_SHORT).show();
				}
			}
		});
		final Intent webIntent=new Intent(this,WebpageActivity.class);
		buttonSettings.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				startActivity(webIntent);
			}
		});
		//try{IP ip=new IP();}catch(Exception e){}
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	AssetManager am;
	boolean copyAssets(String dir,String path) throws IOException
	{
		String[] list;
		try
		{
			list=am.list(dir);
		}
		catch(Exception e)
		{
			return false;
		}
		new File(path).mkdirs();
		for(String filePath:list)
		{
			if(!copyAssets(dir.equals("")?filePath:(dir+"/"+filePath),path+"/"+filePath))
			{
				BufferedInputStream bis=new BufferedInputStream(am.open(filePath));
				BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(path+filePath));
				byte[] buffer=new byte[2048];
				int size;
				while((size=bis.read(buffer))>0)
				{
					bos.write(buffer,0,size);
				}
				bos.flush();
				bis.close();
				bos.close();
			}
			//System.out.println(filePath);
		}
		return true;
	}
}
