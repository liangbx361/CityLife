package com.wb.citylife.mk.push;

import java.util.List;

import net.tsz.afinal.FinalDb;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;
import com.google.gson.Gson;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.OldInfoDetail;
import com.wb.citylife.bean.PushType;
import com.wb.citylife.bean.ShootDetail;
import com.wb.citylife.bean.VoteDetail;
import com.wb.citylife.bean.db.DBMsg;
import com.wb.citylife.config.ChannelType;
import com.wb.citylife.config.DebugConfig;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.mk.common.CommIntent;
import com.wb.citylife.mk.estate.EstateDetailActivity;
import com.wb.citylife.mk.news.NewsDetailActivity;

public class CityLifePushReceiver extends FrontiaPushMessageReceiver {
	
	/** TAG to Log */
    public static final String TAG = CityLifePushReceiver.class.getSimpleName();
	
	@Override
	public void onBind(Context context, int errorCode, String appid,
            String userId, String channelId, String requestId) {
		
		// 绑定成功，设置已绑定flag，可以有效的减少不必要的绑定请求
        if (errorCode == 0) {
            PushUtils.setBind(context, true);
        }
	}

	@Override
	public void onDelTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {

	}

	@Override
	public void onListTags(Context context, int errorCode, List<String> tags,
            String requestId) {
		
	}
	
	/**
     * 接收透传消息的函数。
     * 
     * @param context
     *            上下文
     * @param message
     *            推送的消息
     * @param customContentString
     *            自定义内容,为空或者json字符串
     */
	@Override
	public void onMessage(Context context, String message, String customContentString) {
		DebugConfig.showLog(TAG, "message=" + message);
	}
	
	/**
     * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
     * 
     * @param context
     *            上下文
     * @param title
     *            推送的通知的标题
     * @param description
     *            推送的通知的描述
     * @param customContentString
     *            {"id":"1", "type":1}
     */
	@Override
	public void onNotificationClicked(Context context, String title,
            String description, String customContentString) {
		//处理自定义点击
		DebugConfig.showLog(TAG, "onNotificationClicked[title" + title + ", description=" + description + ", customContentString=" + customContentString + "]");
		
		if(!TextUtils.isEmpty(customContentString)) {
			Gson gson = new Gson();
			PushType pushType = gson.fromJson(customContentString, PushType.class);
			CommIntent.startDetailPage(context, pushType.id, pushType.type);
			
			//存储在数据库中
			FinalDb fDb = CityLifeApp.getInstance().getDb();
			DBMsg msg = new DBMsg();
			msg.setMsgId(pushType.id);
			msg.setType(pushType.type);
			msg.setTitle(title);
			msg.setDesc(description);
			fDb.save(msg);
		}
	}

	@Override
	public void onSetTags(Context arg0, int arg1, List<String> arg2,
			List<String> arg3, String arg4) {


	}
	
	@Override
	public void onUnbind(Context context, int errorCode, String requestId) {
		// 解绑定成功，设置未绑定flag，
        if (errorCode == 0) {
            PushUtils.setBind(context, false);
        }
	}

}
