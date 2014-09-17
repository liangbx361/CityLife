package com.wb.citylife.mk.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalDb;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.common.date.FormatDateTime;
import com.common.net.volley.VolleyErrorHelper;
import com.common.widget.ToastHelper;
import com.umeng.update.UmengUpdateAgent;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.IBaseNetActivity;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.Channel;
import com.wb.citylife.bean.Channel.ChannelItem;
import com.wb.citylife.bean.ScrollNews;
import com.wb.citylife.bean.ScrollNews.NewsItem;
import com.wb.citylife.bean.db.DbChannel;
import com.wb.citylife.bean.db.DbScrollNews;
import com.wb.citylife.config.IntentExtraConfig;
import com.wb.citylife.config.NetConfig;
import com.wb.citylife.config.NetInterface;
import com.wb.citylife.config.RespCode;
import com.wb.citylife.config.ResultCode;
import com.wb.citylife.mk.push.PushUtils;
import com.wb.citylife.task.ChannelRequest;
import com.wb.citylife.task.ScrollNewsRequest;
import com.wb.citylife.widget.PullDoorView;
import com.wb.citylife.widget.PullDoorView.PullDoorViewListener;

public class MainActivity extends IBaseNetActivity implements MainListener,
	Listener<Channel>, ErrorListener, PullDoorViewListener{
	
	private static final String TAG_HOME = "主页";
	private static final String TAG_SEARCH = "搜索";
	private static final String TAG_ACCOUNT = "个人";
	private static final String TAG_SETTINGS = "设置";
	
	private String tabTags[] = {TAG_HOME, TAG_SEARCH, TAG_ACCOUNT, TAG_SETTINGS};
	private Class fragments[] = {HomeFragment.class, SearchMainFragment.class, MyCenterFragment.class, SettingsFragment.class};
	private int tabNameIds[] = {R.string.tab_name_home, R.string.tab_name_search, R.string.tab_name_account, R.string.tab_name_settings};
	private int tabIconIds[] = {R.drawable.tab_home, R.drawable.tab_search, R.drawable.tab_account, R.drawable.tab_settings};
	
	private FragmentTabHost fTabHost;
	
	//退出计数器
	private int exitCount;
	
	//栏目
	private ChannelRequest mChannelRequest;
	private Channel mChannel;
	private List<DbChannel> mChannelList;
	
	//滚动新闻
	private ScrollNewsRequest mScrollNewsRequest;
	private ScrollNews mScrollNews;
	private List<DbScrollNews> mScrollNewsList;
	
	private HomeListener mHomeListener;
	private MyCenterListener mCenterListener;
	
	private PullDoorView welcomeView;
	private NetworkImageView welcomeIv;
	private String welcomeImgUrl;
	private TextView mTipsTextView;
	private Animation mTipsAnimation;
	private boolean disWelcomeView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.activity_main_tab);
		
		getIntentData();
		initView();
		loadDbData();	
		
		//打开推送
		if (!PushUtils.getPushFlag(this)) {
			PushUtils.openPush(this);
		}
		
		//检测更新
		UmengUpdateAgent.update(this);
