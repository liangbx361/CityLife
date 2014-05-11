package com.wb.citylife.parser;


import com.wb.citylife.bean.Register;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class RegisterParser {
	public Register parse(String resultStr) {		
		
		Gson gson = new Gson();
		Register data = gson.fromJson(resultStr, new TypeToken<Register>(){}.getType());
		
		return data;
	}
}