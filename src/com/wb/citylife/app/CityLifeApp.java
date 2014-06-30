package com.wb.citylife.app;

import java.util.List;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.utils.Utils;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.baidu.frontia.FrontiaApplication;
import com.common.device.DeviceUuidFactory;
import com.common.net.volley.cache.VolleyImageCache;
import com.wb.citylife.bean.db.DbChannel;
import com.wb.citylife.bean.db.User;
import com.wb.citylife.config.DbConfig;
import com.wb.citylife.config.DebugConfig;
import com.wb.citylife.db.DbUpdateHandler;

import android.app.Application;

public class CityLifeApp extends FrontiaApplication {
	
	//APP实例
	private static CityLifeApp mApp;
	
	//网络
	private RequestQueue mRequestQueue;	
	private ImageLoader mImageLoader;
	private final VolleyImageCache mImageCache = new VolleyImageCache(4*1024*1024);
	private long requestTag = 0;
	
	//数据库
	private FinalDb mFinalDb;
	
	//用户
	private User mUser;
	
	//手机唯一标识
	private String mPhoneId;
	
	//栏目列表
	private List<DbChannel> mChannels;
	
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
		
		//初始化用户信息
		List<User> userList = mFinalDb.findAllByWhere(User.class, "isLogin=1");
		if(userList != null && userList.size() > 0) {
			mUser = userList.get(0);			
		}
		
		//获取手机唯一标识符
		DeviceUuidFactory device = new DeviceUuidFactory(this);
		mPhoneId = device.getDeviceUuid().toString();
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
	
	/**
	 * 获取请求标签(保证每次返回的都不重复)
	 * @return
	 */
	public synchronized String getRequestTag() {
		requestTag++;
		return requestTag+"";
	}
	
	/**
	 * 获取用户信息
	 * @return
	 */
	public User getUser() {
		return mUser;
	}
	
	/**
	 * 设置用户信息
	 * @param user
	 */
	public void setUser(User user) {
		this.mUser = user;
	}
	
	/**
	 * 获取栏目列表
	 * @return
	 */
	public List<DbChannel> getChannels() {
		return mChannels;
	}
	
	/**
	 * 设置栏目列表
	 * @param channels
	 */
	public void setChannels(List<DbChannel> channels) {
		this.mChannels = channels;
	}
	
	/**
	 * 判断用户是否登录
	 * @return
	 */
	public boolean checkLogin() {
		if(mUser != null && mUser.isLogin > 0) return true;
		else return false;
			
	}
	
	/**
	 * 获取手机唯一标识符
	 * @return
	 */
	public String getPhoneId() {
		return mPhoneId;
	}
}
