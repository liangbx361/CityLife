package com.wb.citylife.mk.mycenter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.config.ResultCode;
import com.common.net.volley.VolleyErrorHelper;
import com.common.widget.ToastHelper;
import com.wb.citylife.bean.Register;
import com.wb.citylife.task.RegisterRequest;

public class ModifyPasswordActivity extends BaseActivity implements Listener<Register>, ErrorListener,
	OnClickListener{
	
	private EditText oldPwdEt;
	private EditText newPwdEt;
	private EditText newPwd2Et;
	private Button submitBtn;
	
	private final int passwordMinLenth = 6;
	
	private String oldPwd;
	private String newPwd;
	
	private RegisterRequest mRegisterRequest;
	private Register mRegister;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_password);
		getIntentData();
		initView();				
	}
	
	@Override
	public void getIntentData() {
		
	}
	
	@Override
	public void initView() {
		oldPwdEt = (EditText) findViewById(R.id.old_password);
		newPwdEt = (EditText) findViewById(R.id.new_password);
		newPwd2Et = (EditText) findViewById(R.id.new_password2);
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
		oldPwd = oldPwdEt.getText().toString();
		newPwd = newPwdEt.getText().toString();
		String newPwd2 = newPwd2Et.getText().toString();
		
		Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
		
		if(oldPwd.equals("")) {
			oldPwdEt.startAnimation(shake);
			ToastHelper.showToastInBottom(this, R.string.password_empty_toast);
			return;
		}
		
		if(oldPwd.length() < passwordMinLenth) {
			String toast = getResources().getString(R.string.password_no_length);
			ToastHelper.showToastInBottom(this, String.format(toast, passwordMinLenth));
			oldPwdEt.startAnimation(shake);
			return;
		}
				
		if(newPwd.equals("")) {
			ToastHelper.showToastInBottom(this, R.string.password_empty_toast);
			newPwdEt.startAnimation(shake);
			return;
		}
		
		if(newPwd.length() < passwordMinLenth) {
			String toast = getResources().getString(R.string.password_no_length);
			ToastHelper.showToastInBottom(this, String.format(toast, passwordMinLenth));
			newPwdEt.startAnimation(shake);
			return;
		}
		
		if(newPwd2.equals("")) {
			ToastHelper.showToastInBottom(this, R.string.password_empty_toast);
			newPwd2Et.startAnimation(shake);
			return;
		}
		
		if(newPwd2.length() < passwordMinLenth) {
			String toast = getResources().getString(R.string.password_no_length);
			ToastHelper.showToastInBottom(this, String.format(toast, passwordMinLenth));
			newPwd2Et.startAnimation(shake);
			return;
		}
		
		if(!newPwd.equals(newPwd2)) {
			ToastHelper.showToastInBottom(this, R.string.password_no_equasl);
			newPwdEt.startAnimation(shake);
			newPwd2Et.startAnimation(shake);
			return;
		}
		
		setIndeterminateBarVisibility(true);
		requestModifyPwd(Method.POST, NetInterface.METHOD_MODIFY_PASSWORD, 
				getRegisterRequestParams(oldPwd, newPwd), this, this);
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
	private Map<String, String> getRegisterRequestParams(String oldPwd, String newPwd) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("oldPassword", oldPwd);
		params.put("newPassword", newPwd);
		params.put("userId", CityLifeApp.getInstance().getUser().userId);
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
	private void requestModifyPwd(int method, String methodUrl, Map<String, String> params,	 
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
		
		if(response.respCode == RespCode.SUCCESS) {			
			ToastHelper.showToastInBottom(this, R.string.password_modify_success);
			finish();			
		} else {
			ToastHelper.showToastInBottom(this, response.respMsg);
		}
	}	
}
