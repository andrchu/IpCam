
package net.kaicong.ipcam.device.sip1018;

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
 * Created by LingYan on 2014/10/10.
 */

public class Sip1018Monitor extends SurfaceView implements SurfaceHolder.Callback, ReceiveSip1018FrameDataListener {

    public boolean mEnableDither = true;
    private SurfaceHolder mSurHolder = null;
    private Rect mRectCanvas;
    private Paint mPaint = new Paint();
    private Bitmap mLastFrame;

    private ThreadRender mThreadRender = null;
    public int videoWidth = 0;
    public int videoHeight = 0;
    public double monitorRatio = 0;

    public Sip1018Monitor(Context context, AttributeSet attrs) {
        super(context, attrs);
        mSurHolder = getHolder();
        mSurHolder.addCallback(this);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //注意，surfaceChanged方法会在surfaceView初始化和转屏时调用，width和height的大小由surfaceView的布局来控制的
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

    public Bitmap getSnapShot() {
        return mLastFrame;
    }

    public void surfaceCreated(SurfaceHolder holder) {

    }

    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void attachCamera(Sip1018Camera sip1018Camera) {

        sip1018Camera.registerReceiveFrameDataListener(this);
        if (mThreadRender == null) {
            mThreadRender = new ThreadRender();
            mThreadRender.start();
        }
    }

    public void deattachCamera(Sip1018Camera sip1018Camera) {

        sip1018Camera.unRegisterReceiveFrameDataListener(this);
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

    @Override
    public void receiveSip1018FrameData(Bitmap bitmap) {
        this.mLastFrame = bitmap;
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
