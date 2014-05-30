package com.wb.citylife.bean;

import java.util.List;

public class ShootList extends BaseBean{

    public boolean hasNextPage;
    public int totalNum;
    public List<ShootItem> datas;
    		
    public class ShootItem { 
    	public String id;
    	public String title;
    	public String time;
    	public String name;
    	public String avatarUrl;
    	public String thumbnailUrl;
    	public int commentNum;
    	public int clickNum;
    }
	
}
