package net.kaicong.ipcam.device.seeworld;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import net.kaicong.ipcam.R;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.GetCameraModel;
import net.kaicong.ipcam.device.record.Record;
import net.kaicong.ipcam.device.sip1018.MyIpCamera;
import net.kaicong.ipcam.device.sip1018.Sip1018Camera;
import net.kaicong.ipcam.device.sip1018.Sip1018Monitor;
import net.kaicong.ipcam.device.sip1018.Sip1018VideoListener;
import net.kaicong.ipcam.utils.LogUtil;

/**
 * Created by LingYan on 2014/8/26.
 */
public class SeeSip1018DeviceActivity extends BaseSeeWorldActivity implements Sip1018VideoListener {

    private RelativeLayout monitorLayout;
    private double videoRadio = 0;
    private Sip1018Monitor monitor;
    private Sip1018Camera sip1018Camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentModelId = GetCameraModel.CAMERA_MODEL_SIP1018;
        setContentView(R.layout.activity_see_sip1018_device);
        monitorLayout = (RelativeLayout) findViewById(R.id.monitor_layout);
        initVideo();
        initCommonView();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && isShowProgressBar) {
            animationImageView.setVisibility(View.VISIBLE);
            progressBarText.setVisibility(View.VISIBLE);
            animationDrawable.start();
        } else if (!isShowProgressBar) {
            animationDrawable.stop();
            animationImageView.setVisibility(View.GONE);
            progressBarText.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isRewardSuccess) {
            monitor.attachCamera(sip1018Camera);
            sip1018Camera.startConnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        monitor.deattachCamera(sip1018Camera);
        sip1018Camera.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        quit(true);
    }

    @Override
    public void quit(boolean isFinish) {
        monitor.deattachCamera(sip1018Camera);
        sip1018Camera.stop();
        if (isFinish) {
            finish();
        }
    }

    @Override
    public void share() {
        umengShareUtils.share(sip1018Camera.getSnapShot());
    }

    @Override
    public Bitmap getBitmap() {
        return sip1018Camera.getSnapShot();
    }

    @Override
    public void startRecording() {
        record.startRecording(sip1018Camera);
    }

    @Override
    public void stopRecording() {
        record.stopRecording(sip1018Camera);
    }

    private void initVideo() {
        Bundle bundle = getIntent().getExtras();
        //通过MyIpCamera对象取到IpCamera对象
        MyIpCamera myIpCamera = (MyIpCamera) bundle.getSerializable(CameraConstants.CAMERA);
        sip1018Camera = new Sip1018Camera(myIpCamera, this);
        monitor = (Sip1018Monitor) findViewById(R.id.monitor);
        monitor.setOnTouchListener(SeeSip1018DeviceActivity.this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        umengShareUtils.dismissShare();
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            doSetLayoutOnOrientation(mVideoWidth, mVideoHeight);
        }
    }


    @Override
    public void videoConnected(int videoWidth, int videoHeight) {
        mVideoWidth = videoWidth;
        mVideoHeight = videoHeight;
        record = new Record(SeeSip1018DeviceActivity.this, mVideoWidth, mVideoHeight, summary.deviceId);
        monitor.monitorRatio = (mVideoWidth * 1.0f) / mVideoHeight;
        doSetLayoutOnOrientation(mVideoWidth, mVideoHeight);
        monitor.attachCamera(sip1018Camera);
        isShowProgressBar = false;
        onWindowFocusChanged(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_CODE_SHARE_BACK) {
            resumePlay();
            postShareComment();
        }
    }

    @Override
    public void resumePlay() {
        monitor.attachCamera(sip1018Camera);
        sip1018Camera.startConnect();
    }

    @Override
    public void changePlay(String ip, int port, String account, String password, String UID) {
        //先暂停之前的播放
        monitor.deattachCamera(sip1018Camera);
        sip1018Camera.stop();
        //重新定义sip1018Camera
        MyIpCamera myIpCamera = new MyIpCamera("", "", ip, String.valueOf(port), account, password, 1000);
        sip1018Camera = new Sip1018Camera(myIpCamera, this);
        //重新开始播放
        monitor.attachCamera(sip1018Camera);
        sip1018Camera.startConnect();
    }

}
