package com.wb.citylife.mk.mycenter;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.config.ResultCode;
import com.common.net.volley.VolleyErrorHelper;
import com.common.security.MD5;
import com.common.widget.ToastHelper;
import com.wb.citylife.bean.Register;
import com.wb.citylife.task.RegisterRequest;

public class RegisterActivity extends BaseActivity implements Listener<Register>, ErrorListener,
	OnClickListener{
	
	private EditText userPhoneEt;
	private EditText passwordEt;
	private EditText password2Et;
	private Button registerBtn;
	
	private final int passwordMinLenth = 6;
	
	private String userPhone;
	private String password;
	
	private RegisterRequest mRegisterRequest;
	private Register mRegister;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		getIntentData();
		initView();				
	}
	
	@Override
	public void getIntentData() {
		
	}
	
	@Override
	public void initView() {
		userPhoneEt = (EditText) findViewById(R.id.userPhone);
		passwordEt = (EditText) findViewById(R.id.password);
		password2Et = (EditText) findViewById(R.id.password2);
		registerBtn = (Button) findViewById(R.id.register);
		registerBtn.setOnClickListener(this);
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
		userPhone = userPhoneEt.getText().toString();
		password = passwordEt.getText().toString();
		String password2 = password2Et.getText().toString();
		
		Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
		
		if(userPhone.equals("")) {
			userPhoneEt.startAnimation(shake);
			ToastHelper.showToastInBottom(this, R.string.username_empty_toast);
			return;
		}
		
		if(!isMobileNO(userPhone)) {
			ToastHelper.showToastInBottom(this, R.string.username_error);
			userPhoneEt.startAnimation(shake);
			return;
		}
		
		if(password.equals("")) {
			ToastHelper.showToastInBottom(this, R.string.password_empty_toast);
			passwordEt.startAnimation(shake);
			return;
		}
		
		if(password.length() < passwordMinLenth) {
			String toast = getResources().getString(R.string.password_no_length);
			ToastHelper.showToastInBottom(this, String.format(toast, passwordMinLenth));
			passwordEt.startAnimation(shake);
			return;
		}
		
		if(password2.equals("")) {
			ToastHelper.showToastInBottom(this, R.string.password_empty_toast);
			password2Et.startAnimation(shake);
			return;
		}
		
		if(password2.length() < passwordMinLenth) {
			String toast = getResources().getString(R.string.password_no_length);
			ToastHelper.showToastInBottom(this, String.format(toast, passwordMinLenth));
			password2Et.startAnimation(shake);
			return;
		}
		
		if(!password.equals(password2)) {
			ToastHelper.showToastInBottom(this, R.string.password_no_equasl);
			passwordEt.startAnimation(shake);
			password2Et.startAnimation(shake);
			return;
		}
		
		setIndeterminateBarVisibility(true);
		requestRegister(Method.POST, NetInterface.METHOD_REGISTER, 
				getRegisterRequestParams(userPhone, password), this, this);
		
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);	
	}
	
	/**
	 * 菜单点击处理
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {			
		return super.onOptionsItemSelected(item);
	}
		
	/**
	 * 获取注册请求参数
	 * @return
	 */
	private Map<String, String> getRegisterRequestParams(String phone, String pwd) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("userPhone", phone);
		try {
			params.put("password", MD5.getDigest(pwd).toUpperCase());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return params;
	}
	
	/**
	 * 执行注册任务请求
	 * @param method
	 * @param url
	 * @param params
	 * @param listenre
	 * @param errorListener
	 */	
	private void requestRegister(int method, String methodUrl, Map<String, String> params,	 
			Listener<Register> listenre, ErrorListener errorListener) {			
		if(mRegisterRequest != null) {
			mRegisterRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mRegisterRequest = new RegisterRequest(method, url, params, listenre, errorListener);
		startRequest(mRegisterRequest);		
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
	public void onResponse(Register response) {		
		setIndeterminateBarVisibility(false);
		mRegister = response;
		
		if(mRegister.respCode == RespCode.SUCCESS) {			
			Intent resultIntent = new Intent();
			resultIntent.putExtra("userPhone", userPhone);
			resultIntent.putExtra("password", password);
			setResult(ResultCode.AUTO_LOGIN, resultIntent);
			finish();
			
		} else {
			ToastHelper.showToastInBottom(this, R.string.register_fail);
		}
	}	
	
	/**
	 * 验证手机号是否正确
	 * @param mobiles
	 * @return
	 */
	public boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);

		return m.matches();
	}
}
