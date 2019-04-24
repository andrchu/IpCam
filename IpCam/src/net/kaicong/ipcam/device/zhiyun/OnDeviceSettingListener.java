package net.kaicong.ipcam.device.zhiyun;

/**
 * Created by LingYan on 2014/11/10 0010.
 */
public interface OnDeviceSettingListener {

    public void onVideoPropertyChanged(ZhiyunVideoSettingInfo zhiyunVideoSettingInfo, boolean isChangeVideoQuality);

    public void onAudioStateChanged(int audioState);

}
