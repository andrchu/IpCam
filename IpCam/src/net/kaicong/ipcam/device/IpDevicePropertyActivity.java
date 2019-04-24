package net.kaicong.ipcam.device;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.R;

import net.kaicong.ipcam.bean.DeviceCamera;
import net.kaicong.ipcam.bean.DeviceProperty;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.GetCameraModel;
import net.kaicong.ipcam.device.sip1018.Sip1018VideoActivity;
import net.kaicong.ipcam.device.sip1018.MyIpCamera;
import net.kaicong.ipcam.device.sip1120.Sip1120VideoActivity;
import net.kaicong.ipcam.device.sip1201.Sip1201VideoActivity;
import net.kaicong.ipcam.device.sip1211.Sip1211VideoActivity;
import net.kaicong.ipcam.device.sip1303.Sip1303VideoByJavaActivity;
import net.kaicong.ipcam.device.sip1406.Sip1406VideoActivity;
import net.kaicong.ipcam.device.sip1601.Sip1601VideoActivity;

/**
 * Created by LingYan on 14-12-23.
 */
public class IpDevicePropertyActivity extends BaseActivity {

    public static final int REQUEST_CODE_SHARE = 1001;
    public static final int REQUEST_CODE_CHANGE_INFO = 1002;


    //new
    private ImageView back;
    private TextView deviceName;
    private ImageView play;

    private LinearLayout lel_share;
    private LinearLayout lel_modifyName;
    private LinearLayout lel_modifyPasd;
    private LinearLayout lel_timeSetting;
    private LinearLayout lel_videoParam;
    private LinearLayout lel_soundParam;
    private LinearLayout lel_imgParam;
    private LinearLayout lel_deviceInfo;

    private DeviceProperty deviceProperty;
    //是否分享或取消分享成功
    private boolean isChangeShare = false;
    //是否修改设备信息成功
    private boolean isChangeInfo = false;
    //类型模式
    private String modelStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceProperty = (DeviceProperty) getIntent().getSerializableExtra("deviceCamera");
        hiddenBar();
        setContentView(R.layout.activity_ip_device_property);

        back = (ImageView) findViewById(R.id.img_myDevice_Detail_ip_back);
        back.setOnClickListener(this);
        deviceName = (TextView) findViewById(R.id.tev_myDevice_Detail_ip_name);
        play = (ImageView) findViewById(R.id.img_myDevice_Detail_ip_play);
        play.setOnClickListener(this);

        lel_share = (LinearLayout) findViewById(R.id.lel_myDevice_Detail_ip_share);
        lel_share.setOnClickListener(this);
        lel_modifyName = (LinearLayout) findViewById(R.id.lel_myDevice_Detail_ip_modifyName);
        lel_modifyName.setOnClickListener(this);
        lel_modifyPasd = (LinearLayout) findViewById(R.id.lel_myDevice_Detail_ip_modifyPasd);
        lel_modifyPasd.setOnClickListener(this);
        lel_timeSetting = (LinearLayout) findViewById(R.id.lel_myDevice_Detail_ip_timeSetting);
        lel_timeSetting.setOnClickListener(this);
        lel_videoParam = (LinearLayout) findViewById(R.id.lel_myDevice_Detail_ip_videoParam);
        lel_videoParam.setOnClickListener(this);
        lel_soundParam = (LinearLayout) findViewById(R.id.lel_myDevice_Detail_ip_soundParam);
        lel_soundParam.setOnClickListener(this);
        lel_imgParam = (LinearLayout) findViewById(R.id.lel_myDevice_Detail_ip_imgParam);
        lel_imgParam.setOnClickListener(this);
        lel_deviceInfo = (LinearLayout) findViewById(R.id.lel_myDevice_Detail_ip_deviceInfo);
        lel_deviceInfo.setOnClickListener(this);

