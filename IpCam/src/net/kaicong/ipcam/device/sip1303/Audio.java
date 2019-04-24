package net.kaicong.ipcam.device.sip1303;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.decoder.util.DecG726;

import net.kaicong.ipcam.utils.LogUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by LingYan on 14-12-8.
 */
public class Audio {

    public static final int MAX_AUDIO_BUFFER_SIZE = 1024;

    private Socket mSocket;// socket连接对象
    private DataOutputStream out;
    private DataInputStream in;// 输入流
    //超时10s
    private int timeOut = 1000 * 10;

    private String ip;
    private int port;
    private String account;
    private String password;

    private volatile boolean stopReceiveAudio = false;
    private int videoInfo[] = new int[4];
    private ReceiveDecodeAudioPacketThread receiveAudioPacketThread;
    private AudioTrack audioTrack = null;
    private boolean initAudioTrack = false;

    public Audio(String ip, int port, String account, String password) {
        this.ip = ip;
        this.port = port;
        this.account = account;
        this.password = password;
        mSocket = new Socket();
    }

    public void startPlayAudio() {
        if (receiveAudioPacketThread == null) {
            receiveAudioPacketThread = new ReceiveDecodeAudioPacketThread();
        }
        receiveAudioPacketThread.start();
    }

    private void initAudioTrack(int audioCodec) {
        int simpleRateInHz = 8000;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        if (!initAudioTrack && (audioCodec == RtspPacket.PT_G726 || audioCodec == RtspPacket.PT_G711)) {
            int bufSize = AudioTrack.getMinBufferSize(simpleRateInHz, channelConfig, audioFormat);
            if (bufSize == AudioTrack.ERROR_BAD_VALUE || bufSize == AudioTrack.ERROR) {
                LogUtil.d("chu", "bad audioTrack min size");
                return;
            }
            LogUtil.d("chu", "AudioTrack minBufSize" + bufSize);
            try {
                audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, simpleRateInHz,
                        channelConfig, audioFormat, bufSize, AudioTrack.MODE_STREAM);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return;
            }
            if (audioCodec == RtspPacket.PT_G726) {
                DecG726.g726_dec_state_create((byte) DecG726.G726_16, DecG726.FORMAT_LINEAR);
            }
//            audioTrack.setStereoVolume(1.0f, 1.0f);
            audioTrack.play();
            initAudioTrack = true;
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
            sendHttpHeader(RtspPacket.STREAM_TYPE6, RtspPacket.CHANNEL1, ip, port, account, password);
        } else {
            disconnect();
        }
    }

    public void stopListen() {
        if (receiveAudioPacketThread != null) {
            stopReceiveAudio = true;
            try {
                receiveAudioPacketThread.join();
                receiveAudioPacketThread.interrupt();
            } catch (InterruptedException e) {

            } finally {
                LogUtil.d("chu", "sip1303 receive audio thread exit");
                receiveAudioPacketThread = null;
            }
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
            stopListen();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out = null;
            in = null;
            mSocket = null;// 置空socket对象
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
        getHttpHead();
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
            LogUtil.d("chu", "audio format=" + (audioFormat == RtspPacket.PT_G726 ? "G726" : "G711a"));
        } catch (NumberFormatException e) {
            LogUtil.d("chu", "number format exception");
        } finally {
            receive();
        }
    }

    private void receive() {
        int audioCodec = videoInfo[3];
        byte headBuf[] = new byte[RtspPacket.RTSP_PACKET_SIZE];
        byte g726OutBuf[] = new byte[MAX_AUDIO_BUFFER_SIZE];
        long g726OutBufLen[] = new long[1];
        CMG711 decoder = new CMG711();
        while (!stopReceiveAudio) {
            //rtsp头
            try {
                in.readFully(headBuf);
            } catch (Exception e) {
                LogUtil.d("chu", "--receive audio header buf exception--");
                continue;
            }
            int pt = RtspPacket.getPayloadType(headBuf);
            int payloadLen = RtspPacket.getPayloadLength(headBuf);
            //音频数据还要减去4字节的私有数据头
            int pack_len = payloadLen - RtspPacket.RTP_PACKET_SIZE - 4;
            if (pt == RtspPacket.PT_G726 || pt == RtspPacket.PT_G711) {
                byte packBuf[] = new byte[pack_len];
                //h264码流
                try {
                    in.readFully(packBuf);
                } catch (Exception e) {
                    LogUtil.d("chu", "--receive audio media buf exception--");
                    continue;
                }
                initAudioTrack(audioCodec);
                if (audioCodec == RtspPacket.PT_G726) {
                    DecG726.g726_decode(packBuf, pack_len, g726OutBuf, g726OutBufLen);
                    audioTrack.write(g726OutBuf, 0, (int) g726OutBufLen[0]);
                }
                if (audioCodec == RtspPacket.PT_G711) {
                    LogUtil.d("chu", "--g711--");
                    byte g711aBuf[] = new byte[pack_len * 2];
                    decoder.decode(packBuf, 0, pack_len, g711aBuf);
                    audioTrack.write(g711aBuf, 0, g711aBuf.length);
                }
            }
        }
        if (initAudioTrack) {
            if (audioCodec == RtspPacket.PT_G726) {
                DecG726.g726_dec_state_destroy();
            }
        }
        initAudioTrack = false;
        System.gc();
    }

    /**
     * 接收音频数据
     */
    private class ReceiveDecodeAudioPacketThread extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                connect();
            } catch (Exception e) {

            } finally {

            }
        }
    }

}
