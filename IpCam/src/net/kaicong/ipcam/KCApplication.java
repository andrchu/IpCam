package net.kaicong.ipcam;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;

import net.kaicong.ipcam.adpater.NutraBaseImageDecoder;
import net.kaicong.ipcam.api.NukeSSLCerts;
import net.kaicong.ipcam.bean.CustomImageDownloader;

import java.io.File;
import java.util.UUID;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by LingYan on 2014/8/28.
 */
public class KCApplication extends Application {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static KCApplication kcApplication = null;
    private static Context mContext = null;
    public static boolean isRefreshDevices = false;

    public static String userHeadUrl = "";
    private RequestQueue mRequestQueue;
    public static final String TAG = UUID.randomUUID().toString();
    public static String Latitude = "0";
    public static String Longitude = "0";
    //云豆数
    public static int Virtualcurrency = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        NukeSSLCerts.nuke();
        mContext = getApplicationContext();
        //StrictMode模式开启检测应用是否存在bug(如在主线程中联网，内存泄露等问题)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
//        }
        //这里必须要将ImageLoader的配置初始化
        initImageLoader(mContext);
        //设置内存缓存图片大小的最大值
        BitmapAjaxCallback.setMaxPixelLimit(1280 * 720);
        //内存缓存图片的最大数
        BitmapAjaxCallback.setCacheLimit(40);
        //内存缓存最大值
        BitmapAjaxCallback.setMaxPixelLimit(6 * 1024 * 1024);
        //设置文件缓存的路径
        AQUtility.setCacheDir(getAndroidQueryCacheDir(mContext));
        //------------------- 极光推送 相关-------------------------------
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            Latitude = String.format("%.2f", location.getLatitude());
            Longitude = String.format("%.2f", location.getLongitude());
        }

        //------------------- 极光推送 相关-------------------------------
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this.getApplicationContext());

        // 设置保留最近通知条数 API
        JPushInterface.setLatestNotificationNumber(getApplicationContext(), 5);
    }

    @SuppressLint("NewApi")
    private static int[] getScreenRes() {
        int[] res = new int[2];
        Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= 13) {
            Point size = new Point();
            display.getSize(size);
            res[0] = size.x;
            res[1] = size.y;
        } else {
            res[0] = display.getWidth(); // deprecated
            res[1] = display.getHeight(); // deprecated
        }
        return res;
    }

    //获取屏幕分辨率宽度
    public static int getWindowWidth() {
        return getScreenRes()[0];
    }

    //获取屏幕分辨率长度
    public static int getWindowHeight() {
        return getScreenRes()[1];
    }

    public static void initImageLoader(Context context) {
        //默认的显示配置
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(5 * 1024 * 1024))
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .diskCache(new UnlimitedDiscCache(getExternalCacheDir(context)))
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                        /**
                         *@see net.kaicong.ipcam.adpater.NutraBaseImageDecoder
                         */
                .imageDecoder(new NutraBaseImageDecoder(true))
//                .writeDebugLogs() // Remove for release app
                .defaultDisplayImageOptions(defaultOptions)
                .imageDownloader(new CustomImageDownloader(context))
                .build();
        ImageLoader.getInstance().init(config);

    }

    public static KCApplication getContext() {
        return (KCApplication) mContext;
    }

    public static KCApplication getInstance() {
        if (kcApplication == null) {
            kcApplication = new KCApplication();
        }
        return kcApplication;
    }

    private static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    private static File getAndroidQueryCacheDir(Context context) {
        if (hasExternalCacheDir()) {
            File cacheDir = context.getExternalCacheDir();
            if (cacheDir != null) {
                cacheDir = new File(cacheDir.getAbsolutePath() + File.separator + "aquery");
                return cacheDir;
            }
        }
        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName()
                + "/cache/aquery/";
        return new File(Environment.getExternalStorageDirectory().getPath()
                + cacheDir);
    }

    private static File getExternalCacheDir(Context context) {
        if (hasExternalCacheDir()) {
            File cacheDir = context.getExternalCacheDir();
            if (cacheDir != null) {
                return cacheDir;
            }
        }
        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName()
                + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath()
                + cacheDir);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    //判断SD卡是否存在
    public static boolean isSDExist() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    //获取APP缓存目录
    public static String getAppCacheDir() {
        final String cachePath = isSDExist() || !isExternalStorageRemovable() ? getExternalCacheDir(
                mContext).getPath()
                : mContext.getCacheDir().getPath();

        return cachePath;
    }

    //获取截图目录
    public static String getSnapHostDir(String deviceType, String fileName) {
        if (isSDExist()) {
            File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "SnapHost" + File.separator + deviceType + File.separator + fileName);
            return file.getAbsolutePath();
        }
        return "";
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}
