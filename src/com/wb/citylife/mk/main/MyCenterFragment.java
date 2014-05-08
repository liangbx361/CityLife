package com.wb.citylife.mk.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.support.v4.preference.PreferenceFragment;

import com.wb.citylife.R;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.db.User;
import com.wb.citylife.mk.mycenter.LoginActivity;

public class MyCenterFragment extends PreferenceFragment implements OnPreferenceClickListener,
	MyCenterListener{
	
	private Activity mActivity;
	private Preference loginPf;	
	private MainListener mainListener;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
		mainListener = (MainListener) activity;
		mainListener.setMyCenter(this);
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
			startActivityForResult(new Intent(mActivity, LoginActivity.class), 0);
		}
		return false;
	}

	@Override
	public void onLogin() {
//		View view = loginPf.getView(null, null);
		User user = CityLifeApp.getInstance().getUser(); 
		if(user.getNickname().equals("")) {
			loginPf.setTitle(user.getUserphone());
		} else {
			loginPf.setTitle(CityLifeApp.getInstance().getUser().getNickname());
		}		
	}
}
