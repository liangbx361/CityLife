package com.wb.citylife.mk.main;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.support.v4.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wb.citylife.R;

public class MyCenterFragment extends PreferenceFragment implements OnPreferenceClickListener {
	
	private Activity mActivity;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.mycenter);
	}
		
	@Override
	public boolean onPreferenceClick(Preference preference) {
		return false;
	}
}
