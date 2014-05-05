package com.wb.citylife.db;

import java.util.List;

import net.tsz.afinal.FinalDb;

import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.db.User;

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
			finalDb.update(user);
		} else {
			finalDb.save(user);
		}
	}
}
