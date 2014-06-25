package com.wb.citylife.mk.img;

import java.util.ArrayList;
import java.util.List;

import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import ru.truba.touchgallery.GalleryWidget.UrlPagerAdapter;

import com.wb.citylife.R;
import com.wb.citylife.bean.ImagesItem;
import com.wb.citylife.config.DebugConfig;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.config.NetConfig;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ImageBrowseFragment extends Fragment {
	
	private Activity mActivity;
	private GalleryViewPager gViewPager;
	private UrlPagerAdapter mAdapter;
	private ImagesItem imagesItem;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
		imagesItem = getArguments().getParcelable(IntentExtraConfig.ESTATE_IMAGE_DATA);
		DebugConfig.showLog("image_browse", "onAttach");
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.image_browse_layout, container, false);		
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);		
		initView(view);
	}
	
	private void initView(View view) {
		gViewPager = (GalleryViewPager) view.findViewById(R.id.viewer);
		List<String> imagesList = new ArrayList<String>();
		for(int i=0; i<imagesItem.images.length; i++) {
			imagesList.add(NetConfig.getPictureUrl(imagesItem.images[i]));
		}
		
		mAdapter = new UrlPagerAdapter(mActivity, imagesList);
		gViewPager.setOffscreenPageLimit(3);
		gViewPager.setAdapter(mAdapter);
	}
}
