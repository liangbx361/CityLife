package com.wb.citylife.parser;


import com.wb.citylife.bean.Comment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CommentParser {
	public Comment parse(String resultStr) {		
		
		Gson gson = new Gson();
		Comment data = gson.fromJson(resultStr, new TypeToken<Comment>(){}.getType());
		
		return data;
	}
}