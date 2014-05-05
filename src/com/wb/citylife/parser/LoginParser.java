package com.wb.citylife.parser;


import com.wb.citylife.bean.Login;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LoginParser {
	public Login parse(String resultStr) {		
		
		Gson gson = new Gson();
		Login data = gson.fromJson(resultStr, new TypeToken<Login>(){}.getType());
		
		return data;
	}
}