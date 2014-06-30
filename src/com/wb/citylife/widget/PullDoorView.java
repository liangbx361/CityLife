package com.wb.citylife.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * 往上推动开门效果
 * 
 * @author way
 * 
 */
public class PullDoorView extends RelativeLayout implements OnGestureListener{

	private Context mContext;
	
	//下滑弹跳
	private Scroller mScroller;
	//上滑加速
	private Scroller mUpScroller;
	private boolean isPullUp = false;
	
	private int mScreenHeigh = 0;
	
	private float density;
	
	private int mLastDownY = 0;

	private int mCurryY;

	private int mDelY;

	private boolean mCloseFlag = false;	
	
	// 手势解析类用于 移动、加速、单击、双击
	private GestureDetector mDetector;
	
	private PullDoorViewListener mListener;

	public PullDoorView(Context context) {
		super(context);
		mContext = context;
		setupView();
	}

	public PullDoorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		setupView();
	}

	private void setupView() {

		// 这个Interpolator你可以设置别的 我这里选择的是有弹跳效果的Interpolator
		Interpolator polator = new BounceInterpolator();
		mScroller = new Scroller(mContext, polator);
		
		AccelerateInterpolator aInterpolator = new AccelerateInterpolator();
		mUpScroller = new Scroller(mContext, aInterpolator);

		// 获取屏幕分辨率
		WindowManager wm = (WindowManager) (mContext
				.getSystemService(Context.WINDOW_SERVICE));
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		mScreenHeigh = dm.heightPixels;
		density = dm.density;

		// 这里你一定要设置成透明背景,不然会影响你看到底层布局
		this.setBackgroundColor(Color.argb(0, 0, 0, 0));		
		mDetector = new GestureDetector(mContext, this);
	}
	
	// 推动门的动画
	public void startBounceAnim(int startY, int dy) {
		isPullUp = false;
		int duration = (int)(500 * density);
		mScroller.startScroll(0, startY, 0, dy, duration);
		invalidate();
	}
	
	public void startAcceleAnim(int startY, int dy) {
		isPullUp = true;
		mCloseFlag = true;
		int duration = (int) ((dy - startY) / density);
		mUpScroller.startScroll(0, startY, 0, dy, duration);
		invalidate();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastDownY = (int) event.getY();
			return true;
			
		case MotionEvent.ACTION_MOVE:
			mCurryY = (int) event.getY();
			mDelY = mCurryY - mLastDownY;
			// 只准上滑有效
			if (mDelY < 0) {
				scrollTo(0, -mDelY);
			}
			break;
			
		case MotionEvent.ACTION_UP:
			mCurryY = (int) event.getY();
			mDelY = mCurryY - mLastDownY;
			if (mDelY < 0) {

				if (Math.abs(mDelY) > mScreenHeigh / 2) {
					// 向上滑动超过半个屏幕高的时候 开启向上消失动画
					startBounceAnim(this.getScrollY(), mScreenHeigh);					
				} else {
					// 向上滑动未超过半个屏幕高的时候 开启向下弹动动画
					startBounceAnim(this.getScrollY(), -this.getScrollY());
				}
			}

			break;
		}
		
		mDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	@Override
	public void computeScroll() {
		if(!isPullUp) {
			if (mScroller.computeScrollOffset()) {
				scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
				//不要忘记更新界面
				postInvalidate();			
			} 
		} else {		
			if(mUpScroller.computeScrollOffset()) {
				scrollTo(mUpScroller.getCurrX(), mUpScroller.getCurrY());
				postInvalidate();
			} else {
				if(mCloseFlag) {
					if(mListener != null) {
						mListener.onClosed();
					}
					mCloseFlag = false;
				}
			}
		}
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		
		if(velocityY < -1000.00f) {
			// 向上滑动超过半个屏幕高的时候 开启向上消失动画
			startAcceleAnim(this.getScrollY(), mScreenHeigh);
			mCloseFlag = true;			
		}
		
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
	
	public interface PullDoorViewListener {
		public void onClosed();			
	}
	
	public void setListener(PullDoorViewListener listener) {
		mListener = listener;
	}

}
