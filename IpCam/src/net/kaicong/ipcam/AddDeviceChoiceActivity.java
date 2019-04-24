package net.kaicong.ipcam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.chu.android.CaptureActivity;

import net.kaicong.ipcam.device.zhiyun.SearchUIDWifiActivity;

import java.util.ArrayList;

/**
 * Created by LingYan on 2014/9/29 0029.
 */
public class AddDeviceChoiceActivity extends BaseActivity {
    public static final int REQUEST_CODE_SCAN_QR_CODE = 1000;
    public static final int REQUEST_CODE_ADD_DEVICE_SUCCESS = REQUEST_CODE_SCAN_QR_CODE + 1;
    public static final int REQUEST_CODE_SEARCH = REQUEST_CODE_ADD_DEVICE_SUCCESS + 1;

    private LinearLayout addByIp;
    private LinearLayout addByDDNS;
//    private LinearLayout addByZhiyun;
    private LinearLayout addBySearch;
    private LinearLayout addByScan;

    private ArrayList<String> uidList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle(getString(R.string.add_device));
        showBackButton();
        setContentView(R.layout.activity_add_device_choice);
        uidList = getIntent().getStringArrayListExtra("uidList");
        initView();
    }

    private void initView() {
        addByIp = (LinearLayout) findViewById(R.id.text_by_ip);
        addByIp.setOnClickListener(this);
        addByDDNS = (LinearLayout) findViewById(R.id.text_by_ddns);
        addByDDNS.setOnClickListener(this);
//        addByZhiyun = (LinearLayout) findViewById(R.id.text_by_zhiyun);
//        addByZhiyun.setOnClickListener(this);
        addBySearch = (LinearLayout) findViewById(R.id.text_by_search);
        addBySearch.setOnClickListener(this);
        addByScan = (LinearLayout) findViewById(R.id.text_by_scan);
        addByScan.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_SCAN_QR_CODE || requestCode == REQUEST_CODE_SEARCH) {
            //二维码扫描
            Intent intent = new Intent();
            intent.setClass(this, AddDevicePropertyActivity.class);
            intent.putExtra(AddDevicePropertyActivity.INTENT_MODE, AddDevicePropertyActivity.ADD_MODE_DDNS);
            intent.putExtra(AddDevicePropertyActivity.INTENT_DEV_UID, data.getStringExtra("dev_uid"));
            startActivityForResult(intent, REQUEST_CODE_ADD_DEVICE_SUCCESS);
        }
        if (requestCode == REQUEST_CODE_ADD_DEVICE_SUCCESS) {
            //添加设备成功
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.text_by_ip:
                intent.setClass(AddDeviceChoiceActivity.this, AddDevicePropertyActivity.class);
                intent.putExtra(AddDevicePropertyActivity.INTENT_MODE, AddDevicePropertyActivity.ADD_MODE_IP);
                startActivityForResult(intent, REQUEST_CODE_ADD_DEVICE_SUCCESS);
                break;
            case R.id.text_by_ddns:
                intent.setClass(AddDeviceChoiceActivity.this, AddDevicePropertyActivity.class);
                intent.putExtra(AddDevicePropertyActivity.INTENT_MODE, AddDevicePropertyActivity.ADD_MODE_DDNS);
                startActivityForResult(intent, REQUEST_CODE_ADD_DEVICE_SUCCESS);
                break;
//            case R.id.text_by_zhiyun:
//                intent.setClass(AddDeviceChoiceActivity.this, AddDevicePropertyActivity.class);
//                intent.putExtra(AddDevicePropertyActivity.INTENT_MODE, AddDevicePropertyActivity.ADD_MODE_ZHIYUN);
//                startActivityForResult(intent, REQUEST_CODE_ADD_DEVICE_SUCCESS);
//                break;
            case R.id.text_by_search:
                intent.setClass(AddDeviceChoiceActivity.this, SearchUIDWifiActivity.class);
                intent.putStringArrayListExtra("uidList", uidList);
                startActivityForResult(intent, REQUEST_CODE_SEARCH);
                break;
            case R.id.text_by_scan:
                intent.setClass(AddDeviceChoiceActivity.this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN_QR_CODE);
                break;
        }
    }

}
