package com.wb.citylife.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class ViewPageForScrollView extends ViewPager{

	public ViewPageForScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ViewPageForScrollView(Context context) {
		super(context);
	}
	
//	@Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
//        MeasureSpec.AT_MOST);
//        super.onMeasure(widthMeasureSpec, expandSpec);
//    }	
}
