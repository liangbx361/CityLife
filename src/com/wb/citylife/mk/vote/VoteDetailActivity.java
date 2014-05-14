package com.wb.citylife.mk.vote;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Text;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.viewpagerindicator.CirclePageIndicator;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.adapter.VoteAdapter;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.common.net.volley.VolleyErrorHelper;
import com.common.widget.ToastHelper;
import com.wb.citylife.bean.VoteDetail;
import com.wb.citylife.bean.VoteSatistics;
import com.wb.citylife.task.VoteDetailRequest;
import com.wb.citylife.task.voteSatisticsRequest;

public class VoteDetailActivity extends BaseActivity implements Listener<VoteDetail>, ErrorListener{
	
	private TextView voteTitleTv;
	private NetworkImageView imgIv;
	private TextView descTv;
	private TextView timeTv;
	private TextView numTv;
	private ViewPager mVotePager;
	private Button mSubmitBtn;
	private CirclePageIndicator mVoteIndicator;
	
	private String voteId;
	
	//投票详情
	private VoteDetailRequest mVoteDetailRequest;
	private VoteDetail mVoteDetail;
	private VoteAdapter mVoteAdapter;
			
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_votedetail);
		
		getIntentData();
		initView();				
	}
			
	@Override
	public void getIntentData() {
		voteId = getIntent().getStringExtra(IntentExtraConfig.DETAIL_ID);
	}
	
	@Override
	public void initView() {
		voteTitleTv = (TextView) findViewById(R.id.vote_title);
		imgIv = (NetworkImageView) findViewById(R.id.img);
		descTv = (TextView) findViewById(R.id.desc);
		timeTv = (TextView) findViewById(R.id.time);
		numTv = (TextView) findViewById(R.id.num);
		mVotePager = (ViewPager) findViewById(R.id.vote_pager);
		mVoteIndicator = (CirclePageIndicator) findViewById(R.id.vote_indicator);
		mSubmitBtn = (Button) findViewById(R.id.submit);			
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
		
		requestVoteDetail(Method.POST, NetInterface.METHOD_VOTE_DETAIL, 
				getVoteDetailRequestParams(), this, this);
		setIndeterminateBarVisibility(true);		
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * 菜单点击处理
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {			
		return super.onOptionsItemSelected(item);
	}
		
	/**
	 * 获取详情请求参数
	 * @return
	 */
	private Map<String, String> getVoteDetailRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
		if(CityLifeApp.getInstance().checkLogin()) {
			params.put("userId", CityLifeApp.getInstance().getUser().userId);
		}
		params.put("id", voteId);
		return params;
	}
	
	/**
	 * 执行详情任务请求
	 * @param method
	 * @param url
	 * @param params
	 * @param listenre
	 * @param errorListener
	 */	
	private void requestVoteDetail(int method, String methodUrl, Map<String, String> params,	 
			Listener<VoteDetail> listenre, ErrorListener errorListener) {			
		if(mVoteDetailRequest != null) {
			mVoteDetailRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mVoteDetailRequest = new VoteDetailRequest(method, url, params, listenre, errorListener);
		startRequest(mVoteDetailRequest);		
	}
	
	/**
	 * 网络请求错误处理
	 */
	@Override
	public void onErrorResponse(VolleyError error) {		
		setIndeterminateBarVisibility(false);
		ToastHelper.showToastInBottom(getApplicationContext(), VolleyErrorHelper.getErrorMessage(error));
	}
	
	/**
	 * 请求详情完成，处理UI更新
	 */
	@Override
	public void onResponse(VoteDetail response) {		
		setIndeterminateBarVisibility(false);
		mVoteDetail = response;
		if(mVoteDetail.respCode == RespCode.SUCCESS) {
			voteTitleTv.setText(mVoteDetail.title);
			imgIv.setImageUrl(mVoteDetail.thumbnailUrl, CityLifeApp.getInstance().getImageLoader());
			imgIv.setDefaultImageResId(R.drawable.base_list_default_icon);
			descTv.setText(mVoteDetail.summary);
			timeTv.setText(mVoteDetail.time);
			numTv.setText(mVoteDetail.participantNum + "人参与");		
			
			mVoteAdapter = new VoteAdapter(this, mVotePager, mVoteIndicator, mSubmitBtn, mVoteDetail);
			mVotePager.setAdapter(mVoteAdapter);
			mVoteIndicator.setViewPager(mVotePager);
		} 
	}
		
}
