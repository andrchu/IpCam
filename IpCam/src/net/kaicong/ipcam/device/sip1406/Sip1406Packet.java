package net.kaicong.ipcam.device.sip1406;

/**
 * Created by LingYan on 15/5/6.
 */
public class Sip1406Packet {

    /**
     * typedef struct _stFLiveStreamHeader
     * {
     * 1 char type;                  // 0->264的vdieo的I帧 1->264的P帧 2->264的audio 3->jpeg的video
     * 1 char streamid;              // 0->主码流640x480 1->次码流320x240
     * 2 unsigned short militime;    // 毫秒时间
     * 4 unsigned int sectime;       // 秒时间
     * 4 unsigned int len;           // 数据长度，不包括头
     * 4 unsigned int frameno;       // 帧号
     * <p/>
     * 16个字节
     * }
     */

    public static final int HEAD_SIZE = 16;

    public static int getPayloadLength(byte[] rtspPacketBuf, int position) {
        int pack_len = 0;
        pack_len = (((rtspPacketBuf[position] & 0xff) << 24) |
                ((rtspPacketBuf[position + 1] & 0xff) << 16) |
                ((rtspPacketBuf[position + 2] & 0xff) << 8) |
                (rtspPacketBuf[position + 3] & 0xff));
        return pack_len;
    }

}
