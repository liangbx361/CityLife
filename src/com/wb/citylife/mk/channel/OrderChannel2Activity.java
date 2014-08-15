package com.wb.citylife.mk.channel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.tsz.afinal.FinalDb;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.adapter.TypeAdapter;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.Item;
import com.wb.citylife.bean.Page;
import com.wb.citylife.bean.db.DbChannel;
import com.wb.citylife.config.ActionConfig;
import com.wb.citylife.widget.dragdropgrid.PagedDragDropGrid;

/**
 * 实现栏目的排序
 * @author liangbx
 *
 */
public class OrderChannel2Activity extends BaseActivity {
	
	//栏目
	private PagedDragDropGrid mTypeGrideView;
	private TypeAdapter mTypeAdapter;
	private List<DbChannel> mChannels;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_channel);
		
		getIntentData();
		initView();				
	}
	
	@Override
	public void getIntentData() {
		mChannels = CityLifeApp.getInstance().getChannels();
	}

	@Override
	public void initView() {
		mTypeGrideView = (PagedDragDropGrid) findViewById(R.id.type_grid);
		
		Page page = new Page();
		List<Item> items = new ArrayList<Item>();
		for(int i=0; i<mChannels.size(); i++) {
			DbChannel channel = mChannels.get(i);
			if(channel.isAdd) {
				items.add(new Item(i, channel.channelId, channel.name, channel.imageUrl));
			}
		}
		page.setItems(items);
		
		mTypeAdapter = new TypeAdapter(this, mTypeGrideView, page);
		mTypeGrideView.setAdapter(mTypeAdapter);
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
		List<Item> items = mTypeAdapter.itemsInPage(0);
		for(int i=0; i<items.size(); i++) {
			Item item = items.get(i);
			for(int j=0; j<mChannels.size(); j++) {
				DbChannel channel = mChannels.get(j);
				if(item.getChannelId().equals(channel.getChannelId())) {
					channel.weight = i;
					break;
				}
			}
		}
		Collections.sort(mChannels, new ChannelComparator());
		FinalDb finalDb = CityLifeApp.getInstance().getDb();
		for(int i=0; i<mChannels.size(); i++) {
			DbChannel channel = mChannels.get(i);
			finalDb.update(channel, "channelId='" + channel.channelId + "'" );
		}
		sendBroadcast(new Intent(ActionConfig.ACTION_UPDATE_CHANNEL));
	}
}
