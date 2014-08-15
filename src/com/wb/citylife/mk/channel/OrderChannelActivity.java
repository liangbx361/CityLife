package com.wb.citylife.mk.channel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.tsz.afinal.FinalDb;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.dslv.DragSortListView.DropListener;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.adapter.OrderChannelAdapter;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.db.DbChannel;
import com.wb.citylife.config.ActionConfig;

/**
 * 实现栏目的排序
 * @author liangbx
 *
 */
public class OrderChannelActivity extends BaseActivity implements DropListener {
	
	//栏目
	private DragSortListView mDslv;
	private OrderChannelAdapter mChannelAdapter;
	private List<DbChannel> mChannels;
	private List<DbChannel> mEditChannels;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_channel);
		
		getIntentData();
		initView();				
	}
	
	@Override
	public void getIntentData() {
		mChannels = CityLifeApp.getInstance().getChannels();
		mEditChannels = new ArrayList<DbChannel>();
		for(int i=0; i<mChannels.size(); i++) {
			DbChannel dItem = mChannels.get(i);
			if(dItem.isAdd) {
				mEditChannels.add(dItem);
			}
		}
	}

	@Override
	public void initView() {
		mDslv = (DragSortListView) findViewById(R.id.list);
		mDslv.setDropListener(this);
		mDslv.setFloatViewManager(new MyDSController(mDslv));
		mChannelAdapter = new OrderChannelAdapter(this, mEditChannels);
		mDslv.setAdapter(mChannelAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * 菜单点击处理
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {		
		
		switch(item.getItemId()) {
		case android.R.id.home:
			saveChannel();
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		
		switch(keyCode) {
		case KeyEvent.KEYCODE_BACK:
			saveChannel();
			finish();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	private void saveChannel() {
		for(int i=0; i<mEditChannels.size(); i++) {
			DbChannel dbChannel = mEditChannels.get(i);
			dbChannel.weight = i;
		}
		FinalDb finalDb = CityLifeApp.getInstance().getDb();
		for(int i=0; i<mChannels.size(); i++) {
			for(int j=0; j<mEditChannels.size(); j++) {
				DbChannel editChannel = mEditChannels.get(j);
				DbChannel channel = mChannels.get(i);
				if(editChannel.channelId.equals(channel.channelId)) {
					channel.weight = editChannel.weight;
					finalDb.update(channel, "channelId='" + channel.channelId + "'" );
					continue;
				}
			}
		}
		Collections.sort(mChannels, new ChannelComparator());
		sendBroadcast(new Intent(ActionConfig.ACTION_UPDATE_CHANNEL));
	}

	@Override
	public void drop(int from, int to) {
		DbChannel channelItem = mEditChannels.get(from);
		mEditChannels.remove(from);
		mEditChannels.add(to, channelItem);
		mChannelAdapter.notifyDataSetChanged();
	}
	
	private class MyDSController extends DragSortController implements ImageListener{
		
		private DragSortListView mDslv;
		private View v;
		private TextView nameTv;
		private ImageView iconIv;
		
		public MyDSController(DragSortListView dslv) {
            super(dslv);
            setDragHandleId(R.id.drag_handle);
            mDslv = dslv;
            LayoutInflater inflater = LayoutInflater.from(OrderChannelActivity.this);
            v = inflater.inflate(R.layout.dslvlist_drag_item, null);
            v.setBackgroundColor(Color.DKGRAY);
            nameTv = (TextView) v.findViewById(R.id.name);
            iconIv = (ImageView) v.findViewById(R.id.type_icon);
		}
		
		@Override
        public View onCreateFloatView(int position) {
			DbChannel dbChannel = mEditChannels.get(position);			
			nameTv.setText(dbChannel.name);
			if(!TextUtils.isEmpty(dbChannel.imageUrl)) {
				CityLifeApp.getInstance().getImageLoader().get(dbChannel.imageUrl, this);
			} else {
				iconIv.setImageDrawable(OrderChannelActivity.this.getResources().getDrawable(R.drawable.trans));
			}
			
			return v;
		}
		
		@Override
        public void onDestroyFloatView(View floatView) {
            //do nothing; block super from crashing
        }
		
		@Override
        public int startDragPosition(MotionEvent ev) {
            int res = super.dragHandleHitPosition(ev);
            int width = mDslv.getWidth();

            if ((int) ev.getX() < width / 3) {
                return res;
            } else {
                return DragSortController.MISS;
            }
        }

		@Override
		public void onErrorResponse(VolleyError error) {
			
		}

		@Override
		public void onResponse(ImageContainer response, boolean isImmediate) {
			iconIv.setImageBitmap(response.getBitmap());
		}
		
	}
}
