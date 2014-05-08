package com.wb.citylife.parser;


import com.wb.citylife.bean.Collect;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CollectParser {
	public Collect parse(String resultStr) {		
		
		Gson gson = new Gson();
		Collect data = gson.fromJson(resultStr, new TypeToken<Collect>(){}.getType());
		
		return data;
	}
}