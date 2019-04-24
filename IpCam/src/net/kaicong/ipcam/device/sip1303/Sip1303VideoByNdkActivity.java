package net.kaicong.ipcam.device.sip1303;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * 1303播放（NDK方式）
 * 效率较高，但是容易出现bug，并且不好控制，目前先不采用这种方式播放
 * Created by LingYan on 14-10-29.
 */
public class Sip1303VideoByNdkActivity extends ActionBarActivity
//        implements SurfaceHolder.Callback,
//        View.OnClickListener,
//        View.OnTouchListener,
//        VideoPopBottomWindow.OnBottomPopClickListener,
//        PTZ_Controller_PopWindow.OnPTZClickListener,
//        VideoIOListener
{

//    private static final int HANDLER_MSG_START_PLAY_VIDEO = 100;
//    private static final int HANDLER_MSG_CONNECT_SUCCESS = HANDLER_MSG_START_PLAY_VIDEO + 1;
//
//    private SurfaceView surfaceView;
//    private ProgressBar progressBar;
//    private VideoPopBottomWindow bottomWindow;
//    private PTZ_Controller_PopWindow ptz_controller_popWindow;
//
//    private boolean isAudioPlaying = false;
//    //原始视频宽度
//    private int originalVideoWidth = 0;
//    //原始视频高度
//    private int originalVideoHeight = 0;
//    //
//    private int videoWidth = 0;
//    private int videoHeight = 0;
//
//    private String mIp;
//    private int mPort;
//    private String mAccount;
//    private String mPassword;
//
//    private CgiControlParams cgiControlParams;
//    private PlayVideoThread playVideoThread = null;
//    private PlayAudioThread playAudioThread = null;
//    //判断进入activity时surfaceChanged是否先执行
//    private boolean isSurfaceReady = false;
//
//    private CgiImageAttr cgiImageAttr;
//
//    private VideoIOSocket videoIOSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化
//        Sip1303Decoder.initH264();
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getSupportActionBar().hide();
//        setContentView(R.layout.activity_sip1303_liveview_by_ndk);
//
//        surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
//        surfaceView.getHolder().addCallback(this);
//        surfaceView.setOnTouchListener(this);
//        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
//        progressBar.setVisibility(View.GONE);
//
//        mIp = getIntent().getStringExtra("mIp");
//        mPort = getIntent().getIntExtra("mPort", 0);
//        mAccount = getIntent().getStringExtra("mAccount");
//        mPassword = getIntent().getStringExtra("mPassword");

//        cgiControlParams = new CgiControlParams(mAccount, mPassword);
//        getImageAttr();
    }

    /**
     * 通过cgi获取到1303的视频属性
     */
