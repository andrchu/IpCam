package net.kaicong.ipcam.device.sip1018;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.misc.objc.NSData;
import com.misc.objc.NSNotification;
import com.misc.objc.NSNotificationCenter;
import com.misc.objc.NSSelector;

import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.utils.LogUtil;
import net.reecam.ipc.IpCamera;
import net.reecam.ipc.SimpleAudioTrack;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * Created by LingYan on 15/2/12.
 */
public class Sip1018Camera {

    //摄像头
    private ExpansionIpCamera ipCamera;
    //视频图片帧
    private Bitmap bitmap;
    private Thread audioPlayThread = null;
    private Handler mChildHandler;

    private volatile boolean isShowing = false;

    private List<ReceiveSip1018FrameDataListener> mReceiveDataListeners = Collections.synchronizedList(new Vector<ReceiveSip1018FrameDataListener>());
    private Sip1018VideoListener sip1018VideoListener;

    public Sip1018Camera(MyIpCamera myIpCamera, Sip1018VideoListener sip1018VideoListener) {
        ipCamera = new ExpansionIpCamera(myIpCamera.getCameraId(), myIpCamera.getCameraName(), myIpCamera.getCameraHost(), myIpCamera.getCameraPort(), myIpCamera.getUser(),
                myIpCamera.getPass());
        this.sip1018VideoListener = sip1018VideoListener;
        addObserver();
    }

    /**
     * 向通知中心添加通知
     */
    private void addObserver() {
        NSNotificationCenter nc = NSNotificationCenter.defaultCenter();
        nc.addObserver(this, new NSSelector("cameraStatusChanged"),
                ExpansionIpCamera.IPCamera_CameraStatusChanged_Notification, ipCamera);// .IpCameraCameraStatusChanged_Notification,
        // camera);
        nc.addObserver(this, new NSSelector("videoStatusChanged"),
                ExpansionIpCamera.IPCamera_VideoStatusChanged_Notification, ipCamera);// .IpCameraVideoStatusChanged_Notification,
        // camera);
        nc.addObserver(this, new NSSelector("OnAudioStatusChanged"),
                ExpansionIpCamera.IPCamera_AudioStatusChanged_Notification, null);// .IpCameraAudioStatusChanged_Notification,
        // null);
        nc.addObserver(this, new NSSelector("talkStatusChanged"),
                ExpansionIpCamera.IPCamera_TalkStatusChanged_Notification, ipCamera);// .IpCameraTalkStatusChanged_Notification,
        // camera);
        nc.addObserver(this, new NSSelector("OnImageChanged"),
                ExpansionIpCamera.IPCamera_Image_Notification, ipCamera);// .IpCameraImage_Notification,
        // camera);
    }

    /**
     * 摄像头状态改变
     *
     * @param note NSNotification
     */
    public void cameraStatusChanged(NSNotification note) {
        /**
         * 连接成功
         * DNSRESOLVING
         * CONNECTING
         * LOGINING
         * VERIFYING
         * CONNECTED
         *
         * 连接失败
         * DNSRESOLVING
         * CONNECTING
         * 中间间隔时间较长，所以设备出现DISCONNECTED就是连接超时
         * DISCONNECTED
         */
        final String status = note.userInfo().get("status").toString();
        LogUtil.d("chu", "cameraStatusChanged: " + status);
        if (status.equals(CameraConstants.CONNECTED)) {
            ipCamera.play_video();
        }
    }

    /**
     * 摄像头视频状态改变
     *
     * @param note NSNotification
     */
    public void videoStatusChanged(NSNotification note) {
        final String status = note.userInfo().get("status").toString();
        LogUtil.d("chu", "videoStatusChanged: " + status);
        if (status.equals("STOPPED")) {
            stopAudioPlay();
            if (ipCamera.started) {
                ipCamera.play_video();
                LogUtil.e("videoStatusChanged videoStatus 0000", "videoStatus: "
                        + status);
            }
        }
    }

    /**
     * 视频图像输出
     *
     * @param note
     */
    public void OnImageChanged(NSNotification note) {
        if (!((ExpansionIpCamera) note.object()).equals(ipCamera)) {
            return;
        }
        NSData data = (NSData) note.userInfo().get("data");
        try {
            bitmap = BitmapFactory.decodeByteArray(data.bytes(), 0,
                    data.length());
        } catch (OutOfMemoryError e) {
            return;
        }
        synchronized (mReceiveDataListeners) {
            for (int i = 0; i < mReceiveDataListeners.size(); i++) {
                ReceiveSip1018FrameDataListener receiveSip1018FrameDataListener = mReceiveDataListeners.get(i);
                receiveSip1018FrameDataListener.receiveSip1018FrameData(bitmap);
            }
        }
        if (!isShowing) {
            Message msg = videoHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putInt("mVideoWidth", bitmap.getWidth());
            bundle.putInt("mVideoHeight", bitmap.getHeight());
            msg.setData(bundle);
            msg.what = 100;
            videoHandler.sendMessage(msg);
        }

    }

