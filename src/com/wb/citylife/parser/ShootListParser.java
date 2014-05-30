package com.wb.citylife.parser;


import com.wb.citylife.bean.ShootList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ShootListParser {
	public ShootList parse(String resultStr) {		
		
		Gson gson = new Gson();
		ShootList data = gson.fromJson(resultStr, new TypeToken<ShootList>(){}.getType());
		
		return data;
	}
}