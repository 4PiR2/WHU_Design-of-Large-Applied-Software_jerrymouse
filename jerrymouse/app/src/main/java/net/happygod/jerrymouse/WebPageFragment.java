package net.happygod.jerrymouse;

import android.app.*;
import android.graphics.*;
import android.os.*;
import android.support.v7.app.AlertDialog;
import android.util.*;
import android.view.*;
import android.webkit.*;
import android.widget.*;

public class WebPageFragment extends Fragment
{
	private Activity activity;
	private WebView webView;
	private ProgressBar progressBar;
	String homepage;

	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_webpage,container,false);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		activity=getActivity();

		progressBar=activity.findViewById(R.id.progressbar);//进度条

		webView=activity.findViewById(R.id.webview);
		webView.loadUrl(homepage);

		//使用webview显示html代码
		//        webView.loadDataWithBaseURL(null,"<html><head><title> 欢迎您 </title></head>" +
		//               "<body><h2>使用webview显示 html代码</h2></body></html>", "text/html" , "utf-8", null);

		webView.addJavascriptInterface(this,"android");//添加js监听 这样html就能调用客户端
		webView.setWebChromeClient(webChromeClient);
		webView.setWebViewClient(webViewClient);

		WebSettings webSettings=webView.getSettings();
		webSettings.setJavaScriptEnabled(true);//允许使用js

		/**
		 * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
		 * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
		 * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
		 * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
		 */
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存，只从网络获取数据.

		//支持屏幕缩放
		webSettings.setSupportZoom(false);
		webSettings.setBuiltInZoomControls(true);

		//显示webview缩放按钮
		webSettings.setDisplayZoomControls(false);
	}

	//WebViewClient主要帮助WebView处理各种通知、请求事件
	private WebViewClient webViewClient=new WebViewClient()
	{
		@Override
		public void onPageFinished(WebView view,String url)
		{//页面加载完成
			progressBar.setVisibility(View.GONE);
		}

		@Override
		public void onPageStarted(WebView view,String url,Bitmap favicon)
		{//页面开始加载
			progressBar.setVisibility(View.VISIBLE);
		}
	};
	//WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
	private WebChromeClient webChromeClient=new WebChromeClient()
	{
		//不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
		@Override
		public boolean onJsAlert(WebView webView,String url,String message,JsResult result)
		{
			android.support.v7.app.AlertDialog.Builder localBuilder=new AlertDialog.Builder(webView.getContext());
			localBuilder.setMessage(message).setPositiveButton("确定",null);
			localBuilder.setCancelable(false);
			localBuilder.create().show();

			//注意:
			//必须要这一句代码:result.confirm()表示:
			//处理结果为确定状态同时唤醒WebCore线程
			//否则不能继续点击按钮
			result.confirm();
			return true;
		}

		@Override
		public void onProgressChanged(WebView view,int newProgress)
		{
			progressBar.setProgress(newProgress);
		}
	};

	//@Override
	public boolean onKeyDown(int keyCode,KeyEvent event)
	{
		return true;
		/*
		if(webView.canGoBack()&&keyCode==KeyEvent.KEYCODE_BACK)
		{//点击返回按钮的时候判断有没有上一页
			webView.goBack(); // goBack()表示返回webView的上一页面
			return true;
		}
		return super.onKeyDown(keyCode,event);
		*/
	}

	//JS调用android的方法
	@JavascriptInterface //仍然必不可少
	public void getClient(String str)
	{
		Log.i("ansen","html调用客户端:"+str);
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		webView.destroy();
		webView=null;
	}
}
