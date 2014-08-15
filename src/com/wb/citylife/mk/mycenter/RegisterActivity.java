package com.wb.citylife.mk.mycenter;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

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
import com.wb.citylife.bean.Register;
import com.wb.citylife.config.ActionConfig;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.config.ResultCode;
import com.wb.citylife.dialog.ConfirmDialog;
import com.wb.citylife.task.BaseRequest;
import com.wb.citylife.task.RegisterRequest;

public class RegisterActivity extends BaseActivity implements Listener<Register>, ErrorListener,
	OnClickListener{
	
	private MenuItem toastMItem;
	private TimeCount timeCount;
	
	private EditText userPhoneEt;
	private EditText passwordEt;
	private EditText password2Et;
	private EditText codeEt;
	private Button registerBtn;
	
	public static final int passwordMinLenth = 6;
	
	private String userPhone;
	private String password;
	private String verifyCode;
	
	private RegisterRequest mRegisterRequest;
	private Register mRegister;
	private RadioGroup genderRg;
	
	private CheckBox proCb;
	private TextView userProTv;
	
	private BaseRequest mVerifyCodeRequest;
	
	private boolean sendEnable = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		getIntentData();
		initView();		
		
		timeCount = new TimeCount(RestPasswordActivity.VERIFY_CODE_SEND_TIME, 1000);
	}
	
	@Override
	public void getIntentData() {
		userPhone = getIntent().getStringExtra(IntentExtraConfig.USER_PHONE);
	}
	
	@Override
	public void initView() {
		userPhoneEt = (EditText) findViewById(R.id.userPhone);
		passwordEt = (EditText) findViewById(R.id.password);
		password2Et = (EditText) findViewById(R.id.password2);
		codeEt = (EditText) findViewById(R.id.verify_code);
		registerBtn = (Button) findViewById(R.id.register);
		registerBtn.setOnClickListener(this);
		genderRg = (RadioGroup) findViewById(R.id.gender);
		
		proCb = (CheckBox) findViewById(R.id.protocol);
		userProTv = (TextView) findViewById(R.id.user_protocol);
		userProTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		userProTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				new ConfirmDialog().getConfirmDialog(RegisterActivity.this, 
						R.string.user_protocol_name, R.string.user_protocol_content).show();;
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
		
		toastMItem = setActionBarItem(menu, R.id.action_post, R.string.ac_name_about);
		toastMItem.setTitle("");
		
		requestVerifyCode(Method.POST, NetInterface.METHOD_GET_VERIFY_CODE, 
				getVerifyCodeRequestParams(), new SMSListener(), this);
		
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
				requestVerifyCode(Method.POST, NetInterface.METHOD_GET_VERIFY_CODE, 
						getVerifyCodeRequestParams(), new SMSListener(), RegisterActivity.this);
			}
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onClick(View v) {
//		userPhone = userPhoneEt.getText().toString();
		password = passwordEt.getText().toString();
		String password2 = password2Et.getText().toString();
		verifyCode = codeEt.getText().toString();
		
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
		
		int gender = 1;
		if(genderRg.getCheckedRadioButtonId() == R.id.female) {
			gender = 2;
		}
		
		if(!proCb.isChecked()) {
			ToastHelper.showToastInBottom(this, R.string.user_protocol_toast);
			return;
		}
		
		setIndeterminateBarVisibility(true);
		requestRegister(Method.POST, NetInterface.METHOD_REGISTER, 
				getRegisterRequestParams(userPhone, password, gender, verifyCode), this, this);		
	}
			
	/**
	 * 获取注册请求参数
	 * @return
	 */
	private Map<String, String> getRegisterRequestParams(String phone, String pwd, int gender, String verifyCode) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("userPhone", phone);
		try {
			params.put("password", MD5.getDigest(pwd).toUpperCase());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		params.put("gender", gender + "");
		params.put("verifyCode", verifyCode);
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
//			Intent resultIntent = new Intent();
//			resultIntent.putExtra("userPhone", userPhone);
//			resultIntent.putExtra("password", password);
//			setResult(ResultCode.AUTO_LOGIN, resultIntent);
//			finish();
			
			finish();
			Intent resultIntent = new Intent(ActionConfig.ACTION_AUTO_LOGIN);
			resultIntent.putExtra("userPhone", userPhone);
			resultIntent.putExtra("password", password);
			sendBroadcast(resultIntent);
			
		} else {
			ToastHelper.showToastInBottom(this, response.respMsg);
		}
	}	
	
	/**
	 * 验证手机号是否正确
	 * @param mobiles
	 * @return
	 */
	public static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(14[5,7])|(17[0,6-8])|(18[^4,\\D]))\\d{8}$");
		Matcher m = p.matcher(mobiles);

		return m.matches();
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
	
	class SMSListener implements Listener<BaseBean> {

		@Override
		public void onResponse(BaseBean response) {
			if(response.respCode == RespCode.SUCCESS) {
				timeCount.start();
				ToastHelper.showToastInBottom(RegisterActivity.this, "短信已发送");
			} else {
				ToastHelper.showToastInBottom(RegisterActivity.this, response.respMsg);
			}
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
}