//		UmengUpdateAgent.forceUpdate(this);
		UmengUpdateAgent.setUpdateAutoPopup(true);
                
        requestChannel(Method.POST, NetInterface.METHOD_CHANNEL, getChannelRequestParams(), this, this);
		requestScrollNews(Method.POST, NetInterface.METHOD_SCROLL_NEWS, 
				getScrollNewsRequestParams(), new ScrollNewsListener(), this);				
	}
	
	public void getIntentData() {		
		welcomeImgUrl = getIntent().getStringExtra(IntentExtraConfig.WELCOME_IMG);
		disWelcomeView = getIntent().getBooleanExtra(IntentExtraConfig.DIS_WELCOME, false);
	}
	
	public void initView() {		
		fTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
		fTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		fTabHost.getTabWidget().setDividerDrawable(null);
		fTabHost.getTabWidget().setBackgroundResource(R.drawable.tab_footer_bg_white);
						
		for(int i=0; i<fragments.length; i++) {
			TabSpec tabSpec = fTabHost.newTabSpec(tabTags[i]); 
			tabSpec.setIndicator(getTabItemView(tabIconIds[i], tabNameIds[i]));
			fTabHost.addTab(tabSpec, fragments[i], null);						
		}
		
		welcomeView = (PullDoorView) findViewById(R.id.welcome_layou);
		welcomeView.setListener(this);
		welcomeIv = (NetworkImageView) findViewById(R.id.welcome);
		welcomeIv.setDefaultImageResId(R.drawable.default_welcome);
		if(!TextUtils.isEmpty(welcomeImgUrl)) {
			welcomeIv.setImageUrl(welcomeImgUrl, CityLifeApp.getInstance().getImageLoader());			
		}
		
		mTipsTextView = (TextView) findViewById(R.id.pulldoor_close_tips);
		mTipsAnimation = AnimationUtils.loadAnimation(this, R.anim.connection);
		
		if(disWelcomeView) {
			welcomeView.setVisibility(View.VISIBLE);
			//默认启动提示上拉文字动画
			if (mTipsTextView != null && mTipsAnimation != null)
				mTipsTextView.startAnimation(mTipsAnimation);
		} else {
			welcomeView.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 加载数据库中的栏目数据
	 */
	public void loadDbData() {
		FinalDb finalDb = CityLifeApp.getInstance().getDb();
		mChannelList = finalDb.findAll(DbChannel.class, "weight asc");
		mScrollNewsList = finalDb.findAll(DbScrollNews.class);
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
		params.put("lastAccessTime", FormatDateTime.date2String(new Date(), FormatDateTime.DATETIME_YMDHMSS_STR));
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
		ToastHelper.showToastInBottom(getApplicationContext(), VolleyErrorHelper.getErrorMessage(error));
	}
	
	/**
	 * 请求完成，处理UI更新
	 */
	@Override
	public void onResponse(Channel response) {
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
					channel.isAdd = true;
					channel.updateNum = 0;
					channel.weight = i;	
					mChannelList.add(channel);
					CityLifeApp.getInstance().getDb().save(channel);
				}				
			} else {
				//比对栏目数据，删除不存在的栏目		
				for(int i=0; i<mChannelList.size(); i++) {
					boolean isFind = false;
					for(int j=0; j<mChannel.datas.size(); j++) {							
						DbChannel dbChannel = mChannelList.get(i);
						ChannelItem channelItem = mChannel.datas.get(j);
						if(channelItem.id.equals(dbChannel.channelId)) {
							isFind = true;
							break;
						}
					}
					if(!isFind) {
						//删除栏目
						DbChannel dbChannel = mChannelList.get(i);
						CityLifeApp.getInstance().getDb().deleteById(DbChannel.class, dbChannel.id);
						mChannelList.remove(i);
						i--;
					}
				}
				
				//比对栏目数据，新增和更新栏目数据
				for(int i=0; i<mChannel.datas.size(); i++) {
					for(int j=0; j<mChannelList.size(); j++) {
						ChannelItem channelItem = mChannel.datas.get(i);
						DbChannel dbChannel = mChannelList.get(j);
						if(channelItem.id.equals(dbChannel.channelId)) {
							//更新栏目
							dbChannel.updateNum = channelItem.updateNum;
							dbChannel.name = channelItem.name;
							dbChannel.imageUrl = channelItem.imageUrl;
							CityLifeApp.getInstance().getDb().update(dbChannel, "channelId='" + dbChannel.channelId + "'");
							break;
						} else {
							if(j == mChannelList.size()-1) {
								//新增栏目
								DbChannel newChannel = new DbChannel();
								newChannel.channelId = channelItem.id;
								newChannel.imageUrl = channelItem.imageUrl;
								newChannel.type = channelItem.type;
								newChannel.name = channelItem.name;
								newChannel.isAdd = false;
								newChannel.updateNum = 0;
								newChannel.weight = mChannelList.get(mChannelList.size()-1).weight + 1;
								mChannelList.add(newChannel);
								CityLifeApp.getInstance().getDb().save(newChannel);
							}
						}
					}
					
				}
			}
			
			if(mHomeListener != null) {
				mHomeListener.onChannelComplete(mChannelList);
			}
		}
	}
	
	/**
	 * 获取滚动新闻请求参数
	 * @return
	 */
	private Map<String, String> getScrollNewsRequestParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", "0");		
		return params;
	}
	
	/**
	 * 执行滚动新闻任务请求
	 * @param method
	 * @param url
	 * @param params
	 * @param listenre
	 * @param errorListener
	 */	
	private void requestScrollNews(int method, String methodUrl, Map<String, String> params,	 
			Listener<ScrollNews> listenre, ErrorListener errorListener) {			
		if(mScrollNewsRequest != null) {
			mScrollNewsRequest.cancel();
		}	
		String url = NetConfig.getServerBaseUrl() + NetConfig.EXTEND_URL + methodUrl;
		mScrollNewsRequest = new ScrollNewsRequest(method, url, params, listenre, errorListener);
		startRequest(mScrollNewsRequest);		
	}
	
	/**
	 * 处理滚动新闻
	 * @author liangbx
	 *
	 */
	class ScrollNewsListener implements Listener<ScrollNews> {

		@Override
		public void onResponse(ScrollNews scrollNews) {
			mScrollNews = scrollNews;			
			
			if(mScrollNews.respCode == RespCode.SUCCESS) {
				FinalDb finalDb = CityLifeApp.getInstance().getDb();
				
				if(mScrollNewsList != null) {
					for(DbScrollNews newItem : mScrollNewsList) {
						finalDb.deleteById(DbScrollNews.class, newItem.id);
					}										
				} 
				
				mScrollNewsList = new ArrayList<DbScrollNews>();
				for(int i=0; i<mScrollNews.datas.size(); i++) {
					DbScrollNews scrolllNews = new DbScrollNews();
					NewsItem newsItem = mScrollNews.datas.get(i);
					scrolllNews.newsId = newsItem.id;
					scrolllNews.type = newsItem.type;
					scrolllNews.title = newsItem.title;
					scrolllNews.imageUrl = newsItem.imageUrl;
					scrolllNews.isVideo = newsItem.isVideo;
					mScrollNewsList.add(scrolllNews);
					finalDb.save(scrolllNews);
				}
				
				if(mHomeListener != null) {
					mHomeListener.onScrollNewsCommplete(mScrollNewsList);
				}
			}
		}
		
	}
	
	@Override
	public void setHomeListener(HomeListener listener) {
		mHomeListener = listener;
		if(mChannelList != null && mChannelList.size() != 0) {
			mHomeListener.onLoadLocalChannel(mChannelList);
		}
		
		if(mScrollNewsList != null && mScrollNewsList.size() != 0) {
			mHomeListener.onLoadLocalScrollNews(mScrollNewsList);
		}
	}	
	
	@Override
	public void setMyCenter(MyCenterListener listener) {
		mCenterListener = listener;
	}
	
	//应用返回时的结果处理
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(resultCode == ResultCode.RESULT_LOGIN) {
			//登录成功后的处理 显示用户名称、用户头像
			mCenterListener.onLogin();
		}
	}

	@Override
	public void hideActionBar() {
		
	}

	@Override
	public void showActionBar() {
		
	}

	@Override
	public void onClosed() {
		welcomeView.setVisibility(View.GONE);
		if (mTipsTextView != null && mTipsAnimation != null)
			mTipsTextView.clearAnimation();
	}	
}
