package com.wb.citylife.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

public class ScaleLinearLayout extends LinearLayout implements AnimationListener{
	
	public interface ZoomState {
		/**
		 * 缩小
		 */
		public static final int ZOOM_OUT = 0;
		
		/**
		 * 放大
		 */
		public static final int ZOOM_IN = 1;
	
	}
	
	private static final float ZOOM_OUT_VALUE = 0.8f;
	private static final float ZOOM_INT_VALUE = 1.0f;
	private static final int DURATION_VALUE = 200;
	
	private int zoomState = ZoomState.ZOOM_OUT; //0:缩小 1：放大
	
	public ScaleLinearLayout(Context context) {
		super(context);		
	}

	@SuppressLint("NewApi")
	public ScaleLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ScaleLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
			
	/**
	 * 放大
	 */
	public AnimationSet zoomIn() {
		ScaleAnimation animation = new ScaleAnimation(ZOOM_OUT_VALUE, ZOOM_INT_VALUE, 
				ZOOM_OUT_VALUE, ZOOM_INT_VALUE, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		AnimationSet animSet = new AnimationSet(true);
		animSet.addAnimation(animation);
		animSet.setDuration(DURATION_VALUE);
		animSet.setFillAfter(true);
		startAnimation(animSet);		
		zoomState = ZoomState.ZOOM_IN;
		
		return animSet;
	}
	
	/**
	 * 缩小
	 */
	public AnimationSet zoomOut() {
		ScaleAnimation animation = new ScaleAnimation(ZOOM_INT_VALUE, ZOOM_OUT_VALUE, 
				ZOOM_INT_VALUE, ZOOM_OUT_VALUE, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		AnimationSet animSet = new AnimationSet(true);
		animSet.addAnimation(animation);
		animSet.setDuration(DURATION_VALUE);
		animSet.setFillAfter(true);
		startAnimation(animSet);
		zoomState = ZoomState.ZOOM_OUT;
		
		return animSet;
	}
	
	/**
	 * 点击缩放事件
	 * @param zoomState
	 */
	public void clickZoomOut() {		
		this.zoomState = ZoomState.ZOOM_OUT;
		zoomOut().setAnimationListener(this);
	}
	
	@Override
	public void onAnimationEnd(Animation animation) {
		if(zoomState == ZoomState.ZOOM_OUT) {
			this.zoomState = ZoomState.ZOOM_IN;
			zoomIn();
		} 
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		
	}
	
}
