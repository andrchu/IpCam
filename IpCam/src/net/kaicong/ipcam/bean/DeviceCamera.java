package net.kaicong.ipcam.bean;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by LingYan on 2014/10/27 0027.
 */
public class DeviceCamera extends ErrorResponse implements Comparator<DeviceCamera> {

    public static final int CAM_TYPE_DDNS = 1;
    public static final int CAM_TYPE_IP = 2;
    public static final int CAM_TYPE_ZHIYUN = 3;

    public int id;
    public int userId;
    public String displayName;
    public String ddnsName;
    public int cameraModelId;
    public String cameraIp;
    public int cameraPort;
    public String cameraUser;
    public String cameraPassword;
    public double longitude;
    public double latitude;
    public String createTime;
    public String updateTime;
    public String bindIp;
    public String bindTime;
    public String lastVisitTime;
    public int visitCount;
    public int cameraType;
    public int isCameraAdmin;
    public int isDeleted;
    public String lanIp;
    public String wanIp;
    public int tcpPort;
    public int modelId;
    public int isShared;
    public String zCloud;
    public String overDueDate;
    public float progress;//智云过期比例（超过一年100%，小于一年=时间差/一年时间）
    public String progressText;
    public List<DeviceCamera> data;

    public Bitmap bitmap = null;

    public static DeviceCamera getDeviceCamera(JSONArray array) {
        DeviceCamera deviceCamera = new DeviceCamera();
        deviceCamera.data = new ArrayList<>();
        try {

            for (int i = 0; i < array.length(); i++) {
                DeviceCamera jsonCamera = new DeviceCamera();
                JSONObject camera = array.getJSONObject(i);
                jsonCamera.id = camera.optInt("Id");
                jsonCamera.userId = camera.optInt("UserId");
                jsonCamera.displayName = camera.optString("DisplayName");
                jsonCamera.ddnsName = camera.optString("DdnsName");
                jsonCamera.cameraModelId = camera.optInt("CamModelId");
                jsonCamera.cameraIp = camera.optString("CamIp");
                jsonCamera.cameraPort = camera.optInt("CamPort");
                jsonCamera.cameraUser = camera.optString("CamUser");
                jsonCamera.cameraPassword = camera.optString("CamPwd");
                jsonCamera.longitude = camera.optDouble("Longitude");
                jsonCamera.latitude = camera.optDouble("Latitude");
                jsonCamera.createTime = camera.optString("CreateTime");
                jsonCamera.updateTime = camera.optString("UpdateTime");
                jsonCamera.bindIp = camera.optString("BindIp");
                jsonCamera.bindTime = camera.optString("BindTime");
                jsonCamera.lastVisitTime = camera.optString("LastVisitTime");
                jsonCamera.visitCount = camera.optInt("VisitCount");
                jsonCamera.cameraType = camera.optInt("CamType");
                jsonCamera.isCameraAdmin = camera.optInt("IsCamAdmin");
                jsonCamera.isDeleted = camera.optInt("IsDeleted");
                jsonCamera.lanIp = camera.optString("LanIp");
                jsonCamera.wanIp = camera.optString("WanIp");
                jsonCamera.tcpPort = camera.optInt("TcpPort");
                jsonCamera.modelId = camera.optInt("ModelId");
                jsonCamera.isShared = camera.optInt("IsShared");
                jsonCamera.zCloud = camera.optString("zcloud");
                jsonCamera.overDueDate = camera.optString("Overdue_date");
                deviceCamera.data.add(jsonCamera);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceCamera;
    }

    /**
     * 按照拼音排序
     *
     * @param deviceCamera
     * @param deviceCamera2
     * @return
     */
    @Override
    public int compare(DeviceCamera deviceCamera, DeviceCamera deviceCamera2) {
        Collator collator = Collator.getInstance(java.util.Locale.CHINA);
        return collator.compare(deviceCamera.displayName, deviceCamera2.displayName);
    }
}
