package net.kaicong.ipcam.device.sip1601;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import net.kaicong.ipcam.BaseSipDeviceActivity;
import net.kaicong.ipcam.R;

import net.kaicong.ipcam.bean.VideoPopWindowIcon;
import net.kaicong.ipcam.device.CgiControlParams;
import net.kaicong.ipcam.device.cgi.CgiImageAttr;
import net.kaicong.ipcam.device.mjpeg.MjpegInputStream;
import net.kaicong.ipcam.device.mjpeg.MjpegView;
import net.kaicong.ipcam.device.record.Record;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.ipcam.utils.ToolUtil;
import net.kaicong.ipcam.utils.CapturePhotoUtils;
import net.kaicong.ipcam.view.VideoSettingPopWindow;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.net.URI;

/**
 * Created by LingYan on 14-12-26.
 */
public class Sip1601VideoActivity extends BaseSipDeviceActivity {

    public static final int PTZ_LEFT = 4;
    public static final int PTZ_RIGHT = 6;
    public static final int PTZ_UP = 0;
    public static final int PTZ_DOWN = 2;
    public static final int PTZ_UP_DOWN = 26;
    public static final int PTZ_UP_DOWN_STOP = 27;
    public static final int PTZ_LEFT_RIGHT = 28;
    public static final int PTZ_LEFT_RIGHT_STOP = 29;
    public static final String KEY_RESOLUTION = "0";
    public static final String KEY_BRIGHTNESS = "1";
    public static final String KEY_CONTRAST = "2";
    public static final String KEY_MODE = "3";
    public static final String KEY_FLIP = "5";
    public static final String KEY_SATURATION = "8";

    private MjpegView monitor;
    private String URL;
    private CgiControlParams cgiControlParams;
    private CgiImageAttr cgiImageAttr;

    private String ip;
    private int port;
    private String account;
    private String password;