        modelStr = deviceProperty.cameraType == DeviceCamera.CAM_TYPE_DDNS ? getString(R.string.device_property_ddns_mode) : getString(R.string.add_by_ip);
        deviceName.setText(deviceProperty.deviceName + "(" + modelStr + ")");
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.img_myDevice_Detail_ip_back:
                quit();
                break;
            case R.id.img_myDevice_Detail_ip_play:
                //点击播放
                switch (deviceProperty.cameraType) {
                    case DeviceCamera.CAM_TYPE_DDNS:
                        startDDNSPlay(deviceProperty);
                        break;
                    case DeviceCamera.CAM_TYPE_IP:
                        startIPPlay(deviceProperty);
                        break;
                }
                break;
            case R.id.lel_myDevice_Detail_ip_modifyName:
                //修改设备信息
                switch (GetCameraModel.getCameraModel(deviceProperty.modelId)) {
                    /**
                     * 1018ddns系列
                     */
                    case GetCameraModel.CAMERA_MODEL_SIP1018:
                        /**
                         * 1303系列
                         */
                    case GetCameraModel.CAMERA_MODEL_SIP1303:
                        /**
                         * 1601系列
                         */
                    case GetCameraModel.CAMERA_MODEL_SIP1601:
                        /**
                         * 1201系列
                         */
                    case GetCameraModel.CAMERA_MODEL_SIP1201:
                        Intent nameIntent = new Intent();
                        nameIntent.setClass(this, ChangeDeviceNameActivity.class);
                        nameIntent.putExtra("device_property", deviceProperty);
                        startActivityForResult(nameIntent, REQUEST_CODE_CHANGE_INFO);
                        break;
                    default:
                        makeToast(getString(R.string.add_device_setting_not_support_yet));
                        break;
                }

                break;
            case R.id.lel_myDevice_Detail_ip_modifyPasd:
                //修改设备信息
                switch (GetCameraModel.getCameraModel(deviceProperty.modelId)) {
                    /**
                     * 1018ddns系列
                     */
                    case GetCameraModel.CAMERA_MODEL_SIP1018:
                        /**
                         * 1303系列
                         */
                    case GetCameraModel.CAMERA_MODEL_SIP1303:
                        /**
                         * 1601系列
                         */
                    case GetCameraModel.CAMERA_MODEL_SIP1601:
                        /**
                         * 1201系列
                         */
                    case GetCameraModel.CAMERA_MODEL_SIP1201:
                        Intent deviceInfoIntent = new Intent();
                        deviceInfoIntent.setClass(this, ChangeDevicePasswordActivity.class);
                        deviceInfoIntent.putExtra("device_property", deviceProperty);
                        startActivityForResult(deviceInfoIntent, REQUEST_CODE_CHANGE_INFO);
                        break;
                    default:
                        makeToast(getString(R.string.add_device_setting_not_support_yet));
                        break;
                }

