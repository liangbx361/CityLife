package com.wb.citylife.parser;


import com.wb.citylife.bean.EstateList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class EstateListParser {
	public EstateList parse(String resultStr) {		
		
		Gson gson = new Gson();
		EstateList data = gson.fromJson(resultStr, new TypeToken<EstateList>(){}.getType());
		
		return data;
	}
}