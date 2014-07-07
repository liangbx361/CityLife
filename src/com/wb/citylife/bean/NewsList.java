package com.wb.citylife.bean;

import java.util.List;

public class NewsList extends BaseBean{

    public boolean hasNextPage;
    public int totalNum;
    public List<NewsItem> datas;
    		
    public class NewsItem { 
    	public String id;
    	public int type;
    	public String title;
    	public String summary;
    	public String thumbnailUrl;
    	public String time;
    	public int commentNum;
    	public int clickNum;
    }
	
}