//    private void getImageAttr() {
//        cgiControlParams.getImageAttr(CgiControlParams.getImageAttrUrl(mIp, mPort), new CgiControlParams.OnCgiTaskListener<CgiImageAttr>() {
//            @Override
//            public void onCgiTaskFinished(CgiImageAttr result) {
//                cgiImageAttr = result;
//            }
//        });
//    }
//
//    private void setImageAttr() {
//        RequestParams params = new RequestParams();
//        params.put("cmd", "setimageattr");
//        params.put("brightness", cgiImageAttr.brightness);
//        params.put("saturation", cgiImageAttr.saturation);
//        params.put("contrast", cgiImageAttr.contrast);
//        params.put("sharpness", cgiImageAttr.sharpness);
//        params.put("flip", cgiImageAttr.flip ? "on" : "off");
//        params.put("mirror", cgiImageAttr.mirror ? "on" : "off");
//        params.put("night", cgiImageAttr.night ? "on" : "off");
//        cgiControlParams.doCgiGetRequest(CgiControlParams.getSetImageAttrUrl(mIp, mPort), params);
//    }
//
//    private void doSetLayoutOnOrientation() {
//
//        //视频宽高比
//        double videoRatio = originalVideoWidth * 1.0 / originalVideoHeight;
//        Configuration cfg = getResources().getConfiguration();
//        if (cfg.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            //屏幕高宽比
//            double phoneRatio = KCApplication.getWindowWidth() * 1.0 / KCApplication.getWindowHeight();
//            //横屏
//            if (videoRatio > phoneRatio) {
//                videoWidth = KCApplication.getWindowHeight();
//                videoHeight = (int) (KCApplication.getWindowHeight() / videoRatio);
//            } else {
//                videoHeight = KCApplication.getWindowHeight();
//                videoWidth = (int) (KCApplication.getWindowHeight() * videoRatio);
//            }
//        } else if (cfg.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            //竖屏
//            //屏幕高宽比
//            double phoneRatio = KCApplication.getWindowHeight() * 1.0 / KCApplication.getWindowWidth();
//            if (videoRatio > phoneRatio) {
//                videoWidth = KCApplication.getWindowWidth();
//                videoHeight = (int) (KCApplication.getWindowWidth() / videoRatio);
//            } else {
//                if (originalVideoWidth > KCApplication.getWindowWidth()) {
//                    videoWidth = KCApplication.getWindowWidth();
//                    videoHeight = originalVideoHeight;
//                } else {
//                    videoWidth = originalVideoWidth;
//                    videoHeight = originalVideoHeight;
//                }
//            }
//        }
//        updateSurfaceView(videoWidth, videoHeight);
//    }
//
//    @Override
//    public void onClick(View view) {
//        RequestParams params = new RequestParams();
//        switch (view.getId()) {
//            case R.id.ptz_up:
//                cgiControlParams.doCgiGetRequest(CgiControlParams.getPtzUpUrl(mIp, mPort), params);
//                break;
//            case R.id.ptz_down:
//                cgiControlParams.doCgiGetRequest(CgiControlParams.getPtzDownUrl(mIp, mPort), params);
//                break;
//            case R.id.ptz_left:
//                cgiControlParams.doCgiGetRequest(CgiControlParams.getPtzLeftUrl(mIp, mPort), params);
//                break;
//            case R.id.ptz_right:
//                cgiControlParams.doCgiGetRequest(CgiControlParams.getPtzRightUrl(mIp, mPort), params);
//                break;
//        }
//    }
//
//    @Override
//    public void onBottomPopClick(View view, int position) {
//        switch (position) {
//            case 0:
//                //截图保存
//
//                break;
//            case 1:
//
//                break;
//            case 2:
//                //播放声音
//                if (!isAudioPlaying) {
//                    startPlayAudio();
//                    isAudioPlaying = true;
//                } else {
//                    stopPlayAudio();
//                    isAudioPlaying = false;
//                }
//                break;
//            case 3:
//                if (ptz_controller_popWindow == null) {
//                    ptz_controller_popWindow = new PTZ_Controller_PopWindow(this);
//                    ptz_controller_popWindow.setOnPTZClickListener(this);
//                }
//                if (ptz_controller_popWindow.isShowing()) {
//                    ptz_controller_popWindow.dismiss();
//                    return;
//                }
//                //ptz控制
//                ptz_controller_popWindow.showAtLocation(findViewById(R.id.root), Gravity.BOTTOM, 0, 0);
//                break;
//        }
//    }
//
//    @Override
//    public boolean onTouch(View view, MotionEvent motionEvent) {
//        switch (motionEvent.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//
//                break;
//            case MotionEvent.ACTION_UP:
//                if (bottomWindow == null) {
//                    bottomWindow = new VideoPopBottomWindow(this);
//                    bottomWindow.setOnBottomPopClickListenerr(this);
//                }
//                if (bottomWindow.isShowing()) {
//                    bottomWindow.dismiss();
//                } else {
//                    bottomWindow.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
//                }
//                break;
//        }
//        return true;
//    }
//
//    @Override
//    public void onPTZClick(View view, int position) {
//        RequestParams params = new RequestParams();
//        switch (position) {
//            case PTZ_Controller_PopWindow.PTZ_CLICK_POSITION_UP:
//                cgiControlParams.doCgiGetRequest(CgiControlParams.getPtzUpUrl(mIp, mPort), params);
//                break;
//            case PTZ_Controller_PopWindow.PTZ_CLICK_POSITION_LEFT:
//                cgiControlParams.doCgiGetRequest(CgiControlParams.getPtzLeftUrl(mIp, mPort), params);
//                break;
//            case PTZ_Controller_PopWindow.PTZ_CLICK_POSITION_RIGHT:
//                cgiControlParams.doCgiGetRequest(CgiControlParams.getPtzRightUrl(mIp, mPort), params);
//                break;
//            case PTZ_Controller_PopWindow.PTZ_CLICK_POSITION_DOWN:
//                cgiControlParams.doCgiGetRequest(CgiControlParams.getPtzDownUrl(mIp, mPort), params);
//                break;
//        }
//    }
//
//    @Override
//    public void videoSocketConnected(VideoIOSocket videoIOSocket) {
//        //连接成功
//        Message message = handler.obtainMessage();
//        message.what = HANDLER_MSG_CONNECT_SUCCESS;
//        handler.sendMessage(message);
//    }
//
//    @Override
//    public void videoSocketConnectFailed(VideoIOSocket videoIOSocket) {
//
//    }
//
//    @Override
//    public void videoSocketDisconnected(VideoIOSocket videoIOSocket) {
//
//    }
//
//    @Override
//    public void getVideoInfoSuccess(VideoIOSocket videoIOSocket, int[] videoInfo) {
//
//    }
//
//    //video播放线程
//    private class PlayVideoThread extends Thread {
//
//        @Override
//        public void run() {
//            super.run();
//            Sip1303Decoder.playVideo();
//        }
//    }
//
//    //audio播放线程
//    private static class PlayAudioThread extends Thread {
//
//        @Override
//        public void run() {
//            super.run();
//            Sip1303Decoder.playAudio();
//        }
//
//    }
//
//    private void startPlayAudio() {
//        Sip1303Decoder.initAudio(mIp, mPort, mAccount, mPassword);
//        if (playAudioThread == null) {
//            playAudioThread = new PlayAudioThread();
//            playAudioThread.start();
//        }
//    }
//
//    private void stopPlayAudio() {
//        Sip1303Decoder.stopAudio();
//        if (playAudioThread != null) {
//            try {
//                playAudioThread.interrupt();
//                playAudioThread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } finally {
//                LogUtil.d("chu", "java play audio thread exit!");
//                Sip1303Decoder.closeAudio();
//                playAudioThread = null;
//            }
//        }
//    }
//
//    //开启播放线程
//    private void startPlayVideo() {
//        // 初始化socket
//        int ret = Sip1303Decoder.initVideo(mIp, mPort, mAccount, mPassword);
//        if (ret < 0) {
//            Toast.makeText(this, getString(R.string.connstus_connection_failed), Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//        int res[] = Sip1303Decoder.getVideoInfo();
//        originalVideoWidth = res[0];
//        originalVideoHeight = res[1];
//        doSetLayoutOnOrientation();
//        LogUtil.d("chu", "originalVideoWidth" + originalVideoWidth);
//        LogUtil.d("chu", "originalVideoHeight" + originalVideoHeight);
//        LogUtil.d("chu", "video width" + videoWidth);
//        LogUtil.d("chu", "video height" + videoHeight);
//        Sip1303Decoder.setup(videoWidth, videoHeight);
//        if (playVideoThread == null) {
//            playVideoThread = new PlayVideoThread();
//            playVideoThread.start();
//        }
//        progressBar.setVisibility(View.GONE);
//    }
//
//    //关闭播放
//    private void stopPlayVideo() {
//        Sip1303Decoder.stopVideo();
//        if (playVideoThread != null) {
//            try {
//                playVideoThread.interrupt();
//                playVideoThread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } finally {
//                LogUtil.d("chu", "java play video thread exit!");
//                // 关闭socket
//                Sip1303Decoder.closeVideo();
//                playVideoThread = null;
//            }
//        }
//    }
//
//    private void updateSurfaceView(int pWidth, int pHeight) {
//        // update surfaceview dimension, this will cause the native window to
//        // change
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) surfaceView
//                .getLayoutParams();
//        params.width = pWidth;
//        params.height = pHeight;
//        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//        surfaceView.setLayoutParams(params);
//    }
//
//    private Handler handler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == HANDLER_MSG_START_PLAY_VIDEO) {
//                isSurfaceReady = true;
//                videoIOSocket = new VideoIOSocket(mIp, mPort, mAccount, mPassword, Sip1303VideoByNdkActivity.this);
//                videoIOSocket.startConnect();
//            }
//            if (msg.what == HANDLER_MSG_CONNECT_SUCCESS) {
//                startPlayVideo();
//                videoIOSocket.disconnect();
//            }
//        }
//
//    };
//
//
//    @Override
//    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
//        // TODO Auto-generated method stub
//        LogUtil.d("chu", "width=" + width + " height=" + height);
//        Sip1303Decoder.setSurface(surfaceHolder.getSurface(), width, height);
//        if (!isSurfaceReady) {
//            Message message = handler.obtainMessage();
//            message.what = HANDLER_MSG_START_PLAY_VIDEO;
//            handler.sendMessage(message);
//        }
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        doSetLayoutOnOrientation();
//    }
//
//    @Override
//    public void surfaceCreated(SurfaceHolder surfaceHolder) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
//        // TODO Auto-generated method stub
//        Sip1303Decoder.setSurface(null, 0, 0);
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        // TODO Auto-generated method stub
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            stopPlayAudio();
//            stopPlayVideo();
//            finish();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
////        if (isSurfaceReady) {
////            startPlayVideo();
////        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        stopPlayAudio();
//        stopPlayVideo();
//        isAudioPlaying = false;
//    }
//
//    @Override
//    protected void onDestroy() {
//        // TODO Auto-generated method stub
//        super.onDestroy();
//        LogUtil.i("chu", "activity onDestroy");
//        Sip1303Decoder.releaseDecoder();
//    }

}
