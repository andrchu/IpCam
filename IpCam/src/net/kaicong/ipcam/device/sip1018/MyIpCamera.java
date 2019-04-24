package net.kaicong.ipcam.device.sip1018;

import java.io.Serializable;

/**
 * Created by LingYan on 2014/8/26.
 */
public class MyIpCamera implements Serializable {
    public static final int DEFAULT_AUDIO_BUFFER_TIME = 1000;

    private String cameraId;
    private String cameraName;
    private String cameraHost;
    private String cameraPort;
    private String user;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private String pass;
    private int audioBufferTime;

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    private int position;
    private int deviceId;

    public MyIpCamera() {

    }

    public MyIpCamera(String cameraId, String cameraName, String cameraHost, String cameraPort, String user, String pass, int audioBufferTime) {
        this.cameraId = cameraId;
        this.cameraName = cameraName;
        this.cameraHost = cameraHost;
        this.cameraPort = cameraPort;
        this.user = user;
        this.pass = pass;
        this.audioBufferTime = audioBufferTime;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public void setCameraHost(String cameraHost) {
        this.cameraHost = cameraHost;
    }

    public void setCameraPort(String cameraPort) {
        this.cameraPort = cameraPort;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setAudioBufferTime(int audioBufferTime) {
        this.audioBufferTime = audioBufferTime;
    }

    public String getCameraId() {
        return cameraId;
    }

    public String getCameraName() {
        return cameraName;
    }

    public String getCameraHost() {
        return cameraHost;
    }

    public String getCameraPort() {
        return cameraPort;
    }

    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }

    public int getAudioBufferTime() {
        return audioBufferTime;
    }
}
