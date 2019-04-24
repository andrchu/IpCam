package net.kaicong.ipcam.db;

/**
 * Created by LingYan on 2014/11/10 0010.
 */
public class CameraBean {

    public int videoQuality;
    public int videoFlip;
    public int videoEnvironment;
    public int audioState;
    public byte snapshot[];

    public CameraBean(int videoQuality, int videoFlip, int videoEnvironment, int audioState, byte snapshot[]) {
        this.videoQuality = videoQuality;
        this.videoFlip = videoFlip;
        this.videoEnvironment = videoEnvironment;
        this.audioState = audioState;
        this.snapshot = snapshot;
    }

}
