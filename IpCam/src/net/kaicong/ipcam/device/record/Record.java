package net.kaicong.ipcam.device.record;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.googlecode.javacv.FFmpegFrameRecorder;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import net.kaicong.ipcam.device.mjpeg.MjpegView;
import net.kaicong.ipcam.device.mjpeg.ReceiveMjpegFrameListener;
import net.kaicong.ipcam.device.sip1018.ReceiveSip1018FrameDataListener;
import net.kaicong.ipcam.device.sip1018.Sip1018Camera;
import net.kaicong.ipcam.device.sip1303.ReceiveSip1303FrameDataListener;
import net.kaicong.ipcam.device.sip1303.VideoIOSocket;

import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;

import java.io.File;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;

/**
 * 录制视频工具类（FFMPEGRecorder）
 * Created by LingYan on 15/1/30.
 */
public class Record implements ReceiveSip1303FrameDataListener,
        IRegisterIOTCListener,
        ReceiveMjpegFrameListener,
        ReceiveSip1018FrameDataListener {

    public final static String VIDEO_CONTENT_URI = "content://media/external/video/media";
    private IplImage yuvIplimage = null;

    private int frameRate = 30;
    private volatile FFmpegFrameRecorder recorder;
    private boolean recording = false;
    private long startTime = 0;

    private String strVideoPath;
    //视频文件在系统中存放的url
    private Uri uriVideoPath;
    private Context context;

    private Bitmap mLastFrame;
    private ThreadRecorder threadRecorder;

    public Record(Context context, int imageWidth, int imageHeight, int deviceId) {
        this.context = context;
        initRecorder(imageWidth, imageHeight, deviceId);
    }

    //---------------------------------------
    // initialize ffmpeg_recorder
    //---------------------------------------
    private void initRecorder(int imageWidth, int imageHeight, int deviceId) {

        if (yuvIplimage == null) {
            yuvIplimage = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_8U, 4);
        }

        strVideoPath = Util.createFinalPath(context, deviceId);
        recorder = new FFmpegFrameRecorder(strVideoPath, imageWidth, imageHeight, 1);
        recorder.setFormat("mp4");
        recorder.setFrameRate(frameRate);
    }

    /**
     * start sip1303 recording
     *
     * @param videoIOSocket
     */
    public void startRecording(VideoIOSocket videoIOSocket) {
        videoIOSocket.registerReceiveFrameDataListener(this);
        startRecord();
    }

    /**
     * start zhiyun recording
     */
    public void startRecording(Camera camera, int mAvChannel) {
        camera.registerIOTCListener(this);
        startRecord();
    }

    /**
     * start mjpeg recording
     */
    public void startRecording(MjpegView mjpegView) {
        mjpegView.registerMjpegFrameListener(this);
        startRecord();
    }

    /**
     * start sip1018 recording
     */
    public void startRecording(Sip1018Camera sip1018Camera) {
        sip1018Camera.registerReceiveFrameDataListener(this);
        startRecord();
    }

    /**
     * stop sip1303 recording
     *
     * @param videoIOSocket
     */
    public void stopRecording(VideoIOSocket videoIOSocket) {
        videoIOSocket.unRegisterReceiveFrameDataListener(this);
        stopRecord();
    }

    /**
     * stop sip1018 recording
     */
    public void stopRecording(Sip1018Camera sip1018Camera) {
        sip1018Camera.unRegisterReceiveFrameDataListener(this);
        stopRecord();
    }

    /**
     * stop zhiyun recording
     *
     * @param camera
     */
    public void stopRecording(Camera camera) {
        camera.unregisterIOTCListener(this);
        stopRecord();
    }

    /**
     * stop mjpeg recording
     *
     * @param mjpegView
     */
    public void stopRecording(MjpegView mjpegView) {
        mjpegView.unRegisterFrameListener();
        stopRecord();
    }

    /**
     * 向系统注册我们录制的视频文件，这样文件才会在sd卡中显示
     */
    private void registerVideo() {
        Uri videoTable = Uri.parse(VIDEO_CONTENT_URI);
        Util.videoContentValues.put(MediaStore.Video.Media.SIZE, new File(strVideoPath).length());
        try {
            uriVideoPath = context.getContentResolver().insert(videoTable, Util.videoContentValues);
        } catch (Throwable e) {
            uriVideoPath = null;
            strVideoPath = null;
            e.printStackTrace();
        } finally {
        }
//        Util.videoContentValues = null;
    }

    private void record(Bitmap bitmap) {
        /* get video data */
        if (yuvIplimage != null && recording && bitmap != null) {
            bitmap.copyPixelsToBuffer(yuvIplimage.getByteBuffer());
            try {
                long t = 1000 * (System.currentTimeMillis() - startTime);
                if (t > recorder.getTimestamp()) {
                    recorder.setTimestamp(t);
                }
                recorder.record(yuvIplimage);
            } catch (FFmpegFrameRecorder.Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * callback for sip1303 frame data
     *
     * @param bitmap
     */
    @Override
    public void receiveFrameData(Bitmap bitmap) {
        mLastFrame = bitmap;
    }

    /**
     * callback for zhiyun frame data
     *
     * @param camera
     * @param avChannel
     * @param bmp
     */
    @Override
    public void receiveFrameData(Camera camera, int avChannel, Bitmap bmp) {
//        if (avChannel == mAvChannel) {
        mLastFrame = bmp;
//        }
    }

    @Override
    public void receiveFrameInfo(Camera camera, int avChannel, long bitRate, int frameRate, int onlineNm, int frameCount, int incompleteFrameCount) {

    }

    @Override
    public void receiveSessionInfo(Camera camera, int resultCode) {

    }

    @Override
    public void receiveChannelInfo(Camera camera, int avChannel, int resultCode) {

    }

    @Override
    public void receiveIOCtrlData(Camera camera, int avChannel, int avIOCtrlMsgType, byte[] data) {

    }

    /**
     * callback for mjpegFrame data
     *
     * @param bitmap
     */
    @Override
    public void receiveMjpegFrame(Bitmap bitmap) {
        mLastFrame = bitmap;
    }

    private void startRecord() {
        try {
            recorder.start();
            startTime = System.currentTimeMillis();
            recording = true;
            if (threadRecorder == null) {
                threadRecorder = new ThreadRecorder();
            }
            threadRecorder.start();
        } catch (FFmpegFrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRecord() {
        if (recorder != null && recording) {
            recording = false;
            try {
                if (threadRecorder != null) {
                    threadRecorder.interrupt();
                    threadRecorder.join();
                }
            } catch (InterruptedException e) {

            }
            threadRecorder = null;
            try {
                recorder.stop();
                recorder.release();
            } catch (FFmpegFrameRecorder.Exception e) {
                e.printStackTrace();
            }
        }
        registerVideo();
    }

    @Override
    public void receiveSip1018FrameData(Bitmap bitmap) {
        this.mLastFrame = bitmap;
    }

    private class ThreadRecorder extends Thread {

        @Override
        public void run() {
            super.run();
            while (recording) {
                record(mLastFrame);
            }
        }
    }

}


