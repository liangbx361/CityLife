package com.wb.citylife.parser;


import com.wb.citylife.bean.Avatar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AvatarParser {
	public Avatar parse(String resultStr) {		
		
		Gson gson = new Gson();
		Avatar data = gson.fromJson(resultStr, new TypeToken<Avatar>(){}.getType());
		
		return data;
	}
}