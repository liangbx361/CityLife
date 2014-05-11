package com.wb.citylife.mk.mycenter;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.common.net.volley.VolleyErrorHelper;
import com.common.widget.ToastHelper;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.Login;
import com.wb.citylife.bean.db.User;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.config.ResultCode;
import com.wb.citylife.db.DbHelper;
import com.wb.citylife.task.LoginRequest;

public class LoginActivity extends BaseActivity implements Listener<Login>, ErrorListener,
	OnClickListener{
	
	private EditText userphoneEt;
	private EditText passwordEt;
	private Button loginBtn;
	private Button registerBtn;
	private TextView forgetPasswordTv;
	
	private LoginRequest mLoginRequest;
	private Login mLogin;
	
	private String userphone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		getIntentData();
		initView();				
	}
			
	@Override
	public void getIntentData() {
		
	}
	
	@Override
	public void initView() {
		userphoneEt = (EditText) findViewById(R.id.userPhone);
		passwordEt = (EditText) findViewById(R.id.password);
		loginBtn = (Button) findViewById(R.id.login);
		registerBtn = (Button) findViewById(R.id.register);
		forgetPasswordTv = (TextView) findViewById(R.id.forget_password);
		
		loginBtn.setOnClickListener(this);
		registerBtn.setOnClickListener(this);
		forgetPasswordTv.setOnClickListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
				
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * 菜单点击处理
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {			
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.login:
			login();
			break;
			
		case R.id.register:
			startActivityForResult(new Intent(this, RegisterActivity.class), 0);
			break;
			
		case R.id.forget_password:
			
			break;
		}
	}
	
	/**
	 * 用户登录处理
	 */
	private void login() {
		String userPhone = userphoneEt.getText().toString();
		String password = passwordEt.getText().toString();
		
		if(userPhone == null || userPhone.equals("")) {
			ToastHelper.showToastInBottom(this, R.string.username_empty_toast);
			return;
		}
		
		if(password == null || password.equals("")) {
			ToastHelper.showToastInBottom(this, R.string.password_empty_toast);
			return;
		}
		
		this.userphone = userPhone;
		requestLogin(Method.POST, NetInterface.METHOD_LOGIN, getLoginRequestParams(userPhone, password), this, this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == ResultCode.AUTO_LOGIN) {
			ToastHelper.showToastInBottom(this, R.string.auto_login);
			String userPhone = data.getStringExtra("userPhone");
			String password = data.getStringExtra("password");
			
			requestLogin(Method.POST, NetInterface.METHOD_LOGIN, getLoginRequestParams(userPhone, password), this, this);		
		}
	}
	
	/**
	 * 获取请求参数
	 * @return
	 */
	private Map<String, String> getLoginRequestParams(String phone, String pwd) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("userPhone", phone);
		params.put("password", pwd);
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
	private void requestLogin(int method, String methodUrl, Map<String, String> params,	 
			Listener<Login> listenre, ErrorListener errorListener) {			
		if(mLoginRequest != null) {
			mLoginRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mLoginRequest = new LoginRequest(method, url, params, listenre, errorListener);
		startRequest(mLoginRequest);		
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
	public void onResponse(Login response) {		
		setIndeterminateBarVisibility(false);
		mLogin = response;
		if(mLogin.respCode == RespCode.SUCCESS) {
			ToastHelper.showToastInBottom(this, R.string.login_success);
			//存储到数据库中，并确保此时数据库中的所有用户为登出状态
			User user = new User();
			user.userId = mLogin.userId;
			user.avatarUrl = mLogin.avatarUrl;
			user.nickname = mLogin.nickname;
			user.accessToken = mLogin.accessToken;
			user.userphone = userphone;
			user.isLogin = 1;
			DbHelper.saveUser(user);
			CityLifeApp.getInstance().setUser(user);
			setResult(ResultCode.RESULT_LOGIN);
			finish();
		} else {
			ToastHelper.showToastInBottom(this, mLogin.respMsg);
		}
	}	
}
