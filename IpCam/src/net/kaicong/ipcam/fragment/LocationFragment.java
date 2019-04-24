package net.kaicong.ipcam.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import net.kaicong.ipcam.R;
import net.kaicong.ipcam.device.seeworld.BaseSeeWorldActivity;
import net.kaicong.ipcam.device.seeworld.Summary;
import net.kaicong.ipcam.utils.AMapUtil;
import net.kaicong.ipcam.utils.LogUtil;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

/**
 * Author: lu_qwen
 * Intro:
 * Time: 2015/5/4.
 */
public class LocationFragment extends Fragment implements
        GeocodeSearch.OnGeocodeSearchListener {
    private GeocodeSearch geocoderSearch;
    private AMap aMap;
    private MapView mapView;
    private LatLonPoint latLonPoint;

    private Marker geoMarker;
    private Marker regeoMarker;

    private UiSettings mUiSettings;

    View view;
    private TextView tev_address;
    RegeocodeQuery query;

    private double longitude = 0;
    private double latitude = 0;

    private boolean isDataLoaded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_location_seeworld, container, false);
        mapView = (MapView) view.findViewById(R.id.map);
        tev_address = (TextView) view.findViewById(R.id.tev_location_address);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        initView();
        return view;
    }

//    public void setSummary(Summary summary) {
//        latLonPoint = new LatLonPoint(31.248138, 121.364194);
//        //mapView.invalidate();
//        getAddress(latLonPoint);
//    }

    /**
     * 设置MapView的大小
     */
    private void initView() {
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // 屏幕宽度（像素）

        ViewGroup.LayoutParams params = mapView.getLayoutParams();
        params.width = width;
        params.height = 9 * width / 16;
        mapView.setLayoutParams(params);
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            mUiSettings = aMap.getUiSettings();
            mUiSettings.setAllGesturesEnabled(false);
            mUiSettings.setZoomControlsEnabled(false);//仅用自带缩放工具
            mUiSettings.setScrollGesturesEnabled(false);//禁用平移

            //mUiSettings.setMyLocationButtonEnabled(true);//定点图标

            geoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            regeoMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
        geocoderSearch = new GeocodeSearch(getActivity());
        geocoderSearch.setOnGeocodeSearchListener(this);
        //定位
        LogUtil.d("lat_long", BaseSeeWorldActivity.longt + "//" + BaseSeeWorldActivity.latt);
        if ((BaseSeeWorldActivity.longt >= 0 && BaseSeeWorldActivity.longt <= 180) && (BaseSeeWorldActivity.latt >= 0 && BaseSeeWorldActivity.latt <= 90)) {//被赋予过值
            latitude = BaseSeeWorldActivity.latt;
            longitude = BaseSeeWorldActivity.longt;
        } else {
//            latitude = 31.248138;
//            longitude = 121.364194;
        }
        latLonPoint = new LatLonPoint(latitude, longitude);
        getAddress(latLonPoint);

        isDataLoaded = true;

    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
//
//    /**
//     * 方法必须重写
//     */
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mapView.onDestroy();
//    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(LatLonPoint latLonPoint) {
        query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    /**
     * 名称 城市编码定位
     */
    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        if (rCode == 0) {
            if (result != null && result.getGeocodeAddressList() != null
                    && result.getGeocodeAddressList().size() > 0) {

                GeocodeAddress address = result.getGeocodeAddressList().get(0);
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        AMapUtil.convertToLatLng(address.getLatLonPoint()), 15));
                geoMarker.setPosition(AMapUtil.convertToLatLng(address
                        .getLatLonPoint()));

            }
        } else {
//            toast(getString(R.string.see_world_location_find_fail));
        }
//        } else if (rCode == 27) {
//            toast("搜索失败,请检查网络连接！");
//        } else if (rCode == 32) {
//            toast("key验证无效");
//        } else {
//            toast("未知错误");
//        }

    }

    /**
     * 坐标定位
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 0) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {
                String addressName = result.getRegeocodeAddress().getProvince()
                        + result.getRegeocodeAddress().getCity()
                        + result.getRegeocodeAddress().getDistrict();
                if (addressName.contains("中华人民共和国")) {
                    tev_address.setText("");
                } else {
                    tev_address.setText(addressName);
                }
                mapView.invalidate();
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        AMapUtil.convertToLatLng(latLonPoint), 15));
                regeoMarker.setPosition(AMapUtil.convertToLatLng(latLonPoint));
            }
        } else {
//            toast(getString(R.string.see_world_location_find_fail));
        }
    }

    private void toast(String str) {
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isDataLoaded && isVisibleToUser) {
            init();
        }
    }

}
