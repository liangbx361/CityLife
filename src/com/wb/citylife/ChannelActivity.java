package com.wb.citylife;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.common.net.volley.VolleyErrorHelper;
import com.common.widget.ToastHelper;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.bean.Channel;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.task.ChannelRequest;

public class ChannelActivity extends BaseActivity implements Listener<Channel>, ErrorListener{
		
	private ChannelRequest mChannelRequest;
	private Channel mChannel;
	
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
		
		//requestChannel(Method.POST, "请求方法", getChannelRequestParams(), this, this);
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
	private Map<String, String> getChannelRequestParams() {
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
	private void requestChannel(int method, String methodUrl, Map<String, String> params,	 
			Listener<Channel> listenre, ErrorListener errorListener) {			
		if(mChannelRequest != null) {
			mChannelRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mChannelRequest = new ChannelRequest(method, url, params, listenre, errorListener);
		startRequest(mChannelRequest);		
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
	public void onResponse(Channel response) {
		mChannel = response;
		setIndeterminateBarVisibility(false);
	}
}
