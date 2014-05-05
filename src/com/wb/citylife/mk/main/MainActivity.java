package com.wb.citylife.mk.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalDb;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.common.net.volley.VolleyErrorHelper;
import com.common.widget.ToastHelper;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.Channel;
import com.wb.citylife.bean.Channel.ChannelItem;
import com.wb.citylife.bean.db.DbChannel;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.task.ChannelRequest;

public class MainActivity extends BaseActivity implements MainListener,
	Listener<Channel>, ErrorListener{
	
	private static final String TAG_HOME = "home";
	private static final String TAG_SEARCH = "search";
	private static final String TAG_ACCOUNT = "account";
	private static final String TAG_SETTINGS = "settings";
	
	private String tabTags[] = {TAG_HOME, TAG_SEARCH, TAG_ACCOUNT, TAG_SETTINGS};
	private Class fragments[] = {HomeFragment.class, SearchFragment.class, MyCenterFragment.class, SettingsFragment.class};
	private int tabNameIds[] = {R.string.tab_name_home, R.string.tab_name_search, R.string.tab_name_account, R.string.tab_name_settings};
	private int tabIconIds[] = {R.drawable.tab_home, R.drawable.tab_search, R.drawable.tab_account, R.drawable.tab_settings};
	
	private FragmentTabHost fTabHost;
	
	//退出计数器
	private int exitCount;
	
	//栏目
	private ChannelRequest mChannelRequest;
	private Channel mChannel;
	private List<DbChannel> mChannelList;
	
	private HomeListener mHomeListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_main_tab);
		
		getIntentData();
		initView();
		loadDbChannel();		
	}
	
	@Override
	public void getIntentData() {
		
	}
	
	@Override
	public void initView() {
		
		fTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
		fTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		fTabHost.getTabWidget().setDividerDrawable(null);
						
		for(int i=0; i<fragments.length; i++) {
			TabSpec tabSpec = fTabHost.newTabSpec(tabTags[i]); 
			tabSpec.setIndicator(getTabItemView(tabIconIds[i], tabNameIds[i]));
			fTabHost.addTab(tabSpec, fragments[i], null);
			fTabHost.getTabWidget().setBackgroundResource(R.drawable.tab_footer_bg_white);			
		}
	}
	
	/**
	 * 加载数据库中的栏目数据
	 */
	public void loadDbChannel() {
		FinalDb finalDb = CityLifeApp.getInstance().getDb();
		mChannelList = finalDb.findAll(DbChannel.class, "weight asc");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(true);
		
		requestChannel(Method.POST, NetInterface.METHOD_CHANNEL, getChannelRequestParams(), this, this);
		setIndeterminateBarVisibility(true);		
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * 给Tab按钮设置图片和文字
	 * @param resId
	 * @param nameId
	 * @return
	 */
	private View getTabItemView(int resId, int nameId) {
		View v = (View)LayoutInflater.from(this).inflate(R.layout.main_tab_item, null);
		
		ImageView iconImageView = (ImageView)v.findViewById(R.id.tab_icon);
		TextView iconName = (TextView)v.findViewById(R.id.tab_icon_name);
		iconImageView.setImageDrawable(getResources().getDrawable(resId));
		iconName.setText(getResources().getString(nameId));
		
		return v;
	}
	
	/**
	 * 2秒内按两次退出程序
	 */
	@Override
	public void onBackPressed() {
		
		if(exitCount==0) {
			ToastHelper.showToastInBottom(this, R.string.exit_toast);
			
			new Thread() {

				@Override
				public void run() {
					try {
						sleep(2000);
						exitCount = 0;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}.start();
			
			exitCount++;
		} else {
			finish();
		}
	}
	
	/**
	 * 获取栏目请求参数
	 * @return
	 */
	private Map<String, String> getChannelRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
				
		return params;
	}
	
	/**
	 * 执行栏目任务请求
	 * @param method
	 * @param url
	 * @param params
	 * @param listenre
	 * @param errorListener
	 */	
	private void requestChannel(int method, String methodUrl, Map<String, String> params,	 
			Listener<Channel> listenre, ErrorListener errorListener) {			
		if(mChannelRequest != null) {
			mChannelRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mChannelRequest = new ChannelRequest(method, url, params, listenre, errorListener);
		startRequest(mChannelRequest);		
	}
	
	/**
	 * 网络请求错误处理
	 *
	 */
	@Override
	public void onErrorResponse(VolleyError error) {		
		setIndeterminateBarVisibility(false);
		ToastHelper.showToastInBottom(getApplicationContext(), VolleyErrorHelper.getErrorMessage(error));
	}
	
	/**
	 * 请求完成，处理UI更新
	 */
	@Override
	public void onResponse(Channel response) {
		setIndeterminateBarVisibility(false);
		mChannel = response;
		//更新数据库中的数据	
		if(mChannel.respCode == RespCode.SUCCESS) {
			
			if(mChannelList == null || mChannelList.size() == 0) {
				for(int i=0; i<mChannel.datas.size(); i++) {
					DbChannel channel = new DbChannel();
					ChannelItem channelItem = mChannel.datas.get(i);
					channel.channelId = channelItem.id;
					channel.imageUrl = channelItem.imageUrl;
					channel.type = channelItem.type;
					channel.name = channelItem.name;
					channel.isAdd = channelItem.isAdd;
					channel.updateNum = 0;
					channel.weight = i;	
					mChannelList.add(channel);
					CityLifeApp.getInstance().getDb().save(channel);
				}				
			} else {			
				for(int i=0; i<mChannel.datas.size(); i++) {
					for(int j=0; j<mChannelList.size(); j++) {
						ChannelItem channelItem = mChannel.datas.get(i);
						DbChannel channel = mChannelList.get(j);
						if(channelItem.id.equals(channel.channelId)) {
							channel.updateNum = channelItem.updateNum;
							channel.name = channelItem.name;
							channel.imageUrl = channelItem.imageUrl;
							CityLifeApp.getInstance().getDb().update(channel);
							break;
						}
					}
				}
			}
			
			if(mHomeListener != null) {
				mHomeListener.onChannelComplete(mChannelList);
			}
		}
	}
	
	@Override
	public void setHomeListener(HomeListener listener) {
		mHomeListener = listener;
		if(mChannelList != null && mChannelList.size() != 0) {
			mHomeListener.onChannelComplete(mChannelList);
		}
	}	

}
