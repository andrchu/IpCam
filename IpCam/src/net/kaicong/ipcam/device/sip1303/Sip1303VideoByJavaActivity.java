package net.kaicong.ipcam.device.sip1303;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import net.kaicong.ipcam.BaseSipDeviceActivity;
import net.kaicong.ipcam.R;

import net.kaicong.ipcam.bean.VideoPopWindowIcon;
import net.kaicong.ipcam.device.CgiControlParams;
import net.kaicong.ipcam.device.cgi.CgiImageAttr;
import net.kaicong.ipcam.device.record.Record;
import net.kaicong.ipcam.utils.CapturePhotoUtils;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.ipcam.utils.ToolUtil;
import net.kaicong.ipcam.view.PTZ_Controller_PopWindow;
import net.kaicong.ipcam.view.VideoSettingPopWindow;

import com.loopj.android.http.RequestParams;

/**
 * 1303有两种解码播放模式
 * 一种是用java写的socket数据接收，拆包组帧与播放
 * 第二种是用ndk写的socket数据接收，拆包组帧与播放
 * 根据目前的测试，采用java的方式连接比ndk要稳定和容易控制（虽然ndk的效率要比java高）
 * Created by LingYan on 2014/11/6 0006.
 */
public class Sip1303VideoByJavaActivity extends BaseSipDeviceActivity
        implements VideoIOListener<VideoIOSocket> {

    private VideoIOSocket videoIOSocket;
    private Audio audio;
    private Sip1303Monitor sip1303Monitor;

    private String mIp;
    private int mPort;
    private String mAccount;
    private String mPassword;
    private CgiControlParams cgiControlParams;
    private CgiImageAttr cgiImageAttr;

    private boolean isOnPause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sip1303_liveview_by_java);

        initIcons();
        initCommonView();

        mIp = getIntent().getStringExtra("mIp");
        mPort = getIntent().getIntExtra("mPort", 0);
        mAccount = getIntent().getStringExtra("mAccount");
        mPassword = getIntent().getStringExtra("mPassword");
        deviceId = getIntent().getIntExtra("mDeviceId", 0);

//        mIp = "222.93.219.158";
//        mPort = 89;
//        mAccount = "admin";
//        mPassword = "123456";

        //获取cgi信息
        cgiControlParams = new CgiControlParams(mAccount, mPassword);
        getImageAttr();

        sip1303Monitor = (Sip1303Monitor) findViewById(R.id.monitor);
        executeOnTouch(sip1303Monitor);
