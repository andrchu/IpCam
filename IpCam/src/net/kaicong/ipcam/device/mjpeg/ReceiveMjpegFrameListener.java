package net.kaicong.ipcam.device.mjpeg;

import android.graphics.Bitmap;

/**
 * Created by LingYan on 15/2/2.
 */
public interface ReceiveMjpegFrameListener {

    public void receiveMjpegFrame(Bitmap bitmap);

}
