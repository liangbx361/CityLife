package com.wb.citylife.mk.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class CommDrawable {
	
	public static Drawable getFavDrawable(Context context, int resId, int favourNum) {
		WindowManager wm = (WindowManager) (context.getSystemService(Context.WINDOW_SERVICE));
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
		Bitmap newBmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		Canvas canvas = new Canvas(newBmp);
		Paint textPaint = new Paint();
		textPaint.setTypeface(Typeface.MONOSPACE);
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(10 * dm.density);
		textPaint.setColor(0xff5c5c5c);
		canvas.drawText(favourNum+"", 23.33f * dm.density, 10.67f * dm.density, textPaint);
		Drawable drawable = new BitmapDrawable(context.getResources(), newBmp);
		
		return drawable;
	}	
}
