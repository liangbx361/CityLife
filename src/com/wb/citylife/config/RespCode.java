package com.wb.citylife.config;

/**
 * 此类用于定义响应结果码和描述
 * @author liangbx
 *
 */
public interface RespCode {
	
	public static final int SUCCESS = 0;
	
	public static final int BIND_FIAL = 1;
	
	public static final int ALREADY_BIND_STATE = 2;
	
	public static final int UNBIND_STATE = 3;
	
	/************************************************ 认证平台状态码和状态描述 **********************************/
	public static final int USERNAME_EMPTY = 1001;
	
	public static final int USERNAME_NOT_FOUND = 1002;
	public static final String USERNAME_NOT_FOUND_DESC = "用户不存在";
	
	public static final int PASSWORD_NOT_MATCH = 1003;
	public static final String PASSWORD_NOT_MATCH_DESC = "密码出错了";
	
	public static final int PASSWORD_EMPTY = 1004;
	public static final String PASSWORD_EMPTY_DESC = "密码为空";
	
	public static final int USER_STATUS_INVALID = 1005;
	public static final String USER_STATUS_INVALID_DESC = "用户已失效";
	
	public static final int ACCESSTIME_FIELD_EMPTY = 1006; 
	public static final String ACCESSTIME_FIELD_EMPTY_DESC = "访问时间为空";
	
	public static final int ACCESSTOKEN_FIELD_EMPTY = 1007;
	public static final String ACCESSTOKEN_FIELD_EMPTY_DESC = "访问令牌为空";
	
	public static final int SIGN_FIELD_EMPTY = 1008;
	public static final String SIGN_FIELD_EMPTY_DESC = "签名为空";
	
	public static final int ACCESSTIME_FORMAT_ERROR = 1009;
	public static final String ACCESSTIME_FORMAT_DESC = "访问时间格式错误";
	
	public static final int ACCESSTOKEN_INVALID = 1010;
	public static final String ACCESSTOKEN_INVALID_DESC = "访问时间失效";
	
	public static final int ACCESSTOKEN_EXPIRED = 1011;
	public static final String ACCESSTOKEN_EXPIRED_DESC = "访问令牌过期";
	
	public static final int SERVER_INTERNAL_ERROR = 2001;
	public static final String SERVER_INTERNAL_ERROR_DESC = "服务器内部错误";		
}
