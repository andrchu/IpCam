package net.kaicong.ipcam.device.mjpeg;

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

import java.io.IOException;

public class MjpegView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder holder;
    private Context saved_context;

    private MjpegViewThread thread;
    private ThreadRender threadRender;
    private MjpegInputStream mIn = null;
    private volatile boolean mRun = false;
    private boolean surfaceDone = false;

    private boolean suspending = false;

    private Bitmap mLastFrame = null;
    private Rect destRect = null;
    private float videoRadio = 0;

    private ReceiveMjpegFrameListener receiveMjpegFrameListener;

    public MjpegView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        saved_context = context;
        holder.addCallback(this);
    }

    public void registerMjpegFrameListener(ReceiveMjpegFrameListener receiveMjpegFrameListener) {
        this.receiveMjpegFrameListener = receiveMjpegFrameListener;
    }

    public void unRegisterFrameListener() {
        this.receiveMjpegFrameListener = null;
    }

    public class MjpegViewThread extends Thread {

        public void run() {
            while (mRun) {
                try {
                    mLastFrame = mIn.readMjpegFrame();
                    if (mLastFrame == null || mLastFrame.isRecycled()) {
                        continue;
                    }
                    if (receiveMjpegFrameListener != null) {
                        receiveMjpegFrameListener.receiveMjpegFrame(mLastFrame);
                    }
                } catch (Exception e) {

                }
            }
            LogUtil.d("chu", "---mjpeg thread exit---");
        }
    }

    private class ThreadRender extends Thread {

        private SurfaceHolder mSurfaceHolder;

        public ThreadRender(SurfaceHolder surfaceHolder) {
            mSurfaceHolder = surfaceHolder;
        }

        public void run() {
            Paint p = new Paint();

            while (mRun) {
                Canvas c = null;
                if (surfaceDone) {
                    try {
                        c = mSurfaceHolder.lockCanvas();
                        c.drawColor(Color.BLACK);
                        synchronized (mSurfaceHolder) {
                            c.drawBitmap(mLastFrame, null, destRect, p);
                        }
                    } catch (Exception e) {

                    } finally {
                        if (c != null) mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
            LogUtil.d("chu", "---mjpeg thread exit---");
        }

    }

    public void startPlayback() {
        if (mIn != null) {
            mRun = true;
            if (thread == null) {
                thread = new MjpegViewThread();
            }
            if (threadRender == null) {
                threadRender = new ThreadRender(holder);
            }
            thread.start();
            threadRender.start();
        }
    }

    public void resumePlayback() {
        if (suspending) {
            if (mIn != null) {
                mRun = true;
                SurfaceHolder holder = getHolder();
                holder.addCallback(this);
                thread = new MjpegViewThread();
                thread.start();
                threadRender = new ThreadRender(holder);
                threadRender.start();
                suspending = false;
            }
        }
    }

    public void stopPlayback() {
        if (mRun) {
            suspending = true;
        }
        mRun = false;
        if (thread != null && threadRender != null) {
            try {
                thread.interrupt();
                thread.join();
                threadRender.interrupt();
                threadRender.join();
            } catch (InterruptedException e) {
            }
            thread = null;
            threadRender = null;
        }
        if (mIn != null) {
            try {
                mIn.close();
            } catch (IOException e) {
            }
            mIn = null;
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int f, int w, int h) {

        synchronized (holder) {
            destRect = destRect(w, h);
            surfaceDone = true;
        }

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceDone = false;
        stopPlayback();
    }

    public void surfaceCreated(SurfaceHolder holder) {

    }

    public void setVideoRadio(float videoRadio) {
        this.videoRadio = videoRadio;
    }

    public void setSource(MjpegInputStream source) {
        mIn = source;
        if (!suspending) {
            startPlayback();
        } else {
            resumePlayback();
        }
    }

    private Rect destRect(int dispWidth, int dispHeight) {
        int tempx;
        int tempy;
        int bmpWidth = 0;
        int bmpHeight = 0;
        //我的设备播放器界面位置适配
        bmpWidth = dispWidth;
        bmpHeight = (int) (dispWidth / videoRadio);
        if (bmpHeight > dispHeight) {
            bmpHeight = dispHeight;
            bmpWidth = (int) (dispHeight * videoRadio);
        }
        tempx = (dispWidth / 2) - (bmpWidth / 2);
        tempy = (dispHeight / 2) - (bmpHeight / 2);
        return new Rect(tempx, tempy, bmpWidth + tempx, bmpHeight + tempy);
    }

    public boolean isStreaming() {
        return mRun;
    }

    public Bitmap getSnapShot() {
        return mLastFrame;
    }

}
