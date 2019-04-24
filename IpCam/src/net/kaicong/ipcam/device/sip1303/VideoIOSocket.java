package net.kaicong.ipcam.device.sip1303;

import android.graphics.Bitmap;

import com.decoder.util.DecH264;

import net.kaicong.ipcam.utils.LogUtil;

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
 * Created by LingYan on 2014/11/5 0005.
 */
public class VideoIOSocket {

    public static final int MAX_FRAME_BUF = 4 * 1280 * 720;

    private Socket mSocket;// socket连接对象
    private DataOutputStream out;
    private DataInputStream in;// 输入流
    //连接状态回调接口
    private VideoIOListener videoIOListener;
    private List<ReceiveSip1303FrameDataListener> mReceiveDataListeners = Collections.synchronizedList(new Vector<ReceiveSip1303FrameDataListener>());
    //超时10s
    private int timeOut = 1000 * 10;
    /**
     * videoInfo[0] video width
     * videoInfo[1] video height
     * videoInfo[2] video format
     * videoInfo[3] audio format
     */
    private int videoInfo[] = new int[4];
    public boolean mEnableDither = true; //The major switch to use RGB565 Non-Dither or ARGB8888 Dither to display

    private SocketConnThread socketConnThread = null;
    private ReceiveVideoStreamInfoThread receiveStreamInfoThread = null;
    private ReceiveVideoPacketThread receivePacketThread = null;

    private String ip;
    private int port;
    private String account;
    private String password;

    private boolean isInit = false;

    private volatile boolean stopReceiveVideo = false;

    public int channelTag = 0;

    private Bitmap pic_bitmap;

    public VideoIOSocket(String ip, int port, String account, String password, VideoIOListener videoIOListener) {
        mSocket = new Socket();
        this.videoIOListener = videoIOListener;
        this.ip = ip;
        this.port = port;
        this.account = account;
        this.password = password;
    }

