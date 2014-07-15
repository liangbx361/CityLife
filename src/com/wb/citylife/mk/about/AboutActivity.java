package com.wb.citylife.mk.about;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

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
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.task.AboutRequest;

public class AboutActivity extends BaseActivity implements Listener<About>, ErrorListener,
	ReloadListener{
		
	private WebView contentWv;
	
	private AboutRequest mAboutRequest;
	private About mAbout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		getIntentData();
		initView();				
	}
			
	@Override
	public void getIntentData() {
		
	}
	
	@Override
	public void initView() {
		contentWv = (WebView) findViewById(R.id.content);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
		
		requestAbout(Method.GET, NetInterface.METHOD_ABOUT, getAboutRequestParams(), this, this);	
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
		
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getAboutRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
				
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
		requestAbout(Method.GET, NetInterface.METHOD_ABOUT, getAboutRequestParams(), this, this);	
		showLoading();
	}
	
	/**
	 * 请求完成，处理UI更新
	 */
	@Override
	public void onResponse(About response) {
		mAbout = response;		
		if(mAbout.respCode == RespCode.SUCCESS) {
			contentWv.loadDataWithBaseURL(NetConfig.getServerBaseUrl(), response.content, null, "utf-8", null);
			showContent();
		} else {
			ToastHelper.showToastInBottom(this, response.respMsg);
		}
			
	}
}
