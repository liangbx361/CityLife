package com.wb.citylife.bean;

import java.util.List;

public class OldInfoList extends BaseBean{

    public boolean hasNextPage;
    public int totalNum;
    public List<OldInfoItem> datas;
    		
    public class OldInfoItem { 
    	public String id;
    	public String title;
    	public String time;
    	public String summary;
    	public int price;
    	public String thumbnailUrl;
    }
	
}
