package net.kaicong.ipcam.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String CAMERA_TABLE_NAME = "zcloud";
    private static final String DATABASE_NAME = "local_cameras.db";

    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        //CursorFactory设置为null,使用默认值
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //创建本地数据库
    private void createCameraDB(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + CAMERA_TABLE_NAME + "" +
                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "zcloudName VARCHAR, " +            //智云号(智云模式)
                "snapshot BLOB)");                  //截图)");
    }

    //数据库第一次被创建时onCreate会被调用
    @Override
    public void onCreate(SQLiteDatabase db) {
        createCameraDB(db);
    }

    //如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE " + CAMERA_TABLE_NAME + " ADD COLUMN other STRING");
    }
}
