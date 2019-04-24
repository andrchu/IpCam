package net.kaicong.ipcam.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.chu.android.CaptureActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.kaicong.ipcam.AddDeviceChoiceActivity;
import net.kaicong.ipcam.AddDevicePropertyActivity;
import net.kaicong.ipcam.BaseFragment;
import net.kaicong.ipcam.R;

import net.kaicong.ipcam.KCApplication;
import net.kaicong.ipcam.adpater.MyDeviceAdapter;
import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.DeviceCamera;
import net.kaicong.ipcam.bean.DeviceProperty;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.db.DBManager;
import net.kaicong.ipcam.device.IpDevicePropertyActivity;
import net.kaicong.ipcam.device.ZhiyunDevicePropertyActivity;
import net.kaicong.ipcam.device.sip1303.Sip1303VideoByJavaActivity;
import net.kaicong.ipcam.device.zhiyun.SearchUIDWifiActivity;
import net.kaicong.ipcam.user.LoginActivity;
import net.kaicong.ipcam.user.UserAccount;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.ipcam.utils.StringUtils;
import net.kaicong.ipcam.utils.ToolUtil;

import net.kaicong.utility.ApiClientUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LingYan on 2014/8/29.
 */

public class MyDeviceFragment extends BaseFragment implements
		AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener,
		PullToRefreshBase.OnRefreshListener2 {
	// Activity的回调code
	public static final int REQUEST_CODE_SCAN_QR_CODE = 1000;
	public static final int REQUEST_CODE_ADD_DEVICE_SUCCESS = REQUEST_CODE_SCAN_QR_CODE + 1;
	public static final int REQUEST_CODE_SEARCH = REQUEST_CODE_ADD_DEVICE_SUCCESS + 1;
	public static final int REQUEST_CODE_ZHIYUN_IMAGE = REQUEST_CODE_SEARCH + 1;
	public static final int REQUEST_CODE_ADD_DEVICE = REQUEST_CODE_ZHIYUN_IMAGE + 1;
	public static final int REQUEST_LOGIN_SUCCESS_REFRESH = REQUEST_CODE_ADD_DEVICE + 1;
	// 摄像机列表
	private List<DeviceCamera> cameras = new ArrayList<>();
	private MyDeviceAdapter adapter;
	private ListView listView;
	private PullToRefreshListView mPullRefreshListView;
	private DBManager dbManager;
	private ArrayList<String> uidList = new ArrayList<>();

	private boolean isDataLoaded = false;

	@Override
	protected void initView(View convertView) {
		super.initView(convertView);
		mPullRefreshListView = (PullToRefreshListView) convertView
				.findViewById(R.id.list);
		mPullRefreshListView.setOnRefreshListener(this);
		adapter = new MyDeviceAdapter(this.getActivity());
		adapter.setData(cameras);
		listView = mPullRefreshListView.getRefreshableView();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);
		dbManager = new DBManager(this.getActivity());
		if (UserAccount.isUserLogin()) {
			getDevices();
		} else {
			makeToast(getString(R.string.login_user_please_login_first));
			Intent intent = new Intent();
			intent.setClass(this.getActivity(), LoginActivity.class);
			startActivityForResult(intent, REQUEST_LOGIN_SUCCESS_REFRESH);
		}
	}

	private void getDevices() {
		Map<String, String> map = new HashMap<>();
		map.put(CameraConstants.USER_ID,
				String.valueOf(UserAccount.getUserID()));
		LogUtil.d("chu", "user_id" + UserAccount.getUserID());
		doPost(UrlResources.URL_DEVICES_LIST,
				ApiClientUtility.getParams(map),
				new VolleyResponse(
						MyDeviceFragment.this.getActivity(),
						true,
						getString(R.string.activity_base_progress_dialog_content)) {

					@Override
					protected void onTaskSuccess(JSONArray result) {
						cameras.clear();
						uidList.clear();
						mPullRefreshListView.onRefreshComplete();
						cameras.addAll(DeviceCamera.getDeviceCamera(result).data);
						if (cameras.size() == 0) {
							makeToast(getString(R.string.tips_no_devices_now));
							return;
						}
						long yearTimestamp = 365 * 24 * 60 * 60;// 一年的秒数
						for (DeviceCamera deviceCamera : cameras) {
							if (deviceCamera.cameraType == DeviceCamera.CAM_TYPE_ZHIYUN) {
								long overDueTimestamp = (ToolUtil
										.getTimestamp(deviceCamera.overDueDate) - System
										.currentTimeMillis()) / 1000;// 秒
								if (overDueTimestamp > yearTimestamp) {
									// 大于一年，100%
									deviceCamera.progress = 100;
									deviceCamera.progressText = "100%";
								} else if (overDueTimestamp < 0) {
									// 已过期
									deviceCamera.progress = 0;
									deviceCamera.progressText = "0%";
								} else {
									// 过期比例
									deviceCamera.progress = (float) ((overDueTimestamp * 1.0 / yearTimestamp) * 100);
									deviceCamera.progressText = String.format(
											"%.1f", deviceCamera.progress)
											+ "%";
								}
								if (!StringUtils
										.isEmpty(deviceCamera.overDueDate)) {
									deviceCamera.overDueDate = deviceCamera.overDueDate
											.substring(0,
													deviceCamera.overDueDate
															.indexOf("T"));
								}
								deviceCamera.bitmap = DBManager.getBitmapFromByteArray(dbManager
										.getSnapshot(deviceCamera.zCloud));
								uidList.add(deviceCamera.zCloud);
							}
						}
						isDataLoaded = true;
						adapter.setData(cameras);
						adapter.notifyDataSetChanged();
					}

					@Override
					protected void onTaskError(int code) {
						super.onTaskError(code);
						mPullRefreshListView.onRefreshComplete();
					}

					@Override
					protected void onTaskFailure() {
						super.onTaskFailure();
						LogUtil.i("error", "myDevice_notfind");
						mPullRefreshListView.onRefreshComplete();
					}

				});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (dbManager != null) {
			dbManager.closeDB();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (KCApplication.isRefreshDevices) {
			getDevices();
			KCApplication.isRefreshDevices = false;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		if (requestCode == REQUEST_CODE_ADD_DEVICE_SUCCESS
				|| requestCode == REQUEST_CODE_ADD_DEVICE
				|| requestCode == REQUEST_LOGIN_SUCCESS_REFRESH) {
			cameras.clear();
			getDevices();
		}
		if (requestCode == REQUEST_CODE_ZHIYUN_IMAGE) {
			if (data != null) {
				if (data.getIntExtra("pay_success", 0) == 100) {
					// 支付成功
					getDevices();
				} else if (data.getByteArrayExtra("snapshot") != null) {
					Bitmap bitmap = DBManager.getBitmapFromByteArray(data
							.getByteArrayExtra("snapshot"));
					int position = data.getIntExtra("mPosition", 0);
					cameras.get(position).bitmap = bitmap;
					adapter.notifyDataSetChanged();
				}
			}
		}
		if (requestCode == REQUEST_CODE_SCAN_QR_CODE
				|| requestCode == REQUEST_CODE_SEARCH) {
			// 二维码扫描
			Intent intent = new Intent();
			intent.setClass(this.getActivity(), AddDevicePropertyActivity.class);
			intent.putExtra(AddDevicePropertyActivity.INTENT_MODE,
					AddDevicePropertyActivity.ADD_MODE_ZHIYUN);
			intent.putExtra(AddDevicePropertyActivity.INTENT_DEV_UID,
					data.getStringExtra("dev_uid"));
			startActivityForResult(intent, REQUEST_CODE_ADD_DEVICE_SUCCESS);
		}
	}

	// 添加设备
	public void addDeviceAction() {
		if (!UserAccount.isUserLogin()) {
			makeToast(getString(R.string.login_user_please_login_first));
			Intent intent = new Intent();
			intent.setClass(this.getActivity(), LoginActivity.class);
			startActivityForResult(intent, REQUEST_LOGIN_SUCCESS_REFRESH);
		} else {
			Intent intent = new Intent();
			intent.setClass(this.getActivity(), AddDeviceChoiceActivity.class);
			intent.putStringArrayListExtra("uidList", uidList);
			startActivityForResult(intent, REQUEST_CODE_ADD_DEVICE);
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!UserAccount.isUserLogin()) {
			if (!hidden) {
				// 重新显示的时候判断当前是否已经登陆
				if (adapter != null) {
					cameras.clear();
					adapter.notifyDataSetChanged();
					makeToast(getString(R.string.login_user_please_login_first));
					isDataLoaded = false;
				}
			}
		} else if (UserAccount.getLoginUserChanged()) {
			// 当用户登录了其他账号时，这时应该刷新页面
			getDevices();
			UserAccount.saveLoginUserChanged(false);
		} else if (UserAccount.isUserLogin()) {
			if (!hidden && !isDataLoaded) {
				// 已登录，重新显示
				getDevices();
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		// Intent intent = new Intent();
		// intent.setClass(MyDeviceFragment.this.getActivity(),
		// Sip1303VideoByJavaActivity.class);
		// startActivity(intent);
		if (cameras.size() > 0) {
			// 因为下拉刷新占用了ListView的headerView，所以这里要减去1
			int position = i - 1;
			DeviceCamera deviceCamera = cameras.get(position);
			DeviceProperty deviceProperty = new DeviceProperty();
			deviceProperty.deviceId = deviceCamera.id;
			deviceProperty.modelId = deviceCamera.modelId;
			deviceProperty.cameraType = deviceCamera.cameraType;
			deviceProperty.wanIp = deviceCamera.wanIp;
			deviceProperty.wanPort = deviceCamera.tcpPort;
			deviceProperty.lanIp = deviceCamera.cameraIp;
			deviceProperty.lanPort = deviceCamera.cameraPort;
			deviceProperty.account = deviceCamera.cameraUser;
			deviceProperty.password = deviceCamera.cameraPassword;
			deviceProperty.deviceName = deviceCamera.displayName;
			deviceProperty.zCloudName = deviceCamera.zCloud;
			deviceProperty.isShared = deviceCamera.isShared == 0 ? false : true;
			deviceProperty.listPosition = position;
			deviceProperty.ddnsName = deviceCamera.ddnsName;
			deviceProperty.overDueDate = deviceCamera.overDueDate;
			deviceProperty.progress = deviceCamera.progress;
			deviceProperty.progressText = deviceCamera.progressText;
			deviceProperty.isCamAdmin = deviceCamera.isCameraAdmin == 1;
			Intent intent = new Intent();
			if (deviceProperty.cameraType == AddDevicePropertyActivity.ADD_MODE_ZHIYUN) {
				intent.setClass(MyDeviceFragment.this.getActivity(),
						ZhiyunDevicePropertyActivity.class);
				intent.putExtra("deviceCamera", deviceProperty);
				startActivityForResult(intent, REQUEST_CODE_ZHIYUN_IMAGE);
			} else {
				intent.setClass(MyDeviceFragment.this.getActivity(),
						IpDevicePropertyActivity.class);
				intent.putExtra("deviceCamera", deviceProperty);
				startActivityForResult(intent, REQUEST_CODE_ZHIYUN_IMAGE);
			}

		}
	}

	@Override
	public boolean onItemLongClick(final AdapterView<?> adapterView, View view,
			int i, long l) {
		if (i <= 0) {
			return false;
		}
		final int position = i - 1;// 所在位置
		final DeviceCamera listCamera = cameras.get(position);

		new AlertDialog.Builder(this.getActivity())
				.setTitle(getString(R.string.add_device_delete_device))
				.setPositiveButton(getString(R.string.btn_ok),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialogInterface, int i) {
								Map<String, String> map = new HashMap<>();
								map.put(CameraConstants.USER_ID,
										String.valueOf(UserAccount.getUserID()));
								map.put(CameraConstants.ID,
										String.valueOf(listCamera.id));
								doPost(UrlResources.URL_DELETE_DEVICE,
										ApiClientUtility.getParams(map),
										new VolleyResponse(
												MyDeviceFragment.this
														.getActivity(), false,
												"loading...") {

											@Override
											protected void onTaskSuccessRoot(
													JSONObject result) {
												super.onTaskSuccess(result);
												if (listCamera.cameraType == DeviceCamera.CAM_TYPE_ZHIYUN) {
													for (String uid : uidList) {
														if (uid.equals(listCamera.zCloud)) {
															uidList.remove(uid);
															break;
														}
													}
												}
												cameras.remove(position);
												adapter.setData(cameras);
												adapter.notifyDataSetChanged();
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
		return true;
	}

	@Override
	public int getLayoutId() {
		return R.layout.fragment_my_devide;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase refreshView) {
		if (!UserAccount.isUserLogin()) {
			makeToast(getString(R.string.login_user_please_login_first));
			Intent intent = new Intent();
			intent.setClass(this.getActivity(), LoginActivity.class);
			startActivityForResult(intent, REQUEST_LOGIN_SUCCESS_REFRESH);
			mPullRefreshListView.onRefreshComplete();
		} else {
			getDevices();
		}
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase refreshView) {

	}

	public void addDevices(Intent intent, int mode) {
		switch (mode) {
		case 0:
			// ip模式
			intent.setClass(this.getActivity(), AddDevicePropertyActivity.class);
			intent.putExtra(AddDevicePropertyActivity.INTENT_MODE,
					AddDevicePropertyActivity.ADD_MODE_IP);
			startActivityForResult(intent, REQUEST_CODE_ADD_DEVICE_SUCCESS);
			break;
		case 1:
			// ddns模式
			intent.setClass(this.getActivity(), AddDevicePropertyActivity.class);
			intent.putExtra(AddDevicePropertyActivity.INTENT_MODE,
					AddDevicePropertyActivity.ADD_MODE_DDNS);
			startActivityForResult(intent, REQUEST_CODE_ADD_DEVICE_SUCCESS);
			break;
		case 2:
			// 智云号模式
			intent.setClass(this.getActivity(), AddDevicePropertyActivity.class);
			intent.putExtra(AddDevicePropertyActivity.INTENT_MODE,
					AddDevicePropertyActivity.ADD_MODE_ZHIYUN);
			startActivityForResult(intent, REQUEST_CODE_ADD_DEVICE_SUCCESS);
			break;
		case 3:
			// 搜索模式
			intent.setClass(this.getActivity(), SearchUIDWifiActivity.class);
			intent.putStringArrayListExtra("uidList", uidList);
			startActivityForResult(intent, REQUEST_CODE_SEARCH);
			break;
		case 4:
			// 扫描模式
			intent.setClass(this.getActivity(), CaptureActivity.class);
			startActivityForResult(intent, REQUEST_CODE_SCAN_QR_CODE);
			break;
		}
	}

}