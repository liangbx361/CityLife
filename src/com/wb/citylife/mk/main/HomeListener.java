package com.wb.citylife.mk.main;

import java.util.List;

import com.wb.citylife.bean.db.DbChannel;
import com.wb.citylife.bean.db.DbScrollNews;

public interface HomeListener {
		
	/**
	 * 加载本地的栏目数据
	 * @param channelList
	 */
	public void onLoadLocalChannel(List<DbChannel> channels);
	
	/**
	 * 加载本地滚动新闻数据
	 * @param scrollNews
	 */
	public void onLoadLocalScrollNews(List<DbScrollNews> scrollNews);
	
	/**
	 * 栏目网络数据监听
	 * @param channel 栏目数据
	 */
	public void onChannelComplete(List<DbChannel> channels);
	
	/**
	 * 加载网络滚动新闻数据
	 * @param scrollNews
	 */
	public void onScrollNewsCommplete(List<DbScrollNews> scrollNews);
}
