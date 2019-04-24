package net.kaicong.ipcam.device;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.R;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.listener.SocializeListeners;

import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.CameraModel;
import net.kaicong.ipcam.bean.DeviceProperty;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.device.zhiyun.HttpPostUtil;
import net.kaicong.ipcam.device.zhiyun.LiveViewActivity;
import net.kaicong.ipcam.user.UserAccount;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.ipcam.utils.StringUtils;
import net.kaicong.ipcam.utils.ToolUtil;
import net.kaicong.ipcam.view.DownloadDialog;
import net.kaicong.utility.ApiClientUtility;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LingYan on 14-12-23.
 */
public class ZhiyunDevicePropertyActivity extends BaseActivity {

	public static final int REQUEST_CODE_ZHIYUN_IMAGE = 999;
	public static final int REQUEST_CODE_PAY = 1000;
	public static final int REQUEST_CODE_SHARE = 1001;
	public static final int REQUEST_CODE_CHANGE_INFO = 1002;
	private static final int REQUEST_CODE_POSITION = 800;

	// new
	private ImageView back;
	private TextView deviceName;
	private ImageView play;

	private LinearLayout lel_share;
	private LinearLayout lel_modifyName;
	private LinearLayout lel_modifyPasd;
	private LinearLayout lel_levUp;
	private LinearLayout lel_becomeOwner;
	private LinearLayout lel_renewals;
	private TextView text_ownerTextView;
	private LinearLayout lel_confirmPosition;
	private ImageView image_becomeOwner;

	// zhiyun
	private TextView zhiyunManageNumText;
	private TextView zhiyunUseDateText;
	private ProgressBar zhiyunUseProgressBar;
	private TextView zhiyunUseProgressText;

	private DeviceProperty deviceProperty;
	private byte snapshot[];
	// 是否支付成功
	private boolean isPaySuccess = false;
	// 是否分享或取消分享成功
	private boolean isChangeShare = false;
	// 是否修改设备信息成功
	private boolean isChangeInfo = false;

	private CgiControlParams cgiControlParams;
	private DownloadDialog downloadDialog;
	private String uploadIp;
	private int uploadPort;
	private String rootVersion;// 1304当前固件版本
	private String authStr;
	private boolean isDeviceOnline = false;// 设备是否在线
	// 是否可以在线升级
	private boolean canlevUp = false;

	private LinearLayout layoutUpdateInfo;
	private Button updateTip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		deviceProperty = (DeviceProperty) getIntent().getSerializableExtra(
				"deviceCamera");
		cgiControlParams = new CgiControlParams(deviceProperty.account,
				deviceProperty.password);

		hiddenBar();
		setContentView(R.layout.activity_zhiyun_device_property);

		back = (ImageView) findViewById(R.id.img_myDevice_Detail_back);
		back.setOnClickListener(this);
		deviceName = (TextView) findViewById(R.id.tev_myDevice_Detail_name);
		deviceName.setText(deviceProperty.deviceName + "("
				+ getString(R.string.device_property_zhiyun_mode) + ")");
		play = (ImageView) findViewById(R.id.img_myDevice_Detail_play);
		play.setOnClickListener(this);

