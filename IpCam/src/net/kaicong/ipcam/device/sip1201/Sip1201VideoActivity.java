
package net.kaicong.ipcam.device.sip1201;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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
import net.kaicong.ipcam.utils.CapturePhotoUtils;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.ipcam.utils.ToolUtil;
import net.kaicong.ipcam.view.VideoSettingPopWindow;

import com.loopj.android.http.RequestParams;

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
public class Sip1201VideoActivity extends BaseSipDeviceActivity {

    /**
     * 高清->1280×720
     * 标清->640×352
     * 极速->320*176
     */

    private MjpegView monitor;
    private int mChannel = 12;

    private String ip;
    private int port;
    private String account;
    private String password;
    private int cameraModel = 0;
    private CgiControlParams cgiControlParams;
    private CgiImageAttr cgiImageAttr;

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
        cameraModel = getIntent().getIntExtra("cameraModel", 0);
        deviceId = getIntent().getIntExtra("mDeviceId", 0);

        monitor = (MjpegView) findViewById(R.id.mv);
        executeOnTouch(monitor);

        mVideoWidth = 640;
        mVideoHeight = 352;

        initCommonView();
        //默认第二通道码流

        cgiControlParams = new CgiControlParams(account, password);
        cgiControlParams.getImageAttr(CgiControlParams.getImageAttrUrl(ip, port), new CgiControlParams.OnCgiTaskListener<CgiImageAttr>() {

            @Override
            public void onCgiTaskFinished(CgiImageAttr result) {
                cgiImageAttr = result;
                cgiImageAttr.resolution = 1;//默认显示标清
                cgiImageAttr.mode = cgiImageAttr.night ? 1 : 0;
            }

        });
        new DoRead().execute(getUrl(mChannel));
//        record = new Record(Sip1201VideoActivity.this, mVideoWidth, mVideoHeight);
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

