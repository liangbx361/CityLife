package com.wb.citylife.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;

public class ListViewForScrollView extends ListView {
	
	private OnLastItemVisibleListener mListener;
	private boolean mLastItemVisible;
	
    public ListViewForScrollView(Context context) {
        super(context);
        init();
    }

    public ListViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ListViewForScrollView(Context context, AttributeSet attrs,
        int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
        
    @Override
    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
        MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
    
    private void init() {
//    	setOnScrollListener(new OnScrollListener() {
//			
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				Log.d("scroll_bottom", "onScrollStateChanged");
//				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && null != mListener && mLastItemVisible) {
//					mListener.onLastItemVisible();					
//				}
//			}
//			
//			@Override
//			public void onScroll(AbsListView view, int firstVisibleItem,
//					int visibleItemCount, int totalItemCount) {
//				if(null != mListener) {
//					mLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount - 1);
//					mListener.onLastItemVisible();	
//				}
//			}
//		});
    }
    
    public void setOnLastItemVisibleListener(OnLastItemVisibleListener listener) {
    	mListener = listener;
    }
    
	public interface OnLastItemVisibleListener {

		/**
		 * Called when the user has scrolled to the end of the list
		 */
		public void onLastItemVisible();

	}
}

