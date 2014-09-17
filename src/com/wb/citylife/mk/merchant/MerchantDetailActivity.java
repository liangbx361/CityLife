package com.wb.citylife.mk.merchant;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.common.net.volley.VolleyErrorHelper;
import com.common.widget.ToastHelper;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.activity.base.ReloadListener;
import com.wb.citylife.bean.About;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.task.AboutRequest;

public class MerchantDetailActivity extends BaseActivity implements Listener<About>, ErrorListener,
	ReloadListener{
		
	private WebView contentWv;
	
	private String detailId;
	private String name;
	
	private AboutRequest mAboutRequest;
	private About mAbout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		getIntentData();
		initView();		
		initWebView();
	}
			
	@Override
	public void getIntentData() {
		detailId = getIntent().getStringExtra(IntentExtraConfig.DETAIL_ID);
		name = getIntent().getStringExtra(IntentExtraConfig.DETAIL_NAME);
	}
	
	@Override
	public void initView() {
		contentWv = (WebView) findViewById(R.id.content);
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		WebSettings webSettings = contentWv.getSettings(); 
		//设置WebView属性，能够执行JavaScript脚本
        webSettings.setJavaScriptEnabled(true); 
        //如果要播放Flash，需要加上这一句  
        webSettings.setPluginState(PluginState.ON);         
        
        String databasePath = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setDatabaseEnabled(true);
        webSettings.setDatabasePath(databasePath);
        
        webSettings.setDomStorageEnabled(true);
        
        //在本页面中响应链接
        contentWv.setWebViewClient(new WebViewClient(){       
            public boolean shouldOverrideUrlLoading(WebView view, String url) {       
                view.loadUrl(url);       
                return true;       
            }

			@Override
			public void onPageFinished(WebView view, String url) {
			}  
												           
        });
        
        contentWv.setWebChromeClient(new WebChromeClient());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
		
		if(!TextUtils.isEmpty(name)) {
			setTitle(name);
		}
		
		requestAbout(Method.POST, NetInterface.METHOD_MERCHANT_DETAIL, getAboutRequestParams(), this, this);	
		showLoading();
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * 菜单点击处理
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {			
		return super.onOptionsItemSelected(item);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		switch(event.getKeyCode()) {
		case KeyEvent.KEYCODE_BACK:
			if(contentWv.canGoBack()) {
				contentWv.goBack();
			} else {
				finish();
			}
			return true;
		}
		
		return super.onKeyDown(keyCode, event); 
	}
	
	public class WebChromeClient extends android.webkit.WebChromeClient {

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			
			if(newProgress == 100) {
				setIndeterminateBarVisibility(false);
			} else {
				setIndeterminateBarVisibility(true);
			}
			
			super.onProgressChanged(view, newProgress);
		}
		
	}
	
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getAboutRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", detailId);		
		return params;
	}
	
	/**
	 * 执行任务请求
	 * @param method
	 * @param url
	 * @param params
	 * @param listenre
	 * @param errorListener
	 */	
	private void requestAbout(int method, String methodUrl, Map<String, String> params,	 
			Listener<About> listenre, ErrorListener errorListener) {			
		if(mAboutRequest != null) {
			mAboutRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mAboutRequest = new AboutRequest(method, url, params, listenre, errorListener);
		startRequest(mAboutRequest);		
	}
	
	/**
	 * 网络请求错误处理
	 *
	 */
	@Override
	public void onErrorResponse(VolleyError error) {	
		showLoadError(this);
		ToastHelper.showToastInBottom(getApplicationContext(), VolleyErrorHelper.getErrorMessage(error));
	}
	
	@Override
	public void onReload() {
		requestAbout(Method.POST, NetInterface.METHOD_MERCHANT_DETAIL, getAboutRequestParams(), this, this);	
		showLoading();
	}
	
	/**
	 * 请求完成，处理UI更新
	 */
	@Override
	public void onResponse(About response) {
		mAbout = response;		
		if(mAbout.respCode == RespCode.SUCCESS) {
			if(!response.content.contains("merchant_url")) {
				contentWv.loadDataWithBaseURL(NetConfig.getServerBaseUrl(), response.content, null, "utf-8", null);
			} else {
				String merchantUrl = response.content.substring(14, response.content.length()-15);
				contentWv.loadUrl(merchantUrl);
			}
			showContent();
		} else {
			ToastHelper.showToastInBottom(this, response.respMsg);
		}
			
	}
}