		lel_share = (LinearLayout) findViewById(R.id.lel_myDevice_Detail_share);
		lel_share.setOnClickListener(this);
		lel_modifyName = (LinearLayout) findViewById(R.id.lel_myDevice_Detail_modifyName);
		lel_modifyName.setOnClickListener(this);
		lel_modifyPasd = (LinearLayout) findViewById(R.id.lel_myDevice_Detail_modifyPasd);
		lel_modifyPasd.setOnClickListener(this);
		lel_levUp = (LinearLayout) findViewById(R.id.lel_myDevice_Detail_lelUp);
		lel_levUp.setOnClickListener(this);
		lel_becomeOwner = (LinearLayout) findViewById(R.id.lel_myDevice_become_owner);
		lel_becomeOwner.setOnClickListener(this);
		text_ownerTextView = (TextView) findViewById(R.id.become_owner);
		lel_confirmPosition = (LinearLayout) findViewById(R.id.lel_myDevice_confirm_position);
		lel_confirmPosition.setOnClickListener(this);
		image_becomeOwner = (ImageView) findViewById(R.id.become_owner_image);
		if (deviceProperty.isCamAdmin) {
			// 显示取消成为设备主人
			text_ownerTextView.setText(getString(R.string.device_cancel_owner));
			image_becomeOwner
					.setImageResource(R.drawable.common_device_cancel_owner);
			// 显示地标认证
			// if (deviceProperty.isShared) {
			// // 设备被分享后才能申请地标认证
			// lel_confirmPosition.setVisibility(View.VISIBLE);
			// }
		} else {
			// 显示成为设备主人
			text_ownerTextView.setText(getString(R.string.device_become_owner));
			// lel_confirmPosition.setVisibility(View.INVISIBLE);
			image_becomeOwner
					.setImageResource(R.drawable.common_device_become_owner);
		}
		lel_renewals = (LinearLayout) findViewById(R.id.lel_myDevice_Detail_zhiyunRenewals);
		lel_renewals.setOnClickListener(this);

		zhiyunManageNumText = (TextView) findViewById(R.id.zhiyun_manage_text);
		zhiyunManageNumText.setText(getString(R.string.add_by_zhiyun) + ":"
				+ deviceProperty.zCloudName);
		zhiyunUseDateText = (TextView) findViewById(R.id.zhiyun_use_date_text);
		if (StringUtils.isEmpty(deviceProperty.overDueDate)
				|| deviceProperty.overDueDate.equals("null")) {
		} else {
			zhiyunUseDateText.setText(getString(
					R.string.device_property_zhiyun_use_date,
					deviceProperty.overDueDate));
		}
		zhiyunUseProgressBar = (ProgressBar) findViewById(R.id.zhiyun_use_progressbar);
		zhiyunUseProgressBar.setProgress((int) deviceProperty.progress);
		zhiyunUseProgressBar.getProgressDrawable().setColorFilter(
				getResources().getColor(R.color.kaicong_orange),
				PorterDuff.Mode.MULTIPLY);
		zhiyunUseProgressText = (TextView) findViewById(R.id.zhiyun_use_progress_text);
		zhiyunUseProgressText.setText(deviceProperty.progressText);
		zhiyunUseProgressText.setTextColor(getResources().getColor(
				R.color.kaicong_orange));

		layoutUpdateInfo = (LinearLayout) findViewById(R.id.layout_update_info);
		updateTip = (Button) findViewById(R.id.button_update);
		updateTip.setOnClickListener(this);

		getCameraModelByZCloud();

