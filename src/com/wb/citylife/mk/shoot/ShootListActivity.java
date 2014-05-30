package com.wb.citylife.mk.shoot;

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

public class ShootListActivity extends BaseActivity {
	
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
		oldViewPager.setAdapter(new ShootPageAdapter(getSupportFragmentManager()));
		tabIndicator.setViewPager(oldViewPager);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
		
		setActionBarItem(menu, R.id.action_publish_shoot, R.string.action_publish_shoot, 
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
		case R.id.action_search:
			return true;
		
		case R.id.action_publish_shoot:{
			startActivity(new Intent(this, ShootPublishActivity.class));
		}break;
		}
				
		
		return super.onOptionsItemSelected(item);
	}
	
	class ShootPageAdapter extends FragmentPagerAdapter implements IconPagerAdapter{
//		String[] titles = {"最新", "最热", "发布随手拍", "我的随手拍"};
		String[] titles = {"最新", "最热", "我的随手拍"};
		int[] icons = {R.drawable.shoot_new_icon_selector, R.drawable.shoot_hot_icon_selector,
				R.drawable.my_shoot_icon_selector};
		
		int pageCount;
		
		public ShootPageAdapter(FragmentManager fm) {
			super(fm);
			pageCount = 2;
			if(CityLifeApp.getInstance().checkLogin()) {
				pageCount += 1;
			}						
		}

		@Override
		public Fragment getItem(int position) {
			Fragment itemFragment = null;
			Bundle bundle = new Bundle();
			if(position == 0) {
				itemFragment = new ShootListFragment();
				bundle.putString(IntentExtraConfig.SHOOT_TYPE, ShootListFragment.SHOOT_TYPE_NEW);
			} else if(position == 1) {
				itemFragment = new ShootListFragment();
				bundle.putString(IntentExtraConfig.SHOOT_TYPE, ShootListFragment.SHOOT_TYPE_HOT);		
//			} else if(position == 2) {
//				itemFragment = new ShootPublishFragment();
			} else if(position == 2) {
				itemFragment = new ShootListFragment();
				bundle.putString(IntentExtraConfig.SHOOT_TYPE, ShootListFragment.SHOOT_TYPE_MY);
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
