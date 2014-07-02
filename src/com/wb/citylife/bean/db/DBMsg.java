package com.wb.citylife.bean.db;

import com.wb.citylife.config.DbConfig;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

@Table(name = DbConfig.TN_MSG)
public class DBMsg {
	
	@Id(column = "id")
	public int id;
	
	//消息ID
	public String msgId;
		
	//消息类型
	public int type;
		
	//标题
	public String title;
		
	//描述
	public String desc;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
		
}
