package com.wb.citylife.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.common.widget.ToastHelper;
import com.viewpagerindicator.CirclePageIndicator;
import com.wb.citylife.R;
import com.wb.citylife.adapter.VoteAdapter.QuestionAdapter.ViewHolder;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.VoteDetail;
import com.wb.citylife.bean.VoteSatistics;
import com.wb.citylife.bean.VoteDetail.QuestionItem;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.config.VoteType;
import com.wb.citylife.mk.vote.VoteDetailActivity;
import com.wb.citylife.task.voteSatisticsRequest;
import com.wb.citylife.widget.StatisticsBar;

public class VoteAdapter extends PagerAdapter implements OnItemClickListener, OnClickListener{
	
	private Context mContext;
	private VoteDetail mVoteDetail;
	private ViewPager mViewPager;
	private Button mSubmitBtn;
	private VoteDetailActivity voteDetailActivity;
		
	private List<View> mViewList = new ArrayList<View>();
	private List<QuestionAdapter> mAdapterList = new ArrayList<VoteAdapter.QuestionAdapter>();
	private List<List<Int>> mCheckList = new ArrayList<List<Int>>(); 
	private List<Bool> animList = new ArrayList<VoteAdapter.Bool>();
	
	//投票统计
	private voteSatisticsRequest mvoteSatisticsRequest;
	private VoteSatistics mvoteSatistics;
	
	private QuestionItem submitQItem;
	
