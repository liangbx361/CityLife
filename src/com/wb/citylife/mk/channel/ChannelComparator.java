package com.wb.citylife.mk.channel;

import java.util.Comparator;

import com.wb.citylife.bean.db.DbChannel;

public class ChannelComparator implements Comparator<DbChannel> {

	@Override
	public int compare(DbChannel channel1, DbChannel channel2) {
		int flag = 0;
		if(channel1.isAdd && channel2.isAdd || !channel1.isAdd && !channel2.isAdd) {
			if(channel1.weight < channel2.weight) {
				flag = -1;
			} else {
				flag = 1;
			}
		} else {
			 if(channel1.isAdd) {
				flag = -1;
			} else {
				flag = 1;
			}
		}
		return flag;
	}
	
}
