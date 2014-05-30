package com.wb.citylife.parser;


import com.wb.citylife.bean.OldInfoDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class OldInfoDetailParser {
	public OldInfoDetail parse(String resultStr) {		
		
		Gson gson = new Gson();
		OldInfoDetail data = gson.fromJson(resultStr, new TypeToken<OldInfoDetail>(){}.getType());
		
		return data;
	}
}