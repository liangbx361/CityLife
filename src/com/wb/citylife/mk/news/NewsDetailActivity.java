package com.wb.citylife.mk.news;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.common.net.volley.VolleyErrorHelper;
import com.common.widget.ToastHelper;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.NewsDetail;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.task.NewsDetailRequest;

public class NewsDetailActivity extends BaseActivity implements Listener<NewsDetail>, ErrorListener{
		
	private NewsDetailRequest mNewsDetailRequest;
	private NewsDetail mNewsDetail;
	
	private String id;
	
	private TextView titleTv;
	private TextView timeTv;
	private NetworkImageView imgIv;
	private TextView contentTv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newsdetail);
		
		getIntentData();
		initView();				
	}
			
	@Override
	public void getIntentData() {
		id = getIntent().getStringExtra("id");
	}
	
	@Override
	public void initView() {
		titleTv = (TextView) findViewById(R.id.title);
		timeTv = (TextView) findViewById(R.id.time);
		imgIv = (NetworkImageView) findViewById(R.id.img);
		contentTv = (TextView) findViewById(R.id.content);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
		
		requestNewsDetail(Method.GET, NetInterface.METHOD_NEWS_DETAIL, getNewsDetailRequestParams(), this, this);
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
	private Map<String, String> getNewsDetailRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put(IntentExtraConfig.ND_ID, id);		
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
	private void requestNewsDetail(int method, String methodUrl, Map<String, String> params,	 
			Listener<NewsDetail> listenre, ErrorListener errorListener) {			
		if(mNewsDetailRequest != null) {
			mNewsDetailRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mNewsDetailRequest = new NewsDetailRequest(method, url, params, listenre, errorListener);
		startRequest(mNewsDetailRequest);		
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
	public void onResponse(NewsDetail response) {
		mNewsDetail = response;
		setIndeterminateBarVisibility(false);
		
		titleTv.setText(mNewsDetail.title);
		timeTv.setText(mNewsDetail.time);
		imgIv.setImageUrl(mNewsDetail.imagesUrl[0], CityLifeApp.getInstance().getImageLoader());
		contentTv.setText(mNewsDetail.content);
	}	
}
