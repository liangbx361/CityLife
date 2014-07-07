package com.wb.citylife.db;

import net.tsz.afinal.FinalDb.DbUpdateListener;
import android.database.sqlite.SQLiteDatabase;

import com.wb.citylife.config.DbConfig;

/**
 * 数据库版本更新处理类，主要负责对数据库表进行修改
 * @author liangbx
 *
 */
public class DbUpdateHandler implements DbUpdateListener {

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		if(oldVersion == 1 && newVersion == 2) {
			db.execSQL("DROP TABLE " + DbConfig.TN_SCROLL_NEWS);
		}
	}

}
