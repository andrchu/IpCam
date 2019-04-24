package net.kaicong.ipcam.device.sip1303;

/**
 * Created by LingYan on 2014/11/5 0005.
 */
public class RtspPacket {

    /* ------音视频码流标志字段----- */
    public static final int PT_H264 = 96;
    public static final int PT_G726 = 97;
    public static final int PT_G711 = 8;
    public static final int PT_DATA = 100;

    /* ------音视频请求字段区分----- */
    public static final int CHANNEL1 = 11;
    public static final int CHANNEL2 = 12;
    public static final int CHANNEL3 = 13;
    public static final String STREAM_TYPE1 = "video_audio_data";
    public static final String STREAM_TYPE2 = "video_audio";
    public static final String STREAM_TYPE3 = "video_data";
    public static final String STREAM_TYPE4 = "audio_data";
    public static final String STREAM_TYPE5 = "video";
    public static final String STREAM_TYPE6 = "audio";
    public static final String STREAM_TYPE7 = "data";

    //rtp包固定12个字节
    public static final int RTP_PACKET_SIZE = 12;
    //rtsp包固定20个字节
    public static final int RTSP_PACKET_SIZE = 20;

    /**
     * RTSP结构体头的结构为
     * typedef struct sRTSP_ITLEAVED_HDR_S {
     * unsigned char daollar; 8, $:dollar sign(24 decimal)
     * unsigned char channelid; 8, channel id
     * unsigned short resv; 16, reseved
     * unsigned int payloadLen; 32, payload length
     * RTP_HDR_S rtpHead; rtp head
     * } RTSP_ITLEAVED_HDR_S;
     * dollar占一个字节对应headerBuf[0]
     * channelid占一个字节对应headerBuf[1]
     * resv占两个字节对应headerBuf[2]和headerBuf[3]
     * payloadLen占四个字节对应headerBuf[4]，headerBuf[5]，headerBuf[6]，headerBuf[7]
     */

    private byte dollar; /*8, $:dollar sign(24 decimal)*/
    private byte channelId; /*8, channel id*/
    private short reseved; /*16, reseved*/
    private int payloadLen; /*32, payload length*/

    private class RtpPacket {
        /* byte 0 */
        /**
         * unsigned short cc :4; CSRC count
         * unsigned short x :1;  header extension flag
         * unsigned short p :1;  padding flag
         * unsigned short version :2;  protocol version
         */
        private byte csrc;

        /* byte 1 */
        /**
         * unsigned short pt:7;  payload type
         * unsigned short marker:1;  marker bit
         */
        private byte payload;

        /* bytes 2, 3 */
        /**
         * unsigned short seqno:16;  sequence number
         */
        private short seqno;

        /* bytes 4-7 */
        int ts;  /*timestamp in ms */
        /* bytes 8-11 */
        int ssrc; /* synchronization source */
    }

    /**
     * 根据如上定义，rtspPacketBuf[4-7](四个字节)转化为payload的长度(int)
     *
     * @param rtspPacketBuf
     * @return
     */
    public static int getPayloadLength(byte[] rtspPacketBuf) {
        int pack_len = 0;
        pack_len = (((rtspPacketBuf[4] & 0xff) << 24) |
                ((rtspPacketBuf[5] & 0xff) << 16) |
                ((rtspPacketBuf[6] & 0xff) << 8) |
                (rtspPacketBuf[7] & 0xff));
        return pack_len;
    }

    /**
     * 根据如上定义，rtspPacketBuf[9]转化为payload的长度
     *
     * @param rtspPacketBuf
     * @return
     */
    public static int getPayloadType(byte[] rtspPacketBuf) {
        int pt = (byte) ((rtspPacketBuf[9] & 0xff) & 0x7f);
        return pt;
    }


}
