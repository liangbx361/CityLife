package com.wb.citylife.dialog;

import com.wb.citylife.R;
import com.wb.citylife.mk.push.PushUtils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class PushDialog extends Dialog {
	
	private CheckBox pushBox;
	private Context mContext;
	
	public PushDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		mContext = context;
	}

	public PushDialog(Context context, int theme) {
		super(context, theme);
		mContext = context;
	}

	public PushDialog(Context context) {
		super(context);
		mContext = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_push);
		
		getWindow().setGravity(Gravity.CENTER);
		setCancelable(true);
		
		initView();
	}	
	
	private void initView() {
		pushBox = (CheckBox) findViewById(R.id.push);
		if(PushUtils.hasBind(mContext)) {
			pushBox.setChecked(true);
		} else {
			pushBox.setChecked(false);
		}
		pushBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {					
					PushUtils.openPush(mContext);
				} else {
					PushUtils.closePush(mContext);
				}
			}
		});
	}
}
