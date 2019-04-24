package net.kaicong.ipcam.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.kaicong.ipcam.BaseFragment;
import net.kaicong.ipcam.R;
import net.kaicong.ipcam.adpater.SharedDevicesAdapter;
import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.GetCameraModel;
import net.kaicong.ipcam.bean.SharedCamera;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.device.seeworld.BaseSeeWorldActivity;
import net.kaicong.ipcam.device.seeworld.SeeSip1018DeviceActivity;
import net.kaicong.ipcam.device.seeworld.SeeSip1211DeviceActivity;
import net.kaicong.ipcam.device.seeworld.SeeSip1303DeviceActivity;
import net.kaicong.ipcam.device.seeworld.SeeSip1601DeviceActivity;
import net.kaicong.ipcam.device.seeworld.SeeZhiyunDeviceActivity;
import net.kaicong.ipcam.device.seeworld.Summary;
import net.kaicong.ipcam.device.sip1018.MyIpCamera;
import net.kaicong.ipcam.user.UserAccount;
import net.kaicong.utility.ApiClientUtility;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LingYan on 15/7/24.
 * 用户分享的所有设备列表
 */
public class SharedDevicesFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private ListView listView;
    private boolean isDataLoaded = false;
    private List<SharedCamera> data = new ArrayList<>();
    private SharedDevicesAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyView;
    private Summary summary;

    @Override
    protected void initView(View convertView) {
        super.initView(convertView);
        listView = (ListView) convertView.findViewById(R.id.list);
        progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
        emptyView = (TextView) convertView.findViewById(R.id.empty_view);
        adapter = new SharedDevicesAdapter();
        adapter.setData(data);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_get_shared_devices;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isDataLoaded && isVisibleToUser) {
            getData();
        }
    }

    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put(CameraConstants.USER_ID, summary.userId + "");
        doPost(UrlResources.URL_GET_SHARED_DEVICES, ApiClientUtility.getParams(map), new VolleyResponse(this.getActivity()
                , false, getString(R.string.activity_base_progress_dialog_content)) {

            @Override
            protected void onTaskSuccess(JSONArray result) {
                progressBar.setVisibility(View.GONE);
                SharedCamera sharedCamera = SharedCamera.getSharedCameraInfo(result);
                for (SharedCamera mCamera : sharedCamera.data) {
                    int selectModelId = GetCameraModel.getCameraModel(mCamera.ddnsModelId);
                    if (selectModelId == GetCameraModel.CAMERA_MODEL_SIP1018 ||
                            selectModelId == GetCameraModel.CAMERA_MODEL_SIP1303 ||
                            selectModelId == GetCameraModel.CAMERA_MODEL_SIP1601 ||
                            selectModelId == GetCameraModel.CAMERA_MODEL_ZHIYUN ||
                            selectModelId == GetCameraModel.CAMERA_MODEL_SIP1201) {

                        //如果设备ID相同，表示是同一台设备，把他移除
                        if (summary.deviceId == mCamera.id) {
                            sharedCamera.data.remove(mCamera);
                            break;
                        }
                    }
                }
                data.addAll(sharedCamera.data);
                adapter.setData(data);
                adapter.notifyDataSetChanged();

                //列表为空
                if (data.size() <= 0) {
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    emptyView.setVisibility(View.GONE);
                }

                isDataLoaded = true;

            }

            @Override
            protected void onTaskError(int code) {
                super.onTaskError(code);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            protected void onTaskFailure() {
                super.onTaskFailure();
                progressBar.setVisibility(View.GONE);
            }

        });
    }

    private class ChangePlayTask extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected Integer doInBackground(Integer... voids) {
            SharedCamera sharedCamera = data.get(voids[0]);
            switch (GetCameraModel.getCameraModel(sharedCamera.productModelId)) {

                /**
                 * 1018ddns系列
                 */
                case GetCameraModel.CAMERA_MODEL_SIP1018:
                    ((BaseSeeWorldActivity) SharedDevicesFragment.this.getActivity()).changePlay(sharedCamera.ddnsWanIp, sharedCamera.ddnsTcpPort, sharedCamera.account, sharedCamera.password, null);
                    break;

                /**
                 * 1303系列
                 */
                case GetCameraModel.CAMERA_MODEL_SIP1201:
                case GetCameraModel.CAMERA_MODEL_SIP1303:
                    ((BaseSeeWorldActivity) SharedDevicesFragment.this.getActivity()).changePlay(sharedCamera.ddnsWanIp, sharedCamera.ddnsTcpPort, sharedCamera.account, sharedCamera.password, null);
                    break;

                /**
                 * 1211系列
                 */
                case GetCameraModel.CAMERA_MODEL_SIP1211:
                    ((BaseSeeWorldActivity) SharedDevicesFragment.this.getActivity()).changePlay(sharedCamera.ddnsWanIp, sharedCamera.ddnsTcpPort, sharedCamera.account, sharedCamera.password, null);
                    break;

                /**
                 * 智云
                 */
                case GetCameraModel.CAMERA_MODEL_ZHIYUN:
                    ((BaseSeeWorldActivity) SharedDevicesFragment.this.getActivity()).changePlay(null, 0, sharedCamera.account, sharedCamera.password, sharedCamera.zCloud);
                    break;

                /**
                 * 1601系列
                 */
                case GetCameraModel.CAMERA_MODEL_SIP1601:
                    ((BaseSeeWorldActivity) SharedDevicesFragment.this.getActivity()).changePlay(sharedCamera.ddnsWanIp, sharedCamera.ddnsTcpPort, sharedCamera.account, sharedCamera.password, null);
                    break;

//            /**
//             * 1201系列
//             */
//            case GetCameraModel.CAMERA_MODEL_SIP1201:
//                intent.setClass(this.getActivity(), SeeSip1201DeviceActivity.class);
//                intent.putExtra("ip", sharedCamera.ddnsWanIp);
//                intent.putExtra("port", sharedCamera.ddnsTcpPort);
//                intent.putExtra("account", sharedCamera.account);
//                intent.putExtra("password", sharedCamera.password);
//                intent.putExtra("mDeviceId", sharedCamera.id);
//                startActivity(intent);
//                break;
                default:
                    return -1;
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer aVoid) {
            super.onPostExecute(aVoid);
            removeProgressDialog();
            if (aVoid == -1) {
                //不支持播放
                makeToast(getString(R.string.add_device_not_support_yet));
            } else if (aVoid == 0) {
                //切换成功
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        if (((BaseSeeWorldActivity) this.getActivity()).currentModelId ==
//                GetCameraModel.getCameraModel(data.get(i).productModelId)) {
//            //如果当前播放器类型和点击的设备属于同类型，则直接切换播放
//            new ChangePlayTask().execute(i);
//        } else {
        //跳转播放, 这样更好操作
        gotoSeeWorld(i);
        ((BaseSeeWorldActivity) this.getActivity()).quitIfRecording();
//        }

    }

    private void gotoSeeWorld(int position) {
        SharedCamera sharedCamera = data.get(position);
        Intent intent = new Intent();
        switch (GetCameraModel.getCameraModel(sharedCamera.ddnsModelId)) {

            /**
             * 1018ddns系列
             */
            case GetCameraModel.CAMERA_MODEL_SIP1018:
                MyIpCamera myIpCamera = new MyIpCamera("", sharedCamera.shareName, sharedCamera.ddnsWanIp, String.valueOf(sharedCamera.ddnsTcpPort), sharedCamera.account,
                        sharedCamera.password, 1000
                );
                intent.setClass(this.getActivity(), SeeSip1018DeviceActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(CameraConstants.CAMERA, myIpCamera);
                intent.putExtras(bundle);
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
//            /**
//             * 1201系列
//             */
//            case GetCameraModel.CAMERA_MODEL_SIP1201:
//                intent.setClass(this.getActivity(), SeeSip1201DeviceActivity.class);
//                intent.putExtra("ip", sharedCamera.ddnsWanIp);
//                intent.putExtra("port", sharedCamera.ddnsTcpPort);
//                intent.putExtra("account", sharedCamera.account);
//                intent.putExtra("password", sharedCamera.password);
//                intent.putExtra("mDeviceId", sharedCamera.id);
//                startActivity(intent);
//                break;
            default:
                makeToast(getString(R.string.add_device_not_support_yet));
                break;
        }
    }

}
