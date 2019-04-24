package net.kaicong.ipcam.device.mjpeg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MjpegInputStream extends DataInputStream {

    private final byte[] SOI_MARKER = {(byte) 0xFF, (byte) 0xD8};
    private final byte[] EOF_MARKER = {(byte) 0xFF, (byte) 0xD9};
    private final String CONTENT_LENGTH = "Content-Length";
    private static final String TAG = "chu";
    private final static int HEADER_MAX_LENGTH = 100;
    //private final static int FRAME_MAX_LENGTH = 40000 + HEADER_MAX_LENGTH;
    private final static int FRAME_MAX_LENGTH = 200000;
    private int mContentLength = -1;
    byte[] header = null;
    byte[] frameData = null;
    int headerLen = -1;
    int headerLenPrev = -1;

    int skip = 1;
    int count = 0;

    //    private List<ReceiveMjpegFrameListener> mReceiveMjpegFrameListeners = Collections.synchronizedList(new Vector<ReceiveMjpegFrameListener>());

    public MjpegInputStream(InputStream in) {
        super(new BufferedInputStream(in, FRAME_MAX_LENGTH));
    }


    private int getEndOfSeqeunce(DataInputStream in, byte[] sequence)
            throws IOException {

        int seqIndex = 0;
        byte c;
        for (int i = 0; i < FRAME_MAX_LENGTH; i++) {
            c = (byte) in.readUnsignedByte();
            if (c == sequence[seqIndex]) {
                seqIndex++;
                if (seqIndex == sequence.length) {
                    return i + 1;
                }
            } else seqIndex = 0;
        }
        return -1;
    }

    private int getStartOfSequence(DataInputStream in, byte[] sequence)
            throws IOException {
        int end = getEndOfSeqeunce(in, sequence);
        return (end < 0) ? (-1) : (end - sequence.length);
    }

    private int getEndOfSeqeunceSimplified(DataInputStream in, byte[] sequence)
            throws IOException {
        int startPos = mContentLength / 2;
        int endPos = 3 * mContentLength / 2;

        skipBytes(headerLen + startPos);

        int seqIndex = 0;
        byte c;
        for (int i = 0; i < endPos - startPos; i++) {
            c = (byte) in.readUnsignedByte();
            if (c == sequence[seqIndex]) {
                seqIndex++;
                if (seqIndex == sequence.length) {

                    return headerLen + startPos + i + 1;
                }
            } else seqIndex = 0;
        }
        return -1;
    }

    private int parseContentLength(byte[] headerBytes)
            throws IOException, NumberFormatException, IllegalArgumentException {
        ByteArrayInputStream headerIn = new ByteArrayInputStream(headerBytes);
        Properties props = new Properties();
        props.load(headerIn);
        return Integer.parseInt(props.getProperty(CONTENT_LENGTH));
    }


    public Bitmap readMjpegFrame() throws IOException {
        mark(FRAME_MAX_LENGTH);
        int headerLen;
        try {
            headerLen = getStartOfSequence(this, SOI_MARKER);
        } catch (IOException e) {
//            LogUtil.d(TAG, "IOException in betting headerLen.");
            reset();
            return null;
        }
        reset();

        if (header == null || headerLen != headerLenPrev) {
            header = new byte[headerLen];
//            LogUtil.d(TAG, "header renewed " + headerLenPrev + " -> " + headerLen);
        }
        headerLenPrev = headerLen;
        readFully(header);

        int ContentLengthNew = -1;
        try {
            ContentLengthNew = parseContentLength(header);
        } catch (NumberFormatException nfe) {
            ContentLengthNew = getEndOfSeqeunceSimplified(this, EOF_MARKER);

            if (ContentLengthNew < 0) {
//                LogUtil.d(TAG, "Worst case for finding EOF_MARKER");
                reset();
                ContentLengthNew = getEndOfSeqeunce(this, EOF_MARKER);
            }
        } catch (IllegalArgumentException e) {
//            LogUtil.d(TAG, "IllegalArgumentException in parseContentLength");
            ContentLengthNew = getEndOfSeqeunceSimplified(this, EOF_MARKER);

            if (ContentLengthNew < 0) {
//                LogUtil.d(TAG, "Worst case for finding EOF_MARKER");
                reset();
                ContentLengthNew = getEndOfSeqeunce(this, EOF_MARKER);
            }
        } catch (IOException e) {
//            LogUtil.d(TAG, "IOException in parseContentLength");
            reset();
            return null;
        }
        mContentLength = ContentLengthNew;
        reset();

        if (frameData == null) {
            frameData = new byte[FRAME_MAX_LENGTH];
//            LogUtil.d(TAG, "frameData newed cl=" + FRAME_MAX_LENGTH);
        }
        if (mContentLength + HEADER_MAX_LENGTH > FRAME_MAX_LENGTH) {
            frameData = new byte[mContentLength + HEADER_MAX_LENGTH];
//            LogUtil.d(TAG, "frameData renewed cl=" + (mContentLength + HEADER_MAX_LENGTH));
        }

        skipBytes(headerLen);

        readFully(frameData, 0, mContentLength);

        if (count++ % skip == 0) {
            return BitmapFactory.decodeStream(new ByteArrayInputStream(frameData, 0, mContentLength));
        } else {
            return null;
        }
    }

    public void setSkip(int s) {
        skip = s;
    }

}
