package net.kaicong.ipcam;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.GetCameraModel;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.db.Cameras;
import net.kaicong.ipcam.device.CheckCameraOnLine;
import net.kaicong.ipcam.device.sip1303.Sip1303Camera;
import net.kaicong.ipcam.user.UserAccount;
import net.kaicong.ipcam.utils.LocationUtil;
import net.kaicong.ipcam.utils.StringUtils;

import com.tutk.IOTC.Camera;

import net.kaicong.utility.ApiClientUtility;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LingYan on 2014/9/29 0029.
 */

public class AddDevicePropertyActivity extends BaseActivity implements CheckCameraOnLine.OnCheckFinishListener {
    public static final int REQUEST_CODE_SELECT_DEVICE = 1000;

    public static final String INTENT_MODE = "mode";
    public static final String INTENT_DEV_UID = "uid";
    public static final int ADD_MODE_IP = 1;
    public static final int ADD_MODE_DDNS = 2;
    public static final int ADD_MODE_ZHIYUN = 3;
    //添加模式
    private int addMode = ADD_MODE_IP;

    private LinearLayout addNameLinearLayout;
    private LinearLayout addPropertyOneLinearLayout;
    private LinearLayout addPropertyTwoLinearLayout;
    private LinearLayout addPropertyThreeLinearLayout;
    private LinearLayout addPropertyFourLinearLayout;
    private LinearLayout addPropertyFiveLinearLayout;
    private TextView textAddName;
    private EditText editAddName;
    private TextView textAddAddPropertyOne;
    private EditText editAddAddPropertyOne;
    private TextView textAddAddPropertyTwo;
    private EditText editAddAddPropertyTwo;
    private TextView textAddAddPropertyThree;
    private EditText editAddAddPropertyThree;
    private TextView textAddAddPropertyFour;
    private EditText editAddAddPropertyFour;
    private TextView textAddAddPropertyFive;
    private EditText editAddAddPropertyFive;

    private EditText editAddAddPropertyLongitude;//经度
    private EditText editAddAddPropertyLatitude;//纬度

    private String getDevUId;//扫描二维码或者局域网搜索获取
    private String deviceModel;//设备所属型号代码

