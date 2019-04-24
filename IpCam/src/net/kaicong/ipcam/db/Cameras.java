package net.kaicong.ipcam.db;

/**
 * Created by LingYan on 2014/11/11 0011.
 */
public class Cameras {

    private int _id;
    private int addType;//添加方式
    private int addDate;//添加日期
    private int modelId;//摄像机类型
    private String ip;//ip
    private String ddnsName;//序列号
    private String zcloudName;//智云号
    private String cameraName;
    private String account;
    private String password;
    private int channel;
    private int videoQuality;
    private int port;
    private String longitude;
    private String latitude;
    private String ddnsOrZhiyunName;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getAddType() {
        return addType;
    }

    public void setAddType(int addType) {
        this.addType = addType;
    }

    public int getAddDate() {
        return addDate;
    }

    public void setAddDate(int addDate) {
        this.addDate = addDate;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDdnsName() {
        return ddnsName;
    }

    public void setDdnsName(String ddnsName) {
        this.ddnsName = ddnsName;
    }

    public String getZcloudName() {
        return zcloudName;
    }

    public void setZcloudName(String zcloudName) {
        this.zcloudName = zcloudName;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDdnsOrZhiyunName() {
        return ddnsOrZhiyunName;
    }

    public void setDdnsOrZhiyunName(String ddnsOrZhiyunName) {
        this.ddnsOrZhiyunName = ddnsOrZhiyunName;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getVideoQuality() {
        return videoQuality;
    }

    public void setVideoQuality(int videoQuality) {
        this.videoQuality = videoQuality;
    }

}