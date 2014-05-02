package com.wb.citylife.parser;


import com.wb.citylife.bean.NewsDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class NewsDetailParser {
	public NewsDetail parse(String resultStr) {		
		
		Gson gson = new Gson();
		NewsDetail data = gson.fromJson(resultStr, new TypeToken<NewsDetail>(){}.getType());
		
		return data;
	}
}