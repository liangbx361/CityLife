package com.wb.citylife.mk.mycenter;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.db.sqlite.DbModel;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.wb.citylife.R;
import com.wb.citylife.activity.base.BaseActivity;
import com.wb.citylife.adapter.MsgListAdapter;
import com.wb.citylife.app.CityLifeApp;
import com.wb.citylife.bean.PageInfo;
import com.wb.citylife.bean.db.DBMsg;
import com.wb.citylife.config.DbConfig;
import com.wb.citylife.dialog.ConfirmDialog;
import com.wb.citylife.mk.common.CommIntent;
import com.wb.citylife.widget.PullListViewHelper;

/**
 * 消息记录模块
 * @author liangbx
 *
 */
public class MsgHistroyActivity extends BaseActivity implements OnItemClickListener, 
	OnItemLongClickListener{
	
	private PullToRefreshListView mPullListView;
	private PullListViewHelper pullHelper;
	private int loadState;
	
	private ListView mMsgLv;
	private MsgListAdapter msgAdapter;
	
	private PageInfo msgPageInfo;
	private FinalDb finalDb;
	private List<DBMsg> msgList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);			
		setContentView(R.layout.activity_msg);
		
		getIntentData();
		initView();
		
		msgPageInfo = new PageInfo(20, 1);
		finalDb = CityLifeApp.getInstance().getDb();
		msgList = new ArrayList<DBMsg>();
		loadData();
	}
	
	@Override
	public void getIntentData() {
		
	}

	@Override
	public void initView() {
		mPullListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		mPullListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				//滑动到底部的处理
				if(loadState == PullListViewHelper.BOTTOM_STATE_LOAD_IDLE) {
					loadState = PullListViewHelper.BOTTOM_STATE_LOADING;
					msgPageInfo.pageNo++;
					loadData();
				}
			}
			
		});
		
		mPullListView.setMode(Mode.DISABLED);
		
		mMsgLv = mPullListView.getRefreshableView();
		mMsgLv.setOnItemClickListener(this);
		mMsgLv.setOnItemLongClickListener(this);
		mMsgLv.setSelector(getResources().getDrawable(R.drawable.base_list_selector));
		mMsgLv.setDrawSelectorOnTop(true);
		
		pullHelper = new PullListViewHelper(this, mMsgLv);
		pullHelper.setBottomClick(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//此处设置菜单		
		setDisplayHomeAsUpEnabled(true);
		setDisplayShowHomeEnabled(false);
		
		return super.onCreateOptionsMenu(menu);
	}
	
	private void loadData() {
		String sql = "select * from " + DbConfig.TN_MSG + " order by id desc limit " + msgPageInfo.pageSize + " offset " + msgPageInfo.pageSize * (msgPageInfo.pageNo-1);
		List<DbModel> modeList = null;
		try {
			modeList = finalDb.findDbModelListBySQL(sql);
		} catch (SQLiteException e) {
			setEmptyToastText(R.string.msg_empty_toast);
			showEmpty();
			e.printStackTrace();
		}
		
		if(modeList == null || modeList.size() == 0) {
			if(msgPageInfo.pageNo == 1) {
				setEmptyToastText(R.string.msg_empty_toast);
				showEmpty();
			} else {
				loadState = PullListViewHelper.BOTTOM_STATE_NO_MORE_DATE;
			}
			return;
		}
		
		for(DbModel dbModel : modeList) {
			DBMsg dbMsg = new DBMsg();
			dbMsg.msgId = dbModel.getString("msgId");
			dbMsg.type = dbModel.getInt("type");
			dbMsg.title = dbModel.getString("title");
			dbMsg.desc = dbModel.getString("desc");
			msgList.add(dbMsg);
		}
		
		if(msgPageInfo.pageNo == 1) {
			msgAdapter = new MsgListAdapter(this, msgList);
			mMsgLv.setAdapter(msgAdapter);
			if(msgList.size() < msgPageInfo.pageSize) {
				loadState = PullListViewHelper.BOTTOM_STATE_NO_MORE_DATE;
				pullHelper.setBottomState(loadState, msgPageInfo.pageSize);
			}
		} else {
			msgAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		DBMsg msg = msgList.get(position-1);
		CommIntent.startDetailPage(this, msg.msgId, msg.type);
	}
	
	/**
	 * 长按删除消息
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
			long id) {
		final DBMsg msg = msgList.get(position-1);
		String title = getResources().getString(R.string.toast);
		String toast = getResources().getString(R.string.del_shoot_info_toast, msg.title);
		new ConfirmDialog().getDialog(this, title, toast, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				FinalDb finalDb = CityLifeApp.getInstance().getDb();
				finalDb.deleteByWhere(DBMsg.class, "msgId='" + msg.msgId + "'");
				msgList.remove(msg);
				msgAdapter.notifyDataSetChanged();
			}
			
		}).show();
		return true;
	}

}