    //连接socket线程
    private class SocketConnThread extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                connect();
            } catch (Exception e) {
                disconnect();
                //连接超时
                if (videoIOListener != null) {
                    videoIOListener.videoSocketConnectFailed(VideoIOSocket.this);
                }
            }
        }
    }

    /**
     * 发送数据，接受视频http响应包
     */
    private class ReceiveVideoStreamInfoThread extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                sendHttpHeader(RtspPacket.STREAM_TYPE5, RtspPacket.CHANNEL2, ip, port, account, password);
                getHttpHead();
            } catch (Exception e) {

            }
        }
    }

    /**
     * 接受视频数据并解码
     */
    private class ReceiveVideoPacketThread extends Thread {

        @Override
        public void run() {
            super.run();
            byte headerBuf[] = new byte[RtspPacket.RTSP_PACKET_SIZE];

            int[] framePara = new int[4];
            byte[] bufOut = new byte[MAX_FRAME_BUF];
            int videoWidth = 0;
            int videoHeight = 0;
            Bitmap bitmap = null;
            ByteBuffer byteBuffer;

            stopReceiveVideo = false;

            while (!stopReceiveVideo) {
                //rtsp头
                try {
                    in.readFully(headerBuf);
                } catch (Exception e) {
//                    LogUtil.d("chu", "--read full header exception--" + e.toString());
//                    if (videoIOListener != null) {
//                        videoIOListener.getPacketException();
//                    }
                    continue;
                }
                //paylaod type
                int pt = RtspPacket.getPayloadType(headerBuf);
                //payload len
                int payloadLen = RtspPacket.getPayloadLength(headerBuf);
                //nal len
                int pack_len = payloadLen - RtspPacket.RTP_PACKET_SIZE;
                if (pt == RtspPacket.PT_H264) {
                    byte packBuf[] = new byte[pack_len];
                    //h264码流
                    try {
                        in.readFully(packBuf);
                    } catch (Exception e) {
//                        if (videoIOListener != null) {
//                            videoIOListener.getPacketException();
//                        }
//                        LogUtil.d("chu", "--read full media exception--");
//                        break;
                        continue;
                    }

                    if (!isInit) {
                        DecH264.InitDecoder();
                        isInit = true;
                    }

                    int ret = DecH264.DecoderNal(packBuf, pack_len, framePara, bufOut, mEnableDither);
                    if (ret < 0) {
//                        LogUtil.d("chu", "decode error");
                        continue;
                    }
                    videoWidth = framePara[2];
                    videoHeight = framePara[3];

                    //创建bitmap
                    if (bitmap == null) {
                        bitmap = Bitmap.createBitmap(videoWidth, videoHeight, Bitmap.Config.ARGB_8888);
                    }
                    //通过包装的方法创建的缓冲区保留了被包装数组内保存的数据.
                    byteBuffer = ByteBuffer.wrap(bufOut);
                    bitmap.copyPixelsFromBuffer(byteBuffer);
                    pic_bitmap = bitmap;
                    synchronized (mReceiveDataListeners) {
                        for (int i = 0; i < mReceiveDataListeners.size(); i++) {
                            ReceiveSip1303FrameDataListener receiveSip1303FrameDataListener = mReceiveDataListeners.get(i);
                            receiveSip1303FrameDataListener.receiveFrameData(bitmap);
                        }
                    }

                }
            }
            LogUtil.d("chu", "sip1303 receive video thread exit");
            System.gc();
        }
    }

    /**
     * 发送媒体数据请求包
     *
     * @param streamType
     * @param channel
     * @param ip
     * @param port
     * @param account
     * @param password
     * @throws Exception
     */
    private void sendHttpHeader(String streamType, int channel, String ip, int port, String account, String password) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("GET http://" + ip + ":" + port + "/livestream/" + channel + "?action=play&media=" + streamType + " HTTP/1.1\r\n");
        sb.append("User-Agent: HiIpcam/V100R003 VodClient/1.0.0\r\n");
        sb.append("Connection: Keep-Alive\r\n");
        sb.append("Cache-Control: no-cache\r\n");
        sb.append("Authorization: " + account + " " + password + "\r\n");
        StringBuilder sbContent = new StringBuilder();
        sbContent.append("Cseq: 1\r\n");
        sbContent.append("Transport: RTP/AVP/TCP;unicast;interleaved=0-1\r\n\r\n");
        sb.append("Content-Length: " + sbContent.length() + "\r\n");
        sb.append(sbContent);
        sb.append("\r\n");
        if (out != null) {
            out.write(sb.toString().getBytes());
        }
    }

    /**
     * @param headBufStr
     */
    private void getVideoInfo(String headBufStr) {
        int videoIndex = headBufStr.indexOf("m=video");
        int audioIndex = headBufStr.indexOf("m=audio");
        //视频信息
        String videoStr = headBufStr.substring(videoIndex, audioIndex);
        int videoStrIndex = videoStr.lastIndexOf('/');
        try {
            int videoHeight = Integer.parseInt(videoStr.substring(videoStrIndex).replace("\r\n", "").replace("/", ""));
            int videoWidth = Integer.parseInt(videoStr.substring(videoStr.substring(0, videoStrIndex).lastIndexOf('/'), videoStrIndex).replace("/", ""));
            int videoFormatIndex = videoStr.lastIndexOf(" ");
            int videoFormat = Integer.parseInt(videoStr.substring(0, videoFormatIndex).replace("m=video ", ""));
            //音频信息
            String audioStr = headBufStr.substring(audioIndex, headBufStr.indexOf("Transport"));
            int audioFormatIndex = audioStr.lastIndexOf(" ");
            int audioFormat = Integer.parseInt(audioStr.substring(0, audioFormatIndex).replace("m=audio ", ""));
            videoInfo[0] = videoWidth;//视频宽度
            videoInfo[1] = videoHeight;//视频高度
            videoInfo[2] = videoFormat;//视频编码方式
            videoInfo[3] = audioFormat;//音频编码方式
            LogUtil.d("chu", "video width=" + videoWidth);
            LogUtil.d("chu", "audio format=" + (audioFormat == RtspPacket.PT_G726 ? "G726" : "G711a"));
        } catch (NumberFormatException e) {
            LogUtil.d("chu", "number format exception");
        } finally {
            if (videoIOListener != null) {
                videoIOListener.getVideoInfoSuccess(this, videoInfo);
            }
        }
    }

    //先通过socket接受http包，然后获取到视音频信息
    private void getHttpHead() throws Exception {
        if (in != null) {
            char headBuf[] = new char[512];
            int i = 0;
            int n = 0;
            while (true) {
                char result = (char) in.read();
                headBuf[i] = result;
                if ((i > 1) && (headBuf[i] == '\n') && (headBuf[i - 1] == '\r')) {
                    if (n == 0) {
                        String str = new String(headBuf);
                        if (str.indexOf("200 OK") == -1) {
                            LogUtil.d("chu", "结果错误");
                            break;
                        }
                        n++;
                    } else if (n == 1) {
                        if ((headBuf[i - 2] == '\n') && (headBuf[i - 3] == '\r'))
                            n++;
                    } else {
                        //最后四个字符是"\r\n\r\n",http响应包结束
                        if ((headBuf[i - 2] == '\n') && (headBuf[i - 3] == '\r')) {
                            String httpPacket = new String(headBuf);
                            getVideoInfo(httpPacket);
                            break;
                        }
                    }
                }
                i++;
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
            if (videoIOListener != null) {
                videoIOListener.videoSocketConnected(this);
            }
        }
    }

    public boolean isSocketConnected() {
        return mSocket.isConnected();
    }

    /**
     * 开启连接线程
     */
    public void startConnect() {
        if (socketConnThread == null) {
            socketConnThread = new SocketConnThread();
            socketConnThread.start();
        }
    }

    /**
     * 断开连接
     *
     * @throws java.io.IOException
     */
    public void disconnect() {
        try {
            if (mSocket != null) {
                if (!mSocket.isInputShutdown()) {
                    mSocket.shutdownInput();
                }
                if (!mSocket.isOutputShutdown()) {
                    mSocket.shutdownOutput();
                }
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                mSocket.close();// 关闭socket
            }
            if (socketConnThread != null) {
                socketConnThread.interrupt();
                socketConnThread.join();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (videoIOListener != null) {
                videoIOListener.videoSocketDisconnected(this);
            }
            stopReceiveHttpHeaderThread();
            socketConnThread = null;
            out = null;
            in = null;
            mSocket = null;// 置空socket对象
        }
    }

    /**
     * 开启接受http包的线程
     */
    public void startReceiveVideoHttpHeader() {
        if (receiveStreamInfoThread == null) {
            receiveStreamInfoThread = new ReceiveVideoStreamInfoThread();
            receiveStreamInfoThread.start();
        }
    }

    //关闭线程
    private void stopReceiveHttpHeaderThread() {
        if (receiveStreamInfoThread != null) {
            try {
                receiveStreamInfoThread.interrupt();
                receiveStreamInfoThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                receiveStreamInfoThread = null;
            }

        }
    }

    /**
     * 开启线程，socket接受buf数据
     */
    public void startReceiveVideoPacket() {
        if (receivePacketThread == null) {
            receivePacketThread = new ReceiveVideoPacketThread();
            receivePacketThread.start();
        }
    }

    /**
     * 断开视频连接
     */
    public void stopShow() {
        stopReceiveVideo = true;
        receivePacketThread = null;
        System.gc();
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

    public Bitmap get1303Comment_Pic() {
        if (socketConnThread != null) {
            return pic_bitmap;
        }
        return null;
    }
}
