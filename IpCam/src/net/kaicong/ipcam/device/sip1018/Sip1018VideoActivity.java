package net.kaicong.ipcam.device.sip1018;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.kaicong.ipcam.BaseSipDeviceActivity;
import net.kaicong.ipcam.KCApplication;
import net.kaicong.ipcam.R;

import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.VideoPopWindowIcon;
import net.kaicong.ipcam.device.cgi.CgiImageAttr;
import net.kaicong.ipcam.device.record.Record;
import net.kaicong.ipcam.utils.CapturePhotoUtils;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.ipcam.utils.ToolUtil;
import net.kaicong.ipcam.view.VideoSettingPopWindow;

import com.misc.objc.NSNotificationCenter;

/**
 * Created by LingYan on 15-1-9.
 */
public class Sip1018VideoActivity extends BaseSipDeviceActivity implements
		Sip1018VideoListener {

	private Sip1018Monitor monitor;
	private Sip1018Camera sip1018Camera;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		// 通过MyIpCamera对象取到IpCamera对象
		MyIpCamera myIpCamera = (MyIpCamera) bundle
				.getSerializable(CameraConstants.CAMERA);

		deviceId = myIpCamera.getDeviceId();
		setContentView(R.layout.activity_camera_video);
		initIcons();
		initCommonView();
		sip1018Camera = new Sip1018Camera(myIpCamera, this);
		monitor = (Sip1018Monitor) findViewById(R.id.monitor);
		executeOnTouch(monitor);
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

	@Override
	protected void onResume() {
		super.onResume();
		monitor.attachCamera(sip1018Camera);
		sip1018Camera.startConnect();
	}

	@Override
	protected void onPause() {
		super.onPause();
		monitor.deattachCamera(sip1018Camera);
		sip1018Camera.stop();
	}

	private void initIcons() {
		int topDrawableIds[] = new int[] { R.drawable.video_play_back,
				R.drawable.video_play_revert, R.drawable.video_play_settings,
				R.drawable.video_play_share };
		int bottomDrawableIds[] = new int[] { R.drawable.video_play_snap,
				R.drawable.video_play_video_camera,
				R.drawable.video_play_volume, R.drawable.video_play_speak,
				R.drawable.ptz_up_down, R.drawable.ptz_left_right };
		for (int i = 0; i < topDrawableIds.length; i++) {
			VideoPopWindowIcon videoPopWindowIcon = new VideoPopWindowIcon();
			videoPopWindowIcon.position = i;
			videoPopWindowIcon.isTop = true;
			videoPopWindowIcon.drawableId = topDrawableIds[i];
			mTopIcons.add(videoPopWindowIcon);
		}
		for (int j = 0; j < bottomDrawableIds.length; j++) {
			VideoPopWindowIcon videoPopWindowIcon = new VideoPopWindowIcon();
			videoPopWindowIcon.position = j;
			videoPopWindowIcon.isTop = false;
			videoPopWindowIcon.drawableId = bottomDrawableIds[j];
			mBottomIcons.add(videoPopWindowIcon);
		}
	}

	// 根据屏幕方向和视频宽高比来动态分配surfaceView的宽高
	protected boolean doSetLayoutOnOrientation(int videoWidth, int videoHeight) {
		int layoutWidth = 0;
		int layoutHeight = 0;
		// 这里要注意，设置monitor的LayoutParams属性时，width在任何情况下都是手机的此时的宽度
		// 因为Holder的宽度是要充满屏幕的，而surfaceView的宽度就要根据比例来计算
		if (videoWidth > 0 && videoHeight > 0) {
			Configuration cfg = getResources().getConfiguration();
			double ratio = ((double) videoWidth) / videoHeight;
			if (cfg.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				layoutWidth = (int) (KCApplication.getWindowHeight() * ratio);
				layoutHeight = KCApplication.getWindowHeight();
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						layoutWidth, layoutHeight);
				params.addRule(RelativeLayout.CENTER_IN_PARENT);
				monitorLayout.setLayoutParams(params);
			} else if (cfg.orientation == Configuration.ORIENTATION_PORTRAIT) {
				layoutWidth = KCApplication.getWindowWidth();
				layoutHeight = (int) (KCApplication.getWindowWidth() / ratio);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						layoutWidth, layoutHeight);
				params.addRule(RelativeLayout.CENTER_IN_PARENT);
				monitorLayout.setLayoutParams(params);
			}
			return true;
		}
		return false;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		doSetLayoutOnOrientation(mVideoWidth, mVideoHeight);
		umShareWindow.dismiss();
	}

	@Override
	public void onVideoPopWindowClick(View parentView, View view,
			final int position, boolean isTop) {
		super.onVideoPopWindowClick(parentView, view, position, isTop);
		if (isTop) {
			// 顶部
			switch (position) {
			case 0:
				// 返回
				quitIfRecording();
				break;
			case 1:
				// 上下左右转动
				if ((boolean) view.getTag() == true) {
					// 处于选中状态，点击后取消选中
					videoTopPopWindow.filterWhiteColor(position);
					sip1018Camera.getIpCamera().ptz_control(
							ExpansionIpCamera.PTZ_COMMAND.PT_DOWN_STOP);
				} else {
					videoTopPopWindow.filterRedColor(position);
					sip1018Camera.getIpCamera().ptz_control(
							ExpansionIpCamera.PTZ_COMMAND.PT_LEFT_DOWN);
					sip1018Camera.getIpCamera().ptz_control(
							ExpansionIpCamera.PTZ_COMMAND.PT_RIGHT_UP);
				}
				break;
			case 2:
				// 视频设置
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
											blinkImageView.clearAnimation();
											blinkImageView
													.setVisibility(View.GONE);
											videoBottomPopWindow
													.filterWhiteColor(1);
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
					if ((boolean) view.getTag() == true) {
						// 处于选中状态，点击后取消选中
						videoTopPopWindow.filterWhiteColor(position);
						videoSettingPopWindow.dismiss();
					} else {
						videoTopPopWindow.filterRedColor(position);
						if (videoSettingPopWindow == null) {
							// 初始化图像属性对象
							CgiImageAttr cgiImageAttr = new CgiImageAttr();
							if (sip1018Camera.getIpCamera().get_resolution() == 2) {
								cgiImageAttr.resolution = 2;
							} else if (sip1018Camera.getIpCamera()
									.get_resolution() == 8) {
								cgiImageAttr.resolution = 1;
							} else if (sip1018Camera.getIpCamera()
									.get_resolution() == 32) {
								cgiImageAttr.resolution = 0;
							}
							cgiImageAttr.mode = sip1018Camera.getIpCamera()
									.get_mode();
							cgiImageAttr.brightness = sip1018Camera
									.getIpCamera().get_brightness();
							cgiImageAttr.contrast = sip1018Camera.getIpCamera()
									.get_contrast();
							if (sip1018Camera.getIpCamera().get_flip() == 3) {
								cgiImageAttr.flip = true;
								cgiImageAttr.mirror = true;
							} else if (sip1018Camera.getIpCamera().get_flip() == 2) {
								cgiImageAttr.flip = false;
								cgiImageAttr.mirror = true;
							} else if (sip1018Camera.getIpCamera().get_flip() == 1) {
								cgiImageAttr.flip = true;
								cgiImageAttr.mirror = false;
							} else if (sip1018Camera.getIpCamera().get_flip() == 0) {
								cgiImageAttr.flip = false;
								cgiImageAttr.mirror = false;
							}
							videoSettingPopWindow = new VideoSettingPopWindow(
									Sip1018VideoActivity.this, cgiImageAttr,
									R.layout.pop_item_sip1018, this);
						}
						int[] xy = new int[2];
						parentView.getLocationOnScreen(xy);
						int y = xy[1] + parentView.getHeight();
						videoSettingPopWindow.showAtLocation(
								findViewById(R.id.root), Gravity.LEFT
										| Gravity.TOP, 0, y);
					}
				}
				break;
			case 3:
				// 分享
				showShareDialog(findViewById(R.id.root));
				break;
			}
		} else {
			// 底部
			switch (position) {
			case 0:
				// 截图保存
				/**
				 * 截图保存 小米手机图片已经保存到DCIM文件夹，但是相册里面看不到，目测是小米系统的bug
				 */
				String name = ToolUtil.getNowTimeStr();
				CapturePhotoUtils.insertImage(getContentResolver(),
						sip1018Camera.getSnapShot(), name, name);
				Toast.makeText(Sip1018VideoActivity.this,
						getString(R.string.tips_snapshot_ok),
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				// 录像
				if ((boolean) view.getTag() == true) {
					// 处于选中状态，点击后取消选中
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
					record.startRecording(sip1018Camera);
					isRecording = true;
				}
				break;
			case 2:
				// 播放声音
				if ((boolean) view.getTag() == true) {
					// 处于选中状态，点击后取消选中
					videoBottomPopWindow.filterWhiteColor(position);
					sip1018Camera.getIpCamera().stop_audio();
				} else {
					videoBottomPopWindow.filterRedColor(position);
					sip1018Camera.getIpCamera().stop_talk();
					sip1018Camera.getIpCamera().play_audio();
				}
				break;
			case 3:
				// 对讲
				if ((boolean) view.getTag() == true) {
					// 处于选中状态，点击后取消选中
					videoBottomPopWindow.filterWhiteColor(position);
					sip1018Camera.getIpCamera().stop_talk();
				} else {
					videoBottomPopWindow.filterRedColor(position);
					sip1018Camera.getIpCamera().stop_audio();
					sip1018Camera.getIpCamera().start_talk();
				}
				break;
			case 4:
				// 上下巡航
				if ((boolean) view.getTag() == true) {
					// 处于选中状态，点击后取消选中
					videoBottomPopWindow.filterWhiteColor(position);
				} else {
					videoBottomPopWindow.filterRedColor(position);
					sip1018Camera.getIpCamera().ptz_control(
							ExpansionIpCamera.PTZ_COMMAND.T_UP);
				}
				break;
			case 5:
				// 左右巡航
				if ((boolean) view.getTag() == true) {
					// 处于选中状态，点击后取消选中
					videoBottomPopWindow.filterWhiteColor(position);
				} else {
					videoBottomPopWindow.filterRedColor(position);
					sip1018Camera.getIpCamera().ptz_control(
							ExpansionIpCamera.PTZ_COMMAND.P_RIGHT);
				}
				break;
			}
		}
	}

	@Override
	public void quit() {
		monitor.deattachCamera(sip1018Camera);
		sip1018Camera.stop();
		finish();
	}

	@Override
	public void onVideoResolutionSet(int position) {
		super.onVideoResolutionSet(position);
		int value = 32;
		if (position == 0) {
			value = 32;
		} else if (position == 1) {
			value = 8;
		} else if (position == 2) {
			value = 2;
		}
		sip1018Camera.getIpCamera().set_resolution(value);
	}

	@Override
	public void onVideoModeSet(int position) {
		super.onVideoModeSet(position);
		sip1018Camera.getIpCamera().set_mode(position);
	}

	@Override
	public void onVideoBrightnessSet(int num) {
		super.onVideoBrightnessSet(num);
		sip1018Camera.getIpCamera().set_brightness(num);
	}

	@Override
	public void onVideoContrastSet(int num) {
		super.onVideoContrastSet(num);
		sip1018Camera.getIpCamera().set_contrast(num);
	}

	@Override
	public void onVideoFlipSet(boolean on) {
		super.onVideoFlipSet(on);
		if (sip1018Camera.getIpCamera().get_flip() == 0) {
			// 当前处于正常显示
			if (on) {
				sip1018Camera.getIpCamera().set_flip(1);
			}
		} else if (sip1018Camera.getIpCamera().get_flip() == 1) {
			// 当前处于翻转状态
			if (!on) {
				sip1018Camera.getIpCamera().set_flip(0);
			}
		} else if (sip1018Camera.getIpCamera().get_flip() == 2) {
			// 当前处于镜像状态
			if (on) {
				sip1018Camera.getIpCamera().set_flip(3);
			}
		} else if (sip1018Camera.getIpCamera().get_flip() == 3) {
			// 当前处于翻转镜像状态
			if (!on) {
				sip1018Camera.getIpCamera().set_flip(2);
			}
		}
	}

	@Override
	public void onVideoMirrorSet(boolean on) {
		super.onVideoMirrorSet(on);
		if (sip1018Camera.getIpCamera().get_flip() == 0) {
			// 当前处于正常显示
			if (on) {
				sip1018Camera.getIpCamera().set_flip(2);
			}
		} else if (sip1018Camera.getIpCamera().get_flip() == 1) {
			// 当前处于翻转状态
			if (on) {
				sip1018Camera.getIpCamera().set_flip(3);
			}
		} else if (sip1018Camera.getIpCamera().get_flip() == 2) {
			// 当前处于镜像状态
			if (!on) {
				sip1018Camera.getIpCamera().set_flip(0);
			}
		} else if (sip1018Camera.getIpCamera().get_flip() == 3) {
			// 当前处于翻转镜像状态
			if (!on) {
				sip1018Camera.getIpCamera().set_flip(1);
			}
		}
	}

	@Override
	protected void stopRecording() {
		record.stopRecording(sip1018Camera);
	}

	@Override
	protected Bitmap getBitmap() {
		return sip1018Camera.getSnapShot();
	}

	@Override
	protected void onPtzLeft() {
		super.onPtzLeft();
		sip1018Camera.getIpCamera().ptz_control(
				ExpansionIpCamera.PTZ_COMMAND.P_LEFT);
	}

	@Override
	protected void onPtzRight() {
		super.onPtzRight();
		sip1018Camera.getIpCamera().ptz_control(
				ExpansionIpCamera.PTZ_COMMAND.P_RIGHT);
	}

	@Override
	protected void onPtzUp() {
		super.onPtzUp();
		sip1018Camera.getIpCamera().ptz_control(
				ExpansionIpCamera.PTZ_COMMAND.T_UP);
	}

	@Override
	protected void onPtzDown() {
		super.onPtzDown();
		sip1018Camera.getIpCamera().ptz_control(
				ExpansionIpCamera.PTZ_COMMAND.T_DOWN);
	}

	@Override
	protected void onPtzStop() {
		super.onPtzStop();
		sip1018Camera.getIpCamera().ptz_control(
				ExpansionIpCamera.PTZ_COMMAND.PT_LEFT_STOP);
		sip1018Camera.getIpCamera().ptz_control(
				ExpansionIpCamera.PTZ_COMMAND.PT_RIGHT_STOP);
		sip1018Camera.getIpCamera().ptz_control(
				ExpansionIpCamera.PTZ_COMMAND.PT_UP_STOP);
		sip1018Camera.getIpCamera().ptz_control(
				ExpansionIpCamera.PTZ_COMMAND.PT_DOWN_STOP);
	}

	@Override
	public void videoConnected(int videoWidth, int videoHeight) {
		mVideoWidth = videoWidth;
		mVideoHeight = videoHeight;
		record = new Record(Sip1018VideoActivity.this, mVideoWidth,
				mVideoHeight, deviceId);
		monitor.monitorRatio = (mVideoWidth * 1.0f) / mVideoHeight;
		doSetLayoutOnOrientation(mVideoWidth, mVideoHeight);
		monitor.attachCamera(sip1018Camera);

		isShowProgressBar = false;
		onWindowFocusChanged(false);
	}
}