    private CheckCameraOnLine checkCameraOnLine;
    //保存
    private Cameras camera = new Cameras();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle(getString(R.string.add_device));
        showBackButton();
        showRightButton(getString(R.string.btn_save));
        setContentView(R.layout.activity_add_device_property);
        addMode = getIntent().getIntExtra(INTENT_MODE, ADD_MODE_IP);//添加方式
        getDevUId = getIntent().getStringExtra(INTENT_DEV_UID);//设备智云号
        checkCameraOnLine = new CheckCameraOnLine(this);
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_SELECT_DEVICE) {
            editAddName.setText(data.getStringExtra("device_type"));
            deviceModel = data.getStringExtra("device_model");
        }
    }

    @Override
    public void doRightButtonAction(View view) {
        super.doRightButtonAction(view);

        //ip模式添加
        if (addMode == ADD_MODE_IP) {
            //设备型号
            String cameraType = editAddName.getText().toString();
            String cameraName = editAddAddPropertyOne.getText().toString();
            String ip = editAddAddPropertyTwo.getText().toString();
            String port = editAddAddPropertyThree.getText().toString();
            String account = editAddAddPropertyFour.getText().toString();
            String password = editAddAddPropertyFive.getText().toString();
            String longitude = editAddAddPropertyLongitude.getText().toString();
            String latitude = editAddAddPropertyLatitude.getText().toString();
            if (StringUtils.isEmpty(deviceModel)) {
                makeToast(getString(R.string.add_device_select_type));
                return;
            }
            if (StringUtils.isEmpty(cameraName)) {
                makeToast(getString(R.string.add_device_edit_device_name));
                return;
            }
            if (StringUtils.isEmpty(ip)) {
                makeToast(getString(R.string.add_device_edit_ip_address));
                return;
            }
            if (StringUtils.isEmpty(port)) {
                makeToast(getString(R.string.add_device_edit_port));
                return;
            }
            if (StringUtils.isEmpty(account)) {
                makeToast(getString(R.string.add_device_edit_account));
                return;
            }
            if (StringUtils.isEmpty(password)) {
                makeToast(getString(R.string.add_device_edit_password));
                return;
            }
            camera.setAddType(ADD_MODE_IP);
            camera.setCameraName(cameraName);
//            camera.setCameraType(Integer.parseInt(cameraType));
            camera.setAccount(account);
            camera.setPassword(password);
            camera.setIp(ip);
            camera.setPort(Integer.parseInt(port));
            camera.setLongitude(longitude);
            camera.setLatitude(latitude);
        }

        //ddns模式添加
        if (addMode == ADD_MODE_DDNS) {
            String cameraName = editAddAddPropertyOne.getText().toString();
            String ddnsName = editAddAddPropertyTwo.getText().toString();
            String account = editAddAddPropertyThree.getText().toString();
            String password = editAddAddPropertyFour.getText().toString();
            String longitude = editAddAddPropertyLongitude.getText().toString();
            String latitude = editAddAddPropertyLatitude.getText().toString();
            if (StringUtils.isEmpty(cameraName)) {
                makeToast(getString(R.string.add_device_edit_device_name));
                return;
            }
            if (StringUtils.isEmpty(ddnsName)) {
                makeToast(getString(R.string.add_device_edit_ddns_name));
                return;
            }
            if (StringUtils.isEmpty(account)) {
                makeToast(getString(R.string.add_device_edit_account));
                return;
            }
            if (StringUtils.isEmpty(password)) {
                makeToast(getString(R.string.add_device_edit_password));
                return;
            }
            if (ddnsName.length() <= 10) {
                //ddns模式
                camera.setAddType(ADD_MODE_DDNS);
            } else {
                //智云模式
                camera.setAddType(ADD_MODE_ZHIYUN);
            }
            camera.setCameraName(cameraName);
            camera.setAccount(account);
            camera.setPassword(password);
            //ddns云号或者智云号
            camera.setDdnsName(ddnsName);
            camera.setLongitude(longitude);
            camera.setLatitude(latitude);
        }

        //智云的方式添加
//        if (addMode == ADD_MODE_ZHIYUN) {
//            String zhiyunNum = editAddName.getText().toString();
//            String cameraName = editAddAddPropertyOne.getText().toString();
//            String account = editAddAddPropertyTwo.getText().toString();
//            String password = editAddAddPropertyThree.getText().toString();
//            String longitude = editAddAddPropertyLongitude.getText().toString();
//            String latitude = editAddAddPropertyLatitude.getText().toString();
//            if (StringUtils.isEmpty(zhiyunNum)) {
//                makeToast(getString(R.string.add_device_edit_zhiyun_name));
//                return;
//            }
//            if (StringUtils.isEmpty(cameraName)) {
//                makeToast(getString(R.string.add_device_edit_device_name));
//                return;
//            }
//            if (StringUtils.isEmpty(account)) {
//                makeToast(getString(R.string.add_device_edit_account));
//                return;
//            }
//            if (StringUtils.isEmpty(password)) {
//                makeToast(getString(R.string.add_device_edit_password));
//                return;
//            }
//            camera.setAddType(ADD_MODE_ZHIYUN);
//            camera.setDdnsOrZhiyunName(zhiyunNum);
//            camera.setCameraName(cameraName);
//            camera.setAccount(account);
//            camera.setPassword(password);
//            camera.setLongitude(longitude);
//            camera.setLatitude(latitude);
//            camera.setChannel(0);
//            camera.setVideoQuality(3);
//        }
        addDevice();
    }

    //添加设备
    private void addDevice() {
        showProgressDialog();
        setProgressText(getString(R.string.progress_text_check));
        //验证
        if (camera.getAddType() == ADD_MODE_IP) {
            int modelId = Integer.parseInt(deviceModel);
            switch (GetCameraModel.getCameraModel(modelId)) {
                case GetCameraModel.CAMERA_MODEL_SIP1018:
                    checkCameraOnLine.checkSip1018(camera.getCameraName(), camera.getIp(),
                            String.valueOf(camera.getPort()), camera.getAccount(), camera.getPassword());
                    break;
                case GetCameraModel.CAMERA_MODEL_SIP1303:
                case GetCameraModel.CAMERA_MODEL_SIP1201:
                    Sip1303Camera sip1303Camera = new Sip1303Camera(camera.getIp(), camera.getPort(), camera.getAccount(), camera.getPassword(), 0);
                    checkCameraOnLine.checkSip1303(sip1303Camera);
                    break;
                case GetCameraModel.CAMERA_MODEL_SIP1601:
                    checkCameraOnLine.checkSip1601(camera.getIp(), camera.getPort(), camera.getAccount(), camera.getPassword());
                    break;
                case GetCameraModel.CAMERA_MODEL_SIP1211:
                    checkCameraOnLine.checkSip1211(camera.getIp(), camera.getPort(), camera.getAccount(), camera.getPassword());
                    break;
                case GetCameraModel.CAMERA_MODEL_SIP1406:
                    checkCameraOnLine.checkSip1406(camera.getIp(), camera.getPort(), camera.getAccount(), camera.getPassword());
                    break;
                case GetCameraModel.CAMERA_MODEL_SIP1120:
                    checkCameraOnLine.checkSip1120(camera.getIp(), camera.getPort(), camera.getAccount(), camera.getPassword());
                    break;
            }
        }
        //智云模式添加
        else if (camera.getAddType() == ADD_MODE_ZHIYUN) {
            checkCameraOnLine.checkZhiYun(camera.getDdnsName(), camera.getCameraName(), camera.getAccount(), camera.getPassword());
        } else if (camera.getAddType() == ADD_MODE_DDNS) {
            //ddns检测
            Map<String, String> map = new HashMap<>();
            map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
            map.put(CameraConstants.DDNS_NAME, camera.getDdnsName());
            doPost(UrlResources.URL_DDNS_CHECK, ApiClientUtility.getParams(map), new VolleyResponse(this, false, getString(R.string.com_facebook_loading)) {

                @Override
                protected void onTaskSuccess(JSONObject result) {
                    camera.setIp(result.optString("wanip"));
                    camera.setPort(result.optInt("tcpport"));
                    camera.setModelId(result.optInt("cammodel_id"));
                    switch (GetCameraModel.getCameraModel(camera.getModelId())) {
                        case GetCameraModel.CAMERA_MODEL_SIP1018:
                            checkCameraOnLine.checkSip1018(camera.getCameraName(), camera.getIp(),
                                    String.valueOf(camera.getPort()), camera.getAccount(), camera.getPassword());
                            break;
                        case GetCameraModel.CAMERA_MODEL_SIP1303:
                        case GetCameraModel.CAMERA_MODEL_SIP1201:
                            Sip1303Camera sip1303Camera = new Sip1303Camera(camera.getIp(), camera.getPort(), camera.getAccount(), camera.getPassword(), 0);
                            checkCameraOnLine.checkSip1303(sip1303Camera);
                            break;
                        case GetCameraModel.CAMERA_MODEL_SIP1601:
                            checkCameraOnLine.checkSip1601(camera.getIp(), camera.getPort(), camera.getAccount(), camera.getPassword());
                            break;
                        case GetCameraModel.CAMERA_MODEL_SIP1211:
                            checkCameraOnLine.checkSip1211(camera.getIp(), camera.getPort(), camera.getAccount(), camera.getPassword());
                            break;
                        case GetCameraModel.CAMERA_MODEL_SIP1406:
                            checkCameraOnLine.checkSip1406(camera.getIp(), camera.getPort(), camera.getAccount(), camera.getPassword());
                            break;
                        case GetCameraModel.CAMERA_MODEL_SIP1120:
                            checkCameraOnLine.checkSip1120(camera.getIp(), camera.getPort(), camera.getAccount(), camera.getPassword());
                            break;
                        default:
                            makeToast(getString(R.string.add_device_not_support_yet));
                            break;
                    }
                    removeProgressDialog();
                }

                @Override
                protected void onTaskError(int code) {
                    super.onTaskError(code);
                    removeProgressDialog();
                }
            });
        }
    }

    private void initView() {
        addNameLinearLayout = (LinearLayout) findViewById(R.id.add_name_linearlayout);
        addPropertyOneLinearLayout = (LinearLayout) findViewById(R.id.linear_add_property_one);
        addPropertyTwoLinearLayout = (LinearLayout) findViewById(R.id.linear_add_property_two);
        addPropertyThreeLinearLayout = (LinearLayout) findViewById(R.id.linear_add_property_three);
        addPropertyFourLinearLayout = (LinearLayout) findViewById(R.id.linear_add_property_four);
        addPropertyFiveLinearLayout = (LinearLayout) findViewById(R.id.linear_add_property_five);
        textAddName = (TextView) findViewById(R.id.text_add_name);
        editAddName = (EditText) findViewById(R.id.edit_add_name);
        textAddAddPropertyOne = (TextView) findViewById(R.id.text_add_property_one);
        editAddAddPropertyOne = (EditText) findViewById(R.id.edit_add_property_one);
        textAddAddPropertyTwo = (TextView) findViewById(R.id.text_add_property_two);
        editAddAddPropertyTwo = (EditText) findViewById(R.id.edit_add_property_two);
        textAddAddPropertyThree = (TextView) findViewById(R.id.text_add_property_three);
        editAddAddPropertyThree = (EditText) findViewById(R.id.edit_add_property_three);
        textAddAddPropertyFour = (TextView) findViewById(R.id.text_add_property_four);
        editAddAddPropertyFour = (EditText) findViewById(R.id.edit_add_property_four);
        textAddAddPropertyFive = (TextView) findViewById(R.id.text_add_property_five);
        editAddAddPropertyFive = (EditText) findViewById(R.id.edit_add_property_five);
        editAddAddPropertyLongitude = (EditText) findViewById(R.id.edit_add_property_longitude);
        editAddAddPropertyLatitude = (EditText) findViewById(R.id.edit_add_property_latitude);

        //获取经纬度
        setLocation();

        //ip添加模式
        if (addMode == ADD_MODE_IP) {
            editAddName.setBackgroundResource(R.drawable.home_list_selector);
            editAddName.setText(getString(R.string.add_select_type));
            Drawable rightDrawable = getResources().getDrawable(R.drawable.add_arrow_forward);
            editAddName.setCompoundDrawablesWithIntrinsicBounds(null, null, rightDrawable, null);
            editAddName.setFocusableInTouchMode(false);//不让键盘拦截
            textAddAddPropertyOne.setText(getString(R.string.add_device_cemera_name));
            editAddAddPropertyOne.setText(getString(R.string.add_my_camera));
            textAddAddPropertyTwo.setText(getString(R.string.add_ip));
            editAddAddPropertyTwo.setHint(getString(R.string.edit_host_hint));
            textAddAddPropertyThree.setText(getString(R.string.edit_port));
            editAddAddPropertyThree.setHint(getString(R.string.edit_port_hint));
            editAddAddPropertyThree.setInputType(InputType.TYPE_CLASS_NUMBER);
            textAddAddPropertyFour.setText(getString(R.string.login_user_account));
            editAddAddPropertyFour.setText(getString(R.string.edit_user_hint));
            textAddAddPropertyFive.setText(getString(R.string.edit_pass));
            editAddAddPropertyFive.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

            editAddName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //选择型号
                    Intent intent = new Intent();
                    intent.setClass(AddDevicePropertyActivity.this, SelectDeviceTypeActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_SELECT_DEVICE);
                }
            });
        }
        //ddns添加模式
        if (addMode == ADD_MODE_DDNS) {
            addNameLinearLayout.setVisibility(View.GONE);
            textAddAddPropertyOne.setText(getString(R.string.add_device_cemera_name));
            editAddAddPropertyOne.setText(getString(R.string.add_my_camera));
            textAddAddPropertyTwo.setText(getString(R.string.add_by_ddns));
            editAddAddPropertyTwo.setText(getDevUId);
            textAddAddPropertyThree.setText(getString(R.string.login_user_account));
            editAddAddPropertyThree.setText(getString(R.string.edit_user_hint));
            textAddAddPropertyFour.setText(getString(R.string.edit_pass));
            editAddAddPropertyFour.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            addPropertyFiveLinearLayout.setVisibility(View.GONE);

            editAddName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //选择型号
                    Intent intent = new Intent();
                    intent.setClass(AddDevicePropertyActivity.this, SelectDeviceTypeActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_SELECT_DEVICE);
                }
            });
        }
        //根据智云号
