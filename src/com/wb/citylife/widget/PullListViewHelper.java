package com.wb.citylife.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wb.citylife.R;

public class PullListViewHelper implements OnClickListener{
	
	//正在加载
	public static final int BOTTOM_STATE_LOADING = 0;
	//加载失败
	public static final int BOTTOM_STATE_LOAD_FAIL = 1;
	//无更多数据
	public static final int BOTTOM_STATE_NO_MORE_DATE = 2;	
	//加载空闲
	public static final int BOTTOM_STATE_LOAD_IDLE = 3;
	
	private View bottomView;
	private int loadState = BOTTOM_STATE_LOAD_IDLE;
		
	public PullListViewHelper(Context context, ListView listView) {
		bottomView = LayoutInflater.from(context).inflate(R.layout.bottom_loading_layout, null);
		BottomHolder holder = new BottomHolder();
		holder.progressBar = (ProgressBar) bottomView.findViewById(R.id.loading_processbar);
		holder.stateTv = (TextView) bottomView.findViewById(R.id.state);
		bottomView.setTag(holder);
		listView.addFooterView(bottomView);
	}
	
	/**
	 * 设置底部显示的状态
	 * @param state
	 * @param pageSize
	 */
	public void setBottomState(int state, int pageSize) {
		BottomHolder holder = (BottomHolder) bottomView.getTag();
		switch (state) {		
		case BOTTOM_STATE_LOADING:			
			holder.progressBar.setVisibility(View.VISIBLE);
			holder.stateTv.setText(pageSize + "条载入中...");
			break;
			
		case BOTTOM_STATE_LOAD_FAIL:
			holder.progressBar.setVisibility(View.GONE);
			holder.stateTv.setText("加载失败，点击重试");
			break;

		case BOTTOM_STATE_NO_MORE_DATE:
			holder.progressBar.setVisibility(View.GONE);
			holder.stateTv.setText("没有更多啦~");
			break;
		}
	}
	
	public void setBottomClick(OnClickListener listener) {
		bottomView.setOnClickListener(listener);
	}
	
	@Override
	public void onClick(View v) {
		
	}	
	
	public class BottomHolder {
		private ProgressBar progressBar;
		private TextView stateTv;
	}
	
}
