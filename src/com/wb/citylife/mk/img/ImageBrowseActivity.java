package com.wb.citylife.mk.img;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.bean.ImagesItem;
import com.wb.citylife.config.IntentExtraConfig;

public class ImageBrowseActivity extends BaseActivity implements ImageBrowseListener{
	
	private FragmentTabHost fTabHost;
	private ArrayList<ImagesItem> imageList;
	private boolean disTab;
	private MenuItem numItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_browser);
		
		getIntentData();
		initView();
	}
	
	@Override
	public void getIntentData() {
		imageList = getIntent().getParcelableArrayListExtra(IntentExtraConfig.ESTATE_IMAGE_DATA);
		disTab = getIntent().getBooleanExtra(IntentExtraConfig.ESTATE_DIS_TAB, true);
	}

	@Override
	public void initView() {
		fTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
		fTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		fTabHost.getTabWidget().setDividerDrawable(null);
		
		for(int i=0; i<imageList.size(); i++) {
			ImagesItem imagesItem = imageList.get(i);
			TabSpec tabSpec = fTabHost.newTabSpec("type"+i);
			tabSpec.setIndicator(getTabItemView(imagesItem));
			Bundle bundle = new Bundle();
			bundle.putParcelable(IntentExtraConfig.ESTATE_IMAGE_DATA, imagesItem);
			fTabHost.addTab(tabSpec, ImageBrowseFragment.class, bundle);
		}
		
		if(!disTab) {
			fTabHost.setVisibility(View.GONE);
		}
		
		fTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				int index = Integer.parseInt(tabId.substring(tabId.length()-1, tabId.length()));
				int num = imageList.get(index).imageNum;
				numItem.setTitle("1/" + num);
			}			
		});
	}
	
	private View getTabItemView(ImagesItem imagesItem) {
		View v = (View)LayoutInflater.from(this).inflate(R.layout.image_tab_item, null);
		
		TextView iconName = (TextView)v.findViewById(R.id.tab_icon_name);
		iconName.setText(imagesItem.name);
		
		return v;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
		
		numItem = setActionBarItem(menu, R.id.action_bar_title, R.string.action_null);
		numItem.setTitle("1/" + imageList.get(0).imageNum);
		
		return super.onCreateOptionsMenu(menu);		
	}

	@Override
	public void setMenuItem(String content) {
		numItem.setTitle(content);
	}
}
