package com.wb.citylife.parser;


import com.wb.citylife.bean.WelcomeAdv;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class WelcomeAdvParser {
	public WelcomeAdv parse(String resultStr) {		
		
		Gson gson = new Gson();
		WelcomeAdv data = gson.fromJson(resultStr, new TypeToken<WelcomeAdv>(){}.getType());
		
		return data;
	}
}