package com.wb.citylife.activity.base;

import com.umeng.analytics.MobclickAgent;

/**
 * Activity基类
 * 
 * @author liangbx
 * 
 */
public abstract class BaseActivity extends BaseNetActivity {

	/**
	 * 获取Intent数据
	 */
	public abstract void getIntentData();

	/**
	 * 初始化控件
	 */
	public abstract void initView();
	
	
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
		
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
