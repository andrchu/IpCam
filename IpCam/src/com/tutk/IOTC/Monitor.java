
package com.tutk.IOTC;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import net.kaicong.ipcam.utils.LogUtil;

/**
 * 智云的播放器，与TUTK工程的Monitor类改动较大
 * 1.符合看看看播放界面要求(转屏)
 * 2.将滑动控制改为按钮控制
 * 3.多次测试，尽量使代码最简洁
 * 4.将转屏控制移到Activity中，这要更好控制
 * Created by LingYan on 2014/10/10.
 */

public class Monitor extends SurfaceView implements SurfaceHolder.Callback, IRegisterIOTCListener {

    public boolean mEnableDither = false;
    private SurfaceHolder mSurHolder = null;

    //    private Rect mRectCanvas = new Rect(); // used for render image.
    private Rect mRectCanvas; // used for render image.
    private Paint mPaint = new Paint();
    private Bitmap mLastFrame;
    private Camera mCamera;
    private int mAVChannel = -1;

    private ThreadRender mThreadRender = null;
    public int videoWidth = 0;
    public int videoHeight = 0;
    public double monitorRatio = 0;

    public Monitor(Context context) {
        super(context);
        mSurHolder = getHolder();
        mSurHolder.addCallback(this);
    }

    public Monitor(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSurHolder = getHolder();
        mSurHolder.addCallback(this);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //注意，surfaceChanged方法会在surfaceView初始化和转屏时调用，width和height的大小由surfaceView的布局来控制的
        LogUtil.d("Monitor", "---surfaceChanged---" + "width=" + width + "---height---" + height);
        synchronized (this) {
            mRectCanvas = destRect(width, height);
        }
    }

    private Rect destRect(int dispWidth, int dispHeight) {
        int tempx;
        int tempy;
        int bmpWidth = 0;
        int bmpHeight = 0;
        //我的设备播放器界面位置适配
        bmpWidth = dispWidth;
        bmpHeight = (int) (dispWidth / monitorRatio);
        if (bmpHeight > dispHeight) {
            bmpHeight = dispHeight;
            bmpWidth = (int) (dispHeight * monitorRatio);
        }
        tempx = (dispWidth / 2) - (bmpWidth / 2);
        tempy = (dispHeight / 2) - (bmpHeight / 2);
        return new Rect(tempx, tempy, bmpWidth + tempx, bmpHeight + tempy);
    }

    public void surfaceCreated(SurfaceHolder holder) {

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void attachCamera(Camera camera, int avChannel) {

        mCamera = camera;
        mCamera.registerIOTCListener(this);
        mAVChannel = avChannel;

        if (mThreadRender == null) {
            mThreadRender = new ThreadRender();
            mThreadRender.start();
        }
    }

    public void deattachCamera() {

        mAVChannel = -1;

        if (mCamera != null) {
            mCamera.unregisterIOTCListener(this);
            mCamera = null;
        }

        if (mThreadRender != null) {
            mThreadRender.stopThread();
            try {
                mThreadRender.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                mThreadRender = null;
            }

        }
    }

    public void receiveFrameData(Camera camera, int avChannel, Bitmap bmp) {

        if (mAVChannel == avChannel) {
            mLastFrame = bmp;
        }

    }

    public void receiveFrameInfo(Camera camera, int sessionChannel, long bitRate, int frameRate, int onlineNm, int frameCount, int incompleteFrameCount) {

    }

    public void receiveChannelInfo(Camera camera, int sessionChannel, int resultCode) {

    }

    public void receiveSessionInfo(Camera camera, int resultCode) {

    }

    public void receiveIOCtrlData(Camera camera, int sessionChannel, int avIOCtrlMsgType, byte[] data) {

    }

    private class ThreadRender extends Thread {
        private boolean mIsRunningThread = false;
        private Object mWaitObjectForStopThread = new Object();

        public void stopThread() {
            mIsRunningThread = false;
            try {
                mWaitObjectForStopThread.notify();
            } catch (Exception e) {
            }
        }

        @Override
        public void run() {
            mIsRunningThread = true;
            Canvas videoCanvas = null;
            if (!mPaint.isDither() && mEnableDither) {
                LogUtil.i("IOTCamera", "==== Enable Dithering ==== !!!This will decrease FPS.");
                mPaint.setDither(mEnableDither);
            } else if (mPaint.isDither() && !mEnableDither) {
                LogUtil.i("IOTCamera", "==== Disable Dithering ====");
                mPaint.setDither(mEnableDither);
            }
            while (mIsRunningThread) {
                if (mLastFrame != null && !mLastFrame.isRecycled()) {
                    try {
                        videoCanvas = mSurHolder.lockCanvas();
                        if (videoCanvas != null) {
                            videoCanvas.drawColor(Color.BLACK);
                            try {
                                videoCanvas.drawBitmap(mLastFrame, null, mRectCanvas, mPaint);
                            } catch (Exception e) {

                            }
                        }
                    } finally {
                        if (videoCanvas != null)
                            mSurHolder.unlockCanvasAndPost(videoCanvas);
                        videoCanvas = null;
                    }
                }
                try {
                    synchronized (mWaitObjectForStopThread) {
                        mWaitObjectForStopThread.wait(33);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            LogUtil.i("IOTCamera", "===ThreadRender exit===");
        }
    }
}
