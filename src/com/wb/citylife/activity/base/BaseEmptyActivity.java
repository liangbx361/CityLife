package com.wb.citylife.activity.base;

import com.wb.citylife.R;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * 此类用于处理当内容为空时显示提示信息
 * @author liangbx
 *
 */
public class BaseEmptyActivity extends FragmentActivity{
	
	private FrameLayout rootLayout;
	private ViewGroup emptyContentLayout;
	private ViewGroup contentLayout;
	private TextView emptyToastTv;
	
	@Override
	public void setContentView(int layoutResID) {		
		rootLayout = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.empty_layout, null);
		emptyContentLayout = (ViewGroup) rootLayout.findViewById(R.id.empty_content_layout);
		emptyToastTv = (TextView) emptyContentLayout.findViewById(R.id.toast);
		emptyContentLayout.setVisibility(View.GONE);
		
		contentLayout = (ViewGroup) rootLayout.findViewById(R.id.content_layout);
		View contentView = LayoutInflater.from(this).inflate(layoutResID, null);
		contentLayout.addView(contentView);								
		super.setContentView(rootLayout);
	}
	
	/**
	 * 设置内容为空时的提示文字
	 * @param toast
	 */
	public void setEmptyToastText(String toast) {
		emptyToastTv.setText(toast);
	}
	
	/**
	 * 设置内容为空时的提示文字
	 * @param toast
	 */
	public void setEmptyToastText(int resId) {
		emptyToastTv.setText(getResources().getString(resId));
	}
	
	/**
	 * 设置内容为空提示是否显示
	 * @param visibility
	 */
	public void setEmptyContentVisibility(int visibility) {
		if(visibility == View.GONE || visibility == View.INVISIBLE) {
			contentLayout.setVisibility(View.VISIBLE);
		} else {
			contentLayout.setVisibility(View.INVISIBLE);
		}
		
		emptyContentLayout.setVisibility(visibility);
	}
}
