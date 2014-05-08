package com.wb.citylife.parser;


import com.wb.citylife.bean.ScrollNews;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ScrollNewsParser {
	public ScrollNews parse(String resultStr) {		
		
		Gson gson = new Gson();
		ScrollNews data = gson.fromJson(resultStr, new TypeToken<ScrollNews>(){}.getType());
		
		return data;
	}
}