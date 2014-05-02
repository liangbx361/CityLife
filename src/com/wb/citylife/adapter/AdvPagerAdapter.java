package com.wb.citylife.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.android.volley.toolbox.NetworkImageView;
import com.wb.citylife.R;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.Advertisement;
import com.wb.citylife.bean.Advertisement.AdvItem;

public class AdvPagerAdapter extends PagerAdapter implements OnClickListener{
	private int mCount;
	private Advertisement mAvdData;
	private List<View> mViewList = new ArrayList<View>();
	private Activity mActivity;
	
	public AdvPagerAdapter(Activity activity, Advertisement advData) {		
		mActivity = activity;
		mAvdData = advData;
		mCount = mAvdData.resources.size();
		for(int i=0; i<mCount; i++) {
			View view = mActivity.getLayoutInflater().inflate(R.layout.adv_item, null);
			view.setTag(i+"");
			view.setOnClickListener(this);
			mViewList.add(view);
		}		
	}
	
	@Override
	public int getCount() {
		return mCount;
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
		AdvItem item = mAvdData.resources.get(position);
		NetworkImageView imageView = (NetworkImageView) view;
		imageView.setDefaultImageResId(R.drawable.base_list_adv_default_icon);
		if(item.imageUrl != null && !item.imageUrl.equals("")) {
			imageView.setImageUrl(item.imageUrl, 
					CityLifeApp.getInstance().getImageLoader());			
		}						
		
		return view;
	}

	@Override
	public void onClick(View v) {
		int index = Integer.parseInt(v.getTag().toString());
		AdvItem advItem = mAvdData.resources.get(index);
		//点击跳转新闻详情页
	}
		 
}