    private Handler videoHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100) {
                isShowing = true;
                if (sip1018VideoListener != null) {
                    sip1018VideoListener.videoConnected(msg.getData().getInt("mVideoWidth"), msg.getData().getInt("mVideoHeight"));
                }
            }
        }

    };

    /**
     * 摄像头音频改变
     *
     * @param note NSNotification
     */

    public void OnAudioStatusChanged(NSNotification note) {
        // TODO
        ExpansionIpCamera camera = (ExpansionIpCamera) note.object();
        LogUtil.e("AUDIO STATUS",
                camera.getHost() + ":"
                        + note.userInfo().get("status").toString()
        );
        final String status = note.userInfo().get("status").toString();
        if (status.equals(CameraConstants.PLAYING)) {
            startAudioPlay();
        } else if (status.equals(CameraConstants.STOPPED)) {
            stopAudioPlay();
        }
    }

    /**
     * 开启摄像头音频
     */
    protected void startAudioPlay() {
        audioPlayThread = new Thread(new Runnable() {

            public void run() {
                final SimpleAudioTrack audioTrack;
                try {
                    // Create a new AudioTrack object using the same parameters
                    audioTrack = new SimpleAudioTrack(8000,
                            AudioFormat.CHANNEL_CONFIGURATION_MONO,
                            AudioFormat.ENCODING_PCM_16BIT);
                    // Start playback
                    audioTrack.init();
                    LogUtil.e("AudioTrack", "Ready");
                    // Write the music buffer to the AudioTrack object
                } catch (Throwable t) {
                    LogUtil.e("AudioTrack", "Playback Failed");
                    return;
                }
                Looper.prepare();
                mChildHandler = new Handler() {
                    public void handleMessage(Message msg) {
                        NSNotification note = (NSNotification) msg.obj;
                        if (note == null) {
                            // user ask to stop
                            mChildHandler = null;
                            LogUtil.e("AudioTrack", "Playback Quit");
                            Looper.myLooper().quit();
                        } else {
                            // play audio data
                            int play_time = (Integer) note.userInfo().get(
                                    CameraConstants.TICK);
                            int get_time = (Integer) note.userInfo().get(CameraConstants.T);
                            int now_time = ExpansionIpCamera.times(null);
                            if ((play_time - now_time) < -10) {
                                // drop delayed packet
                                return;
                            }
                            NSData data = (NSData) note.userInfo().get(CameraConstants.DATA);
                            audioTrack.playAudioTrack(data.bytes(), 0,
                                    data.length());
                        }
                    }
                };
                Looper.loop();
            }
        });
        audioPlayThread.start();
        NSNotificationCenter nc = NSNotificationCenter.defaultCenter();
        nc.addObserver(this, new NSSelector("OnAudio"),
                ExpansionIpCamera.IPCamera_Audio_Notification, ipCamera);
    }

    /**
     * 打开摄像头音频输出
     *
     * @param note
     */
    public void OnAudio(NSNotification note) {
        // send to the playing thread
        if (mChildHandler != null) {
            Message msg = mChildHandler.obtainMessage();
            msg.obj = note;
            mChildHandler.sendMessage(msg);
        }
    }

    /**
     * 停止摄像头音频输出
     */
    protected void stopAudioPlay() {
        // don't need audio data any more
        NSNotificationCenter nc = NSNotificationCenter.defaultCenter();
        nc.removeObserver(this, ExpansionIpCamera.IPCamera_Audio_Notification, ipCamera);
        // notify audio play thread to stop
        if (mChildHandler != null) {
            Message msg = mChildHandler.obtainMessage();
            msg.obj = null;
            mChildHandler.sendMessage(msg);
        }
        audioPlayThread = null;
    }

    //注册接口
    public boolean registerReceiveFrameDataListener(ReceiveSip1018FrameDataListener receiveSip1018FrameDataListener) {
        boolean result = false;
        if (!mReceiveDataListeners.contains(receiveSip1018FrameDataListener)) {
            mReceiveDataListeners.add(receiveSip1018FrameDataListener);
            result = true;
        }
        return result;
    }

    //移除接口
    public boolean unRegisterReceiveFrameDataListener(ReceiveSip1018FrameDataListener receiveSip1018FrameDataListener) {
        boolean result = false;
        if (mReceiveDataListeners.contains(receiveSip1018FrameDataListener)) {
            mReceiveDataListeners.remove(receiveSip1018FrameDataListener);
            result = true;
        }
        return result;
    }

    public Bitmap getSnapShot() {
        return bitmap;
    }

    public void startConnect() {
        ipCamera.stop_video();
        if (!ipCamera.started) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //开始连接摄像头
                    ipCamera.start();
                }
            }).start();
        }
        String status = ipCamera.camera_status.toString();
        if (status.equals(CameraConstants.CONNECTED)) {
            //已连接上,开始播放
            ipCamera.play_video();
        }
    }

    public void stop() {
        NSNotificationCenter notificationCenter = NSNotificationCenter.defaultCenter();
        notificationCenter.removeObserver(this);
        ipCamera.stop_talk();
        ipCamera.stop_audio();
        ipCamera.stop_video();
        ipCamera.stop_record();
        ipCamera.stop();
    }

    public IpCamera getIpCamera() {
        return ipCamera;
    }

}
