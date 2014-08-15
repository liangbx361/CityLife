package com.wb.citylife.mk.mycenter;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.common.widget.ToastHelper;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.config.IntentExtraConfig;

/**
 * 重置密码/用户注册模块共用
 * @author liangbx
 *
 */
public class InputPhoneActivity extends BaseActivity implements OnClickListener{
		
	private EditText userPhoneEt;
	private Button nextBtn;
	
	private String userPhone;
	private int type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_input_phone);
		
		getIntentData();
		initView();
	}
	
	@Override
	public void getIntentData() {
		type = getIntent().getIntExtra(IntentExtraConfig.INPUT_PHONE_TYPE, IntentExtraConfig.INPUT_TYPE_REGISTER);		
	}
	
	@Override
	public void initView() {
		userPhoneEt = (EditText) findViewById(R.id.phone);
		nextBtn = (Button) findViewById(R.id.next);
		nextBtn.setOnClickListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
		
		if(type == IntentExtraConfig.INPUT_TYPE_REGISTER) {
			setTitle(getResources().getString(R.string.register));
		} 
						
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		userPhone = userPhoneEt.getText().toString();
		
		Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
		
		if(userPhone.equals("")) {
			userPhoneEt.startAnimation(shake);
			ToastHelper.showToastInBottom(this, R.string.username_empty_toast);
			return;
		}
		
		if(!RegisterActivity.isMobileNO(userPhone)) {
			ToastHelper.showToastInBottom(this, R.string.username_error);
			userPhoneEt.startAnimation(shake);
			return;
		}
		
		Intent intent = new Intent();
		if(type == IntentExtraConfig.INPUT_TYPE_REGISTER) {
			intent.setClass(this, RegisterActivity.class);
		} else {
			intent.setClass(this, RestPasswordActivity.class);
		}
		intent.putExtra(IntentExtraConfig.USER_PHONE, userPhone);		
		startActivity(intent);
		finish();
	}
}
