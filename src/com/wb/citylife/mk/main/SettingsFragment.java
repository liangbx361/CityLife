package com.wb.citylife.mk.main;

import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.support.v4.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.fb.FeedbackAgent;
import com.wb.citylife.R;

public class SettingsFragment extends PreferenceFragment implements OnPreferenceClickListener {

	private Activity mActivity;
	private Preference feedbackPreference;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		feedbackPreference = (Preference) findPreference(getResources().getString(R.string.pf_feedbak));
		feedbackPreference.setOnPreferenceClickListener(this);
	}
	
	@Override
	public boolean onPreferenceClick(Preference preference) {
		
		if(preference.getKey().contains(getResources().getString(R.string.pf_feedbak))) {
			FeedbackAgent agent = new FeedbackAgent(getActivity());
		    agent.startFeedbackActivity();
		}
		
		return false;
	}	
}
