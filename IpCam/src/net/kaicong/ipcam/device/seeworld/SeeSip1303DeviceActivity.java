package net.kaicong.ipcam.device.seeworld;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import net.kaicong.ipcam.R;
import net.kaicong.ipcam.bean.GetCameraModel;
import net.kaicong.ipcam.device.record.Record;
import net.kaicong.ipcam.device.sip1303.Sip1303Monitor;
import net.kaicong.ipcam.device.sip1303.VideoIOListener;
import net.kaicong.ipcam.device.sip1303.VideoIOSocket;
import net.kaicong.ipcam.utils.LogUtil;

/**
 * 1303有两种解码播放模式
 * 一种是用java写的socket数据接收，拆包组帧与播放
 * 第二种是用ndk写的socket数据接收，拆包组帧与播放
 * 根据目前的测试，采用java的方式连接比ndk要稳定和容易控制（虽然ndk的效率要比java高）
 * Created by LingYan on 2014/11/6 0006.
 */
public class SeeSip1303DeviceActivity extends BaseSeeWorldActivity implements VideoIOListener<VideoIOSocket> {

    private VideoIOSocket videoIOSocket;
    private Sip1303Monitor monitor;
    private String mIp;
    private int mPort;
    private String mAccount;
    private String mPassword;
    private boolean isShowProgressBar = true;
    private boolean isOnPause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentModelId = GetCameraModel.CAMERA_MODEL_SIP1303;
        setContentView(R.layout.activity_see_sip1303_device);
        mIp = getIntent().getStringExtra("mIp");
        mPort = getIntent().getIntExtra("mPort", 0);
        mAccount = getIntent().getStringExtra("mAccount");
        mPassword = getIntent().getStringExtra("mPassword");

        monitor = (Sip1303Monitor) findViewById(R.id.monitor);
        monitor.setOnTouchListener(SeeSip1303DeviceActivity.this);
        videoIOSocket = new VideoIOSocket(mIp, mPort, mAccount, mPassword, this);
        videoIOSocket.startConnect();

        initCommonView();
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
    public void videoSocketConnected(VideoIOSocket videoIOSocket) {
        Message message = handler.obtainMessage();
        message.what = 1;
        handler.sendMessage(message);
    }

    @Override
    public void videoSocketConnectFailed(VideoIOSocket videoIOSocket) {
        Message message = handler.obtainMessage();
        message.what = -1;
        handler.sendMessage(message);
    }

    @Override
    public void videoSocketDisconnected(VideoIOSocket videoIOSocket) {

    }

    @Override
    public void getVideoInfoSuccess(VideoIOSocket videoIOSocket, int[] videoInfo) {
        /**
         * videoInfo[0] = videoWidth;//视频宽度
         videoInfo[1] = videoHeight;//视频高度
         videoInfo[2] = videoFormat;//视频编码方式
         videoInfo[3] = audioFormat;//音频编码方式
         */
        Message message = handler.obtainMessage();
        message.what = 2;
        Bundle data = new Bundle();
        data.putIntArray("videoInfo", videoInfo);
        message.setData(data);
        handler.sendMessage(message);
    }

    @Override
    public void getPacketException() {
        //接收数据包出现异常
        Message message = handler.obtainMessage();
        message.what = -2;
        handler.sendMessage(message);
    }

    public void audioSocketConnected() {
        Message message = handler.obtainMessage();
        message.what = 3;
        handler.sendMessage(message);
    }

    public void getAudioInfoSuccess() {
        Message message = handler.obtainMessage();
        message.what = 4;
        handler.sendMessage(message);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        umengShareUtils.dismissShare();
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            doSetLayoutOnOrientation(mVideoWidth, mVideoHeight);
        }
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == -1) {
                finish();
                LogUtil.d("chu", "videoSocketConnectFailed");
                Toast.makeText(SeeSip1303DeviceActivity.this, getString(R.string.connstus_connection_failed), Toast.LENGTH_LONG).show();
            }
            if (msg.what == -2) {
                isShowProgressBar = true;
                onWindowFocusChanged(true);
                progressBarText.setText(getString(R.string.tips_receive_packet_exception));
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        quit(false);
                    }
                }, 3000);
            }
            if (msg.what == 1) {
                videoIOSocket.startReceiveVideoHttpHeader();
            }
            if (msg.what == 2) {
                int videoInfo[] = msg.getData().getIntArray("videoInfo");
                //视频播放请求
                mVideoWidth = videoInfo[0];
                mVideoHeight = videoInfo[1];
                monitor.monitorRatio = (mVideoWidth * 1.0f) / mVideoHeight;
                doSetLayoutOnOrientation(mVideoWidth, mVideoHeight);
                record = new Record(SeeSip1303DeviceActivity.this, mVideoWidth, mVideoHeight, summary.deviceId);
                videoIOSocket.startReceiveVideoPacket();
                monitor.mEnableDither = false;
                monitor.attachCamera(videoIOSocket);
                isShowProgressBar = false;
                onWindowFocusChanged(false);
            }
        }
    };

    @Override
    public void quit(boolean isFinish) {
        monitor.deattachCamera(videoIOSocket);
        videoIOSocket.stopShow();
        videoIOSocket.disconnect();
        if (isFinish) {
            finish();
        }
    }

    @Override
    public void share() {
        umengShareUtils.share(monitor.getSnapShot());
    }

    @Override
    public Bitmap getBitmap() {
        if (null != videoIOSocket) {
            return videoIOSocket.get1303Comment_Pic();
        } else {
            return null;
        }
    }

    @Override
    public void startRecording() {
        record.startRecording(videoIOSocket);
    }

    @Override
    public void stopRecording() {
        record.stopRecording(videoIOSocket);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoIOSocket != null) {
            monitor.deattachCamera(videoIOSocket);
            videoIOSocket.stopShow();
            isOnPause = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoIOSocket != null && isOnPause && !isRewardSuccess) {
            videoIOSocket.startReceiveVideoPacket();
            monitor.attachCamera(videoIOSocket);
            isOnPause = false;
        }
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
        if (videoIOSocket != null && isOnPause) {
            videoIOSocket.startReceiveVideoPacket();
            monitor.attachCamera(videoIOSocket);
            isOnPause = false;
        }
    }

    @Override
    public void changePlay(String ip, int port, String account, String password, String UID) {
        //断开连接
        monitor.deattachCamera(videoIOSocket);
        videoIOSocket.stopShow();
        videoIOSocket.disconnect();
        //重置参数
        mIp = ip;
        mPort = port;
        mAccount = account;
        mPassword = password;
        videoIOSocket = new VideoIOSocket(mIp, mPort, mAccount, mPassword, this);
        //开始重连
        videoIOSocket.startConnect();
    }

}
