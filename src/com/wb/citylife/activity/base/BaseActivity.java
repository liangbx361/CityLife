package com.wb.citylife.activity.base;

import android.os.Bundle;

public abstract class BaseActivity extends BaseActionBarActivity{
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
	}
	
	/**
	 * 获取Intent数据
	 */
	public abstract void getIntentData();
			
	/**
	 * 初始化控件
	 */
	public abstract void initView();
	
	
}
