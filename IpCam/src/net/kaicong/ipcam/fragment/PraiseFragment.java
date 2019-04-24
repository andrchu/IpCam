package net.kaicong.ipcam.fragment;

import com.handmark.pulltorefresh.library.PullToRefreshBase;

import net.kaicong.ipcam.BaseWorldViewFragment;
import net.kaicong.ipcam.R;

import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.GetCameraModel;
import net.kaicong.ipcam.bean.SharedCamera;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.user.UserAccount;

import com.loopj.android.http.RequestParams;

import net.kaicong.utility.ApiClientUtility;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LingYan on 15-1-4.package com.kaicong.ipcam.device.seeworld;
 */
public class PraiseFragment extends BaseWorldViewFragment {

    @Override
    public void getData() {
        Map<String, String> map = new HashMap<>();
        map.put(CameraConstants.SORT_KEYWORD, CameraConstants.SORT_KEYWORD_PRAISE_COUNT);
        map.put(CameraConstants.PAGE_INDEX, pageIndex + "");
        map.put(CameraConstants.PAGE_SIZE, CameraConstants.NORMAL_PAGE_SIZE);
        map.put(CameraConstants.USER_ID, UserAccount.getUserID() + "");
        doPost(UrlResources.URL_WORLD_VIEW, ApiClientUtility.getParams(map), new VolleyResponse(this.getActivity(), true, getString(R.string.activity_base_progress_dialog_content)) {

            @Override
            protected void onTaskFailure() {
                super.onTaskFailure();
//                if (!isViewLoaded) {
//                    return;
//                }
                mPullRefreshListView.onRefreshComplete();
            }

            @Override
            protected void onTaskSuccess(JSONArray result) {
//                if (!isViewLoaded) {
//                    return;
//                }
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
                isDataLoaded = true;
            }

        });
    }

}

