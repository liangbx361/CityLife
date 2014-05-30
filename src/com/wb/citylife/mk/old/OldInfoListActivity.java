package com.wb.citylife.mk.old;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.config.IntentExtraConfig;

public class OldInfoListActivity extends BaseActivity {
	
	private TabPageIndicator tabIndicator;
	private ViewPager oldViewPager;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_oldinfolist);
		
		getIntentData();
		initView();				
	}
			
	@Override
	public void getIntentData() {
		
	}
	
	@Override
	public void initView() {
		tabIndicator = (TabPageIndicator) findViewById(R.id.tab);
		oldViewPager = (ViewPager) findViewById(R.id.pager);
		oldViewPager.setAdapter(new OldPageAdapter(getSupportFragmentManager()));
		tabIndicator.setViewPager(oldViewPager);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
		
		setActionBarItem(menu, R.id.action_publish_oldinfo, R.string.action_publish_oldinfo, 
				R.drawable.actionbar_publish_oldinfo_icon);
		setActionBarItem(menu, R.id.action_search, R.string.action_search, 
				R.drawable.actionbar_search_icon);
		
		setIndeterminateBarVisibility(true);		
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * 菜单点击处理
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {		
		
		switch(item.getItemId()) {
		case R.id.action_publish_oldinfo:
			startActivity(new Intent(this, PublishOldInfoActivity.class));
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	class OldPageAdapter extends FragmentPagerAdapter implements IconPagerAdapter{
		String[] titles = {"二手市场", "我的二手货"};
		int[] icons = {R.drawable.old_info_icon_selector, R.drawable.my_old_info_icon_selector};
		
		int pageCount;
		
		public OldPageAdapter(FragmentManager fm) {
			super(fm);
			pageCount = 1;
			if(CityLifeApp.getInstance().checkLogin()) {
				pageCount++;
			}						
		}

		@Override
		public Fragment getItem(int position) {
			Fragment itemFragment = new OldListFragment();
			Bundle bundle = new Bundle();
			if(position == 0) {
				bundle.putString(IntentExtraConfig.OLD_TYPE, OldListFragment.TAG_OLD_INFO);
			} else {
				bundle.putString(IntentExtraConfig.OLD_TYPE, OldListFragment.TAG_MY_OLD_INFO);
			}
			itemFragment.setArguments(bundle);
			return itemFragment;
		}

		@Override
		public int getCount() {
			return pageCount;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			
			return titles[position];
		}

		@Override
		public int getIconResId(int index) {
			return icons[index];
		}		
		
		
	}
		
}
