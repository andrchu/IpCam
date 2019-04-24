package net.kaicong.ipcam.device.sip1406;

import android.graphics.Bitmap;

import com.decoder.util.DecH264;

import net.kaicong.ipcam.device.sip1303.ReceiveSip1303FrameDataListener;
import net.kaicong.ipcam.device.sip1303.RtspPacket;
import net.kaicong.ipcam.device.sip1303.VideoIOListener;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.kcalipay.Base64;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * Created by LingYan on 15/5/6.
 */
public class Sip1406Camera {

    public static final int MAX_FRAME_BUF = 4 * 640 * 480;

    private Socket mSocket;// socket连接对象
    private DataOutputStream out;
    private DataInputStream in;// 输入流

    private String ip;
    private int port;
    private String account;
    private String password;

    private Bitmap bitmap;
    private VideoIOListener videoIOListener;
    //超时10s
    private int timeOut = 1000 * 10;

    private String headPattern;
    private boolean isStopReceive = true;
    private boolean isInit = false;
    public boolean mEnableDither = true;

    private ThreadConnect threadConnect;
    private ThreadReadPacket threadReadPacket;

    private List<ReceiveSip1303FrameDataListener> mReceiveDataListeners = Collections.synchronizedList(new Vector<ReceiveSip1303FrameDataListener>());

    public Sip1406Camera(String ip, int port, String account, String password, VideoIOListener videoIOListener) {
        mSocket = new Socket();
        this.videoIOListener = videoIOListener;
        this.ip = ip;
        this.port = port;
        this.account = account;
        this.password = password;

//        headPattern = "GET http://" + ip + ":" + port + "/livestream.cgi?loginuse=" +
//                account + "&loginpas=" + password + "&streamid=0 HTTP/1.1\r\n";

        headPattern = "GET http://" + ip + ":" + port + "/livestream.cgi?user=" +
                account + "&pwd=" + password + "&streamid=0 HTTP/1.1\r\n";

    }

    public void startConnect() {
        if (threadConnect == null) {
            threadConnect = new ThreadConnect();
            threadConnect.start();
        }
    }

    public void startShow() {
        if (threadReadPacket == null) {
            threadReadPacket = new ThreadReadPacket();
            threadReadPacket.start();
        }
        isStopReceive = false;
    }

    //连接socket线程
    private class ThreadConnect extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                connect();
            } catch (Exception e) {
//                disconnect();
                //连接超时
                if (videoIOListener != null) {
                    videoIOListener.videoSocketConnectFailed(Sip1406Camera.this);
                }
            }
        }
    }

    /**
     * 连接网络服务器
     *
     * @throws java.net.UnknownHostException
     * @throws java.io.IOException
     */
    private void connect() throws Exception {
        SocketAddress address = new InetSocketAddress(ip, port);
        mSocket.connect(address, timeOut);// 连接指定IP和端口
        if (mSocket.isConnected()) {
            out = new DataOutputStream(mSocket.getOutputStream());// 获取网络输出流
            in = new DataInputStream(mSocket.getInputStream());// 获取网络输入流
            sendHttpHeader();
            if (videoIOListener != null) {
                videoIOListener.videoSocketConnected(this);
            }
        }
    }


    /**
     * 发送媒体数据请求包
     *
     * @throws Exception
     */
    private void sendHttpHeader() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(headPattern);
        sb.append("Host: " + ip + "\r\n");
        sb.append("User-Agent: HiIpcam/V100R003 VodClient/1.0.0\r\n");
        String authStr = account + ":" + password;
        String authedStr = "Basic " + Base64.encode(authStr.getBytes());
        sb.append("Authorization: " + authedStr + "\r\n");
        sb.append("Connection: Keep-Alive\r\n");
        sb.append("\r\n\r\n");
        if (out != null) {
            out.write(sb.toString().getBytes());
        }
    }

    public class ThreadReadPacket extends Thread {

        byte headerBuf[] = new byte[Sip1406Packet.HEAD_SIZE];
        int width = 0;
        int height = 0;
        int[] framePara = new int[4];
        byte[] bufOut = new byte[MAX_FRAME_BUF];
        ByteBuffer byteBuffer;

        @Override
        public void run() {
            super.run();
            while (!isStopReceive) {
                try {
                    in.readFully(headerBuf);
                } catch (Exception e) {
                    continue;
                }
                int type = ((Byte) headerBuf[0]).intValue();
                int streamId = ((Byte) headerBuf[1]).intValue();
                int frameLen = Sip1406Packet.getPayloadLength(headerBuf, 9);
                if (streamId == 0) {
                    width = 640;
                    height = 480;
                } else if (streamId == 1) {
                    width = 320;
                    height = 240;
                }
                if (type == 0 || type == 1) {
                    byte frame[] = new byte[frameLen];
                    try {
                        in.readFully(frame);
                    } catch (Exception e) {
                        continue;
                    }
                    //解帧
                    if (!isInit) {
                        DecH264.InitDecoder();
                        isInit = true;
                    }

                    int ret = DecH264.DecoderNal(frame, frameLen, framePara, bufOut, mEnableDither);
                    if (ret < 0) {
                        continue;
                    }

                    //创建bitmap
                    if (bitmap == null) {
                        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    }
                    //通过包装的方法创建的缓冲区保留了被包装数组内保存的数据.
                    byteBuffer = ByteBuffer.wrap(bufOut);
                    bitmap.copyPixelsFromBuffer(byteBuffer);

                    synchronized (mReceiveDataListeners) {
                        for (int i = 0; i < mReceiveDataListeners.size(); i++) {
                            ReceiveSip1303FrameDataListener receiveSip1303FrameDataListener = mReceiveDataListeners.get(i);
                            receiveSip1303FrameDataListener.receiveFrameData(bitmap);
                        }
                    }

                }
            }
        }

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


}
