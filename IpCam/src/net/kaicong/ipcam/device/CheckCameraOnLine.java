package net.kaicong.ipcam.device;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.device.cgi.CgiUtils;
import net.kaicong.ipcam.device.sip1018.ExpansionIpCamera;
import net.kaicong.ipcam.device.sip1018.MyIpCamera;
import net.kaicong.ipcam.device.sip1303.Sip1303Camera;
import net.kaicong.ipcam.device.zhiyun.MyCamera;
import net.kaicong.ipcam.utils.LogUtil;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.misc.objc.NSNotification;
import com.misc.objc.NSNotificationCenter;
import com.misc.objc.NSSelector;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;

import net.reecam.ipc.IpCamera;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * 添加设备时验证设备是否在线
 * Created by LingYan on 2014/11/29 0029.
 */
public class CheckCameraOnLine implements IRegisterIOTCListener {

    private OnCheckFinishListener onCheckFinishListener = null;
    private MyCamera mCamera;

    public CheckCameraOnLine(OnCheckFinishListener onCheckFinishListener) {
        this.onCheckFinishListener = onCheckFinishListener;
    }

    /**
     * Sip1018设备在线检验
     *
     * @param cameraName
     * @param cameraIp
     * @param cameraPort
     * @param cameraAccount
     * @param cameraPassword
     */
    public void checkSip1018(String cameraName, String cameraIp, String cameraPort,
                             String cameraAccount, String cameraPassword) {

        IpCamera ipCamera = new IpCamera("", cameraName, cameraIp, cameraPort, cameraAccount, cameraPassword, MyIpCamera.DEFAULT_AUDIO_BUFFER_TIME);
        ipCamera.start();//开始连接
        NSNotificationCenter nc = NSNotificationCenter.defaultCenter();
        nc.addObserver(this, new NSSelector("cameraStatusChanged"),
                ExpansionIpCamera.IPCamera_CameraStatusChanged_Notification, ipCamera);
    }

    /**
     * 关闭Sip1018连接
     *
     * @param ipCamera
     */
    private void closeSip1018Connection(IpCamera ipCamera) {
        NSNotificationCenter notificationCenter = NSNotificationCenter.defaultCenter();
        notificationCenter.removeObserver(this);
        ipCamera.stop();
    }

    /**
     * 检查智云设备是否在线
     *
     * @param uid
     * @param cameraName
     * @param cameraAccount
     * @param cameraPassword
     */
    public void checkZhiYun(String uid, String cameraName, String cameraAccount, String cameraPassword) {
        mCamera = new MyCamera(cameraName, uid, cameraAccount, cameraPassword);
        mCamera.registerIOTCListener(this);
        mCamera.connect(uid);
        mCamera.start(MyCamera.DEFAULT_AV_CHANNEL, cameraAccount, cameraPassword);
    }

    /**
     * 关闭智云连接
     *
     * @param mCamera
     */
    public void closeZhiYunConnection(MyCamera mCamera) {
        mCamera.unregisterIOTCListener(this);
        mCamera.disconnect();
    }

