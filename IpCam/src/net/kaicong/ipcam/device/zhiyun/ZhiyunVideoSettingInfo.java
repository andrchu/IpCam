package net.kaicong.ipcam.device.zhiyun;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LingYan on 2014/11/10 0010.
 */
public class ZhiyunVideoSettingInfo implements Serializable {

    private boolean isWifiSettingSupported = false;
    private boolean isEventSettingSupported = false;
    private boolean isRecordSettingSupported = false;
    private boolean isTimeZoneSupported = false;
    private boolean isSDCardFormatSupported = false;
    private boolean isQualitySettingSupported = false;//视频质量
    private boolean isFlipSettingSupported = false;//视频翻转
    private boolean isEnvironmentSettingSupported = false;//环境设置
    private boolean isVideoChannelSettingSupported = false;

    private int videoQuality;
    private int videoChannel;
    private List<String> channels = new ArrayList<>();

    public List<String> getChannels() {
        return channels;
    }

    public void setChannels(List<String> channels) {
        this.channels = channels;
    }

    public boolean isVideoChannelSettingSupported() {
        return isVideoChannelSettingSupported;
    }

    public void setVideoChannelSettingSupported(boolean isVideoChannelSettingSupported) {
        this.isVideoChannelSettingSupported = isVideoChannelSettingSupported;
    }

    public int getVideoChannel() {
        return videoChannel;
    }

    public void setVideoChannel(int videoChannel) {
        this.videoChannel = videoChannel;
    }

    private int videoFlip;
    private int videoEnvironment;

    public boolean isWifiSettingSupported() {
        return isWifiSettingSupported;
    }

    public void setWifiSettingSupported(boolean isWifiSettingSupported) {
        this.isWifiSettingSupported = isWifiSettingSupported;
    }

    public boolean isEventSettingSupported() {
        return isEventSettingSupported;
    }

    public void setEventSettingSupported(boolean isEventSettingSupported) {
        this.isEventSettingSupported = isEventSettingSupported;
    }

    public boolean isRecordSettingSupported() {
        return isRecordSettingSupported;
    }

    public void setRecordSettingSupported(boolean isRecordSettingSupported) {
        this.isRecordSettingSupported = isRecordSettingSupported;
    }

    public boolean isTimeZoneSupported() {
        return isTimeZoneSupported;
    }

    public void setTimeZoneSupported(boolean isTimeZoneSupported) {
        this.isTimeZoneSupported = isTimeZoneSupported;
    }

    public boolean isSDCardFormatSupported() {
        return isSDCardFormatSupported;
    }

    public void setSDCardFormatSupported(boolean isSDCardFormatSupported) {
        this.isSDCardFormatSupported = isSDCardFormatSupported;
    }

    public boolean isQualitySettingSupported() {
        return isQualitySettingSupported;
    }

    public void setQualitySettingSupported(boolean isQualitySettingSupported) {
        this.isQualitySettingSupported = isQualitySettingSupported;
    }

    public boolean isFlipSettingSupported() {
        return isFlipSettingSupported;
    }

    public void setFlipSettingSupported(boolean isFlipSettingSupported) {
        this.isFlipSettingSupported = isFlipSettingSupported;
    }

    public boolean isEnvironmentSettingSupported() {
        return isEnvironmentSettingSupported;
    }

    public void setEnvironmentSettingSupported(boolean isEnvironmentSettingSupported) {
        this.isEnvironmentSettingSupported = isEnvironmentSettingSupported;
    }

    public int getVideoQuality() {
        return videoQuality;
    }

    public void setVideoQuality(int videoQuality) {
        this.videoQuality = videoQuality;
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
}
