package net.kaicong.ipcam.fragment;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.kaicong.ipcam.BaseFragment;
import net.kaicong.ipcam.MainActivity;
import net.kaicong.ipcam.R;
import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.message.CommentListActivity;
import net.kaicong.ipcam.message.CrmListActivity;
import net.kaicong.ipcam.message.MessListActivity;
import net.kaicong.ipcam.message.RewardMeActivity;
import net.kaicong.ipcam.user.LoginActivity;
import net.kaicong.ipcam.user.UserAccount;
import net.kaicong.utility.ApiClientUtility;

import com.android.volley.VolleyError;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LingYan on 2014/8/29.
 */
public class RecentFragment extends BaseFragment {

	public static final int REQUEST_LOGIN_SUCCESS_REFRESH = 1999;

	private LinearLayout rootLayout;
	private LinearLayout moveWarn;
	private LinearLayout comment;
	private LinearLayout reward;
	private LinearLayout crmLayout;

	private TextView tev_num_warn;
	private TextView tev_num_comment;
	private TextView tev_num_reward;
	private TextView tev_num_crm;

	public final static int WARN_READ = 20101;
	public final static int COMMENT_READ = 20102;
	public final static int REWARD_READ = 20103;

	private int warnNum = 0;
	private int messNum = 0;
	private int rewardNum = 0;
	private int crmNum = 0;