		authStr = new String(Base64.encode(
				(deviceProperty.account + ":" + deviceProperty.password)
						.getBytes(), Base64.DEFAULT));
		authStr = "Basic " + authStr;

	}

	/**
	 * 分享监听器
	 */
	private SocializeListeners.SnsPostListener mShareListener = new SocializeListeners.SnsPostListener() {

		@Override
		public void onStart() {

		}

		@Override
		public void onComplete(SHARE_MEDIA platform, int stCode,
				SocializeEntity entity) {
			if (stCode == 200) {
				Toast.makeText(ZhiyunDevicePropertyActivity.this, "分享成功",
						Toast.LENGTH_SHORT).show();
			} else {
				// Toast.makeText(BaseSipDeviceActivity.this,
				// "分享失败 : error code : " + stCode, Toast.LENGTH_SHORT)
				// .show();
			}
		}
	};

	/**
	 * 获取1304设备固件版本号
	 */

	private void get1304Version() {
		cgiControlParams.doCgiGet("http://" + uploadIp + ":" + uploadPort
				+ "/cgi/sys_get?Group=DeviceInfo",
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int i, Header[] headers, byte[] bytes) {
						isDeviceOnline = true;
						String result = new String(bytes);
						if (result.contains("<Firmware><string>")) {
							String temp1 = result.substring(result
									.indexOf("<Firmware><string>"));
							String temp2 = temp1.substring(0,
									temp1.lastIndexOf("</string></Firmware>"));
							rootVersion = temp2.replace("<Firmware><string>",
									"");
						}
						check1304Update();
					}

					@Override
					public void onFailure(int i, Header[] headers,
							byte[] bytes, Throwable throwable) {
						isDeviceOnline = false;
					}

				});
	}

	/**
	 * 根据智云号获取ip地址
	 */
	private void getCameraIpByDDNS() {
		Map<String, String> map = new HashMap<>();
		map.put("zcloud", deviceProperty.zCloudName);
		map.put(CameraConstants.USER_ID,
				String.valueOf(UserAccount.getUserID()));
		doPost(UrlResources.URL_CHECK_ZCLOUD_INDO,
				ApiClientUtility.getParams(map),
				new VolleyResponse(
						this,
						false,
						getString(R.string.activity_base_progress_dialog_content)) {

					@Override
					protected void onTaskSuccess(JSONObject jsonObject) {
						super.onTaskSuccess(jsonObject);
						String wanIp = jsonObject.optString("WanIp");
						String lanIp = jsonObject.optString("LanIp");
						String localIp = ToolUtil.getLocalIpAddress();
						if (StringUtils.isEmpty(wanIp)
								|| StringUtils.isEmpty(lanIp)) {

						} else {
							LogUtil.d("chu", "--本地ip--" + localIp);
							LogUtil.d("chu", "--内网ip--" + lanIp);
							LogUtil.d("chu", "--外网ip--" + wanIp);
							uploadPort = jsonObject.optInt("CamPort");
							if (uploadPort == 0) {
								uploadPort = 80;
							}
							String lanIpHead = lanIp.substring(0,
									lanIp.lastIndexOf("."));
							String localIpHead = localIp.substring(0,
									localIp.lastIndexOf("."));
							if (lanIpHead.equals(localIpHead)) {
								// 内网可以访问
								uploadIp = lanIp;
							} else {
								uploadIp = wanIp;
							}
							get1304Version();
						}
					}

				});
	}

	/**
     *
     */

	/**
	 * 根据智云号获取设备型号
	 */
	private void getCameraModelByZCloud() {
		Map<String, String> map = new HashMap<>();
		map.put("ZCloud_Id", deviceProperty.zCloudName);
		doPost(UrlResources.URL_GET_MODEL_BY_ZCLOUD,
				ApiClientUtility.getParams(map),
				new VolleyResponse(
						this,
						false,
						getString(R.string.activity_base_progress_dialog_content)) {

					@Override
					protected void onTaskSuccessRoot(JSONObject jsonObject) {
						super.onTaskSuccessRoot(jsonObject);
						int modelId = jsonObject.optInt("ModelId");
						if (modelId == CameraModel.MODEL_ID_1303_1304) {
							// 可以进行升
							canlevUp = true;
							if (System.currentTimeMillis() / 1000
									- UserAccount.getCurrentTime() > UserAccount.DAY) {
								layoutUpdateInfo.setVisibility(View.VISIBLE);
							}
							getCameraIpByDDNS();

						}
					}

				});
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.img_myDevice_Detail_play:
			// 点击播放
			if (deviceProperty.progress == 0) {
				// 已过期
				makeToast(getString(R.string.device_property_overdue_date));
				return;
			}
			startZCloudPlay(deviceProperty);
			break;

		case R.id.img_myDevice_Detail_back:
			// 退出
			quit();
			break;
		case R.id.lel_myDevice_Detail_share:
			// 分享设备
			Intent intent = new Intent();
			intent.setClass(ZhiyunDevicePropertyActivity.this,
					ShareDeviceActivity.class);
			intent.putExtra("isShareOpen", deviceProperty.isShared);
			intent.putExtra("deviceId", deviceProperty.deviceId);
			startActivityForResult(intent, REQUEST_CODE_SHARE);
			break;
		case R.id.lel_myDevice_Detail_modifyName:
			// 修改名称
			Intent namentent = new Intent();
			namentent.setClass(this, ChangeDeviceNameActivity.class);
			namentent.putExtra("device_property", deviceProperty);
			startActivityForResult(namentent, REQUEST_CODE_CHANGE_INFO);
			break;
		case R.id.lel_myDevice_Detail_modifyPasd:
			// 修改密码
			Intent deviceInfoIntent = new Intent();
			deviceInfoIntent.setClass(this, ChangeDevicePasswordActivity.class);
			deviceInfoIntent.putExtra("device_property", deviceProperty);
			startActivityForResult(deviceInfoIntent, REQUEST_CODE_CHANGE_INFO);
			break;
		case R.id.lel_myDevice_become_owner:
			// 成为或者取消成为主人，前提是知道是否是设备主人
			new AlertDialog.Builder(this)
					.setTitle(
							deviceProperty.isCamAdmin ? getString(R.string.device_cancel_owner)
									: getString(R.string.device_become_owner))
					.setMessage(
							deviceProperty.isCamAdmin ? getString(R.string.device_cancel_owner_msg)
									: getString(R.string.device_become_owner_msg))
					.setPositiveButton(getString(R.string.btn_ok),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialogInterface, int i) {
									dialogInterface.dismiss();
									becomeOwner(!deviceProperty.isCamAdmin);
								}

							})
					.setNegativeButton(getString(R.string.btn_cancel),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub
									arg0.dismiss();
								}
							}).create().show();

			break;
		case R.id.lel_myDevice_Detail_lelUp:
			// 固件升级
			if (!canlevUp) {
				// 不能升级
				makeToast("该型号暂不支持固件升级");
			} else {
				// 获取固件下载地址
				if (uploadIp == null || !isDeviceOnline) {
					makeToast(getString(R.string.update_get_ip_failed));
					return;
				}
				Map<String, String> map = new HashMap<>();
				// 1304在这里自定义为S，非官方设定，仅开发用
				map.put("cammodel", "1304");
				map.put(CameraConstants.USER_ID,
						String.valueOf(UserAccount.getUserID()));
				doPost(UrlResources.URL_GET_FIRMWIRE,
						ApiClientUtility.getParams(map),
						new VolleyResponse(
								this,
								true,
								getString(R.string.activity_base_progress_dialog_content)) {

							@Override
							protected void onTaskSuccessRoot(
									JSONObject jsonObject) {
								super.onTaskSuccessRoot(jsonObject);
								if (jsonObject.optInt("code") == 1) {
									String SystemFirmwareUrl = jsonObject
											.optString("SystemFirmwareUrl");
									final String downloadUrl = SystemFirmwareUrl;
									String SystemFirmwareVersion = jsonObject
											.optString("SystemFirmwareVersion");
									String Remark = jsonObject
											.optString("Remark");
									if (!SystemFirmwareVersion
											.equals(rootVersion)) {
										// 可以升级
										new AlertDialog.Builder(
												ZhiyunDevicePropertyActivity.this)
												.setTitle(
														getString(R.string.update_root))
												.setMessage(Remark)
												.setPositiveButton(
														getString(R.string.btn_ok),
														new DialogInterface.OnClickListener() {
															@Override
															public void onClick(
																	DialogInterface dialogInterface,
																	int i) {
																dialogInterface
																		.dismiss();

																downloadDialog = new DownloadDialog(
																		ZhiyunDevicePropertyActivity.this,
																		R.style.ZhiYunVideoSettingDialog,
																		downloadUrl,
																		new DownloadDialog.IDownloadComplete() {

																			@Override
																			public void downloadComplete(
																					String zipPath) {
																				try {
																					uploadRoot(zipPath);
																				} catch (Exception e) {

																				}
																			}

																		});
																downloadDialog
																		.show();
																WindowManager.LayoutParams params = downloadDialog
																		.getWindow()
																		.getAttributes();
																params.width = displayMetrics.widthPixels * 2 / 3;
																params.height = params.width / 5;
																downloadDialog
																		.getWindow()
																		.setAttributes(
																				params);

															}
														})
												.setNegativeButton(
														getString(R.string.btn_cancel),
														new DialogInterface.OnClickListener() {
															@Override
															public void onClick(
																	DialogInterface dialogInterface,
																	int i) {
																dialogInterface
																		.dismiss();
															}
														}).create().show();
									} else {
										// 当前已是最新版本
										makeToast(getString(R.string.update_root_is_new));
									}
								}
							}

						});
			}
			break;

		case R.id.lel_myDevice_Detail_zhiyunRenewals:
			Intent payIntent = new Intent();
			payIntent.setClass(ZhiyunDevicePropertyActivity.this,
					RenewalsZhiyunActivity.class);
			payIntent.putExtra("zcloud", deviceProperty.zCloudName);
			startActivityForResult(payIntent, REQUEST_CODE_PAY);
			break;

		case R.id.button_update:
			layoutUpdateInfo.setVisibility(View.GONE);
			UserAccount.saveCurrentTime();
			break;

		case R.id.lel_myDevice_confirm_position:
			// 地标认证
			if (deviceProperty.isCamAdmin && deviceProperty.isShared) {
				Intent positionIntent = new Intent();
				positionIntent.setClass(ZhiyunDevicePropertyActivity.this,
						CertificatePositionActivity.class);
				startActivityForResult(positionIntent, REQUEST_CODE_POSITION);
			} else {
				makeToast(getString(R.string.device_confirm_position_tip));
			}
			break;

		}
	}

	private void check1304Update() {
		Map<String, String> map = new HashMap<>();
		// 1304在这里自定义为S，非官方设定，仅开发用
		map.put("cammodel", "1304");
		map.put(CameraConstants.USER_ID,
				String.valueOf(UserAccount.getUserID()));
		doPost(UrlResources.URL_GET_FIRMWIRE,
				ApiClientUtility.getParams(map),
				new VolleyResponse(
						this,
						true,
						getString(R.string.activity_base_progress_dialog_content)) {

					@Override
					protected void onTaskSuccessRoot(JSONObject jsonObject) {
						super.onTaskSuccessRoot(jsonObject);
						if (jsonObject.optInt("code") == 1) {
							String SystemFirmwareUrl = jsonObject
									.optString("SystemFirmwareUrl");
							final String downloadUrl = SystemFirmwareUrl;
							String SystemFirmwareVersion = jsonObject
									.optString("SystemFirmwareVersion");
							String Remark = jsonObject.optString("Remark");
							if (!SystemFirmwareVersion.equals(rootVersion)) {
								// 可以升级
								new AlertDialog.Builder(
										ZhiyunDevicePropertyActivity.this)
										.setTitle(
												getString(R.string.update_root))
										.setMessage(Remark)
										.setPositiveButton(
												getString(R.string.btn_ok),
												new DialogInterface.OnClickListener() {
													@Override
													public void onClick(
															DialogInterface dialogInterface,
															int i) {
														dialogInterface
																.dismiss();

														downloadDialog = new DownloadDialog(
																ZhiyunDevicePropertyActivity.this,
																R.style.ZhiYunVideoSettingDialog,
																downloadUrl,
																new DownloadDialog.IDownloadComplete() {

																	@Override
																	public void downloadComplete(
																			String zipPath) {
																		try {
																			uploadRoot(zipPath);
																		} catch (Exception e) {

																		}
																	}

																});
														downloadDialog.show();
														WindowManager.LayoutParams params = downloadDialog
																.getWindow()
																.getAttributes();
														params.width = displayMetrics.widthPixels * 2 / 3;
														params.height = params.width / 5;
														downloadDialog
																.getWindow()
																.setAttributes(
																		params);

													}
												})
										.setNegativeButton(
												getString(R.string.btn_cancel),
												new DialogInterface.OnClickListener() {
													@Override
													public void onClick(
															DialogInterface dialogInterface,
															int i) {
														dialogInterface
																.dismiss();
													}
												}).create().show();
							} else {
								// 当前已是最新版本
								makeToast(getString(R.string.update_root_is_new));
							}
						}
					}

				});
	}

	private void becomeOwner(final boolean isBecomeOwner) {
		String urlString = "";
		Map<String, String> map = new HashMap<>();
		map.put(CameraConstants.USER_ID,
				String.valueOf(UserAccount.getUserID()));
		map.put(CameraConstants.DEVICE_ID,
				String.valueOf(deviceProperty.deviceId));
		if (isBecomeOwner) {
			urlString = UrlResources.URL_BECOME_OWNER;
		} else {
			urlString = UrlResources.URL_CANCEL_OWNER;
		}
		doPost(urlString, ApiClientUtility.getParams(map), new VolleyResponse(
				this, true,
				getString(R.string.activity_base_progress_dialog_content)) {

			@Override
			protected void onTaskSuccessRoot(JSONObject jsonObject) {
				// TODO Auto-generated method stub
				super.onTaskSuccessRoot(jsonObject);
				if (isBecomeOwner) {
					makeToast(getString(R.string.device_become_owner_success));
					text_ownerTextView
							.setText(getString(R.string.device_cancel_owner));
					image_becomeOwner
							.setImageResource(R.drawable.common_device_cancel_owner);
					deviceProperty.isCamAdmin = true;
					// if (deviceProperty.isShared) {
					// lel_confirmPosition.setVisibility(View.VISIBLE);
					// }
				} else {
					makeToast(getString(R.string.device_cancel_owner_success));
					text_ownerTextView
							.setText(getString(R.string.device_become_owner));
					image_becomeOwner
							.setImageResource(R.drawable.common_device_become_owner);
					deviceProperty.isCamAdmin = false;
					// lel_confirmPosition.setVisibility(View.INVISIBLE);
				}

			}

		});

	}

	/**
	 * 启动智云播放
	 * 
	 * @param deviceProperty
	 */
	private void startZCloudPlay(DeviceProperty deviceProperty) {
		// 跳转到智云的播放
		Intent intent = new Intent();
		intent.putExtra("mCameraName", deviceProperty.deviceName);
		intent.putExtra("mDevUID", deviceProperty.zCloudName);
		intent.putExtra("avChannel", 0);
		intent.putExtra("mAccount", deviceProperty.account);
		intent.putExtra("mPassword", deviceProperty.password);
		intent.putExtra("mVideoQuality", 3);
		intent.putExtra("mDeviceId", deviceProperty.deviceId);
		intent.putExtra("isIPDevice", false);
		intent.putExtra("isShareOpen", deviceProperty.isShared);
		intent.setClass(this, LiveViewActivity.class);
		startActivityForResult(intent, REQUEST_CODE_ZHIYUN_IMAGE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		if (requestCode == REQUEST_CODE_ZHIYUN_IMAGE) {
			if (data != null) {
				snapshot = data.getByteArrayExtra("snapshot");
			}
		}
		if (requestCode == REQUEST_CODE_PAY) {
			if (data != null) {
				// 续费成功,返回续费时长
				int number = data.getIntExtra("number", 0);
				if (number == 12) {
					// 续费一年
					int year = Integer.parseInt(deviceProperty.overDueDate
							.substring(0, 4));
					String newOverDueDate = (year + 1)
							+ deviceProperty.overDueDate.substring(4);
					zhiyunUseDateText.setText(getString(
							R.string.device_property_zhiyun_use_date,
							newOverDueDate));
					// 智云续费一年成功后进度都调至100%
					deviceProperty.progress = 100.0f;
					zhiyunUseProgressBar
							.setProgress((int) deviceProperty.progress);
				}
				isPaySuccess = true;
			}
		}
		if (requestCode == REQUEST_CODE_SHARE) {
			isChangeShare = true;
			boolean isShared = data.getBooleanExtra("is_shared", false);
			deviceProperty.isShared = isShared;
			// if (deviceProperty.isShared && deviceProperty.isCamAdmin) {
			// lel_confirmPosition.setVisibility(View.VISIBLE);
			// } else {
			// lel_confirmPosition.setVisibility(View.INVISIBLE);
			// }
		}
		if (requestCode == REQUEST_CODE_CHANGE_INFO) {
			isChangeInfo = true;
			// 当 修改名字和密码的时候要注意刷新页面 和property参数 假刷新
			if (null != data.getStringExtra("name")) {
				deviceProperty.deviceName = data.getStringExtra("name");
				deviceName
						.setText(data.getStringExtra("name")
								+ "("
								+ getString(R.string.device_property_zhiyun_mode)
								+ ")");
			}
			if (null != data.getStringExtra("passward")) {
				deviceProperty.password = data.getStringExtra("passward");
			}
		}
		if (requestCode == REQUEST_CODE_POSITION) {
			// 已填写地标,调用申请地标api
			String sharedPosition = data.getStringExtra("shared_position");
			applyPosition(sharedPosition);
		}

	}

	// 申请地标
	private void applyPosition(String positionStr) {
		Map<String, String> map = new HashMap<>();
		map.put(CameraConstants.DEVICE_ID,
				String.valueOf(deviceProperty.deviceId));
		map.put(CameraConstants.USER_ID,
				String.valueOf(UserAccount.getUserID()));
		map.put("landmark_name", positionStr);
		doPost(UrlResources.URL_APPLY_POSITION,
				ApiClientUtility.getParams(map),
				new VolleyResponse(
						this,
						true,
						getString(R.string.activity_base_progress_dialog_content)) {

					@Override
					protected void onTaskSuccessRoot(JSONObject jsonObject) {
						// TODO Auto-generated method stub
						super.onTaskSuccessRoot(jsonObject);
						makeToast(getString(R.string.device_apply_position_success));
					}

				});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			quit();
		}
		return true;
	}

	private void quit() {
		Intent data = new Intent();
		if (isPaySuccess || isChangeShare || isChangeInfo) {
			data.putExtra("pay_success", 100);
		}
		if (snapshot != null) {
			data.putExtra("mPosition", deviceProperty.listPosition);
			data.putExtra("snapshot", snapshot);
		}
		setResult(RESULT_OK, data);
		finish();
	}

	/**
	 * 1304升级
	 * 
	 * @throws Exception
	 */

	private void uploadRoot(String filePath) throws Exception {

		showProgressDialog();
		setProgressText("正在升级...");
		/**
		 * 解压
		 */
		final String decompressedPath = ToolUtil.decompress(filePath);

		/**
		 * 调用顺序kill-upload-reboot
		 */
		RequestParams requestParams = new RequestParams();
		cgiControlParams.doCgiPostRequest("http://" + uploadIp
				+ "/form/killAppForm", requestParams,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int i, Header[] headers, byte[] bytes) {
						if (i == 200) {
							// kill成功
							LogUtil.d("chu", "--kill success--");
							new UploadTask().execute(decompressedPath);
						}
					}

					@Override
					public void onFailure(int i, Header[] headers,
							byte[] bytes, Throwable throwable) {

					}

				});
	}

	private class UploadTask extends AsyncTask<String, Void, Integer> {

		@Override
		protected Integer doInBackground(String... voids) {
			String param = voids[0];
			HttpPostUtil httpPostUtil = new HttpPostUtil("http://" + uploadIp
					+ "/form/upload", param, authStr);
			try {
				String temp = new String(httpPostUtil.send());
				int code = Integer.parseInt(temp.substring(
						temp.indexOf("$STATE$=") + "$STATE$=".length(),
						temp.indexOf("$STATE$=") + "$STATE$=".length() + 1));
				if (code == 1) {
					// 升级成功
					return 1;
				}
			} catch (Exception e) {
				return -1;
			}
			return -1;
		}

		@Override
		protected void onPostExecute(Integer aVoid) {
			super.onPostExecute(aVoid);
			if (aVoid == 1) {
				// 升级成功
				rebootDevice();
			} else {
				makeToast(getString(R.string.update_root_failed));
			}
		}

	}

	private void rebootDevice() {
		RequestParams params = new RequestParams();
		cgiControlParams.doCgiPostRequest(
				"http://" + uploadIp + "/form/reboot", params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int i, Header[] headers, byte[] bytes) {
						LogUtil.d("chu", "--重启完成--");
						removeProgressDialog();
						new AlertDialog.Builder(
								ZhiyunDevicePropertyActivity.this)
								.setTitle(getString(R.string.see_world_notice))
								.setMessage(
										getString(R.string.update_update_success))
								.setPositiveButton(getString(R.string.btn_ok),
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialogInterface,
													int i) {
												dialogInterface.dismiss();
											}
										}).create().show();
					}

					@Override
					public void onFailure(int i, Header[] headers,
							byte[] bytes, Throwable throwable) {

					}

				});
	}

}
