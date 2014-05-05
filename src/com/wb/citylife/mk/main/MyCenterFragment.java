package com.wb.citylife.mk.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.support.v4.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wb.citylife.R;
import com.wb.citylife.mk.mycenter.LoginActivity;

public class MyCenterFragment extends PreferenceFragment implements OnPreferenceClickListener {
	
	private Activity mActivity;
	private Preference loginPf;	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.mycenter);
		
		initPf();
	}
	
	private void initPf() {
		loginPf = (Preference) findPreference(getResources().getString(R.string.pf_login));
		loginPf.setOnPreferenceClickListener(this);
	}
	
	@Override
	public boolean onPreferenceClick(Preference preference) {
		if(preference.getKey().equals(getResources().getString(R.string.pf_login))) {
			startActivity(new Intent(mActivity, LoginActivity.class));
		}
		return false;
	}
}
