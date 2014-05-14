package com.wb.citylife.widget;

import com.wb.citylife.config.DebugConfig;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class StatisticsBar extends View implements Runnable{
	
	public static final int TEXT_MARGIN_LEFT = 10;
	public static final int DEF_RATE_COLOR = Color.BLUE;
	public static final int DEF_TEXT_COLOR = Color.DKGRAY;
	public static final int DEF_TEXT_SIZE = 16;
	public static final int DEF_ANIM_TIME = 600;
	
	/**
	 * �̶�ʱ�䷽ʽ
	 */
	public static final int ANIM_TYPE_FIXED = 0;
	
	/**
	 * ���̶�ʱ�䷽ʽ
	 */
	public static final int ANIM_TYPE_USET = 1;
	
	public static final int[] DEFAULT_COLOR = {0xff428de0, 0xff5ea223, 0xffc05053, 0xffda7831 };
	
	//ͳ����
	private int rate = 0;
	private Rect rect;
	private Paint paint;			
	
	//����
	private int textMarginLeft;
	private float textRate = 10.0f / 7;
	private Paint textPaint;
	
	private int animTime = DEF_ANIM_TIME;
	private float animInterval = 1000.0f / 60;
	private float timeCount = 0.0f;	
	private boolean anmiState;
	private int animType;
	
	private float rateInvterval;
	private float rateCount;
	
	private int barwidth;
	private int barHeight;
	
	public StatisticsBar(Context context) {
		super(context);
		init(context);
	}
	
	public StatisticsBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public StatisticsBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}
	
	private void init(Context context) {
		rect = new Rect();
		paint = new Paint();
		paint.setColor(DEF_RATE_COLOR);
		
		float density = context.getResources().getDisplayMetrics().density;
		textPaint = new Paint();
		textPaint.setColor(DEF_TEXT_COLOR);
		textPaint.setTextSize(DEF_TEXT_SIZE * density);	
		textPaint.setTypeface(Typeface.MONOSPACE);
		textPaint.setAntiAlias(true);
		textMarginLeft = (int) (TEXT_MARGIN_LEFT * density);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		rect.bottom = barHeight;
		setRectWidth(rate);
		if(!anmiState) {
			DebugConfig.showLog("statics_bar", "============ " + rect.right + ", " + rect.bottom + "\n**********" + barwidth + ", " + barHeight);	
			canvas.drawRect(rect, paint);
		
			int fontHeight = (int) (textPaint.getTextSize() / textRate);
			int textBaseY = barHeight - (barHeight - fontHeight) / 2;
		
			canvas.drawText(rate+"%", rect.right + textMarginLeft, textBaseY, textPaint);	
		} else {
			setRectWidth((int)rateCount);
			canvas.drawRect(rect, paint);
			DebugConfig.showLog("statics_bar", "============ " + rect.right + ", " + rect.height());		
			
			int fontHeight = (int) (textPaint.getTextSize() / textRate);
			int textBaseY = barHeight - (barHeight - fontHeight) / 2;
		
			canvas.drawText((int)rateCount+"%", rect.right + textMarginLeft, textBaseY, textPaint);
			invalidate();
		}		
			
	}
	
	/**
	 * ���ðٷֱȣ�ͳ����������ʾΪ�ý��
	 * @param rate
	 */
	public void setRate(int rate) {
		this.rate = rate;
		rect.bottom = barHeight;
		setRectWidth(rate);
		anmiState = false;
		invalidate();
	}
	
	public void setRate(int rate, int position) {
		int colorIndex = position % DEFAULT_COLOR.length;
		setBarColor(DEFAULT_COLOR[colorIndex]);
		setRate(rate);
	}
	
	/**
	 * ���İٷֱ�����
	 * @param rate
	 */
	public void setRateWithAnim(int rate, int type) {
		this.rate = rate;
		timeCount = 0.0f;
		anmiState = true;
		rateCount = 0.0f;
		rect.right = 0;
		rect.bottom = 20;
		post(this);
		invalidate();
		animType = type;
		
		if(type == ANIM_TYPE_FIXED) {					
			rateInvterval = (float) rate / animTime;		
		} else {
			rateInvterval = 0.6f;
		}
	}
	
	public void setRateWithAnim(int rate, int type, int position) {
		int colorIndex = position % DEFAULT_COLOR.length;
		setBarColor(DEFAULT_COLOR[colorIndex]);
		setRateWithAnim(rate, type);
	}
	
	private void setRectWidth(int rate) {
		rect.right = (int) ((barwidth - textMarginLeft - textPaint.getTextSize() * 3) * rate / 100) ;	
	}
			
	/**
	 * ����ͳ��������ɫ
	 * @param color
	 */
	public void setBarColor(int color) {
		paint.setColor(color);
	}
	
	/**
	 * ���ðٷֱ��������ɫ
	 * @param color
	 */
	public void setTextColor(int color) {
		textPaint.setColor(color);
	}
		
	@Override
	public void run() {
		if(animType == ANIM_TYPE_FIXED) {
			if(timeCount < animTime) {
				timeCount += animInterval;
				rateCount = timeCount * rateInvterval;
				if(rateCount > rate) {
					rateCount = rate;
				}
				postDelayed(this, (int)animInterval);
			}
		} else {
			rateCount += rateInvterval;
			if(rateCount > rate) {
				rateCount = rate;
			}
			postDelayed(this, (int)animInterval);
		}
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}
			
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measuredHeight = measureHeight(heightMeasureSpec);
		int measuredWidth = measureWidth(widthMeasureSpec);		
		setMeasuredDimension(measuredWidth, measuredHeight);
		barwidth = measuredWidth;
		barHeight = measuredHeight;
		DebugConfig.showLog("statics_bar", measuredWidth+":width");
		DebugConfig.showLog("statics_bar", measuredHeight+":height");
	}
	
	private int measureWidth(int widthMeasureSpec) {
		int specMode = MeasureSpec.getMode(widthMeasureSpec);
		int specSize = MeasureSpec.getSize(widthMeasureSpec);		
		return specSize;
	}
	
	private int measureHeight(int heightMeasureSpec) {
		int specMode = MeasureSpec.getMode(heightMeasureSpec);
		int specSize = MeasureSpec.getSize(heightMeasureSpec);
		return specSize;
	}
}