    private String getUrl(int channel) {
        StringBuilder sb = new StringBuilder();
        sb.append("http://");
        sb.append(ip);
        sb.append(":");
        sb.append(port);
        sb.append("/cgi-bin/hi3510/mjpegstream.cgi?-chn=" + channel + "&-usr=");
        sb.append(account);
        sb.append("&-pwd=");
        sb.append(password);
        return sb.toString();
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
                R.drawable.video_play_zoom_in,
                R.drawable.video_play_zoom_out,
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
                HttpGet httpGet = new HttpGet(URI.create(url[0]));
                res = httpclient.execute(httpGet);
                if (res.getStatusLine().getStatusCode() == 401) {
                    return null;
                }
                return new MjpegInputStream(res.getEntity().getContent());
            } catch (Exception e) {
            }
            return null;
        }

        protected void onPostExecute(MjpegInputStream result) {
            if (result == null) {
                Toast.makeText(Sip1201VideoActivity.this, getString(R.string.connstus_connection_failed), Toast.LENGTH_LONG).show();
                Sip1201VideoActivity.this.finish();
            } else {
                if (record == null) {
                    record = new Record(Sip1201VideoActivity.this, mVideoWidth, mVideoHeight, deviceId);
                }
                doSetLayoutOnOrientation(mVideoWidth, mVideoHeight);
                monitor.setVideoRadio((mVideoWidth * 1.0f) / mVideoHeight);
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
                new DoRead().execute(getUrl(mChannel));
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
                                        videoBottomPopWindow.filterWhiteColor(3);
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
                                videoSettingPopWindow = new VideoSettingPopWindow(Sip1201VideoActivity.this, cgiImageAttr, R.layout.pop_item_sip1201, this);
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
                    Toast.makeText(Sip1201VideoActivity.this, getString(R.string.tips_snapshot_ok), Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    //zoom in
                    if (cameraModel != 1212) {
                        Toast.makeText(Sip1201VideoActivity.this, getString(R.string.video_setting_zoom_not_support), Toast.LENGTH_SHORT).show();
                    } else {
                        if ((boolean) view.getTag() == true) {
                            cgiControlParams.set1212Zoom(ip, port, account, password, -1);
                        } else {
                            cgiControlParams.set1212Zoom(ip, port, account, password, 1);
                        }
                    }
                    break;
                case 2:
                    //zoom out
                    if (cameraModel != 1212) {
                        Toast.makeText(Sip1201VideoActivity.this, getString(R.string.video_setting_zoom_not_support), Toast.LENGTH_SHORT).show();
                    } else {
                        if ((boolean) view.getTag() == true) {
                            cgiControlParams.set1212Zoom(ip, port, account, password, -1);
                        } else {
                            cgiControlParams.set1212Zoom(ip, port, account, password, 2);
                        }
                    }
                    break;
                case 3:
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
                case 4:
                    //上下巡航
                    if ((boolean) view.getTag() == true) {
                        //处于选中状态，点击后取消选中
                        videoBottomPopWindow.filterWhiteColor(position);
                        RequestParams params = new RequestParams();
                        cgiControlParams.doCgiGetRequest(CgiControlParams.getSCANStopUrl(ip, port), params);
                    } else {
                        videoBottomPopWindow.filterRedColor(position);
                        RequestParams params = new RequestParams();
                        cgiControlParams.doCgiGetRequest(CgiControlParams.getVSCANUrl(ip, port), params);
                    }
                    break;
                case 5:
                    //左右巡航
                    if ((boolean) view.getTag() == true) {
                        //处于选中状态，点击后取消选中
                        videoBottomPopWindow.filterWhiteColor(position);
                        RequestParams params = new RequestParams();
                        cgiControlParams.doCgiGetRequest(CgiControlParams.getSCANStopUrl(ip, port), params);
                    } else {
                        videoBottomPopWindow.filterRedColor(position);
                        RequestParams params = new RequestParams();
                        cgiControlParams.doCgiGetRequest(CgiControlParams.getHSCANUrl(ip, port), params);
                    }
                    break;
            }
        }
    }

    @Override
    public void onVideoResolutionSet(int position) {
        super.onVideoResolutionSet(position);
        //先关闭播放
        if (monitor != null) {
            if (monitor.isStreaming()) {
                //关闭录像
//                if (record != null) {
//                    record.stopRecording(monitor);
//                    record = null;
//                }
                monitor.stopPlayback();
                suspending = true;
            }
        }
        //高清
        if (position == 0) {
            mChannel = 11;
            mVideoWidth = 1280;
            mVideoHeight = 720;
        } else if (position == 1) {
            mChannel = 12;
            mVideoWidth = 640;
            mVideoHeight = 352;
        } else if (position == 2) {
            mChannel = 13;
            mVideoWidth = 320;
            mVideoHeight = 176;
        }
        new DoRead().execute(getUrl(mChannel));
    }

    @Override
    public void onVideoModeSet(int position) {
        super.onVideoModeSet(position);
        String isOnOff = position == 0 ? "off" : "on";
        cgiControlParams.setCgiNightMode(ip, port, isOnOff);
    }

    @Override
    public void onVideoBrightnessSet(int num) {
        super.onVideoBrightnessSet(num);
        cgiControlParams.setCgiBrightness(ip, port, num);
    }

    @Override
    public void onVideoSaturationSet(int num) {
        super.onVideoSaturationSet(num);
        cgiControlParams.setCgiSaturation(ip, port, num);
    }

    @Override
    public void onVideoContrastSet(int num) {
        super.onVideoContrastSet(num);
        cgiControlParams.setCgiContrast(ip, port, num);
    }

    @Override
    public void onVideoFlipSet(boolean on) {
        super.onVideoFlipSet(on);
        String isOnOff = on ? "on" : "off";
        cgiControlParams.setCgiFlip(ip, port, isOnOff);
    }

    @Override
    public void onVideoMirrorSet(boolean on) {
        super.onVideoMirrorSet(on);
        String isOnOff = on ? "on" : "off";
        cgiControlParams.setCgiMirror(ip, port, isOnOff);
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
        RequestParams params = new RequestParams();
        cgiControlParams.doCgiGetRequest(CgiControlParams.getPtzLeftUrl(ip, port), params);
    }

    @Override
    protected void onPtzRight() {
        super.onPtzRight();
        RequestParams params = new RequestParams();
        cgiControlParams.doCgiGetRequest(CgiControlParams.getPtzRightUrl(ip, port), params);
    }

    @Override
    protected void onPtzUp() {
        super.onPtzUp();
        RequestParams params = new RequestParams();
        cgiControlParams.doCgiGetRequest(CgiControlParams.getPtzUpUrl(ip, port), params);
    }

    @Override
    protected void onPtzDown() {
        super.onPtzDown();
        RequestParams params = new RequestParams();
        cgiControlParams.doCgiGetRequest(CgiControlParams.getPtzDownUrl(ip, port), params);
    }

}
