package net.kaicong.ipcam.device;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.R;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;

import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.DeviceCamera;
import net.kaicong.ipcam.bean.DeviceProperty;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.device.zhiyun.MyCamera;
import net.kaicong.ipcam.user.UserAccount;

import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.ipcam.utils.StringUtils;
import net.kaicong.utility.ApiClientUtility;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 修改设备密码
 * Created by LingYan on 14-12-24.
 */
public class ChangeDevicePasswordActivity extends BaseActivity implements IRegisterIOTCListener {

    private EditText editOldPassword;
    private EditText editNewPassword;

    private TextView tev_name;
    private Button btn_sure;

    private DeviceProperty deviceProperty;
    private MyCamera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_device_pwd);
        initTitle(getString(R.string.about_more_change_pwd));
        deviceProperty = (DeviceProperty) getIntent().getSerializableExtra("device_property");
        showBackButton();
        //showRightButton(getString(R.string.btn_save));
        editOldPassword = (EditText) findViewById(R.id.edit_old_password);
        editNewPassword = (EditText) findViewById(R.id.edit_new_password);

        tev_name = (TextView) findViewById(R.id.tev_deviceName);
        tev_name.setText(deviceProperty.deviceName);
        btn_sure = (Button) findViewById(R.id.btn_sure_modifyPasd);
        btn_sure.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_sure_modifyPasd:
                Map<String, String> map = new HashMap<>();
                String urlStr = "";
                if (StringUtils.isEmpty(editOldPassword.getText().toString()) || StringUtils.isEmpty(editNewPassword.getText().toString())) {
                    makeToast(getString(R.string.common_input_not_empty));
                    return;
                }
                switch (deviceProperty.cameraType) {
                    case DeviceCamera.CAM_TYPE_DDNS:
                        map.put(CameraConstants.DEVICE_ID, String.valueOf(deviceProperty.deviceId));
                        map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
                        map.put("display_name", deviceProperty.deviceName);
                        map.put("cam_user", deviceProperty.account);
                        map.put("cam_pwd", editNewPassword.getText().toString());
                        map.put("cam_ip", deviceProperty.wanIp);
                        map.put("cam_port", String.valueOf(deviceProperty.wanPort));
                        urlStr = UrlResources.URL_UPDATE_DDNS_DEVICE;
                        doPost(urlStr, (ApiClientUtility.getParams(map)), new VolleyResponse(this, true, "Loading...") {

                            @Override
                            protected void onTaskSuccessRoot(JSONObject result) {
                                super.onTaskSuccessRoot(result);
                                makeToast(getString(R.string.device_property_change_info_success));
                                Intent intent = new Intent();
                                intent.putExtra("passward", editNewPassword.getText().toString());
                                ChangeDevicePasswordActivity.this.setResult(RESULT_OK, intent);
                                ChangeDevicePasswordActivity.this.finish();
                            }

                        });
                        break;
                    case DeviceCamera.CAM_TYPE_IP:
                        map.put(CameraConstants.DEVICE_ID, String.valueOf(deviceProperty.deviceId));
                        map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
                        map.put("display_name", deviceProperty.deviceName);
                        map.put("cam_user", deviceProperty.account);
                        map.put("cam_pwd", editNewPassword.getText().toString());
                        map.put("cam_ip", deviceProperty.lanIp);
                        map.put("cam_port", String.valueOf(deviceProperty.lanPort));
                        urlStr = UrlResources.URL_UPDATE_DDNS_DEVICE;
                        doPost(urlStr, (ApiClientUtility.getParams(map)), new VolleyResponse(this, true, "Loading...") {

                            @Override
                            protected void onTaskSuccessRoot(JSONObject result) {
                                super.onTaskSuccessRoot(result);
                                makeToast(getString(R.string.device_property_change_info_success));
                                Intent intent = new Intent();
                                intent.putExtra("passward", editNewPassword.getText().toString());
                                ChangeDevicePasswordActivity.this.setResult(RESULT_OK, intent);
                                ChangeDevicePasswordActivity.this.finish();
                            }

                        });
                        break;
                    case DeviceCamera.CAM_TYPE_ZHIYUN:
                        changeZhiYunPassword(editOldPassword.getText().toString(), editNewPassword.getText().toString());
                        break;
                }
        }
    }

    private void changeZhiYunPassword(String oldPwd, String newPwd) {
        showProgressDialog();
        mCamera = new MyCamera(deviceProperty.deviceName, deviceProperty.zCloudName, deviceProperty.account, deviceProperty.password);
        mCamera.registerIOTCListener(this);
        mCamera.connect(deviceProperty.zCloudName);
        mCamera.start(MyCamera.DEFAULT_AV_CHANNEL, deviceProperty.account, deviceProperty.password);
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETPASSWORD_REQ, AVIOCTRLDEFs.SMsgAVIoctrlSetPasswdReq.parseContent(oldPwd, newPwd));
    }

    @Override
    public void receiveFrameData(Camera camera, int avChannel, Bitmap bmp) {

    }

    @Override
    public void receiveFrameInfo(Camera camera, int avChannel, long bitRate, int frameRate, int onlineNm, int frameCount, int incompleteFrameCount) {

    }

    @Override
    public void receiveSessionInfo(Camera camera, int resultCode) {

    }

    @Override
    public void receiveChannelInfo(Camera camera, int avChannel, int resultCode) {

    }

    @Override
    public void receiveIOCtrlData(Camera camera, int avChannel, int avIOCtrlMsgType, byte[] data) {

        Bundle bundle = new Bundle();
        bundle.putInt("sessionChannel", avChannel);
        bundle.putByteArray("data", data);

        Message msg = new Message();
        msg.what = avIOCtrlMsgType;
        msg.setData(bundle);
        handler.sendMessage(msg);
        LogUtil.d("chu", "---修改数据---" + data[0]);

    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");

            switch (msg.what) {
                case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETPASSWORD_RESP:
                    if (data[0] == 0x00) {
//                        makeToast(getText(R.string.tips_modify_security_code_ok).toString());
                        Map<String, String> map = new HashMap<>();
                        map.put(CameraConstants.DEVICE_ID, String.valueOf(deviceProperty.deviceId));
                        map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
                        map.put("display_name", deviceProperty.deviceName);
                        map.put("cam_user", deviceProperty.account);
                        map.put("cam_pwd", editNewPassword.getText().toString());
                        doPost(UrlResources.URL_UPDATE_ZHIYUN_DEVICE, (ApiClientUtility.getParams(map)), new VolleyResponse(ChangeDevicePasswordActivity.this, false, "Loading...") {

                            @Override
                            protected void onTaskSuccessRoot(JSONObject result) {
                                super.onTaskSuccessRoot(result);
                                makeToast(getString(R.string.device_property_change_info_success));
                                Intent intent = new Intent();
                                intent.putExtra("passward", editNewPassword.getText().toString());
                                ChangeDevicePasswordActivity.this.setResult(RESULT_OK,intent);
                                ChangeDevicePasswordActivity.this.finish();
                                closeDevice();
                            }

                        });
                    }
                    removeProgressDialog();
                    break;
                default:
                    removeProgressDialog();
                    break;
            }
        }
    };

    @Override
    public void doBackButtonAction() {
        super.doBackButtonAction();
        closeDevice();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeDevice();
            finish();
        }
        return true;
    }

    //关闭设备
    private void closeDevice() {
        if (mCamera != null) {
            mCamera.unregisterIOTCListener(this);
            mCamera.disconnect();
            mCamera = null;
        }
    }


}
