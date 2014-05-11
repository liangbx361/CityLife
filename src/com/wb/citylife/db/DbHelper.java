package com.wb.citylife.db;

import java.util.Collections;
import java.util.List;

import net.tsz.afinal.FinalDb;

import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.db.DbChannel;
import com.wb.citylife.bean.db.User;
import com.wb.citylife.mk.channel.ChannelComparator;

public class DbHelper {
	
	/**
	 * 判断用户是否存在
	 * @param user
	 * @return
	 */
	public static boolean checkUserExits(User user) {
		FinalDb finalDb = CityLifeApp.getInstance().getDb();
		List<User> userList = finalDb.findAll(User.class, "userId='" + user.userId + "'" );
		if(userList != null && userList.size() > 0)
			return true;
		return false;
	}
	
	/**
	 * 保存用户信息
	 * @param user
	 */
	public static void saveUser(User user) {
		FinalDb finalDb = CityLifeApp.getInstance().getDb();
		if(checkUserExits(user)) {
			finalDb.update(user, "userId='" + user.userId + "'");
		} else {
			finalDb.save(user);
		}
	}
	
	/**
	 * 对栏目进行排序
	 * @param list
	 */
	public static void orderChannel(List<DbChannel> list) {
		FinalDb finalDb = CityLifeApp.getInstance().getDb();
		Collections.sort(list, new ChannelComparator());
		for(int i=0; i<list.size(); i++) {
			DbChannel channel = list.get(i);
			channel.weight = i;			
			finalDb.update(channel, "channelId='" + channel.channelId + "'");
		}
	}		
}
