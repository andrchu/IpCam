package net.kaicong.ipcam.device.timer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import net.kaicong.ipcam.device.sip1303.ReceiveSip1303FrameDataListener;

import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * Created by LingYan on 15/5/7.
 */
public class TimerCamera {

    private Timer timer;
    private String url;
    private List<ReceiveSip1303FrameDataListener> mReceiveDataListeners = Collections.synchronizedList(new Vector<ReceiveSip1303FrameDataListener>());
    private Bitmap mBitmap;
    private String account;
    private String password;

    public TimerCamera(Context context, String url, String account, String password, ImageView imageView) {
        this.url = url;
        this.account = account;
        this.password = password;
        timer = new Timer();
    }

    //注册接口
    public boolean registerReceiveFrameDataListener(ReceiveSip1303FrameDataListener receiveSip1303FrameDataListener) {
        boolean result = false;
        if (!mReceiveDataListeners.contains(receiveSip1303FrameDataListener)) {
            mReceiveDataListeners.add(receiveSip1303FrameDataListener);
            result = true;
        }
        return result;
    }

    //移除接口
    public boolean unRegisterReceiveFrameDataListener(ReceiveSip1303FrameDataListener receiveSip1303FrameDataListener) {
        boolean result = false;
        if (mReceiveDataListeners.contains(receiveSip1303FrameDataListener)) {
            mReceiveDataListeners.remove(receiveSip1303FrameDataListener);
            result = true;
        }
        return result;
    }

    public void getImage() {

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                mBitmap = getImage(url);
                synchronized (mReceiveDataListeners) {
                    for (int i = 0; i < mReceiveDataListeners.size(); i++) {
                        ReceiveSip1303FrameDataListener receiveSip1303FrameDataListener = mReceiveDataListeners.get(i);
                        receiveSip1303FrameDataListener.receiveFrameData(mBitmap);
                    }

                }

            }

        }, 0, 1000 / 4);

    }

    public Bitmap getImage(URL url) {
        HttpURLConnection connection = null;
        try {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(account, password.toCharArray());
                }
            });
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                return BitmapFactory.decodeStream(connection.getInputStream());
            } else
                return null;
        } catch (Exception e) {
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public Bitmap getImage(String urlString) {
        try {
            URL url = new URL(urlString);
            return getImage(url);
        } catch (MalformedURLException e) {
            return null;
        }
    }


    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public Bitmap getSnapshot() {
        return mBitmap;
    }

}
