package net.kaicong.ipcam.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.R;

import net.kaicong.ipcam.adpater.MyCollectDeviceAdapter;
import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.GetCameraModel;
import net.kaicong.ipcam.bean.MyCollect;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.device.seeworld.SeeSip1018DeviceActivity;
import net.kaicong.ipcam.device.seeworld.SeeSip1201DeviceActivity;
import net.kaicong.ipcam.device.seeworld.SeeSip1303DeviceActivity;
import net.kaicong.ipcam.device.seeworld.SeeSip1601DeviceActivity;
import net.kaicong.ipcam.device.seeworld.SeeZhiyunDeviceActivity;
import net.kaicong.ipcam.device.sip1018.MyIpCamera;


import net.kaicong.utility.ApiClientUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LingYan on 14-12-17.
 */
public class MyCollectDeviceActivity extends BaseActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, PullToRefreshBase.OnRefreshListener2 {

    private ListView listView;
    private PullToRefreshListView mPullRefreshListView;
    private MyCollectDeviceAdapter myCollectDeviceAdapter;
    private List<MyCollect> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle(getString(R.string.see_world_my_collect));
        showBackButton();
        setContentView(R.layout.activity_my_collect_device);
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.list);
        mPullRefreshListView.setOnRefreshListener(this);
        listView = mPullRefreshListView.getRefreshableView();
        myCollectDeviceAdapter = new MyCollectDeviceAdapter();
        myCollectDeviceAdapter.setData(data);
        listView.setAdapter(myCollectDeviceAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        getCollectList();
    }

    private void getCollectList() {

        Map<String, String> map = new HashMap<>();
        map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
        doPost(UrlResources.URL_GET_COLLECT_LIST, ApiClientUtility.getParams(map), new VolleyResponse(this, true, getString(R.string.com_facebook_loading)) {

            @Override
            protected void onTaskError(int code) {
                super.onTaskError(code);
                mPullRefreshListView.onRefreshComplete();
            }

            @Override
            protected void onTaskFailure() {
                super.onTaskFailure();
                mPullRefreshListView.onRefreshComplete();
            }

            @Override
            protected void onTaskSuccess(JSONArray result) {
                super.onTaskSuccess(result);
                mPullRefreshListView.onRefreshComplete();
                data.clear();
                data.addAll(MyCollect.getMyCollectData(result).data);
                if (data.size() == 0) {
                    makeToast(getString(R.string.tips_no_favorite_device));
                    return;
                }
                myCollectDeviceAdapter.setData(data);
                myCollectDeviceAdapter.notifyDataSetChanged();
            }

        });

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        MyCollect myCollect = data.get(i - 1);
        Intent intent = new Intent();
        switch (GetCameraModel.getCameraModel(myCollect.productModelId)) {

            /**
             * 1018ddns系列
             */
            case GetCameraModel.CAMERA_MODEL_SIP1018:
                MyIpCamera myIpCamera = new MyIpCamera("", myCollect.deviceName, myCollect.ip, String.valueOf(myCollect.port), myCollect.account,
                        myCollect.password, 1000);
                intent.setClass(this, SeeSip1018DeviceActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(CameraConstants.CAMERA, myIpCamera);
                intent.putExtras(bundle);
                intent.putExtra("mDeviceId", myCollect.id);
                intent.putExtra("title", myCollect.deviceName);
                startActivity(intent);
                break;

            /**
             * 1303系列
             */
            case GetCameraModel.CAMERA_MODEL_SIP1303:
                intent.setClass(this, SeeSip1303DeviceActivity.class);
                intent.putExtra("mIp", myCollect.ip);
                intent.putExtra("mPort", myCollect.port);
                intent.putExtra("mAccount", myCollect.account);
                intent.putExtra("mPassword", myCollect.password);
                intent.putExtra("mDeviceId", myCollect.id);
                intent.putExtra("title", myCollect.deviceName);
                startActivity(intent);
                break;

            /**
             * 1601系列
             */
            case GetCameraModel.CAMERA_MODEL_SIP1601:
                intent.setClass(this, SeeSip1601DeviceActivity.class);
                intent.putExtra("ip", myCollect.ip);
                intent.putExtra("port", myCollect.port);
                intent.putExtra("account", myCollect.account);
                intent.putExtra("password", myCollect.password);
                intent.putExtra("mDeviceId", myCollect.id);
                intent.putExtra("title", myCollect.deviceName);
                startActivity(intent);
                break;
            /**
             * 1201系列
             */
            case GetCameraModel.CAMERA_MODEL_SIP1201:
                intent.setClass(this, SeeSip1201DeviceActivity.class);
                intent.putExtra("ip", myCollect.ip);
                intent.putExtra("port", myCollect.port);
                intent.putExtra("account", myCollect.account);
                intent.putExtra("password", myCollect.password);
                intent.putExtra("mDeviceId", myCollect.id);
                intent.putExtra("title", myCollect.deviceName);
                startActivity(intent);
                break;

            case GetCameraModel.CAMERA_MODEL_ZHIYUN:
                intent.putExtra("mCameraName", myCollect.deviceName);
                intent.putExtra("mDevUID", myCollect.zCloudNum);
                intent.putExtra("avChannel", 0);
                intent.putExtra("mAccount", myCollect.account);
                intent.putExtra("mPassword", myCollect.password);
                intent.putExtra("mVideoQuality", 3);
                intent.putExtra("mDeviceId", myCollect.id);
                intent.putExtra("title", myCollect.deviceName);
                intent.setClass(this, SeeZhiyunDeviceActivity.class);
                startActivity(intent);
                break;
            default:
                makeToast(getString(R.string.add_device_not_support_yet));
                break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        final int position = i - 1;//所在位置
        final MyCollect myCollect = data.get(position);

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.see_world_delete_collect))
                .setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Map<String, String> map = new HashMap<>();
                        map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
                        map.put(CameraConstants.DEVICE_ID, String.valueOf(myCollect.id));
                        doPost(UrlResources.URL_DELETE_COLLECT, ApiClientUtility.getParams(map), new VolleyResponse(MyCollectDeviceActivity.this, false, getString(R.string.com_facebook_loading)) {

                            @Override
                            protected void onTaskSuccessRoot(JSONObject result) {
                                super.onTaskSuccess(result);
                                data.remove(position);
                                myCollectDeviceAdapter.setData(data);
                                myCollectDeviceAdapter.notifyDataSetChanged();
                            }

                        });
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();
        return true;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        getCollectList();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }

}
