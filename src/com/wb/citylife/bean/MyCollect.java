package com.wb.citylife.bean;

import java.util.List;

public class MyCollect extends BaseBean{

    public boolean hasNextPage;
    public int totalNum;
    public List<CollectItem> datas;
    		
    public class CollectItem { 
    	public String id;
    	public int type;
    	public String time;
    	public String title;
    	public String desc;
    	public String thumbnailUrl;
    	public boolean isVideo;
    }
	
}
