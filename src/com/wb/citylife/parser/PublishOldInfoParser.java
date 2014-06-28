package com.wb.citylife.parser;


import com.wb.citylife.bean.PublishOldInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PublishOldInfoParser {
	public PublishOldInfo parse(String resultStr) {		
		
		Gson gson = new Gson();
		PublishOldInfo data = gson.fromJson(resultStr, new TypeToken<PublishOldInfo>(){}.getType());
		
		return data;
	}
}