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
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.adapter.VoteAdapter.QuestionAdapter.ViewHolder;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.VoteDetail;
import com.wb.citylife.bean.VoteDetail.QuestionItem;
import com.wb.citylife.bean.VoteSatistics;
import com.wb.citylife.config.DebugConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.config.VoteType;
import com.wb.citylife.mk.vote.VoteDetailActivity;
import com.wb.citylife.task.voteSatisticsRequest;
import com.wb.citylife.widget.StatisticsBar;
import com.wb.citylife.widget.TouchControllViewPager;

public class VoteAdapter extends PagerAdapter implements OnItemClickListener, OnClickListener{
	
	private Context mContext;
	private VoteDetail mVoteDetail;
	private TouchControllViewPager mViewPager;
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
	private int submitIndex;
	private boolean submited = false;
	private boolean stopMove = false;
	private String detailId;
	
	private List<SubmitAnswer> answerList = new ArrayList<VoteAdapter.SubmitAnswer>();
	
	public VoteAdapter(Context context, TouchControllViewPager viewPager, CirclePageIndicator voteIndicator, 
			Button submitBtn, VoteDetail voteDetail, String detailId) {
		mContext = context;
		mViewPager = viewPager;
		mSubmitBtn = submitBtn;
		mVoteDetail = voteDetail;	
		voteDetailActivity = (VoteDetailActivity) mContext;
		this.detailId = detailId;
		
		for(int i=0; i<mVoteDetail.datas.size(); i++) {
			QuestionItem qItem = mVoteDetail.datas.get(i);				
			mViewList.add(initView(qItem, i+1));
			
			List<Int> cbList = new ArrayList<Int>();
			mCheckList.add(cbList);
			if((qItem.questionType == VoteType.VOTE_TYPE_MULTIPLE || 
					qItem.questionType == VoteType.VOTE_TYPE_SINGLE) && !qItem.hasVote) {
				for(int j=0; j<qItem.questionOptions.length; j++) {
					cbList.add(new Int(0));
				}
			}
			animList.add(new Bool(false));
		}
		
		//判断首次进入时，第一条问题是否已经投过票
		if(mVoteDetail.datas.size() > 0) {
			QuestionItem qItem = mVoteDetail.datas.get(0);
			if(qItem.hasVote) {
				mSubmitBtn.setVisibility(View.GONE);
			} else {
				if(mVoteDetail.datas.size() == 1) {
					mSubmitBtn.setText(R.string.submit_result);
				}
				mViewPager.setMove(false);
			}
		}
		mSubmitBtn.setOnClickListener(this);
		voteIndicator.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				QuestionItem qItem = mVoteDetail.datas.get(position);
				if(qItem.hasVote) {
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
			public void onPageScrollStateChanged(int state) {
				if(state == 0 && stopMove) {
					stopMove = false;
					mViewPager.setMove(false);
				}
			}
		});
	}
	
	private View initView(QuestionItem qItem, int index) {
		View view;
		if(qItem.questionType == VoteType.VOTE_TYPE_OPINION) {
			view = LayoutInflater.from(mContext).inflate(R.layout.vote_type_feedback, null);
			FeedbackViewHolder viewHolder = new FeedbackViewHolder();
			viewHolder.titleTv = (TextView) view.findViewById(R.id.title);
			viewHolder.titleTv.setText(qItem.questionTitle);
			viewHolder.indexTv = (TextView) view.findViewById(R.id.index);
			viewHolder.indexTv.setText("Q" + index + " / Q" + mVoteDetail.datas.size());
			viewHolder.opinionEt = (EditText) view.findViewById(R.id.opinion);
			viewHolder.answerTv = (TextView) view.findViewById(R.id.answer);
			view.setTag(viewHolder);
			
			if(qItem.hasVote) {
				viewHolder.opinionEt.setVisibility(View.GONE);
				viewHolder.answerTv.setVisibility(View.VISIBLE);
				viewHolder.answerTv.setText(qItem.result[0]);
			}
			mAdapterList.add(null);
		} else {
			view = LayoutInflater.from(mContext).inflate(R.layout.vote_type_select_layout, null);
			TextView titleTv = (TextView) view.findViewById(R.id.title);
			titleTv.setText(qItem.questionTitle);
			TextView indexTv = (TextView) view.findViewById(R.id.index);
			if(qItem.questionType == VoteType.VOTE_TYPE_SINGLE) {
				indexTv.setText("(单选题:" + qItem.questionOptions.length + "选项)Q" + index + " / Q" + mVoteDetail.datas.size());
			} else {
				indexTv.setText("(多选题:" + qItem.questionOptions.length + "选项)Q" + index + " / Q" + mVoteDetail.datas.size());
			}
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
			if(qItem.questionOptions == null)
				return 0;
			else
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
//				if(qItem.questionType == VoteType.VOTE_TYPE_MULTIPLE) {
//					holder.voteCb.setVisibility(View.VISIBLE);
//				} else {
//					holder.voteCb.setVisibility(View.INVISIBLE);
//				}
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
				
				int page = mVoteDetail.datas.indexOf(qItem);
				List<Int> checkList = mCheckList.get(page);
				if(checkList.get(position).value == 1) {
					holder.voteCb.setChecked(true);
				} else {
					holder.voteCb.setChecked(false);
				}
			} else {
				//显示投票结果
				holder.optionGroup.setVisibility(View.GONE);
				holder.resultGroup.setVisibility(View.VISIBLE);				
				String option = (position + 1) + ". " +  qItem.questionOptions[position];
				holder.option2Tv.setText(option);
				int rate = qItem.rate[position];
				int num = Integer.parseInt(qItem.result[position]);
				int itemIndex = mVoteDetail.datas.indexOf(qItem);
				Bool animBool = animList.get(itemIndex);
				if(animBool.value) {
					holder.bar.setRateWithAnim(rate, num, StatisticsBar.ANIM_TYPE_FIXED, position, animList);
				} else {
					holder.bar.setRate(rate, num, position);					
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
		if(qItem.hasVote) {
			ToastHelper.showToastInBottom(mContext, R.string.hint_has_voted);
			return;
		}
		
		if(qItem.questionType == VoteType.VOTE_TYPE_SINGLE) {
			//单选类型直接提交答案
			ViewHolder holder = (ViewHolder) view.getTag();
			List<Int> checkList = mCheckList.get(page);
			for(Int checkValue : checkList) {
				checkValue.value = 0;
			}
			checkList.get(position).value = 1;
			holder.voteCb.setChecked(true);
			mAdapterList.get(page).notifyDataSetChanged();
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
		startSatistics(mViewPager.getCurrentItem());
	}
	
	/**
	 * 执行投票统计
	 * @param position
	 */
	private void startSatistics(int position) {
		if(!CityLifeApp.getInstance().checkLogin()) {
			ToastHelper.showToastInBottom(mContext, R.string.vote_login_toast);
			return;
		}
		
		String answer = "";
		String answerId = "";
		int page = mViewPager.getCurrentItem();
		QuestionItem qItem = mVoteDetail.datas.get(page);
		
		if(qItem.hasVote) return;
		
		if(submited) return;
		
		if(qItem.questionType == VoteType.VOTE_TYPE_MULTIPLE || qItem.questionType == VoteType.VOTE_TYPE_SINGLE) {
			List<Int> checkList = mCheckList.get(page);
			for(int i=0; i<checkList.size(); i++) {
				if(checkList.get(i).value == 1) {
					answer += i + ",";
					answerId += qItem.questionOptionIds[i] + ",";
				}
			}
			
			if(!answer.equals("")) {				
				answer = answer.substring(0, answer.length()-1);
				answerId = answerId.substring(0, answerId.length()-1);
				answer = answerId;
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
		
		//保存答案
		SubmitAnswer submitAnswer = new SubmitAnswer();
		submitAnswer.qItem = qItem;
		submitAnswer.answer = answer;
		submitAnswer.answerId = answerId;
		answerList.add(submitAnswer);
		
		if(mViewPager.getCurrentItem()+1 >= mVoteDetail.datas.size()) {			
			submitIndex = 0;
			submitAnswer = answerList.get(submitIndex);
			submited = true;
			submitQItem = answerList.get(submitIndex).qItem;
			requestvoteSatistics(Method.POST, NetInterface.METHOD_VOTE_SATISTICS, 
					getVoteSatisticsRequestParams(submitAnswer.qItem, submitAnswer.answer, 
							submitAnswer.answerId), new StatisticsListener(), voteDetailActivity);
			voteDetailActivity.showDialog("正在统计投票结果中...");
		} else {
			int nextIndex = mViewPager.getCurrentItem() + 1;
			if(nextIndex >= mVoteDetail.datas.size()-1) {
				mSubmitBtn.setText(R.string.submit_result);
			}
			mViewPager.setMove(true);
			stopMove = true;
			mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
		}
	}
	
	class SubmitAnswer {
		QuestionItem qItem;
		String answer;
		String answerId;
	}
	
	/**
	 * 获取统计请求参数
	 * @return
	 */
	private Map<String, String> getVoteSatisticsRequestParams(QuestionItem qItem, String answer, String answerId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("userId", CityLifeApp.getInstance().getUser().userId);
		params.put("id", detailId);
		params.put("questionId", qItem.questionId);
		params.put("questionType", qItem.questionType+"");
		params.put("answer", answer);
		params.put("phoneId", CityLifeApp.getInstance().getPhoneId());
//		params.put("answerId", answerId);
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
			
			if(response.respCode == RespCode.SUCCESS) {
				submitQItem = answerList.get(submitIndex).qItem;
				submitQItem.hasVote = true;
				if(submitQItem.questionType == VoteType.VOTE_TYPE_SINGLE ||
						submitQItem.questionType == VoteType.VOTE_TYPE_MULTIPLE) {
					
					submitQItem.result = new String[submitQItem.questionOptions.length];
					for(int i=0; i<submitQItem.questionOptions.length; i++) {
						submitQItem.result[i] = response.result[i];
					}
					
					//将投票计接收，并转化成百分比
					submitQItem.rate = new int[submitQItem.result.length];
					int total = 0;
					for(int i=0; i<submitQItem.result.length; i++) {
						submitQItem.rate[i] = Integer.parseInt(submitQItem.result[i]);
						total += submitQItem.rate[i];
					}
					for(int i=0; i<submitQItem.rate.length; i++) {
						submitQItem.rate[i] = submitQItem.rate[i] * 100 / total;
					}
					
					animList.get(submitIndex).value = true;
					mAdapterList.get(submitIndex).notifyDataSetChanged();
					mSubmitBtn.setVisibility(View.GONE);						
				} else {
					mSubmitBtn.setVisibility(View.GONE);
					FeedbackViewHolder viewHolder = (FeedbackViewHolder) mViewList.get(submitIndex).getTag();
					viewHolder.opinionEt.setVisibility(View.GONE);
					viewHolder.answerTv.setVisibility(View.VISIBLE);
					viewHolder.answerTv.setText(response.result[0]);
				}
				
				submitIndex++;
				if(submitIndex < mVoteDetail.datas.size()) {
					SubmitAnswer submitAnswer = answerList.get(submitIndex);
					requestvoteSatistics(Method.POST, NetInterface.METHOD_VOTE_SATISTICS, 
							getVoteSatisticsRequestParams(submitAnswer.qItem, submitAnswer.answer, 
									submitAnswer.answerId), new StatisticsListener(), voteDetailActivity);
				} else {					
					voteDetailActivity.dismissDialog();
					submited = false;
					mViewPager.setCurrentItem(0);
					mViewPager.setMove(true);
				}
			} else {
				ToastHelper.showToastInBottom(mContext, response.respMsg);
				voteDetailActivity.finish();
			}						
		}
	}
	
	class Int {
		public int value;
		
		public Int (int value) {
			this.value = value;
		}
	}
	
	public class Bool {
		public boolean value;
		
		public Bool (boolean value) {
			this.value = value;
		}
	}
	
	class FeedbackViewHolder {
		TextView titleTv;
		TextView indexTv;
		EditText opinionEt;
		TextView answerTv;
	}
}
