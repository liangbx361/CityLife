package com.wb.citylife.mk.main;

public interface MainListener {
	
	/**
	 * 设置主页的监听器
	 * @param listener
	 */
	public void setHomeListener(HomeListener listener);
	
	/**
	 * 设置个人中心监听器
	 * @author listener
	 */
	public void setMyCenter(MyCenterListener listener);
}
