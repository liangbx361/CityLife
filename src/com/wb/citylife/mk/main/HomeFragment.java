package com.wb.citylife.mk.main;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.viewpagerindicator.LinePageIndicator;
import com.wb.citylife.R;
import com.wb.citylife.adapter.AdvPagerAdapter;
import com.wb.citylife.adapter.ChannelAdapter;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.Advertisement;
import com.wb.citylife.bean.db.DbChannel;
import com.wb.citylife.bean.db.DbScrollNews;
import com.wb.citylife.config.ActionConfig;
import com.wb.citylife.config.ChannelType;
import com.wb.citylife.dialog.ChannelDialog;
import com.wb.citylife.mk.channel.OrderChannelActivity;
import com.wb.citylife.mk.news.NewsListActivity;
import com.wb.citylife.widget.GrideViewForScrollView;

public class HomeFragment extends Fragment implements HomeListener,
	OnItemClickListener, OnItemLongClickListener, OnClickListener{
	
	//广告自动播放的时间间隔
	public static final int ADV_AUTO_MOVE_TIME = 1 * 10 * 1000;
	
	private Activity mActivity;
	private MainListener mainListener;
	
	//广告
	private ViewPager mAdvViewPager;
	private AdvPagerAdapter mAdvAdapter;
	private LinePageIndicator mAdvIndicator;
	private AdvTimeCount advAdvTimeCount;
	private Advertisement mAdv;	
	private List<DbScrollNews> scrollNewsList;
	
	//栏目
	private GrideViewForScrollView mTypeGrideView;
	private ChannelAdapter mChannelAdapter;
	private List<DbChannel> mChannelList;
	
	//编辑的栏目位置
	private int channelPosition;
	
	private ChannelDialog optionDialog;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
		mainListener = (MainListener)activity;		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		IntentFilter intentFilter = new IntentFilter();
	    intentFilter.addAction(ActionConfig.ACTION_UPDATE_CHANNEL);
	    mActivity.registerReceiver(mReceiver, intentFilter); 
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.main_item_home, container, false);		
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);		
		initView(view);
		mainListener.setHomeListener(this);
