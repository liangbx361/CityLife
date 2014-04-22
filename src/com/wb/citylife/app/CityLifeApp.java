package com.wb.citylife.app;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.utils.Utils;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.common.net.volley.cache.VolleyImageCache;
import com.wb.citylife.config.DbConfig;
import com.wb.citylife.config.DebugConfig;
import com.wb.citylife.db.DbUpdateHandler;

import android.app.Application;

public class CityLifeApp extends Application {
	
	//APP实例
	private static CityLifeApp mApp;
	
	//网络
	private RequestQueue mRequestQueue;	
	private ImageLoader mImageLoader;
	private final VolleyImageCache mImageCache = new VolleyImageCache(4*1024*1024);	
	
	//数据库
	private FinalDb mFinalDb;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mApp = this;
		
		//请求队列实例化
		mRequestQueue = Volley.newRequestQueue(getApplicationContext(), 
				Utils.getDiskCacheDir(getApplicationContext(), "volley").getAbsolutePath(), null);
		mImageLoader = new ImageLoader(mRequestQueue, mImageCache);
		
		//创建数据库
		mFinalDb = FinalDb.create(this, DbConfig.DB_NAME, DebugConfig.SHOW_DEBUG_MESSAGE, 
						DbConfig.DB_VERSION, new DbUpdateHandler());
	}
	
	/**
	 * 获取APP实例
	 * @return
	 */
	public synchronized static CityLifeApp getInstance() {
		return mApp;
	}	
	
	/**
	 * 获取网络请求队列
	 * @return
	 */
	public RequestQueue getRequestQueue() {
		return mRequestQueue;
	}
	
	/**
	 * 获取图片加载者
	 * @return
	 */
	public ImageLoader getImageLoader() {
        return mImageLoader;
    }
	
	/**
	 * 获取数据库对象
	 * @return
	 */
	public FinalDb getDb() {
		return mFinalDb;
	}	
}
