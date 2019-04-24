package net.kaicong.ipcam.device.seeworld;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.KCApplication;
import net.kaicong.ipcam.R;

import com.android.volley.VolleyError;

import net.kaicong.ipcam.UmengShareUtils;
import net.kaicong.ipcam.api.VolleyHttpUtil;
import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.PositionModel;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.device.record.Record;
import net.kaicong.ipcam.fragment.CommentsFragment;
import net.kaicong.ipcam.fragment.SharedDevicesFragment;
import net.kaicong.ipcam.fragment.SummaryFragment;
import net.kaicong.ipcam.user.UserAccount;

import com.viewpagerindicator.TabPageIndicator;

import net.kaicong.ipcam.utils.CapturePhotoUtils;
import net.kaicong.ipcam.utils.StringUtils;
import net.kaicong.ipcam.utils.ToastUtil;
import net.kaicong.ipcam.utils.ToolUtil;
import net.kaicong.utility.ApiClientUtility;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 看世界播放器之间有很多公共的部分，将这些公共部分封装来供各个播放器继承 Created by LingYan on 14-12-17.
 */
public abstract class BaseSeeWorldActivity extends BaseActivity implements
		View.OnTouchListener {

	private RelativeLayout parentLayout;
	protected TextView progressBarText;
	protected ImageView animationImageView;
	protected AnimationDrawable animationDrawable;
	protected boolean isShowProgressBar = true;
	protected LinearLayout pageIndicatorLayout;
	private LinearLayout seeWorldBottomView;
	protected RelativeLayout monitorLayout;
	private ImageView popSnap;// 返回
	private LinearLayout popThumbUp;// 赞
	private TextView thumbUpCount;//
	private ImageView popRecord;// 分享
	private ImageView popCollect;// 收藏
	private ImageView popReward;// 打赏
	protected Animation blinkAnimation;
	protected ImageView blinkImageView;
	protected Record record;
	protected boolean isRecording = false;
	protected UmengShareUtils umengShareUtils;
	protected Summary summary;

	private ImageView img_back;
	private ImageView img_share;

	public static double longt = 0;// 经
	public static double latt = 0;// 纬

	/**
	 * for viewpager tab indicator
	 */
	private FragmentPagerAdapter pagerAdapter;
	private ViewPager viewPager;
	private TabPageIndicator tabPageIndicator;
	private CommentsFragment commentsFragment;
	private SummaryFragment summaryFragment;
	// private LocationFragment locationFragment;
	private SharedDevicesFragment sharedDevicesFragment;
	private String titles[] = new String[3];

	protected int mVideoWidth = 0;
	protected int mVideoHeight = 0;

	protected String title;

	protected VolleyHttpUtil httpUtil = new VolleyHttpUtil();

	protected boolean isOnPause = false;

	protected boolean isCollected = false;
	// 区分 预览--1//浏览--2
	public static int clickRoute = 0;

	protected static final int RESULT_CODE_SHARE_BACK = 999;

	protected static final int RESULT_CODE_REWARD_BACK = 666;

	private String rewardMoney;

	protected boolean isRewardSuccess = false;

	public int currentModelId = 0;// 默认智云

	public PositionModel positionModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 全屏设置
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
		// WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// getSupportActionBar().hide();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// 横屏
			getSupportActionBar().hide();
			pageIndicatorLayout.setVisibility(View.GONE);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			// 竖屏
			// getSupportActionBar().show();
			pageIndicatorLayout.setVisibility(View.VISIBLE);
		}
	}

	protected void initCommonView() {
		summary = new Summary();
		summary.deviceId = getIntent().getIntExtra("mDeviceId", 0);
		title = getIntent().getStringExtra("title");
		positionModel = new PositionModel();
		// initTitle(title);
		// showBackButton();
		// showRightButton(R.drawable.video_play_share);
		hiddenBar();

		titles[0] = getString(R.string.see_world_view_comments);
		titles[1] = getString(R.string.see_world_view_summary);
		titles[2] = getString(R.string.fragment_more_shared_devices);

		// data
		getSummaryData();

		parentLayout = (RelativeLayout) findViewById(R.id.root);
		animationImageView = (ImageView) findViewById(R.id.imageAnimation);
		animationImageView.setBackgroundResource(R.drawable.spinner);
		// Typecasting the Animation Drawable
		animationDrawable = (AnimationDrawable) animationImageView
				.getBackground();
		pageIndicatorLayout = (LinearLayout) findViewById(R.id.page_indicator_layout);
		seeWorldBottomView = (LinearLayout) findViewById(R.id.ctrl_layout);
		progressBarText = (TextView) findViewById(R.id.progress_bar_text);
		popSnap = (ImageView) findViewById(R.id.pop_1);
		popSnap.setColorFilter(Color.WHITE);
		popSnap.setOnClickListener(this);
		popThumbUp = (LinearLayout) findViewById(R.id.pop_2);
		popThumbUp.setOnClickListener(this);
		thumbUpCount = (TextView) findViewById(R.id.pop_2_text);
		popRecord = (ImageView) findViewById(R.id.pop_3);
		popRecord.setColorFilter(Color.WHITE);
		popRecord.setTag(false);
		popRecord.setOnClickListener(this);
		popCollect = (ImageView) findViewById(R.id.pop_4);
		popCollect.setOnClickListener(this);
		popReward = (ImageView) findViewById(R.id.reward);
		popReward.setOnClickListener(this);
		popReward.setColorFilter(getResources()
				.getColor(R.color.kaicong_orange));
		img_back = (ImageView) findViewById(R.id.img_back);
		img_back.setOnClickListener(this);
		img_back.setColorFilter(getResources().getColor(R.color.kaicong_orange));
		img_share = (ImageView) findViewById(R.id.img_share);
		img_share.setOnClickListener(this);
		img_share.setColorFilter(getResources()
				.getColor(R.color.kaicong_orange));

		umengShareUtils = new UmengShareUtils(this);

		monitorLayout = (RelativeLayout) findViewById(R.id.monitor_layout);

		blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink);
		blinkImageView = (ImageView) findViewById(R.id.imageview_recording);

		checkKeyboardHeight(parentLayout);

	}

	@Override
	public void doBackButtonAction() {
		handler.removeCallbacks(runnable);
		quitIfRecording();
	}

	@Override
	public void doRightButtonAction(View view) {
		super.doRightButtonAction(view);
		share();
	}

	private class SwitchTabAdapter extends FragmentPagerAdapter {

		public SwitchTabAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			if (i == 0) {
				if (commentsFragment == null) {
					commentsFragment = new CommentsFragment();
					commentsFragment.setSummary(summary);
				}
				return commentsFragment;
			} else if (i == 1) {
				if (summaryFragment == null) {
					summaryFragment = new SummaryFragment();
					summaryFragment.setSummary(summary);
					summaryFragment.setPositionModel(positionModel);
				}
				return summaryFragment;
			} else if (i == 2) {
				// if (locationFragment == null) {
				// locationFragment = new LocationFragment();
				// }
				// return locationFragment;
				if (sharedDevicesFragment == null) {
					sharedDevicesFragment = new SharedDevicesFragment();
					sharedDevicesFragment.setSummary(summary);
				}
				return sharedDevicesFragment;
			}
			return null;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

		@Override
		public int getCount() {
			return 3;
		}
	}

	private void checkKeyboardHeight(final View parentLayout) {
		parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(

		new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				Rect r = new Rect();
				parentLayout.getWindowVisibleDisplayFrame(r);
				int screenHeight = parentLayout.getRootView().getHeight();
				int heightDifference = screenHeight - (r.bottom);
				if (heightDifference > 100) {
					commentsFragment.showPicCommentDialog(heightDifference);
				} else if (heightDifference == 0) {

				}
			}
		});
	}

	private Handler handler = new Handler();
	private boolean isShowBottom = false;
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					isShowBottom = false;
					seeWorldBottomView.setVisibility(View.GONE);
				}
			});
		}
	};

	protected void showBottomView() {
		if (isShowBottom) {
			seeWorldBottomView.setVisibility(View.GONE);
			handler.removeCallbacks(runnable);
			isShowBottom = false;
		} else {
			seeWorldBottomView.setVisibility(View.VISIBLE);
			handler.postDelayed(runnable, 5000);
			isShowBottom = true;
		}
	}

	protected void getSummaryData() {
		Map<String, String> map = new HashMap<>();
		map.put(CameraConstants.USER_ID,
				String.valueOf(UserAccount.getUserID()));
		map.put(CameraConstants.DEVICE_ID, String.valueOf(summary.deviceId));
		httpUtil.doJsonObjectRequest(
				UrlResources.URL_GET_PUBLIC_DEVICE_INFO_NEW, ApiClientUtility
						.getParams(map), new VolleyResponse(this, false,
						getString(R.string.com_facebook_loading)) {
					@Override
					public void onTaskSuccess(JSONObject result) {
						super.onTaskSuccess(result);
						summary.userId = result.optInt("user_id");
						summary.shareTitle = result.optString("share_title");
						summary.shareUser = result.optString("username");
						summary.shareTime = result.optString("shared_time");
						summary.shareHotNum = result.optString("visit_count");
						summary.modelId = result.optInt("ddns_modelid");
						summary.praiseCount = result.optInt("praise_count");
						summary.reviewCount = result.optInt("review_count");
						thumbUpCount.setText(String
								.valueOf(summary.praiseCount));
						// summary.isfavorite = result.optBoolean("isfavorite");
						isCollected = result.optBoolean("isfavorite");
						if (isCollected) {
							popCollect.setColorFilter(Color.RED);
						}
						// **********add*********
						if (null != result.opt("longitude")
								&& null != result.opt("latitude")) {
							longt = result.optDouble("longitude");
							latt = result.optDouble("latitude");
						}

						/**
						 * 地标相关
						 */
						positionModel.LandmarkDesc = StringUtils.isEmpty(result
								.optString("LandmarkDesc")) ? "" : result
								.optString("LandmarkDesc");
						positionModel.LandmarkName = StringUtils.isEmpty(result
								.optString("LandmarkName")) ? "" : result
								.optString("LandmarkName");
						positionModel.LandmarkPic = StringUtils.isEmpty(result
								.optString("LandmarkPic")) ? "" : result
								.optString("LandmarkPic");
						if (StringUtils.isEmpty(result.optString("IsVerify"))) {
							// 为空，说明没有提交过申请
							positionModel.IsVerify = -1;
						} else if (result.optBoolean("IsVerify")) {
							// 申请通过
							positionModel.IsVerify = 1;
						} else if (!result.optBoolean("IsVerify")) {
							// 申请暂未通过或者拒绝
							positionModel.IsVerify = 0;
						}
						positionModel.ApplyTime = StringUtils.isEmpty(result
								.optString("ApplyTime")) ? "" : result
								.optString("ApplyTime");
						positionModel.YesOrNoTime = StringUtils.isEmpty(result
								.optString("YesOrNoTime")) ? "" : result
								.optString("YesOrNoTime");
						positionModel.Reason = StringUtils.isEmpty(result
								.optString("Reason")) ? "" : result
								.optString("Reason");

						
						pagerAdapter = new SwitchTabAdapter(
								getSupportFragmentManager());
						viewPager = (ViewPager) findViewById(R.id.pager);
						viewPager.setAdapter(pagerAdapter);
						tabPageIndicator = (TabPageIndicator) findViewById(R.id.indicator);
						tabPageIndicator.setVisibility(View.VISIBLE);
						tabPageIndicator.setViewPager(viewPager);
						viewPager.setOffscreenPageLimit(3);

					}
				});
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		switch (motionEvent.getAction()) {
		case MotionEvent.ACTION_DOWN:

			break;
		case MotionEvent.ACTION_UP:
			showBottomView();
			break;
		}
		return true;
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.pop_1:
			// 保存截图
			String name = ToolUtil.getNowTimeStr();
			CapturePhotoUtils.insertImage(getContentResolver(), getBitmap(),
					name, name);
			Toast.makeText(this, getString(R.string.tips_snapshot_ok),
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.pop_2:
			// thumb
			if (!UserAccount.isUserLogin()) {
				Toast.makeText(this,
						getString(R.string.see_world_login_first_when_thumb),
						Toast.LENGTH_LONG).show();
				return;
			}
			thumbUp();
			break;
		case R.id.pop_3:
			// 录制视频
			if (!(boolean) popRecord.getTag()) {
				// 开始录制
				blinkImageView.setVisibility(View.VISIBLE);
				blinkImageView.setColorFilter(Color.RED);
				blinkImageView.startAnimation(blinkAnimation);
				popRecord.setColorFilter(Color.RED);
				popRecord.setTag(true);
				if (record != null) {
					startRecording();
					isRecording = true;
				}
			} else {
				// 取消录制
				blinkImageView.clearAnimation();
				blinkImageView.setVisibility(View.GONE);
				popRecord.setColorFilter(Color.WHITE);
				popRecord.setTag(false);
				new StopRecordingTask().execute();
				isRecording = false;
			}
			break;
		case R.id.pop_4:
			// collect 收藏设备
			if (!UserAccount.isUserLogin()) {
				ToastUtil.showToast(this,
						R.string.see_world_login_first_when_collect);
				return;
			}
			collectDevice();
			break;
		case R.id.reward:
			// 打赏
			if (!UserAccount.isUserLogin()) {
				ToastUtil.showToast(this,
						R.string.login_user_please_login_first);
				return;
			}
			reWard();
			break;
		case R.id.img_back:
			// 返回
			handler.removeCallbacks(runnable);
			quitIfRecording();
			break;
		case R.id.img_share:
			// 分享
			share();
			break;
		}
	}

	/**
	 * 停止录制
	 */
	public class StopRecordingTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog();
			setProgressText(getString(R.string.progress_text_save_recording));
		}

		@Override
		protected Void doInBackground(Void... voids) {
			stopRecording();
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			Toast.makeText(BaseSeeWorldActivity.this,
					getString(R.string.progress_text_save_record_success),
					Toast.LENGTH_LONG).show();
			removeProgressDialog();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// 返回键处理
			handler.removeCallbacks(runnable);
			quitIfRecording();
		}
		return true;
	}

	public void quitIfRecording() {
		if (isRecording) {
			new AlertDialog.Builder(this)
					.setTitle(
							getString(R.string.video_setting_save_record_and_quit))
					.setPositiveButton(getString(R.string.btn_ok),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialogInterface, int i) {
									dialogInterface.dismiss();
									new StopRecordingTask().execute();
									isRecording = false;
									quit(true);
								}
							})
					.setNegativeButton(getString(R.string.btn_cancel),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialogInterface, int i) {
									dialogInterface.dismiss();
								}
							}).create().show();
		} else {
			quit(true);
		}
	}

	/**
	 * 点赞
	 */
	protected void thumbUp() {
		Map<String, String> map = new HashMap<>();
		map.put(CameraConstants.USER_ID,
				String.valueOf(UserAccount.getUserID()));
		map.put(CameraConstants.DEVICE_ID, String.valueOf(summary.deviceId));
		httpUtil.doJsonObjectRequest(UrlResources.URL_CREATE_PRAISE,
				ApiClientUtility.getParams(map), new VolleyResponse(this,
						false, getString(R.string.com_facebook_loading)) {

					@Override
					public void onSuccess(JSONObject result) {
						super.onTaskSuccess(result);
						thumbUpCount.setText(String
								.valueOf(summary.praiseCount + 1));
						ToastUtil.showToast(BaseSeeWorldActivity.this,
								R.string.see_world_praise_success);
					}

				});
	}

	/**
	 * 收藏设备
	 */
	protected void collectDevice() {
		if (isCollected) {// 已收藏---取消收藏
			new AlertDialog.Builder(this)
					.setTitle(getString(R.string.see_world_delete_collect))
					.setPositiveButton(getString(R.string.btn_ok),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialogInterface, int i) {
									popCollect.setColorFilter(Color.WHITE);// 先修改图标
									Map<String, String> map = new HashMap<>();
									map.put(CameraConstants.USER_ID, String
											.valueOf(UserAccount.getUserID()));
									map.put(CameraConstants.DEVICE_ID,
											String.valueOf(summary.deviceId));
									doPost(UrlResources.URL_DELETE_COLLECT,
											ApiClientUtility.getParams(map),
											new VolleyResponse(
													BaseSeeWorldActivity.this,
													false,
													getString(R.string.com_facebook_loading)) {

												@Override
												protected void onTaskSuccessRoot(
														JSONObject result) {
													super.onTaskSuccess(result);
													isCollected = false;
													ToastUtil
															.showToast(
																	BaseSeeWorldActivity.this,
																	R.string.see_world_collect_deleted);
												}

												@Override
												public void onError(
														VolleyError error) {
													super.onError(error);
													popCollect
															.setColorFilter(Color.RED);// 先修改图标
												}
											});
									dialogInterface.dismiss();
								}
							})
					.setNegativeButton(getString(R.string.btn_cancel),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialogInterface, int i) {
									dialogInterface.dismiss();
								}
							}).create().show();
		} else {// 收藏
			popCollect.setColorFilter(Color.RED);
			Map<String, String> map = new HashMap<>();
			map.put(CameraConstants.USER_ID,
					String.valueOf(UserAccount.getUserID()));
			map.put(CameraConstants.DEVICE_ID, String.valueOf(summary.deviceId));
			httpUtil.doJsonObjectRequest(UrlResources.URL_COLLECT_DEVICE,
					ApiClientUtility.getParams(map), new VolleyResponse(this,
							false, getString(R.string.com_facebook_loading)) {

						@Override
						protected void onTaskSuccessRoot(JSONObject result) {
							isCollected = true;
							ToastUtil.showToast(BaseSeeWorldActivity.this,
									R.string.see_world_collect_success);
						}

						@Override
						public void onError(VolleyError error) {
							super.onError(error);
							popCollect.setColorFilter(Color.WHITE);
						}
					});
		}
	}

	// 打赏
	private void reWard() {
		Intent intent = new Intent(this, ReWardActivity.class);
		intent.putExtra("T_UserId", String.valueOf(summary.userId));// 被打赏者id
		intent.putExtra("T_DeviceId", String.valueOf(summary.deviceId));// 被打赏者分享的设备id
		startActivityForResult(intent, RESULT_CODE_REWARD_BACK);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if (resultCode != RESULT_OK) {
		// return;
		// }
		umengShareUtils.doSSOHandler(requestCode, resultCode, data);
		// 判断返回途径
		if (clickRoute == 1) {
			commentsFragment.setShowDialog();
			commentsFragment.showPicCommentDialog(0);
		}
		if (requestCode == RESULT_CODE_REWARD_BACK && resultCode == 001) {
			if (data.getStringExtra("money") != null
					&& data.getStringExtra("money").length() > 0) {
				rewardMoney = data.getStringExtra("money");
			} else {
				rewardMoney = "0";
			}
			isRewardSuccess = true;
			gotoShare(rewardMoney);
		}
	}

	// 根据屏幕方向和视频宽高比来动态分配surfaceView的宽高
	protected boolean doSetLayoutOnOrientation(int videoWidth, int videoHeight) {
		int layoutWidth = 0;
		int layoutHeight = 0;
		// 这里要注意，设置monitor的LayoutParams属性时，width在任何情况下都是手机的此时的宽度
		// 因为Holder的宽度是要充满屏幕的，而surfaceView的宽度就要根据比例来计算
		Configuration cfg = getResources().getConfiguration();
		if (videoWidth > 0 && videoHeight > 0) {
			double ratio = ((double) videoWidth) / videoHeight;
			if (cfg.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				pageIndicatorLayout.setVisibility(View.GONE);
				layoutWidth = (int) (KCApplication.getWindowHeight() * ratio);
				layoutHeight = KCApplication.getWindowHeight();
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						layoutWidth, layoutHeight);
				params.addRule(RelativeLayout.CENTER_IN_PARENT);
				monitorLayout.setLayoutParams(params);
			} else if (cfg.orientation == Configuration.ORIENTATION_PORTRAIT) {
				pageIndicatorLayout.setVisibility(View.VISIBLE);
				layoutWidth = KCApplication.getWindowWidth();
				layoutHeight = (int) (KCApplication.getWindowWidth() / ratio);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						layoutWidth, layoutHeight);
				monitorLayout.setLayoutParams(params);
				RelativeLayout.LayoutParams pageIndicatorParams = new RelativeLayout.LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT);
				pageIndicatorParams.topMargin = layoutHeight;
				pageIndicatorLayout.setLayoutParams(pageIndicatorParams);
			}
			return true;
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	/**
	 * 跳转到分享
	 * 
	 * @param price
	 */
	private void gotoShare(String price) {
		Intent intent = new Intent();
		intent.setClass(BaseSeeWorldActivity.this, RewardShareActivity.class);
		intent.putExtra("price", price);
		startActivityForResult(intent, RESULT_CODE_SHARE_BACK);
	}

	protected void postShareComment() {
		if (commentsFragment != null) {
			commentsFragment
					.postComment(
							getString(R.string.seeworld_reward_comment,
									rewardMoney), 0);
		}
	}

	public abstract void quit(boolean isFinish);

	public abstract void share();

	public abstract Bitmap getBitmap();

	public abstract void startRecording();

	public abstract void stopRecording();

	public abstract void resumePlay();

	public abstract void changePlay(String ip, int port, String account,
			String password, String UID);

}