    /**
     * 检查1303设备是否在线,通过连接cgi请求来验证
     *
     * @param sip1303Camera
     */
    public void checkSip1303(Sip1303Camera sip1303Camera) {
        CgiControlParams params = new CgiControlParams(sip1303Camera.account, sip1303Camera.password);
        params.getHttpClient().get(CgiControlParams.getDDNSAttr(sip1303Camera.ip, sip1303Camera.port), new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Message message = handler.obtainMessage();
                message.what = 1;
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.d("chu", throwable.toString());
                Message message = handler.obtainMessage();
                message.what = 2;
                handler.sendMessage(message);
            }

        });
    }

    /**
     * Sip1601系列检测
     *
     * @param ip
     * @param port
     * @param account
     * @param password
     */
    public void checkSip1601(String ip, int port, String account, String password) {
        CgiControlParams params = new CgiControlParams(account, password);
        params.getHttpClient().get(CgiControlParams.checkSip1601Device(ip, port), new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Message message = handler.obtainMessage();
                message.what = 1;
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.d("chu", throwable.toString());
                Message message = handler.obtainMessage();
                message.what = 2;
                handler.sendMessage(message);
            }

        });
    }

    /**
     * @param ip
     * @param port
     * @param account
     * @param password
     */
    public void checkSip1211(String ip, int port, String account, String password) {
        CgiControlParams params = new CgiControlParams(account, password);
        params.getHttpClient().get(CgiControlParams.checkSip1211Device(ip, port, account, password), new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                JSONObject object = CgiUtils.convertCgiStr2Json(new String(bytes));
                if (object != null) {
                    int result = object.optInt("ret_check_user");
                    if (result >= 0) {
                        Message message = handler.obtainMessage();
                        message.what = 1;
                        handler.sendMessage(message);
                    }
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.d("chu", throwable.toString());
                Message message = handler.obtainMessage();
                message.what = 2;
                handler.sendMessage(message);
            }

        });
    }

    /**
     * @param ip
     * @param port
     * @param account
     * @param password
     */
    public void checkSip1406(String ip, int port, String account, String password) {
        CgiControlParams params = new CgiControlParams(account, password);
        params.getHttpClient().get(CgiControlParams.checkSip1406Device(ip, port, account, password), new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Message message = handler.obtainMessage();
                message.what = 1;
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.d("chu", throwable.toString());
                Message message = handler.obtainMessage();
                message.what = 2;
                handler.sendMessage(message);
            }

        });
    }

    /**
     * @param ip
     * @param port
     * @param account
     * @param password
     */
    public void checkSip1120(String ip, int port, String account, String password) {
        CgiControlParams params = new CgiControlParams(account, password);
        params.getHttpClient().get(CgiControlParams.checkSip1120Device(ip, port, account, password), new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Message message = handler.obtainMessage();
                message.what = 1;
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.d("chu", throwable.toString());
                Message message = handler.obtainMessage();
                message.what = 2;
                handler.sendMessage(message);
            }

        });
    }

    /**
     * ***************************************************************************
     * *                                                                                                                         *
     * Sip1018设备是否可用检查
     * <p/>
     * ****************************************************************************
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
            //线程run方法中的回调方法，应该用handler消息队列来更新UI操作
            closeSip1018Connection((IpCamera) note.object());
            Message message = handler.obtainMessage();
            message.what = 1;
            handler.sendMessage(message);
        }
        if (status.equals(CameraConstants.DISCONNECTED)) {
            closeSip1018Connection((IpCamera) note.object());
            Message message = handler.obtainMessage();
            message.what = 2;
            handler.sendMessage(message);
        }
    }

    /**
     * ***************************************************************************
     * *                                                                                                                         *
     * 智云设备是否可用检查
     * <p/>
     * ****************************************************************************
     */
    @Override
    public void receiveFrameData(Camera camera, int avChannel, Bitmap bmp) {

    }

    @Override
    public void receiveFrameInfo(Camera camera, int avChannel, long bitRate, int frameRate, int onlineNm, int frameCount, int incompleteFrameCount) {

    }

    @Override
    public void receiveSessionInfo(Camera camera, int resultCode) {
        LogUtil.d("chu", "receiveSessionInfo" + resultCode);
        if (camera == mCamera) {
            Message message = zhiyunHandler.obtainMessage();
            message.what = resultCode;
            zhiyunHandler.sendMessage(message);
        }
    }

    @Override
    public void receiveChannelInfo(Camera camera, int avChannel, int resultCode) {
        LogUtil.d("chu", "receiveChannelInfo" + resultCode);
        Bundle data = new Bundle();
        if (camera == mCamera) {
            Message message = zhiyunHandler.obtainMessage();
            message.what = resultCode;
            data.putBoolean("isReceiveChannel", true);
            message.setData(data);
            zhiyunHandler.sendMessage(message);
        }
    }

    @Override
    public void receiveIOCtrlData(Camera camera, int avChannel, int avIOCtrlMsgType, byte[] data) {

    }

    private Handler zhiyunHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Message message = handler.obtainMessage();
            boolean isReceiveChannel = msg.getData().getBoolean("isReceiveChannel", false);
            Bundle bundle = new Bundle();
            switch (msg.what) {
                case Camera.CONNECTION_STATE_CONNECTING:
                    //正在连接
                    break;
                case Camera.CONNECTION_STATE_CONNECTED:
                    //已连接
                    if (isReceiveChannel) {
                        closeZhiYunConnection(mCamera);
                        message.what = 1;
                        bundle.putInt("result_code", Camera.CONNECTION_STATE_CONNECTED);
                    }
                    break;

                case Camera.CONNECTION_STATE_UNKNOWN_DEVICE:
                    //未知设备
                    closeZhiYunConnection(mCamera);
                    message.what = 2;
                    bundle.putInt("result_code", Camera.CONNECTION_STATE_UNKNOWN_DEVICE);
                    break;

                case Camera.CONNECTION_STATE_DISCONNECTED:
                    //连接失败
                    closeZhiYunConnection(mCamera);
                    message.what = 2;
                    bundle.putInt("result_code", Camera.CONNECTION_STATE_DISCONNECTED);
                    break;

                case Camera.CONNECTION_STATE_TIMEOUT:
                    //连接超时
                    closeZhiYunConnection(mCamera);
                    message.what = 2;
                    bundle.putInt("result_code", Camera.CONNECTION_STATE_TIMEOUT);
                    break;

                case Camera.CONNECTION_STATE_WRONG_PASSWORD:
                    //密码错误
                    closeZhiYunConnection(mCamera);
                    message.what = 2;
                    bundle.putInt("result_code", Camera.CONNECTION_STATE_WRONG_PASSWORD);
                    break;

                case Camera.CONNECTION_STATE_CONNECT_FAILED:
                    //连接失败
                    closeZhiYunConnection(mCamera);
                    message.what = 2;
                    bundle.putInt("result_code", Camera.CONNECTION_STATE_CONNECT_FAILED);
                    break;
            }
            message.setData(bundle);
            handler.sendMessage(message);
        }

    };

    public interface OnCheckFinishListener {

        /**
         * @param isOnLine   是否连接上
         * @param resultCode 返回码
         */
        public void onCheckFinish(boolean isOnLine, int resultCode);

    }

    public void unRegisterListener() {
        this.onCheckFinishListener = null;
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                //连接成功
                if (onCheckFinishListener != null) {
                    onCheckFinishListener.onCheckFinish(true, 0);
                }
            } else if (msg.what == 2) {
                int resultCode = msg.getData().getInt("result_code");
                if (onCheckFinishListener != null) {
                    onCheckFinishListener.onCheckFinish(false, resultCode);
                }
            }
        }

    };

}
