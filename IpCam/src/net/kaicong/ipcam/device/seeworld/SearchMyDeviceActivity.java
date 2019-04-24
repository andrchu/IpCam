package net.kaicong.ipcam.device.seeworld;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.kaicong.ipcam.AddDevicePropertyActivity;
import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.R;

import net.kaicong.ipcam.adpater.MyDeviceAdapter;
import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.DeviceCamera;
import net.kaicong.ipcam.bean.DeviceProperty;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.db.DBManager;
import net.kaicong.ipcam.device.IpDevicePropertyActivity;
import net.kaicong.ipcam.device.ZhiyunDevicePropertyActivity;
import net.kaicong.ipcam.user.UserAccount;
import net.kaicong.ipcam.utils.StringUtils;
import net.kaicong.ipcam.utils.ToolUtil;

import net.kaicong.utility.ApiClientUtility;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LingYan on 2014/8/29.
 */

public class SearchMyDeviceActivity extends BaseActivity implements AdapterView.OnItemClickListener, PullToRefreshBase.OnRefreshListener2 {

    //Activity的回调code
    public static final int REQUEST_CODE_SCAN_QR_CODE = 1000;
    public static final int REQUEST_CODE_ADD_DEVICE_SUCCESS = REQUEST_CODE_SCAN_QR_CODE + 1;
    public static final int REQUEST_CODE_SEARCH = REQUEST_CODE_ADD_DEVICE_SUCCESS + 1;
    public static final int REQUEST_CODE_ZHIYUN_IMAGE = REQUEST_CODE_SEARCH + 1;
    public static final int REQUEST_CODE_ADD_DEVICE = REQUEST_CODE_ZHIYUN_IMAGE + 1;
    public static final int REQUEST_LOGIN_SUCCESS_REFRESH = REQUEST_CODE_ADD_DEVICE + 1;
    //摄像机列表
    private List<DeviceCamera> cameras = new ArrayList<>();
    private MyDeviceAdapter adapter;
    private ListView listView;
    private PullToRefreshListView mPullRefreshListView;
    private DBManager dbManager;