    private boolean isShowProgressBar = true;
    private boolean suspending = false;
    private Record record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sip1601_video);

        initIcons();
        ip = getIntent().getStringExtra("ip");
        port = getIntent().getIntExtra("port", 0);
        account = getIntent().getStringExtra("account");
        password = getIntent().getStringExtra("password");
        deviceId = getIntent().getIntExtra("mDeviceId", 0);

        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(ip);
        sb.append(":");
        sb.append(port);
        sb.append("/videostream.cgi?user=");
        sb.append(account);
        sb.append("&pwd=");
        sb.append(password);
        URL = sb.toString();
        monitor = (MjpegView) findViewById(R.id.mv);
        executeOnTouch(monitor);

        initCommonView();
        new DoRead().execute(URL);

        cgiControlParams = new CgiControlParams();
        cgiControlParams.get1601ImageAttr(ip, port, account, password, new CgiControlParams.OnCgiTaskListener<CgiImageAttr>() {

            @Override
            public void onCgiTaskFinished(CgiImageAttr result) {
                cgiImageAttr = result;
                cgiImageAttr.flip = cgiImageAttr.flip1601 == 1;
                cgiImageAttr.mirror = cgiImageAttr.flip1601 == 2;
                initVideoResolution();
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
                R.drawable.ptz_up_down,
                R.drawable.ptz_left_right
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

    private void initVideoResolution() {
        switch (cgiImageAttr.resolution) {
            case 0:
                //高清
                mVideoWidth = 640;
                mVideoHeight = 480;
                break;
            case 1:
                //标清
                mVideoWidth = 320;
                mVideoHeight = 240;
                break;
            case 2:
                mVideoWidth = 160;
                mVideoHeight = 120;
                break;
            case 3:
                mVideoWidth = 1280;
                mVideoHeight = 720;
                break;
            case 4:
                mVideoWidth = 640;
                mVideoHeight = 360;
                break;
            case 5:
                mVideoWidth = 1280;
                mVideoHeight = 960;
                break;
        }
        record = new Record(Sip1601VideoActivity.this, mVideoWidth, mVideoHeight, deviceId);
    }

    @Override
    public void quit() {
        if (monitor != null) {
            if (monitor.isStreaming()) {
                monitor.stopPlayback();
                suspending = true;
            }
        }
        finish();
    }

    public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {

        protected MjpegInputStream doInBackground(String... url) {
            //TODO: if camera has authentication deal with it and don't just not work
            HttpResponse res = null;
            /**
             * add basic authentication
             */
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(
                    new AuthScope(ip, AuthScope.ANY_PORT),
                    new UsernamePasswordCredentials(account, password));
            DefaultHttpClient httpclient = new DefaultHttpClient();
            httpclient.setCredentialsProvider(credsProvider);
            HttpParams httpParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 5 * 1000);
            HttpConnectionParams.setSoTimeout(httpParams, 5 * 1000);
            try {
                res = httpclient.execute(new HttpGet(URI.create(url[0])));
                if (res.getStatusLine().getStatusCode() == 401) {
                    return null;
                }
                LogUtil.d("chu", "status code=" + res.getStatusLine().getStatusCode());
                return new MjpegInputStream(res.getEntity().getContent());
            } catch (Exception e) {
                LogUtil.d("chu", "ioexception" + e.toString());
            }
            return null;
        }

        protected void onPostExecute(MjpegInputStream result) {
            if (result == null) {
                Toast.makeText(Sip1601VideoActivity.this, getString(R.string.connstus_connection_failed), Toast.LENGTH_LONG).show();
                Sip1601VideoActivity.this.finish();
            } else {
                if (record == null) {
                    record = new Record(Sip1601VideoActivity.this, mVideoWidth, mVideoHeight, deviceId);
                }
                monitor.setVideoRadio((mVideoWidth * 1.0f) / mVideoHeight);
                doSetLayoutOnOrientation(mVideoWidth, mVideoHeight);
                monitor.setSource(result);

                result.setSkip(1);
                isShowProgressBar = false;
                onWindowFocusChanged(false);
            }
        }
    }

    public void onResume() {
        super.onResume();
        if (monitor != null) {
            if (suspending) {
                new DoRead().execute(URL);
                suspending = false;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (monitor != null) {
            if (monitor.isStreaming()) {
                monitor.stopPlayback();
                suspending = true;
            }
        }
    }

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
//                    //上下左右转动
//                    if (view.getTag() == true) {
//                        //处于选中状态，点击后取消选中
//                        videoTopPopWindow.filterColor(position, Color.WHITE);
//
//                        view.setTag(false);
//                    } else {
//                        videoTopPopWindow.filterColor(position, Color.RED);
//
//                        view.setTag(true);
//                    }
//                    break;
                case 1:
                    //视频设置
                    if (isRecording) {
                        new AlertDialog.Builder(this)
                                .setTitle(getString(R.string.video_setting_save_record))
                                .setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        new StopRecordingTask().execute();
                                        isRecording = false;
                                        videoBottomPopWindow.filterWhiteColor(1);
                                        blinkImageView.clearAnimation();
                                        blinkImageView.setVisibility(View.GONE);
                                    }
                                }).setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
                    } else {
                        if ((boolean) view.getTag() == true) {
                            //处于选中状态，点击后取消选中
                            videoTopPopWindow.filterWhiteColor(position);
                            videoSettingPopWindow.dismiss();
                        } else {
                            videoTopPopWindow.filterRedColor(position);
                            if (videoSettingPopWindow == null) {
                                videoSettingPopWindow = new VideoSettingPopWindow(Sip1601VideoActivity.this, cgiImageAttr, R.layout.pop_item_sip1601, this);
                            }
                            int[] xy = new int[2];
                            parentView.getLocationOnScreen(xy);
                            int y = xy[1] + parentView.getHeight();
                            videoSettingPopWindow.showAtLocation(findViewById(R.id.root), Gravity.LEFT | Gravity.TOP, 0, y);
                        }
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
                    //拍照截图
                    /**
                     * 截图保存
                     * 小米手机图片已经保存到DCIM文件夹，但是相册里面看不到，目测是小米系统的bug
                     */
                    String name = ToolUtil.getNowTimeStr();
                    CapturePhotoUtils.insertImage(getContentResolver(), monitor.getSnapShot(), name, name);
                    Toast.makeText(Sip1601VideoActivity.this, getString(R.string.tips_snapshot_ok), Toast.LENGTH_SHORT).show();
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
                        record.startRecording(monitor);
                        isRecording = true;
                    }
                    break;
                case 2:
                    //上下巡航
                    if ((boolean) view.getTag() == true) {
                        //处于选中状态，点击后取消选中
                        videoBottomPopWindow.filterWhiteColor(position);
                        cgiControlParams.set1601PTZControl(ip, port, account, password, PTZ_UP_DOWN_STOP);
                    } else {
                        videoBottomPopWindow.filterRedColor(position);
                        cgiControlParams.set1601PTZControl(ip, port, account, password, PTZ_UP_DOWN);
                    }
                    break;
                case 3:
                    //左右巡航
                    if ((boolean) view.getTag() == true) {
                        //处于选中状态，点击后取消选中
                        videoBottomPopWindow.filterWhiteColor(position);
                        cgiControlParams.set1601PTZControl(ip, port, account, password, PTZ_LEFT_RIGHT_STOP);
                    } else {
                        videoBottomPopWindow.filterRedColor(position);
                        cgiControlParams.set1601PTZControl(ip, port, account, password, PTZ_LEFT_RIGHT);
                    }
                    break;
            }
        }
    }

    @Override
    public void onVideoResolutionSet(int position) {
        super.onVideoResolutionSet(position);
        isShowProgressBar = true;
        onWindowFocusChanged(true);
        progressBarText.setText(getString(R.string.tips_changing));
        /**
         * 1601系列设备只支持高清和标清的切换
         */
        //先关闭播放
        if (monitor != null) {
            if (monitor.isStreaming()) {
//                //关闭录像
//                if (record != null) {
//                    record.stopRecording(monitor);
//                    record = null;
//                }
                monitor.stopPlayback();
                suspending = true;
            }
        }
        if (position == 0) {
            //高清
            mVideoWidth = 640;
            mVideoHeight = 480;
        } else if (position == 1) {
            //标清
            mVideoWidth = 320;
            mVideoHeight = 240;
        }
        cgiControlParams.set1601ImageAttr(ip, port, account, password, KEY_RESOLUTION, String.valueOf(position), new CgiControlParams.OnCgiTaskFinishedListener() {

            @Override
            public void onTaskFinished() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //分辨率切换完成
                        new DoRead().execute(URL);
                    }
                }, 3000);
            }
        });
    }

    @Override
    public void onVideoModeSet(int position) {
        super.onVideoModeSet(position);
        cgiControlParams.set1601ImageAttr(ip, port, account, password, KEY_MODE, String.valueOf(position), null);
    }

    @Override
    public void onVideoBrightnessSet(int num) {
        super.onVideoBrightnessSet(num);
        cgiControlParams.set1601ImageAttr(ip, port, account, password, KEY_BRIGHTNESS, String.valueOf(num), null);
    }

    @Override
    public void onVideoSaturationSet(int num) {
        super.onVideoSaturationSet(num);
        cgiControlParams.set1601ImageAttr(ip, port, account, password, KEY_SATURATION, String.valueOf(num), null);
    }

    @Override
    public void onVideoContrastSet(int num) {
        super.onVideoContrastSet(num);
        cgiControlParams.set1601ImageAttr(ip, port, account, password, KEY_CONTRAST, String.valueOf(num), null);
    }

    @Override
    public void onVideoFlipSet(boolean on) {
        super.onVideoFlipSet(on);
        String value = on ? "1" : "0";
        cgiControlParams.set1601ImageAttr(ip, port, account, password, KEY_FLIP, value, null);
    }

    @Override
    public void onVideoMirrorSet(boolean on) {
        super.onVideoMirrorSet(on);
        String value = on ? "2" : "0";
        cgiControlParams.set1601ImageAttr(ip, port, account, password, KEY_FLIP, value, null);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        doSetLayoutOnOrientation(mVideoWidth, mVideoHeight);
        umShareWindow.dismiss();
    }

    @Override
    protected void stopRecording() {
        record.stopRecording(monitor);
    }

    @Override
    protected Bitmap getBitmap() {
        return monitor.getSnapShot();
    }

    @Override
    protected void onPtzLeft() {
        super.onPtzLeft();
        cgiControlParams.set1601PTZControlOneStep(ip, port, account, password, PTZ_LEFT);
    }

    @Override
    protected void onPtzRight() {
        super.onPtzRight();
        cgiControlParams.set1601PTZControlOneStep(ip, port, account, password, PTZ_RIGHT);
    }

    @Override
    protected void onPtzUp() {
        super.onPtzUp();
        cgiControlParams.set1601PTZControlOneStep(ip, port, account, password, PTZ_UP);
    }

    @Override
    protected void onPtzDown() {
        super.onPtzDown();
        cgiControlParams.set1601PTZControlOneStep(ip, port, account, password, PTZ_DOWN);
    }

}
