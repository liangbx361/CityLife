package com.wb.citylife.parser;


import com.wb.citylife.bean.EstateDetail;
import com.wb.citylife.bean.ImagesItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class EstateDetailParser {
	public EstateDetail parse(String resultStr) {		
		
		Gson gson = new Gson();
		EstateDetail data = gson.fromJson(resultStr, new TypeToken<EstateDetail>(){}.getType());
		for(ImagesItem item : data.imagesUrl) {
			item.imageNum = item.images.length;
		}
		return data;
	}
}