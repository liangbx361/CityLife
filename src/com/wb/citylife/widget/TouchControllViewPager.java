package com.wb.citylife.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class TouchControllViewPager extends ViewPager {

	private boolean moveEnable = true;

	public TouchControllViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TouchControllViewPager(Context context) {
		super(context);
	}

	@Override
	public void scrollTo(int x, int y) {
		if (moveEnable) {
			super.scrollTo(x, y);
		}
	}

	public void setMove(boolean moveEnable) {
		this.moveEnable = moveEnable;
	}
}
