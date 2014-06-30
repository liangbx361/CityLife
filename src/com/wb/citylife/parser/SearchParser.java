package com.wb.citylife.parser;


import com.wb.citylife.bean.Search;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SearchParser {
	public Search parse(String resultStr) {		
		
		Gson gson = new Gson();
		Search data = gson.fromJson(resultStr, new TypeToken<Search>(){}.getType());
		
		return data;
	}
}