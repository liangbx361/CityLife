package com.wb.citylife.mk.settings;

import com.wb.citylife.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class AccountManagerActivity extends PreferenceActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.account_manager);
	}
	
}