	@Override
	protected void initView(View convertView) {
		super.initView(convertView);

		rootLayout = (LinearLayout) convertView.findViewById(R.id.root_layout);
		moveWarn = (LinearLayout) convertView
				.findViewById(R.id.lel_messType_warn);
		moveWarn.setOnClickListener(this);
		comment = (LinearLayout) convertView
				.findViewById(R.id.lel_messType_comment);
		comment.setOnClickListener(this);
		reward = (LinearLayout) convertView
				.findViewById(R.id.lel_messType_reward);
		reward.setOnClickListener(this);
		crmLayout = (LinearLayout) convertView
				.findViewById(R.id.lel_messType_crm);
		crmLayout.setOnClickListener(this);

		tev_num_warn = (TextView) convertView
				.findViewById(R.id.mess_numTip_warn);
		tev_num_comment = (TextView) convertView
				.findViewById(R.id.mess_numTip_comment);
		tev_num_reward = (TextView) convertView
				.findViewById(R.id.mess_numTip_reward);
		tev_num_crm = (TextView) convertView.findViewById(R.id.mess_numTip_crm);
		if (UserAccount.isUserLogin()) {
			rootLayout.setVisibility(View.VISIBLE);
			getWarnNum();
		} else {
			makeToast(getString(R.string.login_user_please_login_first));
			Intent intent = new Intent();
			intent.setClass(this.getActivity(), LoginActivity.class);
			startActivityForResult(intent, REQUEST_LOGIN_SUCCESS_REFRESH);
		}
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
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!UserAccount.isUserLogin()) {
			if (!hidden) {
				// 重新显示的时候判断当前是否已经登陆
				tev_num_warn.setVisibility(View.GONE);
				tev_num_comment.setVisibility(View.GONE);
				tev_num_reward.setVisibility(View.GONE);
				warnNum = 0;
				messNum = 0;
				rewardNum = 0;
				MainActivity.badgeView.hide();
				rootLayout.setVisibility(View.GONE);
				makeToast(getString(R.string.login_user_please_login_first));
			}
		} else if (UserAccount.getLoginUserChanged()) {
			// 当用户登录了其他账号时，这时应该刷新页面
			getWarnNum();
			UserAccount.saveLoginUserChanged(false);
		} else if (UserAccount.isToRefresh()) {
			// 有新消息 刷新页面
			getWarnNum();
			UserAccount.isMessNeedRefresh(false);
		}
		if (UserAccount.isUserLogin()) {
			rootLayout.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.lel_messType_warn:
			// 移动侦测
			Intent intent = new Intent(getActivity(), MessListActivity.class);
			startActivityForResult(intent, WARN_READ);
			break;
		case R.id.lel_messType_comment:
			// 评论消息
			Intent intent2 = new Intent(getActivity(),
					CommentListActivity.class);
			startActivityForResult(intent2, COMMENT_READ);
			break;
		case R.id.lel_messType_reward:
			// 打赏记录
			Intent intent3 = new Intent(getActivity(), RewardMeActivity.class);
			startActivityForResult(intent3, REWARD_READ);
			break;
		case R.id.lel_messType_crm:
			// 系统消息
			Intent intent4 = new Intent(getActivity(), CrmListActivity.class);
			startActivity(intent4);
			break;
		}
	}

	@Override
	public int getLayoutId() {
		return R.layout.fragment_recent;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_LOGIN_SUCCESS_REFRESH) {
			if (UserAccount.isUserLogin()) {
				getWarnNum();
			}
		}
		if (requestCode == WARN_READ) {
			warnNum = Integer.parseInt(data.getStringExtra("readNum"));
			if (warnNum <= 0) {
				tev_num_warn.setVisibility(View.GONE);
			} else {
				if (warnNum < 99) {
					tev_num_warn.setText(warnNum + "");
				} else {
					tev_num_warn.setText("99+");
				}
				setBottomNum();
			}
		}
		if (requestCode == COMMENT_READ) {
			messNum = Integer.parseInt(data.getStringExtra("unreadNum"));
			if (messNum <= 0) {
				tev_num_comment.setVisibility(View.GONE);
			} else {
				if (messNum < 99) {
					tev_num_comment.setText(messNum + "");
				} else {
					tev_num_comment.setText("99+");
				}
			}
			setBottomNum();
		}
		if (requestCode == REWARD_READ) {
			rewardNum = Integer.parseInt(data.getStringExtra("unreadNum"));
			if (rewardNum <= 0) {
				tev_num_reward.setVisibility(View.GONE);
			} else {
				if (rewardNum < 99) {
					tev_num_reward.setText(rewardNum + "");
				} else {
					tev_num_reward.setText("99+");
				}
			}
			setBottomNum();
		}
	}

	// 获取报警消息数目
	private void getWarnNum() {
		Map<String, String> map = new HashMap<>();
		map.put(CameraConstants.USER_ID,
				String.valueOf(UserAccount.getUserID()));
		Log.d("lqw", String.valueOf(UserAccount.getUserID()));
		map.put(CameraConstants.PAGE_SIZE, CameraConstants.NORMAL_PAGE_SIZE);
		map.put(CameraConstants.PAGE_INDEX, "1");
		doPost(UrlResources.URL_WARN_LIST,
				ApiClientUtility.getParams(map),
				new VolleyResponse(
						getActivity(),
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
									if (response.has("unreaded_total")) {
										warnNum = response
												.optInt("unreaded_total");
										if (warnNum > 99) {
											tev_num_warn.setText("99+");
										} else {
											tev_num_warn.setText(warnNum + "");
										}
										if (warnNum != 0) {
											tev_num_warn
													.setVisibility(View.VISIBLE);
										}
										getCommentNum();
									}
								}
							}
						}
					}

					@Override
					public void onError(VolleyError error) {
						super.onError(error);
						tev_num_warn.setVisibility(View.GONE);
						tev_num_comment.setVisibility(View.GONE);
						tev_num_reward.setVisibility(View.GONE);
					}
				});
	}

	private void getCommentNum() {
		Map<String, String> map = new HashMap<>();
		map.put(CameraConstants.USER_ID,
				String.valueOf(UserAccount.getUserID()));
		map.put(CameraConstants.PAGE_INDEX, "1");
		doPost(UrlResources.URL_MESS_COMMENT_LIST,
				ApiClientUtility.getParams(map),
				new VolleyResponse(
						getActivity(),
						false,
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
										messNum = response
												.optInt("unread_total");

										if (messNum > 99) {
											tev_num_comment.setText("99+");
										} else {
											tev_num_comment.setText(messNum
													+ "");
										}
										if (messNum != 0) {
											tev_num_comment
													.setVisibility(View.VISIBLE);
										}
										getRewardNum();
									}
								}
							}
						}
					}

					@Override
					public void onError(VolleyError error) {
						super.onError(error);
						tev_num_comment.setVisibility(View.GONE);
					}
				});
	}

	/**
	 * 获取 打赏消息数
	 */
	private void getRewardNum() {
		Map<String, String> map = new HashMap<>();
		map.put(CameraConstants.USER_ID,
				String.valueOf(UserAccount.getUserID()));
		map.put(CameraConstants.PAGE_INDEX, "1");
		// 别人打赏我
		doPost(UrlResources.URL_MESS_REWARD,
				ApiClientUtility.getParams(map),
				new VolleyResponse(
						getActivity(),
						false,
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
										rewardNum = response
												.optInt("unread_total");
										if (rewardNum > 99) {
											tev_num_reward.setText("99+");
										} else {
											tev_num_reward.setText(rewardNum
													+ "");
										}
										if (rewardNum != 0) {
											tev_num_reward
													.setVisibility(View.VISIBLE);
										}
										setBottomNum();
									}
								}
							}
						}
					}

					@Override
					public void onError(VolleyError error) {
						super.onError(error);
						tev_num_reward.setVisibility(View.GONE);
					}
				});
	}

	/**
	 * 获取 系统消息数
	 */
	private void getCrmNum() {
		// TODO...
	}

	private void setBottomNum() {
		String tmpStr;
		if (warnNum + messNum + rewardNum > 99) {
			tmpStr = "99+";
		} else {
			tmpStr = warnNum + messNum + rewardNum + "";
		}
		if (warnNum + messNum + rewardNum > 0) {
			MainActivity.badgeView.setText(tmpStr);
			MainActivity.badgeView.setTextSize(9);
			MainActivity.badgeView.show();
		} else {
			MainActivity.badgeView.hide();
		}
	}

	public void refresh() {
		getWarnNum();
	}
}
