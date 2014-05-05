package com.wb.citylife.mk.main;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.Menu;

import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.adapter.TypeAdapter;
import com.wb.citylife.bean.Channel;
import com.wb.citylife.bean.Channel.ChannelItem;
import com.wb.citylife.bean.Item;
import com.wb.citylife.bean.Page;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.widget.dragdropgrid.PagedDragDropGrid;

/**
 * 实现栏目的排序
 * @author liangbx
 *
 */
public class OrderChannel extends BaseActivity {
	
	//栏目
	private PagedDragDropGrid mTypeGrideView;
	private TypeAdapter mTypeAdapter;
	private Channel mChannel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getIntentData();
		initView();				
	}
	
	@Override
	public void getIntentData() {
		mChannel = (Channel) getIntent().getSerializableExtra(IntentExtraConfig.OC_CHANNEL);
	}

	@Override
	public void initView() {
		mTypeGrideView = (PagedDragDropGrid) findViewById(R.id.type_grid);
		
		Page page = new Page();
		List<Item> items = new ArrayList<Item>();
		for(int i=0; i<mChannel.datas.size(); i++) {
			ChannelItem channelItem = mChannel.datas.get(i);
			items.add(new Item(i, channelItem.name, channelItem.imageUrl));
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
		
		setIndeterminateBarVisibility(true);		
		return super.onCreateOptionsMenu(menu);
	}
}
