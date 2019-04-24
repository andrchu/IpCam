package net.kaicong.ipcam.message;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.KCApplication;
import net.kaicong.ipcam.MainActivity;
import net.kaicong.ipcam.R;
import net.kaicong.ipcam.adpater.Mess_CommentAdapter;
import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.Mess_ComRecord;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.fragment.RecentFragment;
import net.kaicong.ipcam.user.UserAccount;
import net.kaicong.ipcam.utils.LocationUtil;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.ipcam.view.PicCommentDialog;
import net.kaicong.utility.ApiClientUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: lu_qwen Intro: 消息--评论列表 Time: 2015/7/23.
 */
public class CommentListActivity extends BaseActivity implements
		PullToRefreshBase.OnRefreshListener2,
		PullToRefreshBase.OnLastItemVisibleListener,
		AdapterView.OnItemClickListener, Mess_CommentAdapter.onMyClickListener,
		PicCommentDialog.OnPicCommitListener {

	private Button isRead;
	private TextView empty;
	private PullToRefreshListView mPullRefreshListView;
	private Mess_CommentAdapter adapter;
	private int pageIndex = 1;

	private List<Mess_ComRecord> list;

	private ListView listView;

	private String rewiewId;

	public final static int REQUEST_COMMENT_ITEM = 0x116;
	// 记录已读数目
	private int calNum = 0;
	// 防止重复已读同一个消息
	private String tmpID = "";

	private PicCommentDialog picCommentDialog;

	private int index = 0;

	public static final int REFRESH_MODE_COMMENT = 100;

	private ArrayList<String> list_url = new ArrayList<>();

	private int unReadNum = 0;

	private int from = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mess_commentlist);
		initTitle(getString(R.string.mess_commentMess));
		showBackButton();
		init();
	}

	private void init() {
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		empty = (TextView) findViewById(R.id.empty_view);
		adapter = new Mess_CommentAdapter(this, this);
		list = new ArrayList<>();
		isRead = (Button) findViewById(R.id.btn_isSeen);
		isRead.setVisibility(View.VISIBLE);
		isRead.setOnClickListener(this);

		mPullRefreshListView.setOnRefreshListener(this);
		mPullRefreshListView.setOnLastItemVisibleListener(this);
		mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

		listView = mPullRefreshListView.getRefreshableView();
		adapter.setData(list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);

		from = getIntent().getIntExtra("from", 0);

		getData();
	}

	private void getData() {
		Map<String, String> map = new HashMap<>();
		map.put(CameraConstants.USER_ID,
				String.valueOf(UserAccount.getUserID()));
		map.put(CameraConstants.PAGE_INDEX, pageIndex + "");
		doPost(UrlResources.URL_MESS_COMMENT_LIST,
				ApiClientUtility.getParams(map), new VolleyResponse(
						CommentListActivity.this, true,
						getString(R.string.com_facebook_loading)) {

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
										Mess_ComRecord record = Mess_ComRecord
												.getData(result);
										if (mPullRefreshListView
												.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_START
												|| (int) mPullRefreshListView
														.getTag() == REFRESH_MODE_COMMENT) {
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
											list.addAll(record.data);

											list_url.clear();
											list_url.addAll(record.imglist);

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
											list_url.addAll(record.imglist);
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
		// String.valueOf(UserAccount.getUserID())
		map.put("user_id", String.valueOf(UserAccount.getUserID()));
		doPost(UrlResources.URL_MESS_COMMENT_READALL,
				ApiClientUtility.getParams(map), new VolleyResponse(
						CommentListActivity.this, true,
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
		map.put("review_id", rewiewId);
		map.put("is_update", "1");// 0表示全部删除 1:标记为已读

		doPost(UrlResources.URL_MESS_COMMENT_READONE,
				ApiClientUtility.getParams(map), new VolleyResponse(
						CommentListActivity.this, true,
						getString(R.string.com_facebook_loading)) {
					@Override
					public void onTaskSuccessRoot(JSONObject response) {
						super.onTaskSuccessRoot(response);
						// 成功
						unReadNum--;
						if (tmpID != rewiewId) {
							tmpID = rewiewId;
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

	// 点击回复
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (!UserAccount.isUserLogin()) {
			makeToast(getString(R.string.see_world_login_first_when_comment));
			return;
		}
		doPicComment(position);
		index = position - 1;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK) {
			return;
		}
		if (requestCode == REQUEST_COMMENT_ITEM) {
			// if (tmpID != rewiewId) {
			// singleIsRead();
			// }
		}
	}

	@Override
	public void onImageClick(int position) {
		rewiewId = list.get(position).id;
		String url = list.get(position).picUrl_b;
		if (url.length() >= 1) {
			Intent intent = new Intent(this, MessDetailActivity.class);
			intent.putStringArrayListExtra("url_list", list_url);
			intent.putExtra("pageIndex", pageIndex);
			intent.putExtra("url", url);
			intent.putExtra("kind", 1);
			startActivityForResult(intent, REQUEST_COMMENT_ITEM);
		} else {
		}
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

	// 回复框
	private void doPicComment(int i) {
		if (picCommentDialog == null) {
			picCommentDialog = new PicCommentDialog(CommentListActivity.this,
					R.style.ZhiYunVideoSettingDialog, this, null);
		}
		picCommentDialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		WindowManager.LayoutParams params = picCommentDialog.getWindow()
				.getAttributes();
		params.width = KCApplication.getWindowWidth();
		params.height = getResources().getDimensionPixelSize(
				R.dimen.common_edittext_height);
		params.x = 0;
		params.y = KCApplication.getWindowHeight() - params.height / 2;
		picCommentDialog.getWindow().setAttributes(params);
		picCommentDialog.show();
		picCommentDialog.setMode(PicCommentDialog.MODE_REPLY,
				list.get(i - 1).username);
		picCommentDialog
				.setCursorPosition(list.get(i - 1).username.length() + 4);

	}

	@Override
	public void onPicCommentCommit(int mode, String editStr, int position) {
		if (mode == PicCommentDialog.MODE_DISMISS) {
			dismissDialog();
		} else {
			postComment(editStr, position);
		}
	}

	public void postComment(String editStr, int position) {
		Map<String, String> map = new HashMap<>();
		map.put(CameraConstants.USER_ID,
				String.valueOf(UserAccount.getUserID()));
		map.put(CameraConstants.DEVICE_ID, list.get(index).deviceId);
		map.put(CameraConstants.CONTENT, editStr);
		map.put("Longitude", LocationUtil.getLatitude(this));
		map.put("Latitude", LocationUtil.getLongitude(this));
		map.put("Image", "");
		map = ApiClientUtility.getParams(map);
		// 推送类型
		map.put("TerminalSystemType", "20");
		// 无图模式提交评论
		doPost(UrlResources.URL_COMMIT_COMMENT_2,
				map,
				new VolleyResponse(
						this,
						true,
						getString(R.string.activity_base_progress_dialog_content)) {
					@Override
					public void onTaskSuccessRoot(JSONObject obj) {
						makeToast("回复成功");
						// mPullRefreshListView.setTag(REFRESH_MODE_COMMENT);
						// pageIndex = 1;
						// getData();

						// listView.setSelection(index + 1);
						// adapter.notifyDataSetInvalidated();//通知adapter数据有变化
					}
				});
	}

	private void dismissDialog() {
		if (picCommentDialog != null) {
			picCommentDialog.dismiss();
		}
	}

}