//		advTest();
	}
	
	private void initView(View view) {
		mAdvViewPager = (ViewPager) view.findViewById(R.id.adv_pager);
		mAdvIndicator = (LinePageIndicator) view.findViewById(R.id.adv_indicator);
		mTypeGrideView = (GrideViewForScrollView) view.findViewById(R.id.type_grid);	
		mTypeGrideView.setOnItemClickListener(this);
		mTypeGrideView.setOnItemLongClickListener(this);
		
		optionDialog = new ChannelDialog(mActivity, R.style.popupStyle);
		optionDialog.setListener(this);
	}
	
	@Override
	public void onLoadLocalChannel(List<DbChannel> channelList) {
		mChannelList = channelList;
		mChannelAdapter = new ChannelAdapter(mActivity, channelList, true);
		mTypeGrideView.setAdapter(mChannelAdapter);
	}
	
	@Override
	public void onLoadLocalScrollNews(List<DbScrollNews> scrollNews) {
		scrollNewsList = scrollNews;
		mAdvAdapter = new AdvPagerAdapter(mActivity, scrollNewsList);
		mAdvViewPager.setAdapter(mAdvAdapter);
		mAdvIndicator.setViewPager(mAdvViewPager);
	}
	
	@Override
	public void onChannelComplete(List<DbChannel> channelList) {
		if(mChannelList == null) {
			mChannelList = channelList;
			mChannelAdapter = new ChannelAdapter(mActivity, channelList, true);
			mTypeGrideView.setAdapter(mChannelAdapter);
		} else {
			mChannelAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onScrollNewsCommplete(List<DbScrollNews> scrollNews) {
		if(scrollNewsList == null) {
			scrollNewsList = scrollNews;
			mAdvAdapter = new AdvPagerAdapter(mActivity, scrollNewsList);
			mAdvViewPager.setAdapter(mAdvAdapter);
			mAdvIndicator.setViewPager(mAdvViewPager);
		} else {
			mAdvAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		DbChannel channel = (DbChannel) mChannelAdapter.getItem(position);
		switch(channel.type) {
		case ChannelType.CHANNEL_TYPE_NEWS:
			startActivity(new Intent(getActivity(), NewsListActivity.class));
			break;			
		}		

	}
	
	/**
	 * 长按栏目弹出菜选项
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		DbChannel channel = (DbChannel) mChannelAdapter.getItem(position);
		if(channel.getType() != ChannelType.CHANNEL_TYPE_ADD) {
			channelPosition = position;			
			optionDialog.show();
		}
		return true;
	}	
	
	@Override
	public void onClick(View v) {
		optionDialog.dismiss();
		
		switch(v.getId()) {		
		case R.id.box_option_batch_manager:
			//栏目排序
			CityLifeApp.getInstance().setChannels(mChannelList);
			Intent intent = new Intent(mActivity, OrderChannelActivity.class);
			mActivity.startActivity(intent);
			break;
			
		case R.id.box_option_setlauncher:
			//发送至桌面
			break;
			
		case R.id.box_option_delete_item:
			//删除栏目
			mChannelAdapter.delChannel(channelPosition);			
			break;
		}
	}
		
//	@Override
//	public void onClick(View v) {
//		Item item = (Item)v.getTag(); 
//		if((item.getHolder().delBtn.getVisibility() == View.VISIBLE)) {
//			//提示用户是否要删除栏目
//			Dialog dialog = new ConfirmDialog().getDialog(getActivity(), "", "确认要删除"+item.getName()+"吗？", 
//					new DelConfirmListener(item));
//			dialog.setOnDismissListener(new OnDismissListener() {
//				
//				@Override
//				public void onDismiss(DialogInterface dialog) {
//					mTypeGrideView.cleanDelState();
//				}
//			});
//			dialog.show();
//		} else {
//			//跳转到栏目中
//			((ScaleLinearLayout) v).clickZoomOut();
//			if(item.getId() != -1) {
//				switch((int)item.getId()) {
//				case ChannelType.CHANNEL_TYPE_NEWS:
//					startActivity(new Intent(getActivity(), NewsListActivity.class));
//					break;
//				}
//			} else {
//				mTypeGrideView.cleanDelState();
//			}
//		}
//	}
//	
//	class DelConfirmListener implements DialogInterface.OnClickListener {
//		
//		private Item item;
//		
//		public DelConfirmListener(Item item) {
//			this.item = item;
//		}
//		
//		@Override
//		public void onClick(DialogInterface dialog, int which) {
//			int index = mTypeAdapter.indexOfItem(0, item);						
//			mTypeAdapter.deleteItem(0, index);
//			mTypeGrideView.notifyDataSetChanged();	
//		}
//	}
	
	/**
	 * 广告播放时间计时器
	 * @author liangbx
	 *
	 */
	class AdvTimeCount extends CountDownTimer {

		public AdvTimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			int currentItem = mAdvViewPager.getCurrentItem();
			if(currentItem < mAdvViewPager.getChildCount() - 1) {
				currentItem++;
			} else {
				currentItem = 0;
			}
			mAdvIndicator.setCurrentItem(currentItem);
			advAdvTimeCount.cancel();
			advAdvTimeCount.start();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			
		}		
	}
	
	 BroadcastReceiver mReceiver = new BroadcastReceiver() {
			
		 @Override
		 public void onReceive(Context context, Intent intent) {
		    if(intent.getAction().equals(ActionConfig.ACTION_UPDATE_CHANNEL)) {
		    	mChannelList = CityLifeApp.getInstance().getChannels();
		    	mChannelAdapter = new ChannelAdapter(mActivity, mChannelList, true);
				mTypeGrideView.setAdapter(mChannelAdapter);
				CityLifeApp.getInstance().setChannels(null);
		    } 
		}
	};
	
	@Override
	public void onDestroy() {
		mActivity.unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
	/************************************************ 测试数据 **********************************************/
//	private void advTest() {
//		mAdv = new Advertisement();
//		mAdv.respCode = 0;
//		mAdv.respMsg = "ok";
//		mAdv.totalCount = 2;
//		mAdv.resources = new ArrayList<Advertisement.AdvItem>();
//		
//		Advertisement.AdvItem item  = mAdv.new AdvItem();
//		item.id = "1";
//		item.imageUrl = "http://pic27.nipic.com/20130313/1628220_145734522153_2.jpg";
//		item.title = "舞动青春";
//		item.linkUrl = "";
//		mAdv.resources.add(item);
//		
//		item  = mAdv.new AdvItem();
//		item.id = "2";
//		item.imageUrl = "http://pic16.nipic.com/20110910/4582261_110721084388_2.jpg";
//		item.title = "创意无限";
//		item.linkUrl = "";
//		mAdv.resources.add(item);
//		
//		mAdvAdapter = new AdvPagerAdapter(mActivity, mAdv);
//		mAdvViewPager.setAdapter(mAdvAdapter);
//		mAdvIndicator.setViewPager(mAdvViewPager);
//	}
				
}
	
