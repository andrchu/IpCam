package net.kaicong.ipcam.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.R;
import net.kaicong.ipcam.adpater.MessItemAdapter;
import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.MessRecord;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.user.UserAccount;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.utility.ApiClientUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: lu_qwen Intro: 消息列表 Time: 2015/7/21.
 */
public class MessListActivity extends BaseActivity implements
		PullToRefreshBase.OnRefreshListener2,
		PullToRefreshBase.OnLastItemVisibleListener,
		AdapterView.OnItemClickListener {

	private Button isRead;
	private TextView empty;
	private PullToRefreshListView mPullRefreshListView;
	private MessItemAdapter adapter;
	private int pageIndex = 1;

	private List<MessRecord> list;

	private ListView listView;

	private String msgID;

	private final static int REQUEST_DETAIL = 10001;
	// 记录已读数目
	private int calNum = 0;
	// 防止重复已读同一个消息
	private String tmpID = "";

	// 详情页面用
	private ArrayList<String> list_url = new ArrayList<>();

	private int unReadNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messagelist);
		initTitle(getString(R.string.mess_moveWarn));
		showBackButton();
		init();
	}

	private void init() {
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		isRead = (Button) findViewById(R.id.btn_isSeen);
		isRead.setVisibility(View.VISIBLE);
		empty = (TextView) findViewById(R.id.empty_view);
		isRead.setOnClickListener(this);
		adapter = new MessItemAdapter(this);
		list = new ArrayList<>();

		mPullRefreshListView.setOnRefreshListener(this);
		mPullRefreshListView.setOnLastItemVisibleListener(this);
		mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

		listView = mPullRefreshListView.getRefreshableView();
		adapter.setData(list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);

		getData();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.btn_isSeen:
			allRead();
			break;
		}
	}

	@Override
	public void onLastItemVisible() {

	}

	// 下拉刷新
	@Override
	public void onPullDownToRefresh(PullToRefreshBase refreshView) {
		pageIndex = 1;
		getData();
	}

	// 上啦滚动
	@Override
	public void onPullUpToRefresh(PullToRefreshBase refreshView) {
		pageIndex++;
		getData();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		msgID = list.get(position - 1).id;
		// 传递 当前 的 页数索引
		String url = list.get(position - 1).imgUrl;
		Intent intent = new Intent(this, MessDetailActivity.class);
		intent.putStringArrayListExtra("url_list", list_url);
		intent.putExtra("pageIndex", pageIndex);
		intent.putExtra("url", url);
		intent.putExtra("kind", 0);
		startActivityForResult(intent, REQUEST_DETAIL);
	}

	private void getData() {
		Map<String, String> map = new HashMap<>();
		map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
		map.put(CameraConstants.PAGE_SIZE, CameraConstants.NORMAL_PAGE_SIZE);
		map.put(CameraConstants.PAGE_INDEX, pageIndex + "");
		doPost(UrlResources.URL_WARN_LIST, ApiClientUtility.getParams(map),
				new VolleyResponse(MessListActivity.this, true,
						getString(R.string.com_facebook_loading)) {

					@Override
					public void onSuccess(JSONObject response) {
						super.onSuccess(response);
						if (response != null) {
							if (response.has("code")) {
								int code = response.optInt("code");
								if (code == 1) {
									// 访问成功
									if (response.has("unreaded_total")) {
										unReadNum = response
												.optInt("unreaded_total");
									}
									if (response.has("items")) {
										JSONArray result = response
												.optJSONArray("items");
										mPullRefreshListView
												.onRefreshComplete();
										mPullRefreshListView
												.onRefreshComplete();
										MessRecord record = MessRecord
												.getData(result);
										if (mPullRefreshListView
												.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_START) {
											// 下拉刷新
											if (record.data.size() == 0) {
												empty.setVisibility(View.VISIBLE);
												isRead.setVisibility(View.GONE);
												return;
											}
											empty.setVisibility(View.GONE);

											if (unReadNum == 0) {
												isRead.setVisibility(View.GONE);
											} else {
												isRead.setVisibility(View.VISIBLE);
											}
											list.clear();

											list.clear();
											list.addAll(record.data);
											// 图片地址集合
											list_url.clear();
											list_url.addAll(record.urlList);

											adapter.setData(list);
											adapter.notifyDataSetChanged();
											mPullRefreshListView
													.getRefreshableView()
													.setSelection(0);
											mPullRefreshListView.setTag(0);
										} else if (mPullRefreshListView
												.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_END) {
											// 上拉加载
											list.addAll(record.data);
											list_url.addAll(record.urlList);
											adapter.setData(list);
											adapter.notifyDataSetChanged();
										}
									}
								}
							}
						}
					}

					@Override
					protected void onTaskFailure() {
						super.onTaskFailure();
						mPullRefreshListView.onRefreshComplete();

					}
				});
	}

	private void allRead() {
		Map<String, String> map = new HashMap<>();
		map.put("user_id", String.valueOf(UserAccount.getUserID()));
		map.put("is_update", "1");// 0表示全部删除 1:标记为已读
		// map.put("timestamp", "" + System.currentTimeMillis() / 1000);
		// map.put("guid", UUID.randomUUID().toString());
		doPost(UrlResources.URL_WARN_READALL, ApiClientUtility.getParams(map),
				new VolleyResponse(MessListActivity.this, true,
						getString(R.string.com_facebook_loading)) {
					@Override
					public void onTaskSuccessRoot(JSONObject response) {
						super.onTaskSuccessRoot(response);
						// 成功
						unReadNum = 0;
						isRead.setVisibility(View.GONE);
					}

					@Override
					public void onError(VolleyError error) {
						super.onError(error);
						LogUtil.e("lqw", error + "");
					}
				});
	}

	private void singleIsRead() {
		Map<String, String> map = new HashMap<>();
		map.put("msg_id", msgID);
		map.put("is_update", "1");// 0表示全部删除 1:标记为已读

		doPost(UrlResources.URL_WARN_READONE, ApiClientUtility.getParams(map),
				new VolleyResponse(MessListActivity.this, true,
						getString(R.string.com_facebook_loading)) {
					@Override
					public void onTaskSuccessRoot(JSONObject response) {
						super.onTaskSuccessRoot(response);
						// 成功
						unReadNum--;
						if (tmpID != msgID) {
							tmpID = msgID;
						}
					}

					@Override
					public void onError(VolleyError error) {
						super.onError(error);
						LogUtil.e("lqw", error + "");
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == REQUEST_DETAIL) {
			if (tmpID != msgID) {
				singleIsRead();
			}
		}
	}

	@Override
	public void doBackButtonAction() {
		Intent int1 = new Intent();
		int1.putExtra("readNum", unReadNum + "");
		setResult(RESULT_OK, int1);
		finish();
		super.doBackButtonAction();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent int1 = new Intent();
			int1.putExtra("readNum", unReadNum + "");
			setResult(RESULT_OK, int1);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
