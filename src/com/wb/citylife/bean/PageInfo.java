package com.wb.citylife.bean;

public class PageInfo {
	
	/**
	 * 默认一页的大小为10条数据
	 */
	public static final int DEFAULT_PAGE_SIZE = 10;
	
	public int pageSize;
	
	public int pageNo;
	
	public PageInfo() {
		pageSize = DEFAULT_PAGE_SIZE;
		pageNo = 1;
	}
	
	public PageInfo(int pageSize, int pageNo) {
		this.pageSize = pageSize;
		this.pageNo = pageNo;
	}
}
