package com.wb.citylife.parser;


import com.wb.citylife.bean.NewsList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class NewsListParser {
	public NewsList parse(String resultStr) {		
		
		Gson gson = new Gson();
		NewsList data = gson.fromJson(resultStr, new TypeToken<NewsList>(){}.getType());
		
		return data;
	}
}