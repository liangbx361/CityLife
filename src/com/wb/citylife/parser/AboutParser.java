package com.wb.citylife.parser;


import com.wb.citylife.bean.About;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AboutParser {
	public About parse(String resultStr) {		
		
		Gson gson = new Gson();
		About data = gson.fromJson(resultStr, new TypeToken<About>(){}.getType());
		
		return data;
	}
}