package net.kaicong.ipcam.device.zhiyun;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.TextureView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.kaicong.ipcam.BaseSipDeviceActivity;
import net.kaicong.ipcam.R;

import net.kaicong.ipcam.bean.VideoPopWindowIcon;
import net.kaicong.ipcam.db.DBManager;
import net.kaicong.ipcam.device.record.Record;
import net.kaicong.ipcam.utils.CapturePhotoUtils;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.ipcam.utils.StringUtils;
import net.kaicong.ipcam.utils.ToolUtil;
import net.kaicong.ipcam.view.PTZ_Controller_PopWindow;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.Monitor;

import java.util.Random;
import java.util.RandomAccess;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 智云播放 Created by LingYan on 14-10-29.
 */

public class LiveViewActivity extends BaseSipDeviceActivity implements
		IRegisterIOTCListener, View.OnClickListener {

	private static final int STS_FRAME_DATA_CHANGED = 1234566;
	private static final int STS_CHANGE_CHANNEL_STREAMINFO = 99;
	private static final int STS_SNAPSHOT_SCANED = 98;
	private static final int PTZ_SPEED = 8;// 移动速度
	private static final int PTZ_DELAY = 1500;

	private Monitor monitor = null;
	private MyCamera mCamera = null;
	private String mDevUID;
	private String mAccount;
	private String mPassword;
	private String mCameraName;
	// 默认选择通道
	private int mSelectedChannel;
	private String channels[];
	// 默认视频质量
	private int mVideoQuality = 2;
	// 默认视频方向
	private int mVideoFlip = 1;
	// 默认环境模式
	private int mVideoEnvironment = 0;
	// 默认音频选项
	private int mAudioState = 0;
	// 默认分享选项
	private int mShareMode = 0;

	private PTZ_Controller_PopWindow ptz_controller_popWindow;
	private AlertDialog qualityDialog;
	private AlertDialog flipDialog;
	private AlertDialog environmentDialog;
	private AlertDialog audioDialog;
	private AlertDialog channelDialog;

	private LinearLayout channelLayout;

	private int reConnectCount = 0;

	private volatile boolean isShowing = false;
	private volatile boolean isVideoQualityChanged = false;
	private boolean onLine = false;
	private boolean isBackClicked = false;
	private DBManager dbManager;
	private QuitTask quitTask;
	private Record record;

	// 看看看tips
	private Timer timer;
	private TextView tipsView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zhiyun_liveview);
		mDevUID = getIntent().getStringExtra("mDevUID");
		mSelectedChannel = getIntent().getIntExtra("avChannel", 0);
		mAccount = getIntent().getStringExtra("mAccount");
		mPassword = getIntent().getStringExtra("mPassword");
		mCameraName = getIntent().getStringExtra("mCameraName");
		deviceId = getIntent().getIntExtra("mDeviceId", 0);

		mCamera = new MyCamera(mCameraName, mDevUID, mAccount, mPassword);
		monitor = (Monitor) findViewById(R.id.monitor);
		executeOnTouch(monitor);

		channelLayout = (LinearLayout) findViewById(R.id.imageview);
		channelLayout.setOnClickListener(this);

		initCommonView();
		initLiveView();

		tipsView = (TextView) findViewById(R.id.text_tips);
		timer = new Timer();
		final String tipsArray[] = getResources().getStringArray(
				R.array.text_tips);
		final Random random = new Random();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						tipsView.setText(tipsArray[random.nextInt(6)]);
					}
				});
			}

		}, 0, 2000);

	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus && isShowProgressBar) {
			LogUtil.d("chu", "--hasFocus--");
			animationImageView.setVisibility(View.VISIBLE);
			progressBarText.setVisibility(View.VISIBLE);
			animationDrawable.start();
		} else if (!isShowProgressBar) {
			LogUtil.d("chu", "--loseFocus--");
			animationDrawable.stop();
			animationImageView.setVisibility(View.GONE);
			progressBarText.setVisibility(View.GONE);
		}
	}

	private void initLiveView() {
		int topDrawableIds[] = new int[] { R.drawable.video_play_movie,
				R.drawable.video_play_generic_sorting,
				R.drawable.video_play_weather, R.drawable.video_play_mute,
				R.drawable.video_play_share };
		int bottomDrawableIds[] = new int[] { R.drawable.video_play_back,
				R.drawable.video_play_ptz_arrow, R.drawable.video_play_snap,
				R.drawable.video_play_video_camera };
		for (int i = 0; i < topDrawableIds.length; i++) {
			VideoPopWindowIcon videoPopWindowIcon = new VideoPopWindowIcon();
			videoPopWindowIcon.position = i;
			videoPopWindowIcon.isTop = true;
			videoPopWindowIcon.drawableId = topDrawableIds[i];
			mTopIcons.add(videoPopWindowIcon);
		}
		for (int j = 0; j < 4; j++) {
			VideoPopWindowIcon videoPopWindowIcon = new VideoPopWindowIcon();
			videoPopWindowIcon.position = j;
			videoPopWindowIcon.isTop = false;
			videoPopWindowIcon.drawableId = bottomDrawableIds[j];
			mBottomIcons.add(videoPopWindowIcon);
		}
		dbManager = new DBManager(this);
		connectDevice();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mCamera != null) {
			mCamera.stopSpeaking(mSelectedChannel);
			mCamera.stopListening(mSelectedChannel);
			mCamera.stopShow(mSelectedChannel);
		}
		if (monitor != null)
			monitor.deattachCamera();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mCamera != null) {
			mCamera.startShow(mSelectedChannel, true);
			isShowing = false;
			if (mAudioState == 1)
				mCamera.startListening(mSelectedChannel);
			if (mAudioState == 2)
				mCamera.startSpeaking(mSelectedChannel);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		umShareWindow.dismiss();
		if (mVideoWidth > 0 && mVideoHeight > 0) {
			doSetLayoutOnOrientation(mVideoWidth, mVideoHeight);
		}
	}

	// 视频控制
	@Override
	public void onPTZClick(View view, int position) {
		switch (position) {
		case PTZ_Controller_PopWindow.PTZ_CLICK_POSITION_UP:
			if (mCamera != null && mSelectedChannel >= 0)
				mCamera.sendIOCtrl(mSelectedChannel,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
						AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd.parseContent(
								(byte) AVIOCTRLDEFs.AVIOCTRL_PTZ_UP,
								(byte) PTZ_SPEED, (byte) 0, (byte) 0, (byte) 0,
								(byte) 0));
			break;
		case PTZ_Controller_PopWindow.PTZ_CLICK_POSITION_LEFT:
			if (mCamera != null && mSelectedChannel >= 0)
				mCamera.sendIOCtrl(mSelectedChannel,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
						AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd.parseContent(
								(byte) AVIOCTRLDEFs.AVIOCTRL_PTZ_LEFT,
								(byte) PTZ_SPEED, (byte) 0, (byte) 0, (byte) 0,
								(byte) 0));
			break;
		case PTZ_Controller_PopWindow.PTZ_CLICK_POSITION_RIGHT:
			if (mCamera != null && mSelectedChannel >= 0)
				mCamera.sendIOCtrl(mSelectedChannel,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
						AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd.parseContent(
								(byte) AVIOCTRLDEFs.AVIOCTRL_PTZ_RIGHT,
								(byte) PTZ_SPEED, (byte) 0, (byte) 0, (byte) 0,
								(byte) 0));
			break;
		case PTZ_Controller_PopWindow.PTZ_CLICK_POSITION_DOWN:
			if (mCamera != null && mSelectedChannel >= 0)
				mCamera.sendIOCtrl(mSelectedChannel,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
						AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd.parseContent(
								(byte) AVIOCTRLDEFs.AVIOCTRL_PTZ_DOWN,
								(byte) PTZ_SPEED, (byte) 0, (byte) 0, (byte) 0,
								(byte) 0));
			break;
		case PTZ_Controller_PopWindow.PTZ_CLICK_POSITION_LEFT_RIGHT:
			if (mCamera != null && mSelectedChannel >= 0)
				mCamera.sendIOCtrl(mSelectedChannel,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
						AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd.parseContent(
								(byte) AVIOCTRLDEFs.AVIOCTRL_PTZ_LEFT_DOWN,
								(byte) PTZ_SPEED, (byte) 0, (byte) 0, (byte) 0,
								(byte) 0));
			break;
		case PTZ_Controller_PopWindow.PTZ_CLICK_POSITION_UP_DOWN:
			if (mCamera != null && mSelectedChannel >= 0)
				mCamera.sendIOCtrl(mSelectedChannel,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
						AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd.parseContent(
								(byte) AVIOCTRLDEFs.AVIOCTRL_PTZ_RIGHT_DOWN,
								(byte) PTZ_SPEED, (byte) 0, (byte) 0, (byte) 0,
								(byte) 0));
			break;
		case PTZ_Controller_PopWindow.PTZ_CLICK_POSITION_STOP:
			if (mCamera != null && mSelectedChannel >= 0)
				mCamera.sendIOCtrl(mSelectedChannel,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
						AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd.parseContent(
								(byte) AVIOCTRLDEFs.AVIOCTRL_PTZ_STOP,
								(byte) PTZ_SPEED, (byte) 0, (byte) 0, (byte) 0,
								(byte) 0));
			break;
		case PTZ_Controller_PopWindow.PTZ_CLICK_POSITION_ZOOM_IN:
			if (mCamera != null && mSelectedChannel >= 0)
				mCamera.sendIOCtrl(mSelectedChannel,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
						AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd.parseContent(
								(byte) AVIOCTRLDEFs.AVIOCTRL_LENS_ZOOM_IN,
								(byte) PTZ_SPEED, (byte) 0, (byte) 0, (byte) 0,
								(byte) 0));
			break;
		case PTZ_Controller_PopWindow.PTZ_CLICK_POSITION_ZOOM_OUT:
			if (mCamera != null && mSelectedChannel >= 0)
				mCamera.sendIOCtrl(mSelectedChannel,
						AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND,
						AVIOCTRLDEFs.SMsgAVIoctrlPtzCmd.parseContent(
								(byte) AVIOCTRLDEFs.AVIOCTRL_LENS_ZOOM_OUT,
								(byte) PTZ_SPEED, (byte) 0, (byte) 0, (byte) 0,
								(byte) 0));
			break;
		}
	}

	@Override
	public void onVideoPopWindowClick(View parentView, View view, int position,
			boolean isTop) {
		if (isTop) {
			// 顶部
			switch (position) {
			case 0:
				// 分辨率设置
				if (isRecording) {
					new AlertDialog.Builder(this)
							.setTitle(
									getString(R.string.video_setting_save_record))
							.setPositiveButton(getString(R.string.btn_ok),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialogInterface,
												int i) {
											dialogInterface.dismiss();
											new StopRecordingTask().execute();
											isRecording = false;
											videoBottomPopWindow
													.filterWhiteColor(3);
											blinkImageView.clearAnimation();
											blinkImageView
													.setVisibility(View.GONE);
										}
									})
							.setNegativeButton(getString(R.string.btn_cancel),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialogInterface,
												int i) {
											dialogInterface.dismiss();
										}
									}).create().show();
				} else {
					String array[] = getResources().getStringArray(
							R.array.video_quality);
					if (qualityDialog == null) {
						qualityDialog = new AlertDialog.Builder(this)
								.setSingleChoiceItems(array, mVideoQuality - 1,
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialogInterface,
													int i) {
												dialogInterface.dismiss();
												if (mVideoQuality - 1 == i) {
													return;
												}
												mVideoQuality = i + 1;
												changeVideoQualityOrChannel(
														mSelectedChannel,
														mSelectedChannel);
											}
										}).create();
					}
					qualityDialog.show();
				}
				break;
			case 1:
				// 画面方向设置
				// 视频镜像(翻转，垂直，水平等)
				String arrayFlip[] = getResources().getStringArray(
						R.array.video_orientation);
				if (flipDialog == null) {
					flipDialog = new AlertDialog.Builder(this)
							.setSingleChoiceItems(arrayFlip, mVideoFlip,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialogInterface,
												int i) {
											dialogInterface.dismiss();
											if (mVideoFlip == i) {
												return;
											}
											mVideoFlip = i;
											mCamera.sendIOCtrl(
													Camera.DEFAULT_AV_CHANNEL,
													AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_VIDEOMODE_REQ,
													AVIOCTRLDEFs.SMsgAVIoctrlSetVideoModeReq
															.parseContent(
																	mSelectedChannel,
																	(byte) mVideoFlip));
										}
									}).create();
				}
				flipDialog.show();
				break;
			case 2:
				// 环境模式设置
				String arrayEnvironment[] = getResources().getStringArray(
						R.array.video_environment);
				if (environmentDialog == null) {
					environmentDialog = new AlertDialog.Builder(this)
							.setSingleChoiceItems(arrayEnvironment,
									mVideoEnvironment,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialogInterface,
												int i) {
											dialogInterface.dismiss();
											if (mVideoEnvironment == i) {
												return;
											}
											mVideoEnvironment = i;
											mCamera.sendIOCtrl(
													Camera.DEFAULT_AV_CHANNEL,
													AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_ENVIRONMENT_REQ,
													AVIOCTRLDEFs.SMsgAVIoctrlSetEnvironmentReq
															.parseContent(
																	mSelectedChannel,
																	(byte) mVideoEnvironment));
										}
									}).create();
				}
				environmentDialog.show();
				break;
			case 3:
				// 播放音频设置
				String arrayAudio[] = getResources().getStringArray(
						R.array.audio_state);
				if (audioDialog == null) {
					audioDialog = new AlertDialog.Builder(this)
							.setSingleChoiceItems(arrayAudio, mAudioState,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialogInterface,
												int i) {
											dialogInterface.dismiss();
											if (mAudioState == i) {
												return;
											}
											if (i == 0) {
												// 静音
												mCamera.stopListening(mSelectedChannel);
												mCamera.stopSpeaking(mSelectedChannel);
											} else if (i == 1) {
												// 监听
												mCamera.startListening(mSelectedChannel);
												mCamera.stopSpeaking(mSelectedChannel);
											} else if (i == 2) {
												// 对讲
												mCamera.startSpeaking(mSelectedChannel);
												mCamera.stopListening(mSelectedChannel);
											}
										}
									}).create();
				}
				audioDialog.show();
				break;
			case 4:
				// 分享
				showShareDialog(findViewById(R.id.root));
				break;
			}
		} else {
			// 底部
			switch (position) {
			case 0:
				// 返回
				quitIfRecording();
				break;
			case 1:
				// ptz控制
				if (ptz_controller_popWindow == null) {
					ptz_controller_popWindow = new PTZ_Controller_PopWindow(
							this);
					ptz_controller_popWindow.setOnPTZClickListener(this);
				}
				if (ptz_controller_popWindow.isShowing()) {
					ptz_controller_popWindow.dismiss();
					return;
				}
				// ptz控制
				ptz_controller_popWindow.showAtLocation(
						findViewById(R.id.root), Gravity.BOTTOM, 0, 0);
				break;
			case 2:
				// 拍照
				/**
				 * 截图保存 小米手机图片已经保存到DCIM文件夹，但是相册里面看不到，目测是小米系统的bug
				 */
				String name = ToolUtil.getNowTimeStr();
				CapturePhotoUtils.insertImage(getContentResolver(),
						mCamera.Snapshot(mSelectedChannel), name, name);
				Toast.makeText(LiveViewActivity.this,
						getString(R.string.tips_snapshot_ok),
						Toast.LENGTH_SHORT).show();
				break;
			case 3:
				// 录像
				if (mCamera != null && !isShowProgressBar && onLine) {
					if ((boolean) view.getTag() == true) {
						blinkImageView.clearAnimation();
						blinkImageView.setVisibility(View.GONE);
						videoBottomPopWindow.filterWhiteColor(position);
						new StopRecordingTask().execute();
						isRecording = false;
					} else {
						blinkImageView.setVisibility(View.VISIBLE);
						blinkImageView.setColorFilter(Color.RED);
						blinkImageView.startAnimation(blinkAnimation);
						videoBottomPopWindow.filterRedColor(position);
						record.startRecording(mCamera, mSelectedChannel);
						isRecording = true;
					}
				}
				break;
			}
		}
	}

	/**
	 * 退出
	 */
	private class QuitTask extends AsyncTask<Void, Void, byte[]> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			isShowProgressBar = true;
			onWindowFocusChanged(true);
			isBackClicked = true;
			progressBarText.setText(getString(R.string.tips_quitting));
		}

		@Override
		protected byte[] doInBackground(Void... voids) {
			int width = 400;
			int height = 300;
			/**
			 * 注意，刚开始没有使用ThumbnailUtils.extractThumbnail方法转化为缩略图
			 * 当切换成1280*720的视频时，回调传递这个图像时（尽管已经将compress的质量设为0）
			 * 退出的时候依然会崩溃，因为onActivityResult传值太大
			 */
			byte snapshot[] = DBManager.getByteArrayFromBitmap(
					mCamera.Snapshot(mSelectedChannel), width, height);
			if (onLine && snapshot != null) {
				if (dbManager.queryDeviceZhiyunExists(mDevUID)) {
					dbManager.updateDeviceSnapshotByUID(mDevUID, snapshot);
				} else {
					dbManager.addDeviceByZhiyun(mDevUID, snapshot);
				}
				dbManager.closeDB();
				closeDevice();
				return snapshot;
			} else {
				closeDevice();
				return null;
			}

		}

		@Override
		protected void onPostExecute(byte[] aVoid) {
			super.onPostExecute(aVoid);
			if (aVoid == null) {
				Toast.makeText(LiveViewActivity.this,
						getString(R.string.connstus_connection_failed),
						Toast.LENGTH_LONG).show();
			} else {
				Intent data = new Intent();
				data.putExtra("snapshot", aVoid);
				LiveViewActivity.this.setResult(Activity.RESULT_OK, data);
			}
			LiveViewActivity.this.finish();
			LogUtil.d("chu", "退出成功");
		}
	}

	@Override
	public void quit() {
		if (quitTask == null) {
			quitTask = new QuitTask();
			quitTask.execute();
		}
	}

	// 关闭设备
	private void closeDevice() {
		if (mCamera != null && monitor != null) {
			mCamera.unregisterIOTCListener(LiveViewActivity.this);
			mCamera.stopSpeaking(mSelectedChannel);
			mCamera.stopListening(mSelectedChannel);
			mCamera.stopShow(mSelectedChannel);
			mCamera.disconnect();
			monitor.deattachCamera();
			monitor = null;
			mCamera = null;
		}
	}

	// 连接设备
	private void connectDevice() {
		// 初始化
		if (!StringUtils.isEmpty(mAccount) && !StringUtils.isEmpty(mPassword)) {
			mCamera.registerIOTCListener(this);
			mCamera.connect(mDevUID);
			mCamera.start(MyCamera.DEFAULT_AV_CHANNEL, mAccount, mPassword);
			mCamera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
			mCamera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq
							.parseContent());
			mCamera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());
			mCamera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq.parseContent());
			mCamera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSTREAMCTRL_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlGetStreamCtrlReq
							.parseContent(mSelectedChannel));
			mCamera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_VIDEOMODE_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlGetVideoModeReq
							.parseContent(mSelectedChannel));
			mCamera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_ENVIRONMENT_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlGetEnvironmentReq
							.parseContent(mSelectedChannel));
			mCamera.startShow(mSelectedChannel, true);
		}
	}

	@Override
	public void receiveFrameData(final Camera camera, int avChannel, Bitmap bmp) {
		if (bmp != null) {
			if (isVideoQualityChanged) {
				isShowing = false;
				LogUtil.d("chu", "isShowing = false");
			}
			mVideoWidth = bmp.getWidth();
			mVideoHeight = bmp.getHeight();
			Bundle data = new Bundle();
			data.putInt("videoWidth", bmp.getWidth());
			data.putInt("videoHeight", bmp.getHeight());
			Message msg = handler.obtainMessage();
			msg.what = STS_FRAME_DATA_CHANGED;
			msg.setData(data);
			handler.sendMessage(msg);
		}
	}

	@Override
	public void receiveFrameInfo(final Camera camera, int avChannel,
			long bitRate, int frameRate, int onlineNm, int frameCount,
			int incompleteFrameCount) {

		if (mCamera == camera && avChannel == mSelectedChannel) {
			Bundle bundle = new Bundle();
			bundle.putInt("avChannel", avChannel);
			Message msg = handler.obtainMessage();
			msg.what = STS_CHANGE_CHANNEL_STREAMINFO;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

	@Override
	public void receiveChannelInfo(final Camera camera, int avChannel,
			int resultCode) {

		if (mCamera == camera && avChannel == mSelectedChannel) {
			Bundle bundle = new Bundle();
			bundle.putInt("avChannel", avChannel);
			Message msg = handler.obtainMessage();
			msg.what = resultCode;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

	@Override
	public void receiveSessionInfo(final Camera camera, int resultCode) {

		if (mCamera == camera) {
			Bundle bundle = new Bundle();
			Message msg = handler.obtainMessage();
			msg.what = resultCode;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

	@Override
	public void receiveIOCtrlData(final Camera camera, int avChannel,
			int avIOCtrlMsgType, byte[] data) {

		if (mCamera == camera) {
			Bundle bundle = new Bundle();
			bundle.putInt("avChannel", avChannel);
			bundle.putByteArray("data", data);
			Message msg = handler.obtainMessage();
			msg.what = avIOCtrlMsgType;
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Bundle bundle = msg.getData();
			byte ctrlData[] = bundle.getByteArray("data");
			int avChannel = bundle.getInt("avChannel");
			switch (msg.what) {
			case STS_FRAME_DATA_CHANGED:
				if (!isShowing) {
					int videoWidth = msg.getData().getInt("videoWidth");
					int videoHeight = msg.getData().getInt("videoHeight");
					/**
					 * 注意，初始化Record对象要在主线程中执行
					 */
					record = new Record(LiveViewActivity.this, videoWidth,
							videoHeight, deviceId);
					LogUtil.d("chu", "videoWidth=" + videoWidth);
					monitor.monitorRatio = (mVideoWidth * 1.0f) / mVideoHeight;
					if (doSetLayoutOnOrientation(videoWidth, videoHeight)) {
						monitor.mEnableDither = mCamera.mEnableDither;
						monitor.setVisibility(View.VISIBLE);
						monitor.attachCamera(mCamera, mSelectedChannel);
						isShowing = true;
						isVideoQualityChanged = false;
						isShowProgressBar = false;
						onWindowFocusChanged(false);
						// 移除tips
						timer.cancel();
						tipsView.setVisibility(View.GONE);

						if (mAudioState == 2) {
							mCamera.startSpeaking(mSelectedChannel);
						} else if (mAudioState == 1) {
							mCamera.startListening(mSelectedChannel);
						}
					}
				}
				break;
			case STS_CHANGE_CHANNEL_STREAMINFO:
				// 视频属性变化时的回调（例如fps，bps等）
				break;
			case STS_SNAPSHOT_SCANED:
				Toast.makeText(LiveViewActivity.this,
						getText(R.string.tips_snapshot_ok), Toast.LENGTH_SHORT)
						.show();
				break;
			case Camera.CONNECTION_STATE_CONNECTING:
				if (!mCamera.isSessionConnected()
						|| !mCamera.isChannelConnected(mSelectedChannel)) {
					LogUtil.d("chu", "连接中");
					progressBarText
							.setText(getString(R.string.connstus_connecting));
				}
				break;
			case Camera.CONNECTION_STATE_CONNECTED:
				if (mCamera.isSessionConnected()
						&& avChannel == mSelectedChannel
						&& mCamera.isChannelConnected(mSelectedChannel)) {
					LogUtil.d("chu", "已连接");
					onLine = true;
					progressBarText
							.setText(getString(R.string.connstus_connect_connected));
				}
				break;
			case Camera.CONNECTION_STATE_DISCONNECTED:
				onLine = false;
				break;
			case Camera.CONNECTION_STATE_UNKNOWN_DEVICE:
				progressBarText
						.setText(getString(R.string.connstus_unknown_device));
				onLine = false;
				quit();
				break;
			case Camera.CONNECTION_STATE_TIMEOUT:
				if (mCamera != null) {
					onLine = false;
					reConnectCount++;
					LogUtil.d("chu", "重连次数=" + reConnectCount);
					mCamera.stopSpeaking(mSelectedChannel);
					mCamera.stopListening(mSelectedChannel);
					mCamera.stopShow(mSelectedChannel);
					mCamera.stop(mSelectedChannel);
					mCamera.disconnect();
					mCamera.connect(mDevUID);
					mCamera.start(Camera.DEFAULT_AV_CHANNEL, mAccount,
							mPassword);
					mCamera.startShow(mSelectedChannel, true);

					mCamera.sendIOCtrl(
							Camera.DEFAULT_AV_CHANNEL,
							AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ,
							AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq
									.parseContent());
					mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
							AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
							AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq
									.parseContent());
					mCamera.sendIOCtrl(
							Camera.DEFAULT_AV_CHANNEL,
							AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ,
							AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq
									.parseContent());
					mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
							AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ,
							AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());

					if (mAudioState == 1)
						mCamera.startListening(mSelectedChannel);
					if (mAudioState == 2)
						mCamera.stopSpeaking(mSelectedChannel);
				}
				break;
			case Camera.CONNECTION_STATE_CONNECT_FAILED:
				progressBarText
						.setText(getString(R.string.connstus_connection_failed));
				onLine = false;
				quit();
				break;
			case Camera.CONNECTION_STATE_WRONG_PASSWORD:
				progressBarText
						.setText(getString(R.string.connstus_wrong_password));
				onLine = false;
				quit();
				break;
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_RESP:
				if (mCamera.getSupportedStream().length == 0) {
					channelLayout.setVisibility(View.GONE);
				} else {
					// 支持多通道选择
					channelLayout.setVisibility(View.VISIBLE);
					channels = new String[mCamera.getSupportedStream().length];
					for (int i = 0; i < mCamera.getSupportedStream().length; i++) {
						channels[i] = "CH-"
								+ (mCamera.getSupportedStream()[i].channel + 1);
					}
				}
				break;
			/**
			 * 获取视频质量
			 */
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSTREAMCTRL_RESP:
				int videoQuality = ctrlData[4];
				if (videoQuality >= 0 && videoQuality <= 5) {
					LogUtil.d("chu", "videoQuality=" + videoQuality);
					mVideoQuality = videoQuality;
				}
				break;
			/**
			 * 获取视频镜像
			 */
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_VIDEOMODE_RESP:
				int videoMode = ctrlData[4];
				if (videoMode >= 0 && videoMode <= 3) {
					mVideoFlip = videoMode;
				}
				break;
			/**
			 * 获取视频环境
			 */
			case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_ENVIRONMENT_RESP:
				int envMode = ctrlData[4];
				if (envMode >= 0 && envMode <= 3) {
					mVideoEnvironment = envMode;
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.imageview:
			if (isRecording) {
				new AlertDialog.Builder(this)
						.setTitle(getString(R.string.video_setting_save_record))
						.setPositiveButton(getString(R.string.btn_ok),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(
											DialogInterface dialogInterface,
											int i) {
										dialogInterface.dismiss();
										new StopRecordingTask().execute();
										isRecording = false;
										videoBottomPopWindow
												.filterWhiteColor(3);
									}
								})
						.setNegativeButton(getString(R.string.btn_cancel),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(
											DialogInterface dialogInterface,
											int i) {
										dialogInterface.dismiss();
									}
								}).create().show();
			} else {
				if (channelDialog == null) {
					channelDialog = new AlertDialog.Builder(this)
							.setSingleChoiceItems(channels, mSelectedChannel,
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialogInterface,
												int i) {
											dialogInterface.dismiss();
											if (mSelectedChannel == i) {
												return;
											}
											changeVideoQualityOrChannel(
													mSelectedChannel, i);
											mSelectedChannel = i;
										}
									}).create();
				}
				channelDialog.show();
			}
			break;
		}
	}

	/**
	 * 修改视频质量或者更改通道操作
	 * 
	 * @param oldChannel
	 */
	private void changeVideoQualityOrChannel(int oldChannel, int newChannel) {
		isShowProgressBar = true;
		onWindowFocusChanged(true);
		progressBarText.setText(getString(R.string.tips_changing));
		// 关闭录像
		// if (record != null) {
		// record.stopRecording(mCamera);
		// record = null;
		// }
		mCamera.stopShow(oldChannel);
		monitor.deattachCamera();
		mCamera.stopListening(oldChannel);
		mCamera.stopSpeaking(oldChannel);
		if (newChannel == oldChannel) {
			// 修改视频质量操作
			mCamera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL,
					AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETSTREAMCTRL_REQ,
					AVIOCTRLDEFs.SMsgAVIoctrlSetStreamCtrlReq.parseContent(
							oldChannel, (byte) (mVideoQuality)));
		}
		mCamera.startShow(newChannel, true);
		isVideoQualityChanged = true;
	}

	@Override
	protected void stopRecording() {
		record.stopRecording(mCamera);
	}

	@Override
	protected Bitmap getBitmap() {
		return mCamera.Snapshot(mSelectedChannel);
	}

}