                break;
            case R.id.lel_myDevice_Detail_ip_share:
                //分享设备
                switch (GetCameraModel.getCameraModel(deviceProperty.modelId)) {
                    /**
                     * 1018ddns系列
                     */
                    case GetCameraModel.CAMERA_MODEL_SIP1018:
                        /**
                         * 1303系列
                         */
                    case GetCameraModel.CAMERA_MODEL_SIP1303:
                        /**
                         * 1601系列
                         */
                    case GetCameraModel.CAMERA_MODEL_SIP1601:
                        /**
                         * 1201系列
                         */
                    case GetCameraModel.CAMERA_MODEL_SIP1201:
                        if (deviceProperty.cameraType == DeviceCamera.CAM_TYPE_IP) {
                            //ip模式无法分享设备
                            makeToast(getString(R.string.device_property_ip_cannot_share));
                            return;
                        }
                        Intent intent = new Intent();
                        intent.setClass(this, ShareDeviceActivity.class);
                        intent.putExtra("isShareOpen", deviceProperty.isShared);
                        intent.putExtra("deviceId", deviceProperty.deviceId);
                        startActivityForResult(intent, REQUEST_CODE_SHARE);
                        break;
                    default:
                        makeToast(getString(R.string.add_device_share_not_support_yet));
                        break;
                }
                break;
            case R.id.lel_myDevice_Detail_ip_timeSetting:
                //时间设置
                break;
            case R.id.lel_myDevice_Detail_ip_videoParam:
                //视频参数
                break;
            case R.id.lel_myDevice_Detail_ip_soundParam:
                //音频参数
                break;
            case R.id.lel_myDevice_Detail_ip_imgParam:
                //图像参数
                break;
            case R.id.lel_myDevice_Detail_ip_deviceInfo:
                //设备信息
                break;
        }
    }

    /**
     * 启动DDNS播放
     *
     * @param deviceProperty
     */
    private void startDDNSPlay(DeviceProperty deviceProperty) {

        Intent intent = new Intent();

        switch (GetCameraModel.getCameraModel(deviceProperty.modelId)) {
            /**
             * 1018ddns系列
             */
            case GetCameraModel.CAMERA_MODEL_SIP1018:
                MyIpCamera myIpCamera = new MyIpCamera();
                myIpCamera.setCameraName(deviceProperty.deviceName);
                myIpCamera.setCameraId("");
                myIpCamera.setCameraHost(deviceProperty.wanIp);
                myIpCamera.setCameraPort(deviceProperty.wanPort + "");
                myIpCamera.setUser(deviceProperty.account);
                myIpCamera.setPass(deviceProperty.password);
                myIpCamera.setDeviceId(deviceProperty.deviceId);
                intent.setClass(this, Sip1018VideoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(CameraConstants.CAMERA, myIpCamera);
                intent.putExtras(bundle);
                intent.putExtra("isIPDevice", false);
                intent.putExtra("isShareOpen", deviceProperty.isShared);
                startActivity(intent);
                break;

            /**
             * 1303系列
             */
            case GetCameraModel.CAMERA_MODEL_SIP1303:
                intent.setClass(this, Sip1303VideoByJavaActivity.class);
                intent.putExtra("mIp", deviceProperty.wanIp);
                intent.putExtra("mPort", deviceProperty.wanPort);
                intent.putExtra("mAccount", deviceProperty.account);
                intent.putExtra("mPassword", deviceProperty.password);
                intent.putExtra("mDeviceId", deviceProperty.deviceId);
                intent.putExtra("isIPDevice", false);
                intent.putExtra("isShareOpen", deviceProperty.isShared);
                startActivity(intent);
                break;

            /**
             * 1601系列
             */
            case GetCameraModel.CAMERA_MODEL_SIP1601:
                intent.setClass(this, Sip1601VideoActivity.class);
                intent.putExtra("ip", deviceProperty.wanIp);
                intent.putExtra("port", deviceProperty.wanPort);
                intent.putExtra("account", deviceProperty.account);
                intent.putExtra("password", deviceProperty.password);
                intent.putExtra("mDeviceId", deviceProperty.deviceId);
                intent.putExtra("isIPDevice", false);
                intent.putExtra("isShareOpen", deviceProperty.isShared);
                startActivity(intent);
                break;

            /**
             * 1201系列
             */
            case GetCameraModel.CAMERA_MODEL_SIP1201:
                intent.setClass(this, Sip1201VideoActivity.class);
                intent.putExtra("ip", deviceProperty.wanIp);
                intent.putExtra("port", deviceProperty.wanPort);
                intent.putExtra("account", deviceProperty.account);
                intent.putExtra("password", deviceProperty.password);
                intent.putExtra("cameraModel", deviceProperty.modelId);
                intent.putExtra("mDeviceId", deviceProperty.deviceId);
                intent.putExtra("isIPDevice", false);
                intent.putExtra("isShareOpen", deviceProperty.isShared);
                startActivity(intent);
                break;

            /**
             * 1211系列
             */
            case GetCameraModel.CAMERA_MODEL_SIP1211:
                intent.setClass(this, Sip1211VideoActivity.class);
                intent.putExtra("ip", deviceProperty.wanIp);
                intent.putExtra("port", deviceProperty.wanPort);
                intent.putExtra("account", deviceProperty.account);
                intent.putExtra("password", deviceProperty.password);
                intent.putExtra("cameraModel", deviceProperty.modelId);
                intent.putExtra("mDeviceId", deviceProperty.deviceId);
                intent.putExtra("isIPDevice", false);
                intent.putExtra("isShareOpen", deviceProperty.isShared);
                startActivity(intent);
                break;

            /**
             * 1406系列
             */
            case GetCameraModel.CAMERA_MODEL_SIP1406:
                intent.setClass(this, Sip1406VideoActivity.class);
                intent.putExtra("mIp", deviceProperty.wanIp);
                intent.putExtra("mPort", deviceProperty.wanPort);
                intent.putExtra("mAccount", deviceProperty.account);
                intent.putExtra("mPassword", deviceProperty.password);
                intent.putExtra("mDeviceId", deviceProperty.deviceId);
                intent.putExtra("isIPDevice", false);
                intent.putExtra("isShareOpen", deviceProperty.isShared);
                startActivity(intent);
                break;

            /**
             * 1120系列
             */
            case GetCameraModel.CAMERA_MODEL_SIP1120:
                intent.setClass(this, Sip1120VideoActivity.class);
                intent.putExtra("mIp", deviceProperty.lanIp);
                intent.putExtra("mPort", deviceProperty.lanPort);
                intent.putExtra("mAccount", deviceProperty.account);
                intent.putExtra("mPassword", deviceProperty.password);
                intent.putExtra("mDeviceId", deviceProperty.deviceId);
                intent.putExtra("isIPDevice", false);
                intent.putExtra("isShareOpen", deviceProperty.isShared);
                startActivity(intent);
                break;

            default:
                makeToast(getString(R.string.add_device_not_support_yet));
                break;
        }
    }

    /**
     * 启动IP播放
     *
     * @param deviceProperty
     */
    private void startIPPlay(DeviceProperty deviceProperty) {

        Intent intent = new Intent();
        switch (GetCameraModel.getCameraModel(deviceProperty.modelId)) {

            /**
             * 1018 ip系列
             */
            case GetCameraModel.CAMERA_MODEL_SIP1018:
                MyIpCamera myIpCamera = new MyIpCamera();
                myIpCamera.setCameraName(deviceProperty.deviceName);
                myIpCamera.setCameraId("");
                myIpCamera.setCameraHost(deviceProperty.lanIp);
                myIpCamera.setCameraPort(deviceProperty.lanPort + "");
                myIpCamera.setUser(deviceProperty.account);
                myIpCamera.setPass(deviceProperty.password);
                myIpCamera.setDeviceId(deviceProperty.deviceId);
                intent.setClass(this, Sip1018VideoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(CameraConstants.CAMERA, myIpCamera);
                intent.putExtras(bundle);
                intent.putExtra("isIPDevice", true);
                intent.putExtra("isShareOpen", deviceProperty.isShared);
                startActivity(intent);
                break;

            /**
             * 1303系列
             */
            case GetCameraModel.CAMERA_MODEL_SIP1303:
                intent.setClass(this, Sip1303VideoByJavaActivity.class);
                intent.putExtra("mIp", deviceProperty.lanIp);
                intent.putExtra("mPort", deviceProperty.lanPort);
                intent.putExtra("mAccount", deviceProperty.account);
                intent.putExtra("mPassword", deviceProperty.password);
                intent.putExtra("mDeviceId", deviceProperty.deviceId);
                intent.putExtra("isIPDevice", true);
                intent.putExtra("isShareOpen", deviceProperty.isShared);
                startActivity(intent);
                break;

            /**
             * 1601系列
             */
            case GetCameraModel.CAMERA_MODEL_SIP1601:
                intent.setClass(this, Sip1601VideoActivity.class);
                intent.putExtra("ip", deviceProperty.lanIp);
                intent.putExtra("port", deviceProperty.lanPort);
                intent.putExtra("account", deviceProperty.account);
                intent.putExtra("password", deviceProperty.password);
                intent.putExtra("mDeviceId", deviceProperty.deviceId);
                intent.putExtra("isIPDevice", true);
                intent.putExtra("isShareOpen", deviceProperty.isShared);
                startActivity(intent);
                break;

            /**
             * 1201系列
             */
            case GetCameraModel.CAMERA_MODEL_SIP1201:
                intent.setClass(this, Sip1201VideoActivity.class);
                intent.putExtra("ip", deviceProperty.lanIp);
                intent.putExtra("port", deviceProperty.lanPort);
                intent.putExtra("account", deviceProperty.account);
                intent.putExtra("password", deviceProperty.password);
                intent.putExtra("cameraModel", deviceProperty.modelId);
                intent.putExtra("mDeviceId", deviceProperty.deviceId);
                intent.putExtra("isIPDevice", true);
                intent.putExtra("isShareOpen", deviceProperty.isShared);
                startActivity(intent);
                break;

            /**
             * 1211系列
             */
            case GetCameraModel.CAMERA_MODEL_SIP1211:
                intent.setClass(this, Sip1211VideoActivity.class);
                intent.putExtra("ip", deviceProperty.lanIp);
                intent.putExtra("port", deviceProperty.lanPort);
                intent.putExtra("account", deviceProperty.account);
                intent.putExtra("password", deviceProperty.password);
                intent.putExtra("cameraModel", deviceProperty.modelId);
                intent.putExtra("mDeviceId", deviceProperty.deviceId);
                intent.putExtra("isIPDevice", true);
                intent.putExtra("isShareOpen", deviceProperty.isShared);
                startActivity(intent);
                break;

            /**
             * 1406系列
             */
            case GetCameraModel.CAMERA_MODEL_SIP1406:
                intent.setClass(this, Sip1406VideoActivity.class);
                intent.putExtra("mIp", deviceProperty.lanIp);
                intent.putExtra("mPort", deviceProperty.lanPort);
                intent.putExtra("mAccount", deviceProperty.account);
                intent.putExtra("mPassword", deviceProperty.password);
                intent.putExtra("mDeviceId", deviceProperty.deviceId);
                intent.putExtra("isIPDevice", true);
                intent.putExtra("isShareOpen", deviceProperty.isShared);
                startActivity(intent);
                break;

            /**
             * 1120系列
             */
            case GetCameraModel.CAMERA_MODEL_SIP1120:
                intent.setClass(this, Sip1120VideoActivity.class);
                intent.putExtra("mIp", deviceProperty.lanIp);
                intent.putExtra("mPort", deviceProperty.lanPort);
                intent.putExtra("mAccount", deviceProperty.account);
                intent.putExtra("mPassword", deviceProperty.password);
                intent.putExtra("mDeviceId", deviceProperty.deviceId);
                intent.putExtra("isIPDevice", true);
                intent.putExtra("isShareOpen", deviceProperty.isShared);
                startActivity(intent);
                break;

            default:
                makeToast(getString(R.string.add_device_not_support_yet));
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_SHARE) {
            isChangeShare = true;
        }
        if (requestCode == REQUEST_CODE_CHANGE_INFO) {
            isChangeInfo = true;
            //当 修改名字和密码的时候要注意刷新页面 和property参数  假刷新
            if (null != data.getStringExtra("name")) {
                deviceProperty.deviceName = data.getStringExtra("name");
                deviceName.setText(data.getStringExtra("name") + "(" + modelStr + ")");
            }
            if (null != data.getStringExtra("passward")) {
                deviceProperty.password = data.getStringExtra("passward");
            }

        }
    }

    private void quit() {
        Intent data = new Intent();
        if (isChangeShare || isChangeInfo) {
            data.putExtra("pay_success", 100);
        }
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            quit();
        }
        return true;
    }

}