	public VoteAdapter(Context context, ViewPager viewPager, CirclePageIndicator voteIndicator, 
			Button submitBtn, VoteDetail voteDetail) {
		mContext = context;
		mViewPager = viewPager;
		mSubmitBtn = submitBtn;
		mVoteDetail = voteDetail;	
		voteDetailActivity = (VoteDetailActivity) mContext;
		
		for(int i=0; i<mVoteDetail.datas.size(); i++) {
			QuestionItem qItem = mVoteDetail.datas.get(i);				
			mViewList.add(initView(qItem));
			
			List<Int> cbList = new ArrayList<Int>();
			mCheckList.add(cbList);
			if(qItem.questionType == VoteType.VOTE_TYPE_MULTIPLE && !qItem.hasVote) {
				for(int j=0; j<qItem.questionOptions.length; j++) {
					cbList.add(new Int(0));
				}
			}
			animList.add(new Bool(false));
			
			if(i == 0) {
				if(qItem.questionType == VoteType.VOTE_TYPE_SINGLE) {
					mSubmitBtn.setVisibility(View.GONE);
				} else {					
					mSubmitBtn.setVisibility(View.VISIBLE);
				}
			}
		}
		
		mSubmitBtn.setOnClickListener(this);
		voteIndicator.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				QuestionItem qItem = mVoteDetail.datas.get(position);
				if(qItem.questionType == VoteType.VOTE_TYPE_SINGLE) {
					mSubmitBtn.setVisibility(View.GONE);
				} else {					
					mSubmitBtn.setVisibility(View.VISIBLE);
				}
				for(Bool bool : animList) {
					bool.value = false;
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
	}
	
	private View initView(QuestionItem qItem) {
		View view;
		if(qItem.questionType == VoteType.VOTE_TYPE_OPINION) {
			view = LayoutInflater.from(mContext).inflate(R.layout.vote_type_feedback, null);
			TextView titleTv = (TextView) view.findViewById(R.id.title);
			titleTv.setText(qItem.questionTitle);
		} else {
			view = LayoutInflater.from(mContext).inflate(R.layout.vote_type_select_layout, null);
			TextView titleTv = (TextView) view.findViewById(R.id.title);
			titleTv.setText(qItem.questionTitle);
			ListView voteLv = (ListView) view.findViewById(R.id.vote_list);
			QuestionAdapter adapter = new QuestionAdapter(mContext, qItem);
			voteLv.setAdapter(adapter);
			voteLv.setOnItemClickListener(this);
			mAdapterList.add(adapter);			
		}	
		
		return view;
	}
	
	@Override
	public int getCount() {		
		return mVoteDetail.datas.size();
	}
	
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
	
	@Override  
    public void destroyItem(ViewGroup container, int position,  
            Object object) {
		container.removeView(mViewList.get(position));  
	}
	
	@Override  
    public int getItemPosition(Object object) {  

		return super.getItemPosition(object);  
    } 
	
	@Override  
    public Object instantiateItem(ViewGroup container, int position) { 
		View view = mViewList.get(position);
		container.addView(view);	
		
		return view;
	}
	
	class QuestionAdapter extends BaseAdapter {
		
		private Context mContext;
		private QuestionItem qItem;
		
		public QuestionAdapter(Context context, QuestionItem qItem) {
			mContext = context;
			this.qItem = qItem;
		}
		
		@Override
		public int getCount() {
			return qItem.questionOptions.length;
		}

		@Override
		public Object getItem(int position) {
			return qItem.questionOptions[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if(convertView == null) {
				view = LayoutInflater.from(mContext).inflate(R.layout.vote_item_layout, null);
				holder = new ViewHolder();
				
				holder.optionGroup = (ViewGroup) view.findViewById(R.id.option_layout);
				holder.voteCb = (CheckBox) view.findViewById(R.id.check);				
				if(qItem.questionType == VoteType.VOTE_TYPE_MULTIPLE) {
					holder.voteCb.setVisibility(View.VISIBLE);
				}
				TextView optionTv = (TextView) view.findViewById(R.id.option);
				holder.optionTv = optionTv;
				
				holder.resultGroup = (ViewGroup) view.findViewById(R.id.result_layout);
				holder.option2Tv = (TextView) view.findViewById(R.id.option2);
				holder.bar = (StatisticsBar) view.findViewById(R.id.bar);
				
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
						
			if(!qItem.hasVote) {
				//显示投票选项
				holder.optionGroup.setVisibility(View.VISIBLE);
				holder.resultGroup.setVisibility(View.GONE);					
				String option = (position + 1) + ". " +  qItem.questionOptions[position];
				holder.optionTv.setText(option);				
			} else {
				//显示投票结果
				holder.optionGroup.setVisibility(View.GONE);
				holder.resultGroup.setVisibility(View.VISIBLE);				
				String option = (position + 1) + ". " +  qItem.questionOptions[position];
				holder.option2Tv.setText(option);
				int rate = qItem.results[position];
				int itemIndex = mVoteDetail.datas.indexOf(qItem);
				Bool animBool = animList.get(itemIndex);
				if(animBool.value) {
					holder.bar.setRateWithAnim(rate, StatisticsBar.ANIM_TYPE_FIXED, position);
				} else {
					holder.bar.setRate(rate, position);					
				}
			}
			holder.optionGroup.invalidate();
			holder.resultGroup.invalidate();
			view.requestLayout();
						
			return view;
		}
		
		class ViewHolder {			
			ViewGroup optionGroup;
			CheckBox voteCb;
			TextView optionTv;
			ViewGroup resultGroup;
			TextView option2Tv;
			StatisticsBar bar;
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		int page = mViewPager.getCurrentItem();
		QuestionItem qItem = mVoteDetail.datas.get(page);
		if(qItem.hasVote) return;
		
		if(qItem.questionType == VoteType.VOTE_TYPE_SINGLE) {
			//单选类型直接提交答案
			startSatistics(position);
		} else {
			//设置选项的状态
			ViewHolder holder = (ViewHolder) view.getTag();
			List<Int> checkList = mCheckList.get(page);
			if(checkList.get(position).value == 0) {
				checkList.get(position).value = 1;
				holder.voteCb.setChecked(true);
			} else {
				checkList.get(position).value = 0;
				holder.voteCb.setChecked(false);
			}
		}
	}
	
	/**
	 * 多选类型和意见反馈类型提交按钮
	 */
	@Override
	public void onClick(View v) {		
		startSatistics(0);
	}
	
	/**
	 * 执行投票统计
	 * @param position
	 */
	private void startSatistics(int position) {
		if(!CityLifeApp.getInstance().checkLogin()) {
			ToastHelper.showToastInBottom(mContext, "你需要先登录才能投票哦~");
			return;
		}
		
		String answer = "";
		int page = mViewPager.getCurrentItem();
		QuestionItem qItem = mVoteDetail.datas.get(page);
		
		if(qItem.hasVote) return;
		
		if(qItem.questionType == VoteType.VOTE_TYPE_SINGLE) {
			answer = position + "";
		} else if(qItem.questionType == VoteType.VOTE_TYPE_MULTIPLE) {
			List<Int> checkList = mCheckList.get(page);
			for(int i=0; i<checkList.size(); i++) {
				if(checkList.get(i).value == 1) {
					answer += i + ",";
				}
			}
			
			if(!answer.equals("")) {				
				answer = answer.substring(0, answer.length()-1);
			} else {
				ToastHelper.showToastInBottom(mContext, "请至少选择一项");
				return;			
			}
		} else {
			View view = mViewList.get(page);
			EditText opinionEt = (EditText) view.findViewById(R.id.opinion);
			answer = opinionEt.getText().toString();
			
			if(answer.equals("")) {
				ToastHelper.showToastInBottom(mContext, "请填写您的意见后再提交");
				return;
			} 						
		}
		
		submitQItem = qItem;
		voteDetailActivity.setIndeterminateBarVisibility(true);
		requestvoteSatistics(Method.POST, NetInterface.METHOD_VOTE_SATISTICS, 
				getVoteSatisticsRequestParams(qItem, answer), new StatisticsListener(), voteDetailActivity);
	}
	
	/**
	 * 获取统计请求参数
	 * @return
	 */
	private Map<String, String> getVoteSatisticsRequestParams(QuestionItem qItem, String answer) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", CityLifeApp.getInstance().getUser().userId);
		params.put("id", mVoteDetail.id);
		params.put("questionId", qItem.questionId);
		params.put("questionType", qItem.questionType+"");
		params.put("answer", answer);
		return params;
	}
	
	/**
	 * 执行统计任务请求
	 * @param method
	 * @param url
	 * @param params
	 * @param listenre
	 * @param errorListener
	 */	
	private void requestvoteSatistics(int method, String methodUrl, Map<String, String> params,	 
			Listener<VoteSatistics> listenre, ErrorListener errorListener) {			
		if(mvoteSatisticsRequest != null) {
			mvoteSatisticsRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mvoteSatisticsRequest = new voteSatisticsRequest(method, url, params, listenre, errorListener);
		voteDetailActivity.startRequest(mvoteSatisticsRequest);		
	}
	
	/**
	 * 投票统计处理
	 * @author liangbx
	 *
	 */
	class StatisticsListener implements Listener<VoteSatistics> {
		
		/**
		 * 请求完成，处理UI更新
		 */
		@Override
		public void onResponse(VoteSatistics response) {
			voteDetailActivity.setIndeterminateBarVisibility(false);
			mvoteSatistics = response;
			if(response.respCode == RespCode.SUCCESS) {
				submitQItem.hasVote = true;
				submitQItem.results = new int[submitQItem.questionOptions.length];
				for(int i=0; i<submitQItem.questionOptions.length; i++) {
					submitQItem.results[i] = response.result[i];
				}
				
				int page = mViewPager.getCurrentItem();
				animList.get(page).value = true;
				mAdapterList.get(page).notifyDataSetChanged();
				mSubmitBtn.setVisibility(View.GONE);								
			}
		}
	}
	
	class Int {
		public int value;
		
		public Int (int value) {
			this.value = value;
		}
	}
	
	class Bool {
		public boolean value;
		
		public Bool (boolean value) {
			this.value = value;
		}
	}
}
