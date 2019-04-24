package net.kaicong.ipcam.message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;


import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.R;
import net.kaicong.ipcam.adpater.Mess_crmAdapter;
import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.Mess_CrmRecord;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.user.UserAccount;
import net.kaicong.utility.ApiClientUtility;

public class CrmListActivity extends BaseActivity implements
		PullToRefreshBase.OnRefreshListener2,
		PullToRefreshBase.OnLastItemVisibleListener {
	private TextView empty;
	private PullToRefreshListView mPullRefreshListView;
	private int pageIndex = 1;

	private ListView listView;

	private List<Mess_CrmRecord> list;

	private Mess_crmAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mess_crmlist);
		initTitle(getString(R.string.mess_crmMess));
		showBackButton();
		init();
	}

	private void init() {
		empty = (TextView) findViewById(R.id.empty_view);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		list = new ArrayList<>();
		adapter = new Mess_crmAdapter(this);
		
		mPullRefreshListView.setOnRefreshListener(this);
		mPullRefreshListView.setOnLastItemVisibleListener(this);
		mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

		listView = mPullRefreshListView.getRefreshableView();

		adapter.setData(list);
		listView.setAdapter(adapter);

		getData();
	}

	private void getData() {
		Map<String, String> map = new HashMap<>();
		//“57647”
		map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
		map.put(CameraConstants.PAGE_INDEX, pageIndex + "");
		doPost(UrlResources.URL_MESS_CRM, ApiClientUtility.getParams(map),
				new VolleyResponse(CrmListActivity.this, true,
						getString(R.string.com_facebook_loading)) {
					@Override
					protected void onTaskSuccess(JSONArray result) {
						super.onTaskSuccess(result);
						mPullRefreshListView.onRefreshComplete();
						Mess_CrmRecord record = Mess_CrmRecord.getData(result);
						if (mPullRefreshListView.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_START) {
							// 下拉刷新
							if (record.data.size() == 0) {
								empty.setVisibility(View.VISIBLE);
								return;
							}
							empty.setVisibility(View.GONE);
							list.clear();
		                    list.addAll(record.data);
		                    adapter.setData(list);
		                    adapter.notifyDataSetChanged();
							mPullRefreshListView.getRefreshableView()
									.setSelection(0);
							mPullRefreshListView.setTag(0);
						} else if (mPullRefreshListView.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_END) {
							// 上拉加载
							list.addAll(record.data);
							adapter.setData(list);
							adapter.notifyDataSetChanged();
						}
					}

					@Override
					protected void onTaskFailure() {
						super.onTaskFailure();
						mPullRefreshListView.onRefreshComplete();
					}
				});
	}
 
	@Override
	public void onLastItemVisible() {

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase refreshView) {
		pageIndex = 1;
		getData();

	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase refreshView) {
		pageIndex++;
		getData();

	}

}
