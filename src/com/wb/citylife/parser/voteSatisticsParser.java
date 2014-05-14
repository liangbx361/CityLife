package com.wb.citylife.parser;


import com.wb.citylife.bean.VoteSatistics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class voteSatisticsParser {
	public VoteSatistics parse(String resultStr) {		
		
		Gson gson = new Gson();
		VoteSatistics data = gson.fromJson(resultStr, new TypeToken<VoteSatistics>(){}.getType());
		
		return data;
	}
}