//        videoIOSocket = new VideoIOSocket("10.10.12.36", 9036, "admin", "123456", this);
        videoIOSocket = new VideoIOSocket(mIp, mPort, mAccount, mPassword, this);
        videoIOSocket.startConnect();
        audio = new Audio(mIp, mPort, mAccount, mPassword);
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

    private void initIcons() {
        int topDrawableIds[] = new int[]{
                R.drawable.video_play_back,
//                R.drawable.video_play_revert,
                R.drawable.video_play_settings,
                R.drawable.video_play_share
        };
        int bottomDrawableIds[] = new int[]{
                R.drawable.video_play_snap,
                R.drawable.video_play_video_camera,
                R.drawable.video_play_volume,
                R.drawable.video_play_ptz_arrow
        };
        for (int i = 0; i < topDrawableIds.length; i++) {
            VideoPopWindowIcon videoPopWindowIcon = new VideoPopWindowIcon();
            videoPopWindowIcon.position = i;
            videoPopWindowIcon.isTop = true;
            videoPopWindowIcon.drawableId = topDrawableIds[i];
            mTopIcons.add(videoPopWindowIcon);
        }
        for (int j = 0; j < bottomDrawableIds.length; j++) {
            VideoPopWindowIcon videoPopWindowIcon = new VideoPopWindowIcon();
            videoPopWindowIcon.position = j;
            videoPopWindowIcon.isTop = false;
            videoPopWindowIcon.drawableId = bottomDrawableIds[j];
            mBottomIcons.add(videoPopWindowIcon);
        }
    }

    /**
     * 通过cgi获取到1303的视频属性
     */
    private void getImageAttr() {
        cgiControlParams.getImageAttr(CgiControlParams.getImageAttrUrl(mIp, mPort), new CgiControlParams.OnCgiTaskListener<CgiImageAttr>() {
            @Override
            public void onCgiTaskFinished(CgiImageAttr result) {
                cgiImageAttr = result;
                cgiImageAttr.mode = cgiImageAttr.night ? 1 : 0;
            }
        });
    }

    @Override
    public void videoSocketConnected(VideoIOSocket videoIOSocket) {
        Message message = handler.obtainMessage();
        message.what = 1;
        handler.sendMessage(message);
    }

    private void startPlayAudio() {
        audio.startPlayAudio();
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        doSetLayoutOnOrientation(mVideoWidth, mVideoHeight);
        umShareWindow.dismiss();
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == -1) {
                finish();
                LogUtil.d("chu", "videoSocketConnectFailed");
                Toast.makeText(Sip1303VideoByJavaActivity.this, getString(R.string.connstus_connection_failed), Toast.LENGTH_LONG).show();
            }
            if (msg.what == -2) {
                LogUtil.d("chu", "--start--");
                Toast.makeText(Sip1303VideoByJavaActivity.this, getString(R.string.tips_receive_packet_exception), Toast.LENGTH_SHORT).show();
                quit();
            }
            if (msg.what == 1) {
                videoIOSocket.startReceiveVideoHttpHeader();
            }
            if (msg.what == 2) {
                int videoInfo[] = msg.getData().getIntArray("videoInfo");
                //视频播放请求
                mVideoWidth = videoInfo[0];
                mVideoHeight = videoInfo[1];
                record = new Record(Sip1303VideoByJavaActivity.this, mVideoWidth, mVideoHeight, deviceId);
                sip1303Monitor.monitorRatio = (mVideoWidth * 1.0f) / mVideoHeight;
                doSetLayoutOnOrientation(mVideoWidth, mVideoHeight);
                videoIOSocket.startReceiveVideoPacket();
                sip1303Monitor.mEnableDither = false;
                sip1303Monitor.attachCamera(videoIOSocket);
                isShowProgressBar = false;
                onWindowFocusChanged(false);

            }
        }
    };

    @Override
    public void onVideoPopWindowClick(View parentView, View view, int position, boolean isTop) {
        super.onVideoPopWindowClick(parentView, view, position, isTop);
        if (isTop) {
            //顶部
            switch (position) {
                case 0:
                    //返回
                    quitIfRecording();
                    break;
//                case 1:
//                    //预制位
//
//                    break;
                case 1:
                    //视频设置
                    if ((boolean) view.getTag() == true) {
                        //处于选中状态，点击后取消选中
                        videoTopPopWindow.filterWhiteColor(position);
                        videoSettingPopWindow.dismiss();
                    } else {
                        videoTopPopWindow.filterRedColor(position);
                        if (videoSettingPopWindow == null) {
                            videoSettingPopWindow = new VideoSettingPopWindow(Sip1303VideoByJavaActivity.this, cgiImageAttr, R.layout.pop_item_sip1303, this);
                        }
                        int[] xy = new int[2];
                        parentView.getLocationOnScreen(xy);
                        int y = xy[1] + parentView.getHeight();
                        videoSettingPopWindow.showAtLocation(findViewById(R.id.root), Gravity.LEFT | Gravity.TOP, 0, y);
                    }
                    break;
                case 2:
                    //分享
                    showShareDialog(findViewById(R.id.root));
                    break;
            }
        } else {
            //底部
            switch (position) {
                case 0:
                    //截图保存
                    /**
                     * 截图保存
                     * 小米手机图片已经保存到DCIM文件夹，但是相册里面看不到，目测是小米系统的bug
                     */
                    String name = ToolUtil.getNowTimeStr();
                    CapturePhotoUtils.insertImage(getContentResolver(), sip1303Monitor.getSnapShot(), name, name);
                    Toast.makeText(Sip1303VideoByJavaActivity.this, getString(R.string.tips_snapshot_ok), Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    //录像
                    if ((boolean) view.getTag() == true) {
                        //处于选中状态，点击后取消选中
                        blinkImageView.clearAnimation();
                        blinkImageView.setVisibility(View.GONE);
                        videoBottomPopWindow.filterWhiteColor(position);
                        new StopRecordingTask().execute();
                        isRecording = false;
                    } else {
                        blinkImageView.setVisibility(View.VISIBLE);
                        blinkImageView.setColorFilter(Color.RED);
                        blinkImageView.startAnimation(blinkAnimation);
                        videoBottomPopWindow.filterRedColor(position);
                        record.startRecording(videoIOSocket);
                        isRecording = true;
                    }
                    break;
                case 2:
                    //播放声音
                    if ((boolean) view.getTag() == true) {
                        //处于选中状态，点击后取消选中
                        videoBottomPopWindow.filterWhiteColor(position);
                        audio.stopListen();
                    } else {
                        videoBottomPopWindow.filterRedColor(position);
                        startPlayAudio();
                    }
                    break;
                case 3:
                    //ptz控制
                    if (ptz_controller_popWindow == null) {
                        ptz_controller_popWindow = new PTZ_Controller_PopWindow(this);
                        ptz_controller_popWindow.setOnPTZClickListener(this);
                    }
                    if (ptz_controller_popWindow.isShowing()) {
                        ptz_controller_popWindow.dismiss();
                        return;
                    }
                    //ptz控制
                    ptz_controller_popWindow.showAtLocation(findViewById(R.id.root), Gravity.BOTTOM, 0, 0);
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoIOSocket != null) {
            sip1303Monitor.deattachCamera(videoIOSocket);
            videoIOSocket.stopShow();
            isOnPause = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoIOSocket != null && isOnPause) {
            videoIOSocket.startReceiveVideoPacket();
            sip1303Monitor.attachCamera(videoIOSocket);
            isOnPause = false;
        }
    }

    @Override
    public void onPTZClick(View view, int position) {
        RequestParams params = new RequestParams();
        switch (position) {
            case PTZ_Controller_PopWindow.PTZ_CLICK_POSITION_UP:
                cgiControlParams.doCgiGetRequest(CgiControlParams.getPtzUpUrl(mIp, mPort), params);
                break;
            case PTZ_Controller_PopWindow.PTZ_CLICK_POSITION_LEFT:
                cgiControlParams.doCgiGetRequest(CgiControlParams.getPtzLeftUrl(mIp, mPort), params);
                break;
            case PTZ_Controller_PopWindow.PTZ_CLICK_POSITION_RIGHT:
                cgiControlParams.doCgiGetRequest(CgiControlParams.getPtzRightUrl(mIp, mPort), params);
                break;
            case PTZ_Controller_PopWindow.PTZ_CLICK_POSITION_DOWN:
                cgiControlParams.doCgiGetRequest(CgiControlParams.getPtzDownUrl(mIp, mPort), params);
                break;
            case PTZ_Controller_PopWindow.PTZ_CLICK_POSITION_LEFT_RIGHT:
                cgiControlParams.doCgiGetRequest(CgiControlParams.getHSCANUrl(mIp, mPort), params);
                break;
            case PTZ_Controller_PopWindow.PTZ_CLICK_POSITION_UP_DOWN:
                cgiControlParams.doCgiGetRequest(CgiControlParams.getVSCANUrl(mIp, mPort), params);
                break;
            case PTZ_Controller_PopWindow.PTZ_CLICK_POSITION_STOP:
                cgiControlParams.doCgiGetRequest(CgiControlParams.getSCANStopUrl(mIp, mPort), params);
                break;
        }
    }

    @Override
    public void onVideoModeSet(int position) {
        super.onVideoModeSet(position);
        String isOnOff = position == 0 ? "off" : "on";
        cgiControlParams.setCgiNightMode(mIp, mPort, isOnOff);
    }

    @Override
    public void onVideoBrightnessSet(int num) {
        super.onVideoBrightnessSet(num);
        cgiControlParams.setCgiBrightness(mIp, mPort, num);
    }

    @Override
    public void onVideoSaturationSet(int num) {
        super.onVideoSaturationSet(num);
        cgiControlParams.setCgiSaturation(mIp, mPort, num);
    }

    @Override
    public void onVideoContrastSet(int num) {
        super.onVideoContrastSet(num);
        cgiControlParams.setCgiContrast(mIp, mPort, num);
    }

    @Override
    public void onVideoFlipSet(boolean on) {
        super.onVideoFlipSet(on);
        String isOnOff = on ? "on" : "off";
        cgiControlParams.setCgiFlip(mIp, mPort, isOnOff);
    }

    @Override
    public void onVideoMirrorSet(boolean on) {
        super.onVideoMirrorSet(on);
        String isOnOff = on ? "on" : "off";
        cgiControlParams.setCgiMirror(mIp, mPort, isOnOff);
    }

    @Override
    public void quit() {
        sip1303Monitor.deattachCamera(videoIOSocket);
        videoIOSocket.stopShow();
        audio.stopListen();
        videoIOSocket.disconnect();
        audio.disconnect();
        finish();
    }

    @Override
    protected void stopRecording() {
        record.stopRecording(videoIOSocket);
    }

    @Override
    protected Bitmap getBitmap() {
        return sip1303Monitor.getSnapShot();
    }

    @Override
    protected void onPtzLeft() {
        super.onPtzLeft();
        RequestParams params = new RequestParams();
        cgiControlParams.doCgiGetRequest(CgiControlParams.getPtzLeftUrl(mIp, mPort), params);
    }

    @Override
    protected void onPtzRight() {
        super.onPtzRight();
        RequestParams params = new RequestParams();
        cgiControlParams.doCgiGetRequest(CgiControlParams.getPtzRightUrl(mIp, mPort), params);
    }

    @Override
    protected void onPtzUp() {
        super.onPtzUp();
        RequestParams params = new RequestParams();
        cgiControlParams.doCgiGetRequest(CgiControlParams.getPtzUpUrl(mIp, mPort), params);
    }

    @Override
    protected void onPtzDown() {
        super.onPtzDown();
        RequestParams params = new RequestParams();
        cgiControlParams.doCgiGetRequest(CgiControlParams.getPtzDownUrl(mIp, mPort), params);
    }

}
