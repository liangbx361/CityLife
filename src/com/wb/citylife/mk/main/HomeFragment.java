package com.wb.citylife.mk.main;

import java.util.ArrayList;
import java.util.List;

import com.viewpagerindicator.CirclePageIndicator;
import com.wb.citylife.R;
import com.wb.citylife.adapter.AdvPagerAdapter;
import com.wb.citylife.adapter.TypeAdapter;
import com.wb.citylife.bean.Advertisement;
import com.wb.citylife.bean.Item;
import com.wb.citylife.bean.Page;
import com.wb.citylife.config.ChannelType;
import com.wb.citylife.dialog.ConfirmDialog;
import com.wb.citylife.mk.news.NewsListActivity;
import com.wb.citylife.widget.ScaleLinearLayout;
import com.wb.citylife.widget.dragdropgrid.PagedDragDropGrid;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class HomeFragment extends Fragment implements OnClickListener{
	
	//广告自动播放的时间间隔
	public static final int ADV_AUTO_MOVE_TIME = 1 * 10 * 1000;
	
	private Activity mActivity;
	
	//广告
	private ViewPager mAdvViewPager;
	private AdvPagerAdapter mAdvAdapter;
	private CirclePageIndicator mAdvIndicator;
	private AdvTimeCount advAdvTimeCount;
	private Advertisement mAdv;
	
	//栏目
	private PagedDragDropGrid mTypeGrideView;
	private TypeAdapter mTypeAdapter;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		
		advTest();
		typeTest();
	}
	
	private void initView(View view) {
		mAdvViewPager = (ViewPager) view.findViewById(R.id.adv_pager);
		mAdvIndicator = (CirclePageIndicator) view.findViewById(R.id.adv_indicator);
		mTypeGrideView = (PagedDragDropGrid) view.findViewById(R.id.type_grid);
		mTypeGrideView.setClickListener(this);		
	}
	
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

	@Override
	public void onClick(View v) {
		Item item = (Item)v.getTag(); 
		if((item.getHolder().delBtn.getVisibility() == View.VISIBLE)) {
			//提示用户是否要删除栏目
			Dialog dialog = new ConfirmDialog().getDialog(getActivity(), "", "确认要删除"+item.getName()+"吗？", 
					new DelConfirmListener(item));
			dialog.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					mTypeGrideView.cleanDelState();
				}
			});
			dialog.show();
		} else {
			//跳转到栏目中
			((ScaleLinearLayout) v).clickZoomOut();
			if(item.getId() != -1) {
				switch((int)item.getId()) {
				case ChannelType.CHANNEL_TYPE_NEWS:
					startActivity(new Intent(getActivity(), NewsListActivity.class));
					break;
				}
			} else {
				mTypeGrideView.cleanDelState();
			}
		}
	}
	
	class DelConfirmListener implements DialogInterface.OnClickListener {
		
		private Item item;
		
		public DelConfirmListener(Item item) {
			this.item = item;
		}
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			int index = mTypeAdapter.indexOfItem(0, item);						
			mTypeAdapter.deleteItem(0, index);
			mTypeGrideView.notifyDataSetChanged();	
		}
	}
	
	/************************************************ 测试数据 **********************************************/
	private void advTest() {
		mAdv = new Advertisement();
		mAdv.respCode = 0;
		mAdv.respMsg = "ok";
		mAdv.totalCount = 2;
		mAdv.resources = new ArrayList<Advertisement.AdvItem>();
		
		Advertisement.AdvItem item  = mAdv.new AdvItem();
		item.id = 1;
		item.imageUrl = "http://pic27.nipic.com/20130313/1628220_145734522153_2.jpg";
		item.title = "舞动青春";
		item.linkUrl = "";
		mAdv.resources.add(item);
		
		item  = mAdv.new AdvItem();
		item.id = 1;
		item.imageUrl = "http://pic16.nipic.com/20110910/4582261_110721084388_2.jpg";
		item.title = "创意无限";
		item.linkUrl = "";
		mAdv.resources.add(item);
		
		mAdvAdapter = new AdvPagerAdapter(mActivity, mAdv);
		mAdvViewPager.setAdapter(mAdvAdapter);
		mAdvIndicator.setViewPager(mAdvViewPager);
	}
	
	private void typeTest() {
		Page page1 = new Page();
		List<Item> items = new ArrayList<Item>();
		items.add(new Item(1, "资讯", R.drawable.coupon_icon));
		items.add(new Item(2, "房地产", R.drawable.coupon_icon));
		items.add(new Item(3, "二手市场", R.drawable.coupon_icon));
		items.add(new Item(4, "随手拍", R.drawable.coupon_icon));
		items.add(new Item(5, "投票", R.drawable.coupon_icon));
		items.add(new Item(-1, "添加", R.drawable.type_add_icon));
		page1.setItems(items);
		
		mTypeAdapter = new TypeAdapter(getActivity(), mTypeGrideView, page1);
		mTypeGrideView.setAdapter(mTypeAdapter);		
	}
	
}
	
