package net.kaicong.ipcam.device.zhiyun;

import android.graphics.Bitmap;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;

/**
 * Created by LingYan on 15/5/11.
 */
public class ZhiyunCmd implements IRegisterIOTCListener {

    private MyCamera myCamera;
    private String uid;
    private String account;
    private String password;

    public ZhiyunCmd(String uid, String name, String account, String password) {
        this.uid = uid;
        this.account = account;
        this.password = password;
        myCamera = new MyCamera(name, uid, account, password);
    }

    public void sendCmd(int cmd) {
        myCamera.registerIOTCListener(this);
        myCamera.connect(uid);
        myCamera.start(MyCamera.DEFAULT_AV_CHANNEL, account, password);
        myCamera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.AVIOCTRL_EVENT_EXPT_REBOOT, AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
    }

    @Override
    public void receiveFrameData(Camera camera, int avChannel, Bitmap bmp) {

    }

    @Override
    public void receiveFrameInfo(Camera camera, int avChannel, long bitRate, int frameRate, int onlineNm, int frameCount, int incompleteFrameCount) {

    }

    @Override
    public void receiveSessionInfo(Camera camera, int resultCode) {

    }

    @Override
    public void receiveChannelInfo(Camera camera, int avChannel, int resultCode) {

    }

    @Override
    public void receiveIOCtrlData(Camera camera, int avChannel, int avIOCtrlMsgType, byte[] data) {

    }

}
