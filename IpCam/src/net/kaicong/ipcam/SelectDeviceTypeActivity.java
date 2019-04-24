package net.kaicong.ipcam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by LingYan on 2014/9/30 0030.
 */
public class SelectDeviceTypeActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private List<HashMap<String, Object>> data = new ArrayList<>();
    private String[] devices_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_device_type);
        initTitle(getString(R.string.add_select_type));
        showBackButton();
        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(this);

        String[] devices = getResources().getStringArray(R.array.devices);
        //设备列表模式
        devices_mode = getResources().getStringArray(R.array.devices_mode);
        int[] devicesIcon = new int[]{

//                <!--B系列1018-->
                R.drawable.sip428,
                R.drawable.sip1017,
                R.drawable.sip1018,
                R.drawable.sip10180000,
                R.drawable.sip10180011,
                R.drawable.sip1019,
                R.drawable.sip1020,
                R.drawable.sip1021,
                R.drawable.sip1022,
                R.drawable.sip1018,
                R.drawable.sip1018,

//                <!--M系列1201-->
                R.drawable.sip1201,
                R.drawable.sip1201,
                R.drawable.sip12020000,
                R.drawable.sip1203,
                R.drawable.sip1204,
                R.drawable.sip1205,
                R.drawable.sip1206,
                R.drawable.sip1207,
                R.drawable.sip1210,
                R.drawable.sip1213,
                R.drawable.sip1214,
                R.drawable.sip1215,
                R.drawable.sip1201,
                R.drawable.sip1212,

//                <!--1211系列-->
                R.drawable.sip1211,

//                <!--F系列1406-->
                R.drawable.sip1306,
                R.drawable.sip13060000,
                R.drawable.sip1406,
                R.drawable.sip1306,
                R.drawable.sip1306,

//                <!--H系列1120-->
                R.drawable.sip264,
                R.drawable.sip1113,
                R.drawable.sip1118,
                R.drawable.sip1119,
                R.drawable.sip1120,
                R.drawable.sip1121,
                R.drawable.sip1128,
                R.drawable.sip1308,
                R.drawable.sip1120,

//                <!--新增的1601系列-->
                R.drawable.sip1601,
                R.drawable.sip1602,
                R.drawable.sip1603,
                R.drawable.sip1604,
                R.drawable.sip1605,
                R.drawable.sip1606,
                R.drawable.sip1606,  //Sip1606w
                R.drawable.sip1601,

//                <!--新增的1303系列-->
                R.drawable.sip1303,
                R.drawable.sip1305,

        }; //Sip913
        for (int i = 0; i < devices.length; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("device_name", devices[i]);
            map.put("device_icon", devicesIcon[i]);
            data.add(map);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.item_select_devices,
                new String[]{"device_icon", "device_name"}, new int[]{R.id.imageview, R.id.device_type});
        listView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String deviceType = (String) data.get(i).get("device_name");
        Intent data = new Intent();
        data.putExtra("device_type", deviceType);
        data.putExtra("device_model", devices_mode[i]);
        setResult(RESULT_OK, data);
        finish();
    }

}
