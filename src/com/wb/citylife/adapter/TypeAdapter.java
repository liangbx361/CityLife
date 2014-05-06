package com.wb.citylife.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.wb.citylife.R;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.Item;
import com.wb.citylife.bean.Page;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.widget.dragdropgrid.PagedDragDropGrid;
import com.wb.citylife.widget.dragdropgrid.PagedDragDropGridAdapter;

public class TypeAdapter implements PagedDragDropGridAdapter {
	
	private Context mContext;
	private PagedDragDropGrid mGridview;
	
	private List<Page> pages = new ArrayList<Page>();
	
	public TypeAdapter(Context context, PagedDragDropGrid gridview, Page page) {
		super();
		mContext = context;
		mGridview = gridview;
		pages.add(page);
	}
	
	@Override
	public int pageCount() {
		return pages.size();
	}

	@Override
	public int itemCountInPage(int page) {
		return itemsInPage(page).size();
	}
	
	public List<Item> itemsInPage(int page) {
		if (pages.size() > page) {
			return pages.get(page).getItems();
		}	
		return Collections.emptyList();
	}

	@Override
	public View view(int page, int index) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.type_item_layout, null);						
		ViewHolder holder = new ViewHolder();
		holder.icon = (NetworkImageView) view.findViewById(R.id.type_icon);
		holder.name = (TextView) view.findViewById(R.id.type_name);
		holder.delBtn = (ImageButton) view.findViewById(R.id.type_del);
		
		Item item = getItem(page, index);
		item.setHolder(holder);
		holder.icon.setImageUrl(NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + item.getImageUrl(), 
				CityLifeApp.getInstance().getImageLoader());
		holder.name.setText(item.getName());
		
		return view;
	}
	
	private Item getItem(int page, int index) {
		List<Item> items = itemsInPage(page);
		return items.get(index);
	}
	
	public class ViewHolder {
		public NetworkImageView icon;
		public TextView name;
		public ImageButton delBtn;
	}

	@Override
	public int rowCount() {
		return AUTOMATIC;
	}

	@Override
	public int columnCount() {
		return AUTOMATIC;
	}
	
	@Override
	public void printLayout() {
		int i=0;
		for (Page page : pages) {
			Log.d("Page", Integer.toString(i++));
			
			for (Item item : page.getItems()) {
				Log.d("Item", Long.toString(item.getId()));
			}
		}
	}
	
	@Override
	public void swapItems(int pageIndex, int itemIndexA, int itemIndexB) {
		getPage(pageIndex).swapItems(itemIndexA, itemIndexB);
	}
	
	private Page getPage(int pageIndex) {
		return pages.get(pageIndex);
	}
	
	@Override
	public void moveItemToPreviousPage(int pageIndex, int itemIndex) {
		int leftPageIndex = pageIndex-1;
		if (leftPageIndex >= 0) {
			Page startpage = getPage(pageIndex);
			Page landingPage = getPage(leftPageIndex);
			
			Item item = startpage.removeItem(itemIndex);
			landingPage.addItem(item);	
		}	
	}

	@Override
	public void moveItemToNextPage(int pageIndex, int itemIndex) {
		int rightPageIndex = pageIndex+1;
		if (rightPageIndex < pageCount()) {
			Page startpage = getPage(pageIndex);
			Page landingPage = getPage(rightPageIndex);
			
			Item item = startpage.removeItem(itemIndex);
			landingPage.addItem(item);			
		}			
	}

	@Override
	public void deleteItem(int pageIndex, int itemIndex) {
		getPage(pageIndex).deleteItem(itemIndex);		
	}
		
	public int indexOfItem(int pageIndex, Item item) {
		int itemIndex = -1;
		Page page = getPage(pageIndex);
		for(int i=0; i<page.getItems().size(); i++) {
			Item typeItem = page.getItems().get(i);
			if(typeItem.getId() == item.getId()) {
				itemIndex = i;
				break;
			}
		}
		return itemIndex;
	}

	@Override
	public int deleteDropZoneLocation() {
		
		return BOTTOM;
	}

	@Override
	public boolean showRemoveDropZone() {
		
		return true;
	}

	@Override
	public int getPageWidth(int page) {
		
		return 0;
	}

	@Override
	public Object getItemAt(int page, int index) {
		
		return getPage(page).getItems().get(index);
	}

	@Override
	public boolean disableZoomAnimationsOnChangePage() {
		
		return true;
	}

}
