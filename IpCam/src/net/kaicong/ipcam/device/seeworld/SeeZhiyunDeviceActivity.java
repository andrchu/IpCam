package net.kaicong.ipcam.device.seeworld;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.kaicong.ipcam.R;
import net.kaicong.ipcam.bean.GetCameraModel;
import net.kaicong.ipcam.device.record.Record;
import net.kaicong.ipcam.device.zhiyun.MyCamera;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.ipcam.utils.StringUtils;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.Monitor;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 智云播放
 * Created by LingYan on 14-10-29.
 */

public class SeeZhiyunDeviceActivity extends BaseSeeWorldActivity implements IRegisterIOTCListener {

    private static final int STS_FRAME_DATA_CHANGED = 1234566;
    private static final int STS_CHANGE_CHANNEL_STREAMINFO = 99;

    private Monitor monitor = null;
    private MyCamera mCamera = null;
    private String mDevUID;
    private String mAccount;
    private String mPassword;
    private String mCameraName;
    private int mSelectedChannel;
    private int reConnectCount = 0;
    private volatile boolean isShowing = false;
    private boolean onLine = false;
    private boolean isShowProgressBar = true;
    private QuitTask quitTask;

    //看看看tips
    private Timer timer;
    private TextView tipsView;
    private Animation operatingAnim;
    //断线重连
    protected ImageView reConnect;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentModelId = GetCameraModel.CAMERA_MODEL_ZHIYUN;
        setContentView(R.layout.activity_see_zhiyun_device);

        operatingAnim = AnimationUtils.loadAnimation(SeeZhiyunDeviceActivity.this, R.anim.common_image_rotate);

        mDevUID = getIntent().getStringExtra("mDevUID");
        mSelectedChannel = getIntent().getIntExtra("avChannel", 0);
        mAccount = getIntent().getStringExtra("mAccount");
        mPassword = getIntent().getStringExtra("mPassword");
        mCameraName = getIntent().getStringExtra("mCameraName");

        mCamera = new MyCamera(mCameraName, mDevUID, mAccount, mPassword);
        monitor = (Monitor) findViewById(R.id.monitor);
        monitor.setOnTouchListener(SeeZhiyunDeviceActivity.this);

        monitorLayout = (RelativeLayout) findViewById(R.id.monitor_layout);
        connectDevice();
        initCommonView();
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            //当以横屏方式进入播放时，这时先要隐藏pagelayout
            pageIndicatorLayout.setVisibility(View.GONE);
        }

        tipsView = (TextView) findViewById(R.id.text_tips);
        timer = new Timer();
        final String tipsArray[] = getResources().getStringArray(R.array.text_tips);
        final Random random = new Random();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tipsView.setText(tipsArray[random.nextInt(6)]);
                    }
                });
            }

        }, 0, 2000);

        reConnect = (ImageView) findViewById(R.id.re_connect);
        reConnect.setColorFilter(Color.WHITE);
        reConnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                LinearInterpolator lin = new LinearInterpolator();
                operatingAnim.setInterpolator(lin);
                reConnect.startAnimation(operatingAnim);

                progressBarText.setText(getString(R.string.connstus_connecting));
                connectDevice();

            }

        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && isShowProgressBar) {
            LogUtil.d("chu", "--hasFocus--");
            animationImageView.setVisibility(View.VISIBLE);
            progressBarText.setVisibility(View.VISIBLE);
            animationDrawable.start();
        } else if (!isShowProgressBar) {
            LogUtil.d("chu", "--loseFocus--");
            animationDrawable.stop();
            animationImageView.setVisibility(View.GONE);
            progressBarText.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.stopSpeaking(mSelectedChannel);
            mCamera.stopListening(mSelectedChannel);
            mCamera.stopShow(mSelectedChannel);
        }
        if (monitor != null)
            monitor.deattachCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d("chu", "--重新播放--");
        if (mCamera != null && !isRewardSuccess) {
            mCamera.startShow(mSelectedChannel, true);
            isShowing = false;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        umengShareUtils.dismissShare();
        if (mVideoHeight > 0 && mVideoHeight > 0) {
            doSetLayoutOnOrientation(mVideoWidth, mVideoHeight);
        }
        //转屏时重置image位置
        if (operatingAnim != null && operatingAnim.hasStarted()) {
            reConnect.clearAnimation();
            reConnect.startAnimation(operatingAnim);
        }
    }

    private class QuitTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
