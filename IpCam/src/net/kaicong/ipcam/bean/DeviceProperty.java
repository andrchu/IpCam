package net.kaicong.ipcam.bean;

import java.io.Serializable;

/**
 * Created by LingYan on 14-12-23.
 */
public class DeviceProperty implements Serializable {

    public int cameraType;
    public int deviceId;
    public int listPosition;
    public int modelId;
    public String zCloudName;
    public String lanIp;
    public String wanIp;
    public int lanPort;
    public int wanPort;
    public String account;
    public String password;
    public String deviceName;
    public boolean isShared;
    public String ddnsName;
    public String overDueDate;
    public float progress;
    public String progressText;
    public boolean isCamAdmin;

}
