package com.wb.citylife.mk.mycenter;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.common.net.volley.VolleyErrorHelper;
import com.common.widget.ToastHelper;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.BaseBean;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.task.BaseRequest;

public class ModifyNickNameActivity extends BaseActivity implements OnClickListener,
	Listener<BaseBean>, ErrorListener{
	
	private EditText nicknameEt;
	private Button submitBtn;
	
	private BaseRequest mBaseRequest;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_nickname);
		
		getIntentData();
		initView();
	}
	
	@Override
	public void getIntentData() {
		
	}
	
	@Override
	public void initView() {
		nicknameEt = (EditText) findViewById(R.id.nickName);
		submitBtn = (Button) findViewById(R.id.submit);
		submitBtn.setOnClickListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
				
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public void onClick(View v) {
		String nickName = nicknameEt.getText().toString();		
		Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
		
		if(nickName.equals("")) {
			nicknameEt.startAnimation(shake);
			ToastHelper.showToastInBottom(this, R.string.nickname_empty_toast);
			return;
		}
		
		setIndeterminateBarVisibility(true);
		requestBase(Method.POST, NetInterface.METHOD_MODIFY_NICKNAME, getBaseRequestParams(nickName), this, this);
	}	
	
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getBaseRequestParams(String nickName) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", CityLifeApp.getInstance().getUser().getUserId());
		params.put("nickName", nickName);		
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
	private void requestBase(int method, String methodUrl, Map<String, String> params,	 
			Listener<BaseBean> listenre, ErrorListener errorListener) {			
		if(mBaseRequest != null) {
			mBaseRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mBaseRequest = new BaseRequest(method, url, params, listenre, errorListener);
		startRequest(mBaseRequest);		
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
	public void onResponse(BaseBean response) {
		setIndeterminateBarVisibility(false);
		if(response.respCode == RespCode.SUCCESS) {
			ToastHelper.showToastInBottom(this, R.string.nickname_modify_success);
			finish();
		}
	}
}
