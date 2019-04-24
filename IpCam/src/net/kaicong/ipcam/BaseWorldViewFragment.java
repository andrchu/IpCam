package net.kaicong.ipcam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.kaicong.ipcam.adpater.WorldViewAdapter;
import net.kaicong.ipcam.bean.GetCameraModel;
import net.kaicong.ipcam.bean.PositionModel;
import net.kaicong.ipcam.bean.SharedCamera;
import net.kaicong.ipcam.device.seeworld.SeeSip1018DeviceActivity;
import net.kaicong.ipcam.device.seeworld.SeeSip1211DeviceActivity;
import net.kaicong.ipcam.device.seeworld.SeeSip1303DeviceActivity;
import net.kaicong.ipcam.device.seeworld.SeeSip1601DeviceActivity;
import net.kaicong.ipcam.device.seeworld.SeeZhiyunDeviceActivity;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.device.sip1018.MyIpCamera;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LingYan on 15-1-4.package com.kaicong.ipcam.device.seeworld;
 */
public abstract class BaseWorldViewFragment extends BaseFragment implements
		PullToRefreshBase.OnRefreshListener2,
		PullToRefreshBase.OnLastItemVisibleListener,
		WorldViewAdapter.OnWorldViewItemClickListener {

	protected List<SharedCamera> data = new ArrayList<>();
	protected PullToRefreshListView mPullRefreshListView;
	protected WorldViewAdapter mAdapter;
	protected int pageIndex = 1;
	protected boolean isDataLoaded = false;
	protected PositionModel positionModel;

	// protected boolean isViewLoaded = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initView(View convertView) {
		super.initView(convertView);
		mPullRefreshListView = (PullToRefreshListView) convertView
				.findViewById(R.id.pull_refresh_list);
		mAdapter = new WorldViewAdapter(this.getActivity());
		mAdapter.setData(data);
		mPullRefreshListView.setOnRefreshListener(this);
		mPullRefreshListView.setOnLastItemVisibleListener(this);
		mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);

		ListView actualListView = mPullRefreshListView.getRefreshableView();
		actualListView.setAdapter(mAdapter);
		mAdapter.setOnWorldViewItemClickListener(this);
		// isViewLoaded = true;
		positionModel = new PositionModel();
	}

	@Override
	public int getLayoutId() {
		return R.layout.fragment_world_view_list;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mAdapter != null) {
			mAdapter.cancelAnimation();
		}
	}

	// 下拉刷新
	@Override
	public void onPullDownToRefresh(PullToRefreshBase refreshView) {
		pageIndex = 1;
		getData();
	}

	// 上拉加载
	@Override
	public void onPullUpToRefresh(PullToRefreshBase refreshView) {
		pageIndex++;
		getData();
	}

	// 滑动到最后一个
	@Override
	public void onLastItemVisible() {

	}

	@Override
	public void onWorldViewItemClick(View view, int position) {
		SharedCamera sharedCamera = data.get(position);
		Intent intent = new Intent();
		switch (GetCameraModel.getCameraModel(sharedCamera.ddnsModelId)) {

		/**
		 * 1018ddns系列
		 */
		case GetCameraModel.CAMERA_MODEL_SIP1018:
			MyIpCamera myIpCamera = new MyIpCamera("", sharedCamera.shareName,
					sharedCamera.ddnsWanIp,
					String.valueOf(sharedCamera.ddnsTcpPort),
					sharedCamera.account, sharedCamera.password, 1000);
			intent.setClass(this.getActivity(), SeeSip1018DeviceActivity.class);
			Bundle bundle1018 = new Bundle();
			bundle1018.putSerializable(CameraConstants.CAMERA, myIpCamera);
			intent.putExtras(bundle1018);
			intent.putExtra("mDeviceId", sharedCamera.id);
			intent.putExtra("title", sharedCamera.shareName);
			startActivity(intent);
			break;

		/**
		 * 1303系列
		 */
		case GetCameraModel.CAMERA_MODEL_SIP1201:
		case GetCameraModel.CAMERA_MODEL_SIP1303:
			intent.setClass(this.getActivity(), SeeSip1303DeviceActivity.class);
			intent.putExtra("mIp", sharedCamera.ddnsWanIp);
			intent.putExtra("mPort", sharedCamera.ddnsTcpPort);
			intent.putExtra("mAccount", sharedCamera.account);
			intent.putExtra("mPassword", sharedCamera.password);
			intent.putExtra("mDeviceId", sharedCamera.id);
			intent.putExtra("title", sharedCamera.shareName);
			startActivity(intent);
			break;

		/**
		 * 1211系列
		 */
		case GetCameraModel.CAMERA_MODEL_SIP1211:
			intent.setClass(this.getActivity(), SeeSip1211DeviceActivity.class);
			intent.putExtra("ip", sharedCamera.ddnsWanIp);
			intent.putExtra("port", sharedCamera.ddnsTcpPort);
			intent.putExtra("account", sharedCamera.account);
			intent.putExtra("password", sharedCamera.password);
			intent.putExtra("mDeviceId", sharedCamera.id);
			intent.putExtra("title", sharedCamera.shareName);
			startActivity(intent);
			break;

		case GetCameraModel.CAMERA_MODEL_ZHIYUN:
			intent.putExtra("mCameraName", sharedCamera.shareName);
			intent.putExtra("mDevUID", sharedCamera.zCloud);
			intent.putExtra("avChannel", 0);
			intent.putExtra("mAccount", sharedCamera.account);
			intent.putExtra("mPassword", sharedCamera.password);
			intent.putExtra("mVideoQuality", 3);
			intent.putExtra("mPosition", position);
			intent.putExtra("mDeviceId", sharedCamera.id);
			intent.putExtra("title", sharedCamera.shareName);
			intent.setClass(this.getActivity(), SeeZhiyunDeviceActivity.class);
			startActivity(intent);
			break;
		/**
		 * 1601系列
		 */
		case GetCameraModel.CAMERA_MODEL_SIP1601:
			intent.setClass(this.getActivity(), SeeSip1601DeviceActivity.class);
			intent.putExtra("ip", sharedCamera.ddnsWanIp);
			intent.putExtra("port", sharedCamera.ddnsTcpPort);
			intent.putExtra("account", sharedCamera.account);
			intent.putExtra("password", sharedCamera.password);
			intent.putExtra("mDeviceId", sharedCamera.id);
			intent.putExtra("title", sharedCamera.shareName);
			startActivity(intent);
			break;
		// /**
		// * 1201系列
		// */
		// case GetCameraModel.CAMERA_MODEL_SIP1201:
		// intent.setClass(this.getActivity(), SeeSip1201DeviceActivity.class);
		// intent.putExtra("ip", sharedCamera.ddnsWanIp);
		// intent.putExtra("port", sharedCamera.ddnsTcpPort);
		// intent.putExtra("account", sharedCamera.account);
		// intent.putExtra("password", sharedCamera.password);
		// intent.putExtra("mDeviceId", sharedCamera.id);
		// startActivity(intent);
		// break;
		default:
			makeToast(getString(R.string.add_device_not_support_yet));
			break;
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (!isDataLoaded && isVisibleToUser) {
			getData();
		}
	}

	public abstract void getData();

}
