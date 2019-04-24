package net.kaicong.ipcam.device.seeworld;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.SearchView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.R;
import net.kaicong.ipcam.adpater.WorldViewAdapter;
import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.GetCameraModel;
import net.kaicong.ipcam.bean.SharedCamera;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.device.sip1018.MyIpCamera;
import net.kaicong.ipcam.user.MyCollectDeviceActivity;
import net.kaicong.ipcam.user.UserAccount;
import net.kaicong.ipcam.utils.StringUtils;
import net.kaicong.utility.ApiClientUtility;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LingYan on 15/4/29.
 */
public class SearchSeeWorldActivity extends BaseActivity implements PullToRefreshBase.OnRefreshListener2,
        PullToRefreshBase.OnLastItemVisibleListener,
        WorldViewAdapter.OnWorldViewItemClickListener {

    public static final int REQUEST_CODE_LOGIN_SUCCESS = 1000;
    protected List<SharedCamera> data = new ArrayList<>();
    protected PullToRefreshListView mPullRefreshListView;
    protected WorldViewAdapter mAdapter;
    protected int pageIndex = 1;
    private SearchView searchView;
    private String textResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle(getString(R.string.title_search_camera_title));
        showBackButton();
//        showRightButton(R.drawable.common_collect_device);
        setContentView(R.layout.activity_search_seeworld);
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
        mAdapter = new WorldViewAdapter(this);
        mAdapter.setData(data);
        mPullRefreshListView.setOnRefreshListener(this);
        mPullRefreshListView.setOnLastItemVisibleListener(this);
        mPullRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        ListView actualListView = mPullRefreshListView.getRefreshableView();
        actualListView.setAdapter(mAdapter);
        mAdapter.setOnWorldViewItemClickListener(this);

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
                    data.clear();
                    textResult = "";
                    mAdapter.notifyDataSetChanged();
                    return true;
                }
                return false;
            }

        });
    }

    private void doSearch(String text) {
        Map<String, String> map = new HashMap<>();
        map.put(CameraConstants.SORT_KEYWORD, CameraConstants.SORT_KEYWORD_PRAISE_COUNT);
        map.put("keyword", text);
        map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
        map.put(CameraConstants.PAGE_INDEX, String.valueOf(pageIndex));
        map.put(CameraConstants.PAGE_SIZE, CameraConstants.NORMAL_PAGE_SIZE);
        doPost(UrlResources.URL_SEARCH_SEE_WORLD_LIST, ApiClientUtility.getParams(map), new VolleyResponse(this, true, getString(R.string.title_search_camera)) {

            @Override
            protected void onTaskFailure() {
                super.onTaskFailure();
                mPullRefreshListView.onRefreshComplete();
            }

            @Override
            protected void onTaskSuccess(JSONArray result) {
                super.onTaskSuccess(result);
                mPullRefreshListView.onRefreshComplete();
                List<SharedCamera> resultData = new ArrayList<>();
                SharedCamera sharedCamera = SharedCamera.getSharedCameraInfo(result);
                for (SharedCamera mCamera : sharedCamera.data) {
                    int selectModelId = GetCameraModel.getCameraModel(mCamera.ddnsModelId);
                    if (selectModelId == GetCameraModel.CAMERA_MODEL_SIP1018 ||
                            selectModelId == GetCameraModel.CAMERA_MODEL_SIP1303 ||
                            selectModelId == GetCameraModel.CAMERA_MODEL_SIP1601 ||
                            selectModelId == GetCameraModel.CAMERA_MODEL_ZHIYUN ||
                            selectModelId == GetCameraModel.CAMERA_MODEL_SIP1201) {
                        resultData.add(mCamera);
                    }
                }

                if (mPullRefreshListView.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_START) {
                    //下拉刷新
                    data.clear();
                    data.addAll(resultData);
                } else if (mPullRefreshListView.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_END) {
                    //上拉加载
                    data.addAll(resultData);
                }
                if (data.size() <= 0) {
                    return;
                }
                mAdapter.setData(data);
                mAdapter.notifyDataSetChanged();
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_LOGIN_SUCCESS) {
            Intent intent = new Intent();
            intent.setClass(SearchSeeWorldActivity.this, MyCollectDeviceActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onLastItemVisible() {

    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        pageIndex = 1;
        doSearch(textResult);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        pageIndex++;
        doSearch(textResult);
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
                MyIpCamera myIpCamera = new MyIpCamera("", sharedCamera.shareName, sharedCamera.ddnsWanIp, String.valueOf(sharedCamera.ddnsTcpPort), sharedCamera.account,
                        sharedCamera.password, 1000
                );
                intent.setClass(this, SeeSip1018DeviceActivity.class);
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
                intent.setClass(this, SeeSip1303DeviceActivity.class);
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
                intent.setClass(this, SeeSip1211DeviceActivity.class);
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
                intent.setClass(this, SeeZhiyunDeviceActivity.class);
                startActivity(intent);
                break;
            /**
             * 1601系列
             */
            case GetCameraModel.CAMERA_MODEL_SIP1601:
                intent.setClass(this, SeeSip1601DeviceActivity.class);
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
