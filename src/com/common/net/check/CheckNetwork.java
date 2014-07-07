package com.common.net.check;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckNetwork {
	
	/**
	 * 获取当前的网络类型
	 * @param context
	 * @return
	 */
	public static int getNetWorkType(Context context) {
		ConnectivityManager connectMgr = (ConnectivityManager) context.
				getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		 NetworkInfo info = connectMgr.getActiveNetworkInfo();
		 return info.getType();
	}
	
	/**
	 * 判断是否为Wifi状态
	 * @param context
	 * @return
	 */
	public static boolean checkWifi(Context context) {
		int type = getNetWorkType(context);
		return type == ConnectivityManager.TYPE_WIFI ? true : false;
	}
}
