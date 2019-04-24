package net.kaicong.ipcam.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lingyan on 14-12-22.
 */
public class MyCollect {

    public int id;
    public int productModelId;
    public String deviceName;
    public String ip;
    public int port;
    public String account;
    public String password;
    public String zCloudNum;
    public String deviceIcon;
    public String favoriteTime;
    public List<MyCollect> data;

    public static MyCollect getMyCollectData(JSONArray array) {
        MyCollect myCollect = new MyCollect();
        try {
            myCollect.data = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                MyCollect resultCollect = new MyCollect();
                resultCollect.id = jsonObject.optInt("id");
                resultCollect.deviceIcon = jsonObject.optString("last_snapshot");
                resultCollect.deviceName = jsonObject.optString("share_title");
                resultCollect.productModelId = jsonObject.optInt("ddns_modelid");
                resultCollect.zCloudNum = jsonObject.optString("zcloud");
                resultCollect.ip = jsonObject.optString("ddns_wanip");
                resultCollect.port = jsonObject.optInt("ddns_tcpport");
                resultCollect.favoriteTime = jsonObject.optString("favorite_time");
                resultCollect.account = jsonObject.optString("account");
                resultCollect.password = jsonObject.optString("password");
                myCollect.data.add(resultCollect);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return myCollect;
    }

}
