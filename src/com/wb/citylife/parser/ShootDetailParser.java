package com.wb.citylife.parser;


import com.wb.citylife.bean.ShootDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ShootDetailParser {
	public ShootDetail parse(String resultStr) {		
		
		Gson gson = new Gson();
		ShootDetail data = gson.fromJson(resultStr, new TypeToken<ShootDetail>(){}.getType());
		
		return data;
	}
}