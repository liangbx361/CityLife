package com.wb.citylife.db;

import android.database.sqlite.SQLiteDatabase;
import net.tsz.afinal.FinalDb.DbUpdateListener;

/**
 * 数据库版本更新处理类，主要负责对数据库表进行修改
 * @author liangbx
 *
 */
public class DbUpdateHandler implements DbUpdateListener {

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
