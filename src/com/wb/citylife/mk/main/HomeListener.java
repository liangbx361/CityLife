package com.wb.citylife.mk.main;

import java.util.List;

import com.wb.citylife.bean.db.DbChannel;

public interface HomeListener {
	
	/**
	 * 栏目数据监听
	 * @param channel 栏目数据
	 * @param isAdd 是否为增加数据状态
	 */
	public void onChannelComplete(List<DbChannel> channelList);
}
