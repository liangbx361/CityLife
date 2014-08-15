package com.wb.citylife.mk.mycenter;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.common.net.volley.VolleyErrorHelper;
import com.common.security.MD5;
import com.common.widget.ToastHelper;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.bean.BaseBean;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.task.BaseRequest;

/**
 * 重置密码模块
 * @author liangbx
 *
 */
public class RestPasswordActivity extends BaseActivity implements Listener<BaseBean>, ErrorListener{
	
	public static final int VERIFY_CODE_SEND_TIME = 60 * 2 * 1000;
	
	private MenuItem toastMItem;
	private TimeCount timeCount;
	
	private BaseRequest mVerifyCodeRequest;
	private String userPhone;
	
	private EditText passwordEt;
	private EditText codeEt;
	private Button submitBtn;
	
	private String password;
	private String verifyCode;
	
	private BaseRequest mRestPwdRequest;
	
	private boolean sendEnable = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_pwd_reset);
		
		getIntentData();
		initView();
		timeCount = new TimeCount(VERIFY_CODE_SEND_TIME, 1000);
	}
	
	@Override
	public void getIntentData() {
		userPhone = getIntent().getStringExtra(IntentExtraConfig.USER_PHONE);
				
	}
	
	@Override
	public void initView() {
		passwordEt = (EditText) findViewById(R.id.new_password);
		codeEt = (EditText) findViewById(R.id.verify_code);
		submitBtn = (Button) findViewById(R.id.submit);
		submitBtn.setOnClickListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
		
		toastMItem = setActionBarItem(menu, R.id.action_post, R.string.ac_name_about);
		toastMItem.setTitle("");
		
		requestVerifyCode(Method.POST, NetInterface.METHOD_GET_VERIFY_CODE, getVerifyCodeRequestParams(), this, this);
				
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * 菜单点击处理
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {	
		
		switch(item.getItemId()) {
		case R.id.action_post:
			if(sendEnable) {
				sendEnable  = false;
				requestVerifyCode(Method.POST, NetInterface.METHOD_GET_VERIFY_CODE, getVerifyCodeRequestParams(), this, this);
			}
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		switch(v.getId()) {
		case R.id.submit:
			
			password = passwordEt.getText().toString();
			verifyCode = codeEt.getText().toString();
			
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			if(password.equals("")) {
				ToastHelper.showToastInBottom(this, R.string.password_empty_toast);
				passwordEt.startAnimation(shake);
				return;
			}
			
			if(password.length() < RegisterActivity.passwordMinLenth) {
				String toast = getResources().getString(R.string.password_no_length);
				ToastHelper.showToastInBottom(this, String.format(toast, RegisterActivity.passwordMinLenth));
				passwordEt.startAnimation(shake);
				return;
			}
			
			if(verifyCode.equals("")) {
				ToastHelper.showToastInBottom(this, R.string.verify_empty_toast);
				codeEt.startAnimation(shake);
				return;
			}
			
			if(verifyCode.length() < 6) {
				ToastHelper.showToastInBottom(this, R.string.verify_no_length);
				codeEt.startAnimation(shake);
				return;
			}
			
			requestResetPwd(Method.POST, NetInterface.METHOD_RESET_PASSWORD, 
					getResetPwdRequestParams(password, verifyCode), new ResetPwdListener(), this);
			
			break;
		}
	}
	
	/**
	 * 验证码发送时间计数
	 * @author liangbx
	 *
	 */
	class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			toastMItem.setTitle("点击重发");
			sendEnable = true;
		}
		
		@Override
		public void onTick(long millisUntilFinished) {
			
			toastMItem.setTitle(millisUntilFinished / 1000 + "秒后可重发");
		}
	}

	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getVerifyCodeRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("userPhone", userPhone);		
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
	private void requestVerifyCode(int method, String methodUrl, Map<String, String> params,	 
			Listener<BaseBean> listenre, ErrorListener errorListener) {			
		if(mVerifyCodeRequest != null) {
			mVerifyCodeRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mVerifyCodeRequest = new BaseRequest(method, url, params, listenre, errorListener);
		startRequest(mVerifyCodeRequest);		
	}
	
	@Override
	public void onErrorResponse(VolleyError error) {
		ToastHelper.showToastInBottom(getApplicationContext(), VolleyErrorHelper.getErrorMessage(error));
	}

	@Override
	public void onResponse(BaseBean response) {
		if(response.respCode == RespCode.SUCCESS) {
			timeCount.start();
			ToastHelper.showToastInBottom(this, "短信已发送");
		} else {
			ToastHelper.showToastInBottom(this, response.respMsg);
		}
	}
	
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getResetPwdRequestParams(String pwd, String code) {
		Map<String, String> params = new HashMap<String, String>();
		try {
			params.put("newPassword", MD5.getDigest(pwd).toUpperCase());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}		
		params.put("verifyCode", code);
		params.put("userPhone", userPhone);	
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
	private void requestResetPwd(int method, String methodUrl, Map<String, String> params,	 
			Listener<BaseBean> listenre, ErrorListener errorListener) {			
		if(mRestPwdRequest != null) {
			mRestPwdRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mRestPwdRequest = new BaseRequest(method, url, params, listenre, errorListener);
		startRequest(mRestPwdRequest);		
	}
	
	class ResetPwdListener implements Listener<BaseBean> {

		@Override
		public void onResponse(BaseBean response) {
			
			if(response.respCode == RespCode.SUCCESS) {
				ToastHelper.showToastInBottom(RestPasswordActivity.this, "密码重置成功，请重新登录");
				finish();
			} else {
				ToastHelper.showToastInBottom(RestPasswordActivity.this, response.respMsg);
			}
		}
		
	}
}
