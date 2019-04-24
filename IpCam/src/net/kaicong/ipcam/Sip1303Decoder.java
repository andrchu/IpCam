package net.kaicong.ipcam;

import android.view.Surface;

/**
 * @author LingYan
 */

public class Sip1303Decoder {

    static {
        try {
            System.loadLibrary("net/kaicong");
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
    }

    // 错误码
    public static final int SOCKET_CONNECT_FAILED = -1; // socket链接失败
    public static final int SOCKET_BAD_IP_ADDRESS = -2; // ip地址格式不正确
    public static final int SOCKET_SEND_REQUEST_ERROR = -3; // 发送参数错误
    public static final int SOCKET_GET_HTTP_HEAD_ERROR = -4; // 获取http头错误
    public static final int SOCKET_GET_STREAMING_HEAD_ERROR = -5; // 获取rtp数据流包头错误
    public static final int SOCKET_MALLOC_RECV_BUFFER_ERROR = -6; // 分配buf内存失败
    public static final int SOCKET_CREATE_PTHREAD_ERROR = -7; // 创建播放线程失败

    public static final int DECODE_INIT_ERROR = -101; // 初始化失败
    public static final int DECODE_ANDROIDBITMAP_LOCKPIXELS_ERROR = -102; // lockPixels失败
    public static final int DECODE_NAL_BUF_IS_NULL = -103; // 传递的nal包为空
    public static final int DECODE_DECODE_VIDEO_FAILED = -104; // 解码失败

    /**
     * Initialize a socket tcp ip protocol-based connectivity
     *
     * @param ip
     * @param port
     * @param account
     * @param password
     * @return
     */
    public static native int initVideo(String ip, int port, String account,
                                       String password);

    /**
     * Creating a pthread, perform socket data reception, ffmpeg decoding,
     * surfaceView Play
     *
     * @return
     */
    public static native int playVideo();

    /**
     * The amount of thread while loop flag is set to 0, the end of the cycle,
     * and exit thread
     *
     * @return
     */
    public static native int stopVideo();

    /**
     * When you exit the program closes the socket connection and release
     * resources
     *
     * @return
     */
    public static native int closeVideo();

    /**
     * Initialize a h264 decoder
     *
     * @return
     */
    public static native int initH264();

    /**
     * Release resources created by ffmpeg,Executed after the play thread exit
     *
     * @return
     */
    public static native int releaseDecoder();

    /**
     * In onSurfaceChanged method it calls the method in the implementation of
     * the ffmpeg initialization,and set format and size of window buffer
     *
     * @param pSurface
     * @return
     */
    public static native int setSurface(Surface pSurface, int width, int height);

    /**
     * According to obtain the video size to create a bitmap
     *
     * @param mWidth  According to the width of the screen and the video size is
     *                calculated after
     * @param mHeight According to the height of the screen and the video size is
     *                calculated after
     * @return
     */
    public static native int setup(int mWidth, int mHeight);

    /**
     * Get the camera original video size
     *
     * @return
     */
    public static native int[] getVideoInfo();


    /**
     * Initialize a socket tcp ip protocol-based connectivity
     *
     * @param ip
     * @param port
     * @param account
     * @param password
     * @return
     */
    public static native int initAudio(String ip, int port, String account,
                                       String password);

    /**
     * Creating a pthread, perform socket data reception, g711a audio decoding,
     *
     * @return
     */
    public static native int playAudio();

    /**
     * The amount of thread while loop flag is set to 0, the end of the cycle,
     * and exit thread
     *
     * @return
     */
    public static native int stopAudio();

    /**
     * When you exit the program closes the socket connection and release
     * resources
     *
     * @return
     */
    public static native int closeAudio();


}
