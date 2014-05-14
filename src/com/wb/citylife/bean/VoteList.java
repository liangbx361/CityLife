package com.wb.citylife.bean;

import java.util.List;

public class VoteList extends BaseBean{

    public boolean hasNextPage;
    public int totalNum;
    public List<VoteItem> datas;
    		
    public class VoteItem { 
    	public String id;
    	public String title;
    	public String summary;
    	public String thumbnailurl;
    	public String time;
    	public int commentNum;
    	public int clickNum;
    }
	
}
