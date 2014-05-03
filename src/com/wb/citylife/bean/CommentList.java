package com.wb.citylife.bean;

import java.util.List;

public class CommentList extends BaseBean{

    public boolean hasNextPage;
    public int totalNum;
    public List<CommentItem> datas;
    		
    public class CommentItem { 
    	public String userId;
    	public String username;
    	public String time;
    	public String comment;
    }
	
}