    private SearchView searchView;
    private String textResult;
    protected int pageIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_seeworld);

        initTitle(getString(R.string.title_search_camera_title));
        showBackButton();

        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        adapter = new MyDeviceAdapter(this);
        adapter.setData(cameras);
        listView = mPullRefreshListView.getRefreshableView();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        dbManager = new DBManager(this);

        searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setIconifiedByDefault(true);
        searchView.onActionViewExpanded();
        searchView.setFocusable(false);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                if (StringUtils.isEmpty(s)) {
                    makeToast(getString(R.string.common_input_not_empty));
                    return false;
                }
                if (searchView != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                    }
                    searchView.clearFocus();
                }
                textResult = s;
                doSearch(textResult);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (StringUtils.isEmpty(s)) {
                    cameras.clear();
                    adapter.notifyDataSetChanged();
                    return true;
                }
                return false;
            }

        });

    }

    private void doSearch(String text) {
        Map<String, String> map = new HashMap<>();
        map.put("keyword", text);
        map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
        map = ApiClientUtility.getParams(map);
        map.put(CameraConstants.PAGE_INDEX, String.valueOf(pageIndex));
        map.put(CameraConstants.PAGE_SIZE, CameraConstants.NORMAL_PAGE_SIZE);
        doPost(UrlResources.URL_SEARCH_MY_DEVICE_LIST, map, new VolleyResponse(this, true, getString(R.string.title_search_camera)) {

            @Override
            protected void onTaskFailure() {
                super.onTaskFailure();
                mPullRefreshListView.onRefreshComplete();
            }

            @Override
            protected void onTaskSuccess(JSONArray result) {
                super.onTaskSuccess(result);
                mPullRefreshListView.onRefreshComplete();
                cameras.addAll(DeviceCamera.getDeviceCamera(result).data);
                if (cameras.size() == 0) {
                    makeToast(getString(R.string.tips_no_devices_now));
                    return;
                }
                long yearTimestamp = 365 * 24 * 60 * 60;//一年的秒数
                for (DeviceCamera deviceCamera : cameras) {
                    if (deviceCamera.cameraType == DeviceCamera.CAM_TYPE_ZHIYUN) {
                        long overDueTimestamp = (ToolUtil.getTimestamp(deviceCamera.overDueDate) - System.currentTimeMillis()) / 1000;//秒
                        if (overDueTimestamp > yearTimestamp) {
                            //大于一年，100%
                            deviceCamera.progress = 100;
                            deviceCamera.progressText = "100%";
                        } else if (overDueTimestamp < 0) {
                            //已过期
                            deviceCamera.progress = 0;
                            deviceCamera.progressText = "0%";
                        } else {
                            //过期比例
                            deviceCamera.progress = (float) ((overDueTimestamp * 1.0 / yearTimestamp) * 100);
                            deviceCamera.progressText = String.format("%.1f", deviceCamera.progress) + "%";
                        }
                        if (!StringUtils.isEmpty(deviceCamera.overDueDate)) {
                            if (deviceCamera.overDueDate.indexOf("T") != -1) {
                                deviceCamera.overDueDate = deviceCamera.overDueDate.substring(0, deviceCamera.overDueDate.indexOf("T"));
                            }
                        }
                        deviceCamera.bitmap = DBManager.getBitmapFromByteArray(dbManager.getSnapshot(deviceCamera.zCloud));
                    }
                }
                adapter.setData(cameras);
                adapter.notifyDataSetChanged();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_ADD_DEVICE_SUCCESS || requestCode == REQUEST_CODE_ADD_DEVICE || requestCode == REQUEST_LOGIN_SUCCESS_REFRESH) {
            cameras.clear();
            doSearch(textResult);
        }
        if (requestCode == REQUEST_CODE_ZHIYUN_IMAGE) {
            if (data != null) {
                if (data.getIntExtra("pay_success", 0) == 100) {
                    //支付成功
                    doSearch(textResult);
                } else if (data.getByteArrayExtra("snapshot") != null) {
                    Bitmap bitmap = DBManager.getBitmapFromByteArray(data.getByteArrayExtra("snapshot"));
                    int position = data.getIntExtra("mPosition", 0);
                    cameras.get(position).bitmap = bitmap;
                    adapter.notifyDataSetChanged();
                }
            }
        }
        if (requestCode == REQUEST_CODE_SCAN_QR_CODE || requestCode == REQUEST_CODE_SEARCH) {
            //二维码扫描
            Intent intent = new Intent();
            intent.setClass(this, AddDevicePropertyActivity.class);
            intent.putExtra(AddDevicePropertyActivity.INTENT_MODE, AddDevicePropertyActivity.ADD_MODE_ZHIYUN);
            intent.putExtra(AddDevicePropertyActivity.INTENT_DEV_UID, data.getStringExtra("dev_uid"));
            startActivityForResult(intent, REQUEST_CODE_ADD_DEVICE_SUCCESS);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        Intent intent = new Intent();
//        intent.setClass(MyDeviceFragment.this.getActivity(), Sip1303VideoByJavaActivity.class);
//        startActivity(intent);
        if (cameras.size() > 0) {
            //因为下拉刷新占用了ListView的headerView，所以这里要减去1
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
            Intent intent = new Intent();
            if (deviceProperty.cameraType == AddDevicePropertyActivity.ADD_MODE_ZHIYUN) {
                intent.setClass(this, ZhiyunDevicePropertyActivity.class);
                intent.putExtra("deviceCamera", deviceProperty);
                startActivityForResult(intent, REQUEST_CODE_ZHIYUN_IMAGE);
            } else {
                intent.setClass(this, IpDevicePropertyActivity.class);
                intent.putExtra("deviceCamera", deviceProperty);
                startActivityForResult(intent, REQUEST_CODE_ZHIYUN_IMAGE);
            }

        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        pageIndex++;
        doSearch(textResult);
    }

}