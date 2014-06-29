package com.wb.citylife.parser;


import com.wb.citylife.bean.Publish;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PublishParser {
	public Publish parse(String resultStr) {		
		
		Gson gson = new Gson();
		Publish data = gson.fromJson(resultStr, new TypeToken<Publish>(){}.getType());
		
		return data;
	}
}