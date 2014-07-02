package com.wb.citylife.mk.main;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.wb.citylife.R;
import com.wb.citylife.activity.base.IBaseNetActivity;
import com.wb.citylife.config.IntentExtraConfig;

public class SearchActivity extends IBaseNetActivity{
	
	private FragmentManager mFragmentManager;
	private int searchType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		
		getIntentData();
		initView();
		
	}
	
	public void getIntentData() {
		searchType = getIntent().getIntExtra(IntentExtraConfig.SEARCH_TYPE, 0);
	}
	
	public void initView() {
		mFragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		SearchFragment searchFragment = new SearchFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(IntentExtraConfig.SEARCH_TYPE, searchType);
		searchFragment.setArguments(bundle);
		transaction.replace(R.id.search_fragment, searchFragment);
		transaction.commit();
	}
}
