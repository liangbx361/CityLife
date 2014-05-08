package com.wb.citylife;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.common.net.volley.VolleyErrorHelper;
import com.common.widget.ToastHelper;
import com.wb.citylife.bean.ScrollNews;
import com.wb.citylife.task.ScrollNewsRequest;

public class ScrollNewsActivity extends BaseActivity implements Listener<ScrollNews>, ErrorListener{
		
	private ScrollNewsRequest mScrollNewsRequest;
	private ScrollNews mScrollNews;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getIntentData();
		initView();				
	}
			
	@Override
	public void getIntentData() {
		
	}
	
	@Override
	public void initView() {
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
		
		requestScrollNews(Method.POST, NetInterface.METHOD_SCROLL_NEWS, getScrollNewsRequestParams(), this, this);
		setIndeterminateBarVisibility(true);		
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
	private Map<String, String> getScrollNewsRequestParams() {
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
	private void requestScrollNews(int method, String methodUrl, Map<String, String> params,	 
			Listener<ScrollNews> listenre, ErrorListener errorListener) {			
		if(mScrollNewsRequest != null) {
			mScrollNewsRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mScrollNewsRequest = new ScrollNewsRequest(method, url, params, listenre, errorListener);
		startRequest(mScrollNewsRequest);		
	}
	
	/**
	 * 网络请求错误处理
	 *
	 */
	@Override
	public void onErrorResponse(VolleyError error) {		
		setIndeterminateBarVisibility(false);
		ToastHelper.showToastInBottom(getApplicationContext(), VolleyErrorHelper.getErrorMessage(error));
	}
	
	/**
	 * 请求完成，处理UI更新
	 */
	@Override
	public void onResponse(ScrollNews response) {
		mScrollNews = response;
		setIndeterminateBarVisibility(false);
	}
}
