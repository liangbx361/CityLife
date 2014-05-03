package com.wb.citylife.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wb.citylife.R;
import com.wb.citylife.bean.CommentList;
import com.wb.citylife.bean.CommentList.CommentItem;

public class CommentAdapter extends BaseAdapter {
	
	private Context mContext;
	private CommentList mCommentList;
	
	public CommentAdapter(Context context, CommentList commentList) {
		mContext = context;
		mCommentList = commentList;
	}

	@Override
	public int getCount() {
		return mCommentList.datas.size();
	}

	@Override
	public Object getItem(int position) {
		return mCommentList.datas.get(position);
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
			view = LayoutInflater.from(mContext).inflate(R.layout.comment_item_layout, null);
			holder = new ViewHolder();
			holder.usernameTv = (TextView) view.findViewById(R.id.username);
			holder.timeTv = (TextView) view.findViewById(R.id.time);
			holder.commentTv = (TextView) view.findViewById(R.id.comment);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		
		CommentItem commentItem = mCommentList.datas.get(position);
		holder.usernameTv.setText(commentItem.username);
		holder.timeTv.setText(commentItem.time);
		holder.commentTv.setText(commentItem.comment);

		return view;
	}
	
	public class ViewHolder {
		TextView usernameTv;
		TextView timeTv;
		TextView commentTv;
	}	
}
