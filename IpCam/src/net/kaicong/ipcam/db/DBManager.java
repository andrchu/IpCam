package net.kaicong.ipcam.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

import net.kaicong.ipcam.AddDevicePropertyActivity;

public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * 添加通过ip模式添加的摄像机
     *
     * @param videoPropertyBean
     * @return
     */
    public void addDeviceByIp(VideoPropertyBean videoPropertyBean) {
        ContentValues values = new ContentValues();
        values.put("addType", 2);//ip模式添加
        values.put("addDate", System.currentTimeMillis() / 1000);
        values.put("cameraType", videoPropertyBean.getCameraType());
        values.put("ip", videoPropertyBean.getIp());
        values.put("brightness", videoPropertyBean.getBrightness());
        values.put("saturation", videoPropertyBean.getSaturation());
        values.put("videoFlip", videoPropertyBean.getVideoFlip());
        values.put("videoEnvironment", videoPropertyBean.getVideoEnvironment());
        values.put("videoQuality", videoPropertyBean.getVideoQuality());
        db.insert(DBHelper.CAMERA_TABLE_NAME, null, values);
    }

    /**
     * 通过智云号获取截图
     *
     * @param uid
     * @return
     */
    public byte[] getSnapshot(String uid) {
        Cursor cursor = db.rawQuery("SELECT snapshot FROM " + DBHelper.CAMERA_TABLE_NAME + " WHERE zcloudName = ?", new String[]{uid});
        while (cursor.moveToNext()) {
            byte result[] = cursor.getBlob(cursor.getColumnIndex("snapshot"));
            cursor.close();
            return result;
        }
        cursor.close();
        return null;
    }

    /**
     * 添加通过ip模式添加的摄像机
     *
     * @param videoPropertyBean
     * @return
     */
    public void addDeviceByDDNS(VideoPropertyBean videoPropertyBean) {
        ContentValues values = new ContentValues();
        values.put("addType", 3);//ddns模式添加，不显示
        values.put("addDate", System.currentTimeMillis() / 1000);
        values.put("cameraType", videoPropertyBean.getCameraType());
        values.put("ddnsName", videoPropertyBean.getIp());
        values.put("brightness", videoPropertyBean.getBrightness());
        values.put("saturation", videoPropertyBean.getSaturation());
        values.put("videoFlip", videoPropertyBean.getVideoFlip());
        values.put("videoEnvironment", videoPropertyBean.getVideoEnvironment());
        values.put("videoQuality", videoPropertyBean.getVideoQuality());
        db.insert(DBHelper.CAMERA_TABLE_NAME, null, values);
    }

    /**
     * 通过智云模式添加设备
     */
    public void addDeviceByZhiyun(String uid, byte snapshot[]) {
        ContentValues values = new ContentValues();
        values.put("zcloudName", uid);
        values.put("snapshot", snapshot);
        db.insert(DBHelper.CAMERA_TABLE_NAME, null, values);
    }

    /**
     * 根据标志来查询获取id
     *
     * @param tag
     * @param mode
     * @return
     */
    public int getDeviceId(String tag, int mode) {
        Cursor cursor = null;
        if (mode == AddDevicePropertyActivity.ADD_MODE_IP) {
            cursor = db.rawQuery("SELECT _id FROM " + DBHelper.CAMERA_TABLE_NAME + " WHERE ip = ?", new String[]{tag});
        } else if (mode == AddDevicePropertyActivity.ADD_MODE_ZHIYUN) {
            cursor = db.rawQuery("SELECT _id FROM " + DBHelper.CAMERA_TABLE_NAME + " WHERE zcloudName = ?", new String[]{tag});
        } else if (mode == AddDevicePropertyActivity.ADD_MODE_DDNS) {
            cursor = db.rawQuery("SELECT _id FROM " + DBHelper.CAMERA_TABLE_NAME + " WHERE ddnsName = ?", new String[]{tag});
        }
        while (cursor.moveToNext()) {
            int mId = cursor.getInt(cursor.getColumnIndex("_id"));
            cursor.close();
            return mId;
        }
        cursor.close();
        return 0;
    }

    /**
     * 更新数据
     *
     * @param videoPropertyBean
     */
    public void updateDeviceProperty(VideoPropertyBean videoPropertyBean) {
        ContentValues values = new ContentValues();
        values.put("brightness", videoPropertyBean.getBrightness());
        values.put("saturation", videoPropertyBean.getSaturation());
        values.put("videoFlip", videoPropertyBean.getVideoFlip());
        values.put("videoEnvironment", videoPropertyBean.getVideoEnvironment());
        values.put("videoQuality", videoPropertyBean.getVideoQuality());
        db.update(DBHelper.CAMERA_TABLE_NAME, values, "_id = ?", new String[]{videoPropertyBean.get_id() + ""});
    }

    /**
     * 更新缩略图
     *
     * @param uid
     * @param snapshot
     */
    public void updateDeviceSnapshotByUID(String uid, byte[] snapshot) {
        ContentValues values = new ContentValues();
        values.put("snapshot", snapshot);
        db.update(DBHelper.CAMERA_TABLE_NAME, values, "snapshot = ?", new String[]{uid});
    }

    //是否存在对应IP的摄像头
    public boolean queryDeviceIpExists(String ipStr) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.CAMERA_TABLE_NAME + " WHERE ip = ?", new String[]{ipStr});
        while (cursor.moveToNext()) {
            //该ip已存在
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    //是否存在对应IP的摄像头
    public boolean queryDeviceDDNSExists(String ddnsName) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.CAMERA_TABLE_NAME + " WHERE ddnsName = ?", new String[]{ddnsName});
        while (cursor.moveToNext()) {
            //该ddns已存在
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    //是否存在对应智云号的摄像头
    public boolean queryDeviceZhiyunExists(String zhiyunStr) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.CAMERA_TABLE_NAME + " WHERE zcloudName = ?", new String[]{zhiyunStr});
        while (cursor.moveToNext()) {
            //该智云设备已存在
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    /**
     * 删除设备
     *
     * @param tag  标志
     * @param mode 添加模式
     * @return
     */
    public void deleteDevice(String tag, int mode) {
        if (mode == AddDevicePropertyActivity.ADD_MODE_IP) {
            db.delete(DBHelper.CAMERA_TABLE_NAME, "ip = ?", new String[]{tag});
        } else if (mode == AddDevicePropertyActivity.ADD_MODE_ZHIYUN) {
            db.delete(DBHelper.CAMERA_TABLE_NAME, "zcloudName = ?", new String[]{tag});
        } else if (mode == AddDevicePropertyActivity.ADD_MODE_DDNS) {
            db.delete(DBHelper.CAMERA_TABLE_NAME, "ddnsName = ?", new String[]{tag});
        }
    }

    public VideoPropertyBean getDBCameras(String tag, int mode) {
        Cursor cursor = null;
        VideoPropertyBean videoPropertyBean = new VideoPropertyBean();
        if (mode == AddDevicePropertyActivity.ADD_MODE_IP) {
            cursor = db.rawQuery("SELECT * FROM " + DBHelper.CAMERA_TABLE_NAME + " WHERE ip = ?", new String[]{tag});
        } else if (mode == AddDevicePropertyActivity.ADD_MODE_ZHIYUN) {
            cursor = db.rawQuery("SELECT * FROM " + DBHelper.CAMERA_TABLE_NAME + " WHERE zcloudName = ?", new String[]{tag});
        } else if (mode == AddDevicePropertyActivity.ADD_MODE_DDNS) {
            cursor = db.rawQuery("SELECT * FROM " + DBHelper.CAMERA_TABLE_NAME + " WHERE ddnsName = ?", new String[]{tag});
        }
        while (cursor.moveToNext()) {
            videoPropertyBean.set_id(cursor.getInt(cursor.getColumnIndex("_id")));
            videoPropertyBean.setCameraType(cursor.getInt(cursor.getColumnIndex("cameraType")));
            videoPropertyBean.setZcloudName(cursor.getString(cursor.getColumnIndex("zcloudName")));
            videoPropertyBean.setIp(cursor.getString(cursor.getColumnIndex("ip")));
            videoPropertyBean.setDdnsName(cursor.getString(cursor.getColumnIndex("ddnsName")));
            videoPropertyBean.setSnapshot(cursor.getBlob(cursor.getColumnIndex("snapshot")));
            videoPropertyBean.setBrightness(cursor.getInt(cursor.getColumnIndex("brightness")));
            videoPropertyBean.setSaturation(cursor.getInt(cursor.getColumnIndex("saturation")));
            videoPropertyBean.setVideoFlip(cursor.getInt(cursor.getColumnIndex("videoFlip")));
            videoPropertyBean.setVideoEnvironment(cursor.getInt(cursor.getColumnIndex("videoEnvironment")));
            videoPropertyBean.setVideoQuality(cursor.getInt(cursor.getColumnIndex("videoQuality")));
        }
        cursor.close();
        return videoPropertyBean;
    }

    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }

    //bitmap转化为byte数组（缩略图）
    public static byte[] getByteArrayFromBitmap(Bitmap bitmap, int width, int height) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            return bos.toByteArray();
        } else {
            return null;
        }
    }

    public static Bitmap getBitmapFromByteArray(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        InputStream is = new ByteArrayInputStream(bytes);
        return BitmapFactory.decodeStream(is, null, getBitmapOptions(2));
    }

    public static BitmapFactory.Options getBitmapOptions(int scale) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;//表示使用BitmapFactory创建的Bitmap用于存储Pixel的内存空间在系统内存不足时可以被回收
        options.inInputShareable = true;
        options.inSampleSize = scale;
        try {
            BitmapFactory.Options.class.getField("inNativeAlloc").setBoolean(options, true);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return options;
    }

}
