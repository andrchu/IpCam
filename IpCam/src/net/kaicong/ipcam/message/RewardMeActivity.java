package net.kaicong.ipcam.message;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.MainActivity;
import net.kaicong.ipcam.R;
import net.kaicong.ipcam.adpater.RewardRecordAdapter;
import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.RewardRecord;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.user.UserAccount;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.utility.ApiClientUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: lu_qwen Intro: 别人搭上我的记录 Time: 2015/7/28.
 */
public class RewardMeActivity extends BaseActivity implements
		PullToRefreshBase.OnRefreshListener2,
		PullToRefreshBase.OnLastItemVisibleListener {
	private ListView listView;
	private PullToRefreshListView mPullRefreshListView;

	private TextView emptyView;
	private List<RewardRecord> toMeData = new ArrayList<>();
	private RewardRecordAdapter adapter;

	private TextView totalSum;
	private LinearLayout lel_showSum;

	private int sum = 0;

	private int pageIndex = 1;

	private int unReadNum;

	private int from = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mess_rewardme);
		initTitle(getString(R.string.aboutUs_rewardRecord));
		showBackButton();
		init();
	}

	private void init() {
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		emptyView = (TextView) findViewById(R.id.empty_view);

		totalSum = (TextView) findViewById(R.id.tev_total_reward);
		lel_showSum = (LinearLayout) findViewById(R.id.lel_showNum);

		adapter = new RewardRecordAdapter();

		mPullRefreshListView.setOnRefreshListener(this);
		mPullRefreshListView.setOnLastItemVisibleListener(this);
		mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

		listView = mPullRefreshListView.getRefreshableView();

		adapter.setData(toMeData);
		listView.setAdapter(adapter);

		from = getIntent().getIntExtra("from", 0);

		getData();
	}

	private void getData() {
		Map<String, String> map = new HashMap<>();
		map.put(CameraConstants.USER_ID,
				String.valueOf(UserAccount.getUserID()));
		map.put(CameraConstants.PAGE_INDEX, pageIndex + "");
		// 别人打赏我
		// UrlResources.URL_REWARD_TO_ME
		doPost(UrlResources.URL_MESS_REWARD,
				ApiClientUtility.getParams(map),
				new VolleyResponse(
						this,
						true,
						getString(R.string.activity_base_progress_dialog_content)) {

					@Override
					public void onSuccess(JSONObject response) {
						super.onSuccess(response);
						if (response != null) {
							if (response.has("code")) {
								int code = response.optInt("code");
								if (code == 1) {
									// 访问成功
									if (response.has("unread_total")) {
										unReadNum = response
												.optInt("unread_total");
									}
									if (response.has("items")) {
										JSONArray result = response
												.optJSONArray("items");
										mPullRefreshListView
												.onRefreshComplete();

										RewardRecord record = RewardRecord
												.getRewardRecord(result);
										if (mPullRefreshListView
												.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_START) {
											if (record.data.size() == 0) {
												emptyView
														.setVisibility(View.VISIBLE);
												lel_showSum
														.setVisibility(View.GONE);
												return;
											}
											toMeData.clear();
											toMeData.addAll(record.data);
											lel_showSum
													.setVisibility(View.VISIBLE);
											emptyView.setVisibility(View.GONE);
											adapter.setData(toMeData);
											sum = 0;
											for (int i = 0; i < record.feeList
													.size(); i++) {
												sum += (int) (record.feeList
														.get(i) * 100);
											}
											LogUtil.d("lqw_sum", sum+"");
											totalSum.setText(sum/100.0 + "");
											adapter.notifyDataSetChanged();
										} else if (mPullRefreshListView
												.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_END) {
											// 上拉加载
											toMeData.addAll(record.data);
											adapter.setData(toMeData);
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

	@Override
	public void doBackButtonAction() {
		Intent int1 = new Intent();
		if (from == 0) {
			int1.putExtra("unreadNum", unReadNum + "");
			setResult(RESULT_OK, int1);
		} else {
			int1.setClass(this, MainActivity.class);
			startActivity(int1);
		}
		finish();
		super.doBackButtonAction();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent int1 = new Intent();
			if (from == 0) {
				int1.putExtra("unreadNum", unReadNum + "");
				setResult(RESULT_OK, int1);
			} else {
				int1.setClass(this, MainActivity.class);
				startActivity(int1);
			}
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
