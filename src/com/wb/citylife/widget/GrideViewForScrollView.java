package com.wb.citylife.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.GridView;

public class GrideViewForScrollView extends GridView {

	public GrideViewForScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public GrideViewForScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GrideViewForScrollView(Context context) {
		super(context);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);

	}
}
