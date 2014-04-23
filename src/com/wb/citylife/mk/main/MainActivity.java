package com.wb.citylife.mk.main;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;

public class MainActivity extends BaseActivity {
	
	private static final String TAG_HOME = "home";
	private static final String TAG_SEARCH = "search";
	private static final String TAG_ACCOUNT = "account";
	private static final String TAG_SETTINGS = "settings";
	
	private String tabTags[] = {TAG_HOME, TAG_SEARCH, TAG_ACCOUNT, TAG_SETTINGS};
	private Class fragments[] = {HomeFragment.class, SearchFragment.class, AccountFragment.class, SettingsFragment.class};
	private int tabNameIds[] = {R.string.tab_name_home, R.string.tab_name_search, R.string.tab_name_account, R.string.tab_name_settings};
	private int tabIconIds[] = {R.drawable.tab_home, R.drawable.tab_search, R.drawable.tab_account, R.drawable.tab_settings};
	
	private FragmentTabHost fTabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_main_tab);
		
		getIntentData();
		initView();
	}
	
	@Override
	public void getIntentData() {
		
	}
	
	@Override
	public void initView() {
		
		fTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
		fTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
						
		for(int i=0; i<fragments.length; i++) {
			TabSpec tabSpec = fTabHost.newTabSpec(tabTags[i]); 
			tabSpec.setIndicator(getTabItemView(tabIconIds[i], tabNameIds[i]));
			fTabHost.addTab(tabSpec, fragments[i], null);
			fTabHost.getTabWidget().setBackgroundResource(R.drawable.tab_footer_bg_white);			
		}
	}	
	
	/**
	 * 给Tab按钮设置图片和文字
	 * @param resId
	 * @param nameId
	 * @return
	 */
	private View getTabItemView(int resId, int nameId) {
		View v = (View)LayoutInflater.from(this).inflate(R.layout.main_tab_item, null);
		
		ImageView iconImageView = (ImageView)v.findViewById(R.id.tab_icon);
		TextView iconName = (TextView)v.findViewById(R.id.tab_icon_name);
		iconImageView.setImageDrawable(getResources().getDrawable(resId));
		iconName.setText(getResources().getString(nameId));
		
		return v;
	}
}
