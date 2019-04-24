package net.kaicong.ipcam;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.kaicong.ipcam.bean.VideoPopWindowIcon;
import net.kaicong.ipcam.device.OnSwipeTouchListener;
import net.kaicong.ipcam.device.ShareDeviceActivity;
import net.kaicong.ipcam.device.record.Record;
import net.kaicong.ipcam.view.PTZ_Controller_PopWindow;
import net.kaicong.ipcam.view.VideoPopWindow;
import net.kaicong.ipcam.view.VideoSettingPopWindow;
import net.kaicong.umshare.UMShareWindow;

import com.kaicong.myprogresshud.ProgressHUD;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.media.MailShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by LingYan on 15-1-9.
 */
public abstract class BaseSipDeviceActivity extends ActionBarActivity implements PTZ_Controller_PopWindow.OnPTZClickListener,
        VideoPopWindow.OnVideoPopWindowClickListener,
        VideoSettingPopWindow.OnVideoSettingListener,
        DialogInterface.OnCancelListener {

    public static final int REQUEST_CODE_SHARE = 1001;
    protected PTZ_Controller_PopWindow ptz_controller_popWindow;
    protected VideoPopWindow videoTopPopWindow;
    protected VideoPopWindow videoBottomPopWindow;
    protected VideoSettingPopWindow videoSettingPopWindow;
    protected ImageView animationImageView;
    protected AnimationDrawable animationDrawable;
    protected RelativeLayout monitorLayout;
    protected TextView progressBarText;
    //顶部菜单对应图片数组
    protected List<VideoPopWindowIcon> mTopIcons = new ArrayList<>();
    //底部菜单对应图片数组
    protected List<VideoPopWindowIcon> mBottomIcons = new ArrayList<>();

    protected int mVideoWidth = 0;
    protected int mVideoHeight = 0;

    protected Record record;
    private ProgressHUD progressHUD;
    protected boolean isShowProgressBar = true;
    protected boolean isRecording = false;
    protected Animation blinkAnimation;
    protected ImageView blinkImageView;
    //设备是否正在转动
    protected boolean isPTZing = false;

    protected UMShareWindow umShareWindow;
    protected boolean isIPDevice = false;
    protected boolean isShareOpen = false;
    protected int deviceId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏设置
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getSupportActionBar().hide();
        isIPDevice = getIntent().getBooleanExtra("isIPDevice", false);
        isShareOpen = getIntent().getBooleanExtra("isShareOpen", false);
    }

    protected void initCommonView() {
        // Typecasting the Image View
        animationImageView = (ImageView) findViewById(R.id.imageAnimation);
        progressBarText = (TextView) findViewById(R.id.progress_bar_text);
        monitorLayout = (RelativeLayout) findViewById(R.id.monitor_layout);
        animationImageView.setBackgroundResource(R.drawable.spinner);
        // Typecasting the Animation Drawable
        animationDrawable = (AnimationDrawable) animationImageView.getBackground();
        blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink);
        blinkImageView = (ImageView) findViewById(R.id.imageview_recording);

        umShareWindow = new UMShareWindow(this, new UMShareWindow.OnUMShareWindowClickListener() {

            @Override
            public void onUMShareClick(UMSocialService umSocialService, boolean isSeeWorld, SHARE_MEDIA platform) {
                if (isSeeWorld) {
                    if (isIPDevice) {
                        //ip模式无法分享设备
                        Toast.makeText(BaseSipDeviceActivity.this, getString(R.string.device_property_ip_cannot_share), Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        Intent intent = new Intent();
                        intent.setClass(BaseSipDeviceActivity.this, ShareDeviceActivity.class);
                        intent.putExtra("isShareOpen", isShareOpen);
                        intent.putExtra("deviceId", deviceId);
                        startActivityForResult(intent, REQUEST_CODE_SHARE);
                    }
                } else {
                    UMImage umImage;
                    if (getBitmap() != null && !getBitmap().isRecycled()) {
                        umImage = new UMImage(BaseSipDeviceActivity.this, getBitmap());
                    } else {
                        umImage = new UMImage(BaseSipDeviceActivity.this, R.drawable.see_launcher);
                    }
                    // 设置邮件分享内容， 如果需要分享图片则只支持本地图片
                    MailShareContent mail = new MailShareContent();
                    mail.setTitle(getString(R.string.see_world_share_from));
                    mail.setShareImage(umImage);
                    umSocialService.setShareMedia(mail);
                    // 设置微信好友分享内容
                    WeiXinShareContent weixinContent = new WeiXinShareContent();
                    weixinContent.setShareImage(umImage);
                    umSocialService.setShareMedia(weixinContent);
                    // 设置朋友圈分享的内容
                    CircleShareContent circleMedia = new CircleShareContent();
                    circleMedia.setShareImage(umImage);
                    umSocialService.setShareMedia(circleMedia);
                    SinaShareContent sinaShareContent = new SinaShareContent();
                    sinaShareContent.setShareImage(umImage);
                    umSocialService.setShareMedia(sinaShareContent);
                    //开启分享
                    umSocialService.postShare(BaseSipDeviceActivity.this, platform, mShareListener);
                }
            }

        });

    }

    protected void showShareDialog(View parentView) {
        umShareWindow.showAtLocation(parentView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    /**
     * 分享监听器
     */
    private SocializeListeners.SnsPostListener mShareListener = new SocializeListeners.SnsPostListener() {

        @Override
        public void onStart() {

        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int stCode,
                               SocializeEntity entity) {
            if (stCode == 200) {
                Toast.makeText(BaseSipDeviceActivity.this, "分享成功", Toast.LENGTH_SHORT)
                        .show();
            } else {
//                Toast.makeText(BaseSipDeviceActivity.this,
//                        "分享失败 : error code : " + stCode, Toast.LENGTH_SHORT)
//                        .show();
            }
        }
    };

    //根据屏幕方向和视频宽高比来动态分配surfaceView的宽高
    protected boolean doSetLayoutOnOrientation(int videoWidth, int videoHeight) {
        int layoutWidth = 0;
        int layoutHeight = 0;
        //这里要注意，设置monitor的LayoutParams属性时，width在任何情况下都是手机的此时的宽度
        //因为Holder的宽度是要充满屏幕的，而surfaceView的宽度就要根据比例来计算
        if (videoWidth > 0 && videoHeight > 0) {
            Configuration cfg = getResources().getConfiguration();
            double ratio = ((double) videoWidth) / videoHeight;
            if (cfg.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                layoutWidth = (int) (KCApplication.getWindowHeight() * ratio);
                layoutHeight = KCApplication.getWindowHeight();
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(layoutWidth, layoutHeight);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                monitorLayout.setLayoutParams(params);
            } else if (cfg.orientation == Configuration.ORIENTATION_PORTRAIT) {
                layoutWidth = KCApplication.getWindowWidth();
                layoutHeight = (int) (KCApplication.getWindowWidth() / ratio);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(layoutWidth, layoutHeight);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                monitorLayout.setLayoutParams(params);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onPTZClick(View view, int position) {

    }

    /**
     * onTouch方法处理
     *
     * @param surfaceView
     */
    protected void executeOnTouch(final SurfaceView surfaceView) {
        surfaceView.setOnTouchListener(new OnSwipeTouchListener(this) {

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                onPtzLeft();
                isPTZing = true;
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                onPtzRight();
                isPTZing = true;
            }

            @Override
            public void onSwipeTop() {
                super.onSwipeTop();
                onPtzUp();
                isPTZing = true;
            }

            @Override
            public void onSwipeBottom() {
                super.onSwipeBottom();
                onPtzDown();
                isPTZing = true;
            }

            @Override
            public void onSwipeSingleTapUp() {
                super.onSwipeSingleTapUp();
                if (isPTZing) {
                    //如果当前正在转动,则停止
                    onPtzStop();
                }
                if (ptz_controller_popWindow != null && ptz_controller_popWindow.isShowing()) {
                    ptz_controller_popWindow.dismiss();
                }
                if (videoSettingPopWindow != null && videoSettingPopWindow.isShowing()) {
                    videoSettingPopWindow.dismiss();
                    return;
                }
                if (videoTopPopWindow == null) {
                    videoTopPopWindow = new VideoPopWindow(BaseSipDeviceActivity.this, mTopIcons, BaseSipDeviceActivity.this);
                }
                if (videoTopPopWindow.isShowing()) {
                    videoTopPopWindow.dismiss();
                } else {
                    videoTopPopWindow.showAtLocation(surfaceView, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, getStatusBarHeight());
                }

                if (videoBottomPopWindow == null) {
                    videoBottomPopWindow = new VideoPopWindow(BaseSipDeviceActivity.this, mBottomIcons, BaseSipDeviceActivity.this);
                }
                if (videoBottomPopWindow.isShowing()) {
                    videoBottomPopWindow.dismiss();
                } else {
                    videoBottomPopWindow.showAtLocation(surfaceView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
            }
        });
    }

    @Override
    public void onVideoPopWindowClick(View parentView, View view, int position, boolean isTop) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        umShareWindow.doSSOHandler(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_SHARE) {
            KCApplication.isRefreshDevices = true;
        }
    }

    protected int getStatusBarHeight() {
        Rect rectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().
                getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        return statusBarHeight;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (ptz_controller_popWindow != null && ptz_controller_popWindow.isShowing()) {
                ptz_controller_popWindow.dismiss();
            }
            if (videoSettingPopWindow != null && videoSettingPopWindow.isShowing()) {
                videoSettingPopWindow.dismiss();
            }
            //返回键处理
            quitIfRecording();
            return true;
        }
        return true;
    }

    protected void quitIfRecording() {
        if (isRecording) {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.video_setting_save_record_and_quit))
                    .setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            new StopRecordingTask().execute();
                            isRecording = false;
                            quit();
                        }
                    }).setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).create().show();
        } else {
            quit();
        }
    }


    @Override
    public void onVideoResolutionSet(int position) {

    }

    @Override
    public void onVideoModeSet(int position) {

    }

    @Override
    public void onVideoBrightnessSet(int num) {

    }

    @Override
    public void onVideoSaturationSet(int num) {

    }

    @Override
    public void onVideoContrastSet(int num) {

    }

    @Override
    public void onVideoFlipSet(boolean on) {

    }

    @Override
    public void onVideoMirrorSet(boolean on) {

    }

    //默认的内容
    public void showProgressDialog(String content) {
        if (progressHUD == null) {
            progressHUD = ProgressHUD.show(this, content);
        }
        progressHUD.show();
    }

    public void setProgressText(String content) {
        if (progressHUD != null) {
            progressHUD.setMessage(content);
        }
    }

    //移除对话框
    public void removeProgressDialog() {
        if (progressHUD != null && progressHUD.isShowing()) {
            progressHUD.dismiss();
        }
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {

    }

    /**
     * 停止录制
     */
    public class StopRecordingTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog(getString(R.string.progress_text_save_recording));
        }

        @Override
        protected Void doInBackground(Void... voids) {
            stopRecording();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(BaseSipDeviceActivity.this, getString(R.string.progress_text_save_record_success), Toast.LENGTH_LONG).show();
            removeProgressDialog();
        }
    }


    protected void onPtzLeft() {

    }

    protected void onPtzRight() {

    }

    protected void onPtzUp() {

    }

    protected void onPtzDown() {

    }

    protected void onPtzStop() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
        if (umShareWindow != null && umShareWindow.isShowing()) {
            umShareWindow.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    public abstract void quit();

    protected abstract void stopRecording();

    protected abstract Bitmap getBitmap();


}