//            super.onPreExecute();
//            isShowProgressBar = true;
//            onWindowFocusChanged(true);
//            progressBarText.setText(getString(R.string.tips_quitting));
//            finish();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            closeDevice();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!onLine) {
                Toast.makeText(SeeZhiyunDeviceActivity.this, getString(R.string.connstus_connection_failed), Toast.LENGTH_LONG).show();
            }
            LogUtil.d("chu", "退出成功");
        }
    }

    //关闭设备
    private void closeDevice() {
        if (monitor != null) {
            monitor.deattachCamera();
            monitor = null;
        }
        if (mCamera != null) {
            mCamera.unregisterIOTCListener(SeeZhiyunDeviceActivity.this);
            mCamera.stopSpeaking(mSelectedChannel);
            mCamera.stopListening(mSelectedChannel);
            mCamera.stopShow(mSelectedChannel);
            mCamera.disconnect();
        }
    }

    //连接设备
    private void connectDevice() {
        //初始化
        if (!StringUtils.isEmpty(mAccount) && !StringUtils.isEmpty(mPassword)) {
            mCamera.registerIOTCListener(this);
            mCamera.connect(mDevUID);
            mCamera.start(MyCamera.DEFAULT_AV_CHANNEL, mAccount, mPassword);
            mCamera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ, AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
            mCamera.sendIOCtrl(MyCamera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ, AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq.parseContent());
            mCamera.startShow(mSelectedChannel, true);
        }
    }

    @Override
    public void receiveFrameData(final Camera camera, int avChannel, Bitmap bmp) {
        if (bmp != null) {
            mVideoWidth = bmp.getWidth();
            mVideoHeight = bmp.getHeight();
            Bundle data = new Bundle();
            data.putInt("videoWidth", bmp.getWidth());
            data.putInt("videoHeight", bmp.getHeight());
            Message msg = handler.obtainMessage();
            msg.what = STS_FRAME_DATA_CHANGED;
            msg.setData(data);
            handler.sendMessage(msg);
        }
    }

    @Override
    public void receiveFrameInfo(final Camera camera, int avChannel, long bitRate, int frameRate, int onlineNm, int frameCount, int incompleteFrameCount) {

        if (mCamera == camera && avChannel == mSelectedChannel) {
            Bundle bundle = new Bundle();
            bundle.putInt("avChannel", avChannel);
            Message msg = handler.obtainMessage();
            msg.what = STS_CHANGE_CHANNEL_STREAMINFO;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    @Override
    public void receiveChannelInfo(final Camera camera, int avChannel, int resultCode) {

        if (mCamera == camera && avChannel == mSelectedChannel) {
            Bundle bundle = new Bundle();
            bundle.putInt("avChannel", avChannel);
            Message msg = handler.obtainMessage();
            msg.what = resultCode;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    @Override
    public void receiveSessionInfo(final Camera camera, int resultCode) {

        if (mCamera == camera) {
            Bundle bundle = new Bundle();
            Message msg = handler.obtainMessage();
            msg.what = resultCode;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    @Override
    public void receiveIOCtrlData(final Camera camera, int avChannel, int avIOCtrlMsgType, byte[] data) {

        if (mCamera == camera) {
            Bundle bundle = new Bundle();
            bundle.putInt("avChannel", avChannel);
            bundle.putByteArray("data", data);
            Message msg = handler.obtainMessage();
            msg.what = avIOCtrlMsgType;
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            byte ctrlData[] = bundle.getByteArray("data");
            int avChannel = bundle.getInt("avChannel");
            switch (msg.what) {
                case STS_FRAME_DATA_CHANGED:
                    if (!isShowing) {
                        int videoWidth = msg.getData().getInt("videoWidth");
                        int videoHeight = msg.getData().getInt("videoHeight");
                        LogUtil.d("chu", "videoWidth=" + videoWidth);
                        monitor.monitorRatio = (mVideoWidth * 1.0f) / mVideoHeight;
                        record = new Record(SeeZhiyunDeviceActivity.this, videoWidth, videoHeight, summary.deviceId);
                        if (doSetLayoutOnOrientation(videoWidth, videoHeight)) {
                            monitor.mEnableDither = mCamera.mEnableDither;
                            monitor.setVisibility(View.VISIBLE);
                            monitor.attachCamera(mCamera, mSelectedChannel);

                            handlerConnected();
                        }
                    }
                    break;
                case STS_CHANGE_CHANNEL_STREAMINFO:
                    //视频属性变化时的回调（例如fps，bps等）
                    break;
                case Camera.CONNECTION_STATE_CONNECTING:
                    if (!mCamera.isSessionConnected() || !mCamera.isChannelConnected(mSelectedChannel)) {
                        LogUtil.d("chu", "连接中");
                        progressBarText.setText(getString(R.string.connstus_connecting));
                    }
                    break;
                case Camera.CONNECTION_STATE_CONNECTED:
                    if (mCamera.isSessionConnected() && avChannel == mSelectedChannel && mCamera.isChannelConnected(mSelectedChannel)) {
                        LogUtil.d("chu", "已连接");
                        onLine = true;
                        progressBarText.setText(getString(R.string.connstus_connect_connected));
                    }
                    break;
                case Camera.CONNECTION_STATE_DISCONNECTED:
                    onLine = false;
                    break;
                case Camera.CONNECTION_STATE_UNKNOWN_DEVICE:
                    makeToast(getString(R.string.connstus_unknown_device));
//                    progressBarText.setText(getString(R.string.connstus_unknown_device));
                    handlerDisconnected();
                    onLine = false;
                    quit(false);
                    break;
                case Camera.CONNECTION_STATE_TIMEOUT:
                    if (mCamera != null) {
                        onLine = false;
                        reConnectCount++;
                        LogUtil.d("chu", "重连次数=" + reConnectCount);
                        mCamera.stopSpeaking(mSelectedChannel);
                        mCamera.stopListening(mSelectedChannel);
                        mCamera.stopShow(mSelectedChannel);
                        mCamera.stop(mSelectedChannel);
                        mCamera.disconnect();
                        mCamera.connect(mDevUID);
                        mCamera.start(Camera.DEFAULT_AV_CHANNEL, mAccount, mPassword);
                        mCamera.startShow(mSelectedChannel, true);

                        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ, AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq.parseContent());
                        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ, AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
                        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ, AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq.parseContent());
                        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_TIMEZONE_REQ, AVIOCTRLDEFs.SMsgAVIoctrlTimeZone.parseContent());
                    }
                    break;
                case Camera.CONNECTION_STATE_CONNECT_FAILED:
                    makeToast(getString(R.string.connstus_connection_failed));
//                    progressBarText.setText(getString(R.string.connstus_connection_failed));
                    handlerDisconnected();
                    onLine = false;
                    quit(false);
                    break;
                case Camera.CONNECTION_STATE_WRONG_PASSWORD:
                    makeToast(getString(R.string.connstus_wrong_password));
//                    progressBarText.setText(getString(R.string.connstus_wrong_password));
                    handlerDisconnected();
                    onLine = false;
                    quit(false);
                    break;
                case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_RESP:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void handlerConnected() {
        isShowing = true;
        isShowProgressBar = false;
        onWindowFocusChanged(false);
        //移除tips
        timer.cancel();
        tipsView.setVisibility(View.GONE);
        reConnect.clearAnimation();
        reConnect.setVisibility(View.GONE);
    }

    private void handlerDisconnected() {
        isShowing = false;
        isShowProgressBar = false;
        onWindowFocusChanged(false);
        //移除tips
        timer.cancel();

        tipsView.setVisibility(View.GONE);
        reConnect.setVisibility(View.VISIBLE);
        reConnect.clearAnimation();
        progressBarText.setVisibility(View.VISIBLE);
        progressBarText.setText(getString(R.string.common_click_reconnect));
    }

    @Override
    public void quit(boolean isFinish) {
        quitTask = new QuitTask();
        quitTask.execute();
        if (isFinish) {
            finish();
        }
    }

    @Override
    public void share() {
        umengShareUtils.share(mCamera.Snapshot(mSelectedChannel));
    }

    @Override
    public Bitmap getBitmap() {
        if (null != mCamera && null != mCamera.Snapshot(mSelectedChannel)) {
            return mCamera.Snapshot(mSelectedChannel);
        } else {
            return null;
        }
    }

    @Override
    public void startRecording() {
        if (mCamera.isSessionConnected()) {
            record.startRecording(mCamera, mSelectedChannel);
        }
    }

    @Override
    public void stopRecording() {
        record.stopRecording(mCamera);
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
        if (mCamera != null) {
            mCamera.startShow(mSelectedChannel, true);
            isShowing = false;
        }
    }

    @Override
    public void changePlay(String ip, int port, String account, String password, String UID) {
        //关闭连接
        closeDevice();
        //重新配置参数
        mDevUID = UID;
        mSelectedChannel = MyCamera.DEFAULT_AV_CHANNEL;
        mAccount = account;
        mPassword = password;
        mCameraName = "";
        mCamera = new MyCamera(mCameraName, mDevUID, mAccount, mPassword);
        //重新连接
        connectDevice();
    }

}
