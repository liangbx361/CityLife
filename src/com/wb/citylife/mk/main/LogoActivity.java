package com.wb.citylife.mk.main;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.IBaseNetActivity;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.WelcomeAdv;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.task.WelcomeAdvRequest;

public class LogoActivity extends IBaseNetActivity implements Listener<WelcomeAdv>, ErrorListener, 
	ImageListener{
	
	private int waitTime = 2 * 1000;
	private int timeCount = 0;
	private boolean isLoad = false;
	
	private WelcomeAdvRequest mWelcomeAdvRequest;
	private WelcomeAdv mWelcomeAdv;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logo);
		
		requestWelcomeAdv(Method.POST, NetInterface.METHOD_WELCOME_ADV, getWelcomeAdvRequestParams(), this, this);
		new TimeCount().start();
	}
	
	
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getWelcomeAdvRequestParams() {
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
	private void requestWelcomeAdv(int method, String methodUrl, Map<String, String> params,	 
			Listener<WelcomeAdv> listenre, ErrorListener errorListener) {			
		if(mWelcomeAdvRequest != null) {
			mWelcomeAdvRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mWelcomeAdvRequest = new WelcomeAdvRequest(method, url, params, listenre, errorListener);
		startRequest(mWelcomeAdvRequest);		
	}
	
	/**
	 * 网络请求错误处理
	 *
	 */
	@Override
	public void onErrorResponse(VolleyError error) {		
	}
	
	/**
	 * 请求完成，处理UI更新
	 */
	@Override
	public void onResponse(WelcomeAdv response) {
		mWelcomeAdv = response;
		CityLifeApp.getInstance().getImageLoader().get(response.imageUrl, this);
	}

	@Override
	public void onResponse(ImageContainer container, boolean arg1) {
		isLoad = true;
	}	
	
	class TimeCount extends Thread {

		@Override
		public void run() {			
			while(true) {
				timeCount += 200;
				try {
					sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}						
			
				if(timeCount > waitTime) {
					if(isLoad) {
						mHandler.sendEmptyMessage(0);
						break;
					}
				}
			}
		}		
	}
	
	private void nextPage() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra(IntentExtraConfig.WELCOME_IMG, mWelcomeAdv.imageUrl);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);		
	}
	
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			nextPage();
		}
		
	};
}
