package com.wb.citylife.wxapi;

import android.os.Bundle;

import com.common.widget.ToastHelper;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.umeng.socialize.view.WXCallbackActivity;

public class WXEntryActivity extends WXCallbackActivity {

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
	}

	@Override
	public void onReq(BaseReq req) {
		super.onReq(req);
	}

	@Override
	public void onResp(BaseResp resp) {
		super.onResp(resp);
		if(resp.errCode == 0) {
			ToastHelper.showToastInBottom(this, "分享成功");
		}
	}
	
}
