package net.kaicong.ipcam.device.record;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.provider.MediaStore.Video;

import java.io.File;

/**
 * Created by LingYan on 15/1/30.
 */

public class Util {

    public final static String FILE_START_NAME = "ID_";
    public final static String VIDEO_EXTENSION = ".mp4";
    public static ContentValues videoContentValues = null;

    public static String createFinalPath(Context context, int deviceId) {
        long dateTaken = System.currentTimeMillis();
        String title = FILE_START_NAME + deviceId + "_" + dateTaken;
        String filename = title + VIDEO_EXTENSION;
        String filePath = genrateFilePath(context, filename, true, null);

        ContentValues values = new ContentValues(7);
        values.put(Video.Media.TITLE, title);
        values.put(Video.Media.DISPLAY_NAME, filename);
        values.put(Video.Media.DATE_TAKEN, dateTaken);
        values.put(Video.Media.MIME_TYPE, "video/mp4");
        values.put(Video.Media.DATA, filePath);
        videoContentValues = values;

        return filePath;
    }

    private static String genrateFilePath(Context context, String fileName, boolean isFinalPath, File tempFolderPath) {
        String dirPath = Environment.getExternalStorageDirectory() + "/Android/data/" + context.getPackageName() + "/video";
        if (isFinalPath) {
            File file = new File(dirPath);
            if (!file.exists() || !file.isDirectory())
                file.mkdirs();
        } else
            dirPath = tempFolderPath.getAbsolutePath();
        String filePath = dirPath + "/" + fileName;
        return filePath;
    }

}
