package com.wb.citylife.activity.base;

import android.app.Activity;
import android.os.Bundle;

/**
 * 此类用于处理换肤
 * @author liangbx
 *
 */
public class BaseSkinActivity extends Activity{
	
	private int currentSkinFlag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentSkinFlag = -1;	
	}
}
