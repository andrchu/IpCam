package net.kaicong.ipcam.device.sip1406;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import net.kaicong.ipcam.BaseSipDeviceActivity;
import net.kaicong.ipcam.R;
import net.kaicong.ipcam.bean.VideoPopWindowIcon;
import net.kaicong.ipcam.device.OnSwipeTouchListener;
import net.kaicong.ipcam.device.record.Record;
import net.kaicong.ipcam.device.sip1303.ReceiveSip1303FrameDataListener;
import net.kaicong.ipcam.device.sip1303.VideoIOListener;
import net.kaicong.ipcam.device.timer.TimerCamera;
import net.kaicong.ipcam.utils.CapturePhotoUtils;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.ipcam.utils.ToolUtil;
import net.kaicong.ipcam.view.VideoPopWindow;

/**
 * Created by LingYan on 15/5/6.
 */
public class Sip1406VideoActivity extends BaseSipDeviceActivity implements ReceiveSip1303FrameDataListener {

    private TimerCamera timerCamera;
    private ImageView imageView;

    private String mIp;
    private int mPort;
    private String mAccount;
    private String mPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sip1404_video);

        initIcons();
        initCommonView();

        mIp = getIntent().getStringExtra("mIp");
        mPort = getIntent().getIntExtra("mPort", 0);
        mAccount = getIntent().getStringExtra("mAccount");
        mPassword = getIntent().getStringExtra("mPassword");
        deviceId = getIntent().getIntExtra("mDeviceId", 0);

        String url = "http://" + mIp + ":" + mPort + "/snapshot.cgi?user=" + mAccount + "&pwd=" + mPassword;

        imageView = (ImageView) findViewById(R.id.monitor);
        imageView.setOnTouchListener(new OnSwipeTouchListener(this) {

            @Override
            public void onSwipeSingleTapUp() {
                super.onSwipeSingleTapUp();
                if (videoTopPopWindow == null) {
                    videoTopPopWindow = new VideoPopWindow(Sip1406VideoActivity.this, mTopIcons, Sip1406VideoActivity.this);
                }
                if (videoTopPopWindow.isShowing()) {
                    videoTopPopWindow.dismiss();
                } else {
                    videoTopPopWindow.showAtLocation(imageView, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, getStatusBarHeight());
                }
            }
        });
        timerCamera = new TimerCamera(this, url, mAccount, mPassword, imageView);
        timerCamera.registerReceiveFrameDataListener(this);
        timerCamera.getImage();

        mVideoWidth = 640;
        mVideoHeight = 480;

    }

    private void initIcons() {
        int topDrawableIds[] = new int[]{
                R.drawable.video_play_back,
                R.drawable.video_play_snap,
                R.drawable.video_play_share
        };
        for (int i = 0; i < topDrawableIds.length; i++) {
            VideoPopWindowIcon videoPopWindowIcon = new VideoPopWindowIcon();
            videoPopWindowIcon.position = i;
            videoPopWindowIcon.isTop = true;
            videoPopWindowIcon.drawableId = topDrawableIds[i];
            mTopIcons.add(videoPopWindowIcon);
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
                case 1:
                    //拍照截图
                    /**
                     * 截图保存
                     * 小米手机图片已经保存到DCIM文件夹，但是相册里面看不到，目测是小米系统的bug
                     */
                    String name = ToolUtil.getNowTimeStr();
                    CapturePhotoUtils.insertImage(getContentResolver(), timerCamera.getSnapshot(), name, name);
                    Toast.makeText(Sip1406VideoActivity.this, getString(R.string.tips_snapshot_ok), Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    //分享
                    showShareDialog(findViewById(R.id.root));
                    break;
            }
        }
    }

    @Override
    public void quit() {
        timerCamera.unRegisterReceiveFrameDataListener(this);
        timerCamera.stop();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerCamera.unRegisterReceiveFrameDataListener(this);
        timerCamera.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        timerCamera.getImage();
    }

    @Override
    protected void stopRecording() {

    }

    @Override
    protected Bitmap getBitmap() {
        return timerCamera.getSnapshot();
    }

    @Override
    public void receiveFrameData(final Bitmap bitmap) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bitmap);
            }
        });
    }

}
