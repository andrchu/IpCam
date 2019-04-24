package net.kaicong.ipcam.device;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.R;
import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.DeviceCamera;
import net.kaicong.ipcam.bean.DeviceProperty;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.user.UserAccount;
import net.kaicong.ipcam.utils.StringUtils;
import net.kaicong.utility.ApiClientUtility;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: lu_qwen
 * Intro:  修改名称
 * Time: 2015/7/14.
 */
public class ChangeDeviceNameActivity extends BaseActivity {

    private EditText name;
    private Button sure;

    private DeviceProperty deviceProperty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_device_name);
        initTitle(getString(R.string.device_property_device_modifyName));
        deviceProperty = (DeviceProperty) getIntent().getSerializableExtra("device_property");
        showBackButton();

        name = (EditText) findViewById(R.id.edt_deviceName);

        sure = (Button) findViewById(R.id.btn_sure_modifyName);
        sure.setOnClickListener(this);

        if(deviceProperty !=null) {
            name.setText(deviceProperty.deviceName);
            name.setSelection(deviceProperty.deviceName.length());
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_sure_modifyName:
                Map<String, String> map = new HashMap<>();
                String urlStr = "";
                if (StringUtils.isEmpty(name.getText().toString())) {
                    makeToast(getString(R.string.common_input_not_empty));
                    return;
                }
                switch (deviceProperty.cameraType) {
                    case DeviceCamera.CAM_TYPE_DDNS:
                        map.put(CameraConstants.DEVICE_ID, String.valueOf(deviceProperty.deviceId));
                        map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
                        map.put("display_name", name.getText().toString().trim());
                        map.put("cam_user", deviceProperty.account);
                        map.put("cam_pwd", deviceProperty.password);
                        map.put("cam_ip", deviceProperty.wanIp);
                        map.put("cam_port", String.valueOf(deviceProperty.wanPort));
                        urlStr = UrlResources.URL_UPDATE_DDNS_DEVICE;
                        break;
                    case DeviceCamera.CAM_TYPE_IP:
                        map.put(CameraConstants.DEVICE_ID, String.valueOf(deviceProperty.deviceId));
                        map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
                        map.put("display_name", name.getText().toString().trim());
                        map.put("cam_user", deviceProperty.account);
                        map.put("cam_pwd", deviceProperty.password);
                        map.put("cam_ip", deviceProperty.lanIp);
                        map.put("cam_port", String.valueOf(deviceProperty.lanPort));
                        urlStr = UrlResources.URL_UPDATE_DDNS_DEVICE;
                        break;
                    case DeviceCamera.CAM_TYPE_ZHIYUN:
                        map.put(CameraConstants.DEVICE_ID, String.valueOf(deviceProperty.deviceId));
                        map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
                        map.put("display_name", name.getText().toString().trim());
                        map.put("cam_user", deviceProperty.account);
                        map.put("cam_pwd", deviceProperty.password);
                        urlStr = UrlResources.URL_UPDATE_ZHIYUN_DEVICE;
                        break;
                }
                doPost(urlStr, (ApiClientUtility.getParams(map)), new VolleyResponse(this, true, "Loading...") {

                    @Override
                    protected void onTaskSuccessRoot(JSONObject result) {
                        super.onTaskSuccessRoot(result);
                        makeToast(getString(R.string.device_property_change_info_success));
                        Intent intent = new Intent();
                        intent.putExtra("name",name.getText().toString().trim());
                        ChangeDeviceNameActivity.this.setResult(RESULT_OK,intent);
                        ChangeDeviceNameActivity.this.finish();
                    }

                });

                break;
        }
    }
}
