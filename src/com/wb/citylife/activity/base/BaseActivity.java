package com.wb.citylife.activity.base;

import com.umeng.analytics.MobclickAgent;
import android.app.ProgressDialog;

/**
 * Activity基类
 * 
 * @author liangbx
 * 
 */
public abstract class BaseActivity extends BaseNetActivity {
	
	private ProgressDialog pDialog;
	
	/**
	 * 获取Intent数据
	 */
	public abstract void getIntentData();

	/**
	 * 初始化控件
	 */
	public abstract void initView();
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	public void showDialog(String message) {
		pDialog = new ProgressDialog(this);
		pDialog.setIndeterminate(true);
		pDialog.setMessage(message);
		pDialog.setCancelable(false);
		pDialog.show();
	}
	
	public void dismissDialog () {
		pDialog.dismiss();
	}
}
