package net.kaicong.ipcam.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by LingYan on 15/1/30.
 */
public class IOUtils {

    /**
     * 将inputStream转化为byteArray
     *
     * @param inStream
     * @return
     * @throws IOException
     */
    public static byte[] getBytesFromInputStream(InputStream inStream)
            throws IOException {

        // Get the size of the file
        long streamLength = inStream.available();

        if (streamLength > Integer.MAX_VALUE) {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) streamLength];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = inStream.read(bytes,
                offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file ");
        }

        // Close the input stream and return bytes
        inStream.close();
        return bytes;
    }


}
