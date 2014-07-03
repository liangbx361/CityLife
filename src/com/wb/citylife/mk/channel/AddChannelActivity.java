package com.wb.citylife.mk.channel;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.adapter.AddChannelAdapter;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.db.DbChannel;
import com.wb.citylife.config.ActionConfig;

public class AddChannelActivity extends BaseActivity{
	
	private ListView channelLv;
	private List<DbChannel> mChannels;
	private AddChannelAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_channel);
		
		getIntentData();
		initView();				
	}
	
	@Override
	public void getIntentData() {
		mChannels = CityLifeApp.getInstance().getChannels();
	}

	@Override
	public void initView() {
		channelLv = (ListView) findViewById(R.id.edit_channel_list);
		mAdapter = new AddChannelAdapter(this, mChannels);
		channelLv.setAdapter(mAdapter);
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
		
		switch(item.getItemId()) {
		case android.R.id.home:
			saveChannel();
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		
		switch(keyCode) {
		case KeyEvent.KEYCODE_BACK:
			saveChannel();
			finish();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	private void saveChannel() {
		sendBroadcast(new Intent(ActionConfig.ACTION_UPDATE_CHANNEL));
	}
}
