package com.wb.citylife.mk.channel;

import android.os.Bundle;

import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;

public class AddChannelActivity extends BaseActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_channel);
		
		getIntentData();
		initView();				
	}
	
	@Override
	public void getIntentData() {
		
	}

	@Override
	public void initView() {
		
	}

}
