
package net.kaicong.ipcam.db;

/**
 * Created by LingYan on 2014/9/29 0029.
 */
public class VideoPropertyBean {

    private int _id;
    private int addType;//添加方式
    private int addDate;//添加日期
    private int cameraType;//摄像机类型
    private String ip;//ip
    private String ddnsName;//序列号
    private String zcloudName;//智云号

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

    public int getCameraType() {
        return cameraType;
    }

    public void setCameraType(int cameraType) {
        this.cameraType = cameraType;
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

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public int getSaturation() {
        return saturation;
    }

    public void setSaturation(int saturation) {
        this.saturation = saturation;
    }

    public int getVideoFlip() {
        return videoFlip;
    }

    public void setVideoFlip(int videoFlip) {
        this.videoFlip = videoFlip;
    }

    public int getVideoEnvironment() {
        return videoEnvironment;
    }

    public void setVideoEnvironment(int videoEnvironment) {
        this.videoEnvironment = videoEnvironment;
    }

    public int getVideoQuality() {
        return videoQuality;
    }

    public void setVideoQuality(int videoQuality) {
        this.videoQuality = videoQuality;
    }

    public byte[] getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(byte[] snapshot) {
        this.snapshot = snapshot;
    }

    private int brightness;
    private int saturation;
    private int videoFlip;
    private int videoEnvironment;
    private int videoQuality;
    private byte snapshot[];


}

