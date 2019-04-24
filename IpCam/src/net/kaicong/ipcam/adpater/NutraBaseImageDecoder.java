package net.kaicong.ipcam.adpater;

import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.decode.ImageDecodingInfo;

import java.io.IOException;
import java.io.InputStream;

/**
 * https://github.com/nostra13/Android-Universal-Image-Loader/issues/539
 * 图片加载库UIL加载损坏的图像的时候，会报DECODING_ERROR错误
 * 这不是library的问题，是图片的问题，根据上面链接里的描述实现这个配置类
 * 配制方法：
 * 在KCApplication里添加如下配置
 * .imageDecoder(new NutraBaseImageDecoder(true))
 */
public class NutraBaseImageDecoder extends BaseImageDecoder {

    public NutraBaseImageDecoder(boolean loggingEnabled) {
        super(loggingEnabled);
    }

    @Override
    protected InputStream getImageStream(ImageDecodingInfo decodingInfo) throws IOException {
        InputStream stream = decodingInfo.getDownloader()
                .getStream(decodingInfo.getImageUri(), decodingInfo.getExtraForDownloader());
        return stream == null ? null : new JpegClosedInputStream(stream);
    }

    private class JpegClosedInputStream extends InputStream {

        private static final int JPEG_EOI_1 = 0xFF;
        private static final int JPEG_EOI_2 = 0xD9;
        private final InputStream inputStream;
        private int bytesPastEnd;

        private JpegClosedInputStream(final InputStream iInputStream) {
            inputStream = iInputStream;
            bytesPastEnd = 0;
        }

        @Override
        public int read() throws IOException {
            int buffer = inputStream.read();
            if (buffer == -1) {
                if (bytesPastEnd > 0) {
                    buffer = JPEG_EOI_2;
                } else {
                    ++bytesPastEnd;
                    buffer = JPEG_EOI_1;
                }
            }

            return buffer;
        }
    }
}