//        if (addMode == ADD_MODE_ZHIYUN) {
////            textAddName.setVisibility(View.GONE);
//            if (!StringUtils.isEmpty(getDevUId)) {
//                editAddName.setText(getDevUId);
//            } else {
//                editAddName.setHint(getString(R.string.add_input_zhiyun_num));
//            }
//            textAddAddPropertyOne.setText(getString(R.string.add_device_cemera_name));
//            editAddAddPropertyOne.setText(getString(R.string.add_my_camera));
//            textAddAddPropertyTwo.setText(getString(R.string.login_user_account));
//            editAddAddPropertyTwo.setText(getString(R.string.edit_user_hint));
//            textAddAddPropertyThree.setText(getString(R.string.edit_pass));
//            editAddAddPropertyThree.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//            addPropertyFourLinearLayout.setVisibility(View.GONE);
//            addPropertyFiveLinearLayout.setVisibility(View.GONE);
//        }
    }

    private void setLocation() {
        editAddAddPropertyLatitude.setText(KCApplication.Latitude);
        editAddAddPropertyLongitude.setText(KCApplication.Longitude);
    }

    @Override
    public void onCheckFinish(boolean isOnLine, int resultCode) {
        //removeProgressDialog();
        if (isOnLine) {
            setProgressText(getString(R.string.progress_text_check_device_success));
            Map<String, String> map = new HashMap<>();
            switch (camera.getAddType()) {
                case ADD_MODE_ZHIYUN:
                    map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
                    map.put(CameraConstants.DISPLAY_NAME, camera.getCameraName());
                    map.put(CameraConstants.LONGITUDE, camera.getLongitude());
                    map.put(CameraConstants.LATITUDE, camera.getLatitude());
                    map.put(CameraConstants.CAM_USER, camera.getAccount());
                    map.put(CameraConstants.CAM_PWD, camera.getPassword());
                    map.put(CameraConstants.ZCLOUD, camera.getDdnsName());
                    doPost(UrlResources.URL_BIND_DEVICE_ZHIYUN, ApiClientUtility.getParams(map), new VolleyResponse(this, false, getString(R.string.com_facebook_loading)) {

                        @Override
                        public void onTaskSuccessRoot(JSONObject result) {
                            removeProgressDialog();
                            makeToast(getString(R.string.add_device_by_zhiyun_success));
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        protected void onTaskError(int code) {
                            removeProgressDialog();
                        }
                    });
                    break;
                case ADD_MODE_DDNS:
                    map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
                    map.put(CameraConstants.DISPLAY_NAME, camera.getCameraName());
                    map.put(CameraConstants.DDNS_NAME, camera.getDdnsName());
                    map.put(CameraConstants.LONGITUDE, camera.getLongitude());
                    map.put(CameraConstants.LATITUDE, camera.getLatitude());
                    map.put(CameraConstants.CAM_USER, camera.getAccount());
                    map.put(CameraConstants.CAM_PWD, camera.getPassword());
                    map.put(CameraConstants.CAMMODEL_ID, String.valueOf(camera.getModelId()));
                    doPost(UrlResources.URL_BIND_DEVICE_DDNS, ApiClientUtility.getParams(map), new VolleyResponse(this, false, getString(R.string.com_facebook_loading)) {

                        @Override
                        public void onTaskSuccessRoot(JSONObject result) {
                            removeProgressDialog();
                            super.onTaskSuccess(result);
                            Toast.makeText(AddDevicePropertyActivity.this, getString(R.string.add_device_by_ddns_success), Toast.LENGTH_LONG).show();
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        protected void onTaskError(int code) {
                            removeProgressDialog();
                        }

                    });
                    break;
                case ADD_MODE_IP:
                    map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
                    map.put(CameraConstants.DISPLAY_NAME, camera.getCameraName());
                    map.put(CameraConstants.CAM_USER, camera.getAccount());
                    map.put(CameraConstants.CAM_PWD, camera.getPassword());
                    map.put(CameraConstants.CAM_IP, camera.getIp());
                    map.put(CameraConstants.CAM_PORT, String.valueOf(camera.getPort()));
                    map.put(CameraConstants.LONGITUDE, camera.getLongitude());
                    map.put(CameraConstants.LATITUDE, camera.getLatitude());
                    map.put(CameraConstants.CAMMODEL_ID, deviceModel);
                    doPost(UrlResources.URL_BIND_DEVICE_IP, ApiClientUtility.getParams(map), new VolleyResponse(this, false, getString(R.string.com_facebook_loading)) {

                        @Override
                        public void onTaskSuccessRoot(JSONObject result) {
                            removeProgressDialog();
                            Toast.makeText(AddDevicePropertyActivity.this, getString(R.string.add_device_by_ip_success), Toast.LENGTH_LONG).show();
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        protected void onTaskError(int code) {
                            removeProgressDialog();
                        }

                    });

                    break;
            }
        } else {
            removeProgressDialog();
            String errMsg = "";
            switch (resultCode) {
                case Camera.CONNECTION_STATE_UNKNOWN_DEVICE:
                    errMsg = getString(R.string.connstus_unknown_device);
                    break;
                case Camera.CONNECTION_STATE_TIMEOUT:
                    errMsg = getString(R.string.connstus_connection_timeout);
                    break;
                case Camera.CONNECTION_STATE_WRONG_PASSWORD:
                    errMsg = getString(R.string.connstus_wrong_password);
                    break;
                case Camera.CONNECTION_STATE_CONNECT_FAILED:
                    errMsg = getString(R.string.connstus_connection_failed);
                    break;
            }
            Toast.makeText(AddDevicePropertyActivity.this, getString(R.string.progress_text_check_device_failed, errMsg), Toast.LENGTH_LONG).show();
        }
    }
}
