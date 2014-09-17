package com.wb.citylife.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.wb.citylife.R;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.MerchantList;
import com.wb.citylife.bean.MerchantList.MerchantItem;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.mk.merchant.MerchantDetailActivity;

public class MerchantListAdapter extends BaseAdapter implements OnClickListener {

	private Context mContext;
	private MerchantList mMerchantList;

	public MerchantListAdapter(Context context, MerchantList merchantList) {
		mContext = context;
		mMerchantList = merchantList;
	}

	@Override
	public int getCount() {
		int count = mMerchantList.datas.size() / 2; 
		return mMerchantList.datas.size() % 2 > 0 ? count + 1 : count;
	}

	@Override
	public Object getItem(int position) {

		return mMerchantList.datas.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = null;
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
			view = inflater.inflate(R.layout.merchant_list_item, null);
			holder = new ViewHolder();
			holder.item1Vg = (ViewGroup) view.findViewById(R.id.item1);
			holder.item1LogoIv = (NetworkImageView) view.findViewById(R.id.item1_logo);
			holder.item1NameTv = (TextView) view.findViewById(R.id.item1_name);
			
			holder.item2Vg = (ViewGroup) view.findViewById(R.id.item2);
			holder.item2LogoIv = (NetworkImageView) view.findViewById(R.id.item2_logo);
			holder.item2NameTv = (TextView) view.findViewById(R.id.item2_name);
			view.setTag(holder);
		} else {
			view = convertView;
			holder = (ViewHolder) view.getTag();
		}
		
		int index = position * 2;
		if(index < mMerchantList.datas.size()) {
			holder.item1Vg.setTag(index + "");
			holder.item1Vg.setOnClickListener(this);
			holder.item1LogoIv.setVisibility(View.VISIBLE);
			holder.item1NameTv.setVisibility(View.VISIBLE);
			MerchantItem item = mMerchantList.datas.get(index);
			holder.item1LogoIv.setDefaultImageResId(R.drawable.merchant_default_icon);
			holder.item1LogoIv.setImageUrl(item.logoUrl, CityLifeApp.getInstance().getImageLoader());
			holder.item1NameTv.setText(item.name);
		} else {
			holder.item1LogoIv.setVisibility(View.INVISIBLE);
			holder.item1NameTv.setVisibility(View.INVISIBLE);
		}
		
		index++;
		if(index < mMerchantList.datas.size()) {
			holder.item2Vg.setTag(index + "");
			holder.item2Vg.setOnClickListener(this);
			holder.item2LogoIv.setVisibility(View.VISIBLE);
			holder.item2NameTv.setVisibility(View.VISIBLE);
			MerchantItem item = mMerchantList.datas.get(index);
			holder.item2LogoIv.setDefaultImageResId(R.drawable.merchant_default_icon);
			holder.item2LogoIv.setImageUrl(item.logoUrl, CityLifeApp.getInstance().getImageLoader());
			holder.item2NameTv.setText(item.name);
		} else {
			holder.item2LogoIv.setVisibility(View.INVISIBLE);
			holder.item2NameTv.setVisibility(View.INVISIBLE);
		}
		
		return view;
	}

	class ViewHolder {
		private ViewGroup item1Vg;
		private NetworkImageView item1LogoIv;
		private TextView item1NameTv;
		
		private ViewGroup item2Vg;
		private NetworkImageView item2LogoIv;
		private TextView item2NameTv;
		private int position;
	}
	
	@Override
	public void onClick(View v) {
		int index = Integer.parseInt(v.getTag().toString());
		MerchantItem mItem = mMerchantList.datas.get(index);
		Intent intent = new Intent(mContext, MerchantDetailActivity.class);
		intent.putExtra(IntentExtraConfig.DETAIL_ID, mItem.id);
		intent.putExtra(IntentExtraConfig.DETAIL_NAME, mItem.name);
		mContext.startActivity(intent);
	}
}
