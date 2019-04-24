package com.chu.android;

import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LogUtil {
    public static final String sTAG = LogUtil.class.getSimpleName();
    //LOG开关(release发布时关闭)
    public static boolean ENABLE_LOG = false;

    public static void log(String TAG, String msg) {
        if (!ENABLE_LOG) {
            return;
        }

        if (null == TAG) {
            TAG = sTAG;
        }

        Log.i(TAG, msg);
    }

    public static void e(String TAG, String msg) {
        if (!ENABLE_LOG) {
            return;
        }

        if (null == TAG) {
            TAG = sTAG;
        }

        Log.e(TAG, msg);
    }

    public static void i(String TAG, String msg) {
        if (!ENABLE_LOG) {
            return;
        }

        if (null == TAG) {
            TAG = sTAG;
        }

        Log.i(TAG, msg);
    }

    public static void v(String TAG, String msg) {
        if (!ENABLE_LOG) {
            return;
        }

        if (null == TAG) {
            TAG = sTAG;
        }

        Log.v(TAG, msg);
    }

    public static void d(String TAG, String msg) {
        if (!ENABLE_LOG) {
            return;
        }

        if (null == TAG) {
            TAG = sTAG;
        }

        Log.d(TAG, msg);
    }

    public static void w(String TAG, String msg) {
        if (!ENABLE_LOG) {
            return;
        }

        if (null == TAG) {
            TAG = sTAG;
        }

        Log.w(TAG, msg);
    }

    public static void w(String TAG, Exception msg) {
        if (!ENABLE_LOG) {
            return;
        }

        if (null == TAG) {
            TAG = sTAG;
        }
        Log.w(TAG, msg);
    }

    public static void w(String TAG, String str, Exception msg) {
        if (!ENABLE_LOG) {
            return;
        }

        if (null == TAG) {
            TAG = sTAG;
        }
        Log.w(TAG, str, msg);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void uMengLog(String TAG) {
        TAG += ":meminfo";
        log(TAG, "printMemInfo:");

        log(TAG, "Max heap size = " + Runtime.getRuntime().maxMemory() / 1024
                / 1024 + "M");
        log(TAG,
                "Allocate heap size = "
                        + android.os.Debug.getNativeHeapAllocatedSize() / 1024
                        + "K"
        );

        Method _readProclines = null;
        try {
            Class procClass;
            procClass = Class.forName("android.os.Process");
            Class parameterTypes[] = new Class[]{String.class,
                    String[].class, long[].class};
            _readProclines = procClass.getMethod("readProcLines",
                    parameterTypes);
            Object arglist[] = new Object[3];
            final String[] mMemInfoFields = new String[]{"MemTotal:",
                    "MemFree:", "Buffers:", "Cached:"};
            long[] mMemInfoSizes = new long[mMemInfoFields.length];
            mMemInfoSizes[0] = 30;
            mMemInfoSizes[1] = -30;
            arglist[0] = new String("/proc/meminfo");
            arglist[1] = mMemInfoFields;
            arglist[2] = mMemInfoSizes;
            if (_readProclines != null) {
                _readProclines.invoke(null, arglist);
                for (int i = 0; i < mMemInfoSizes.length; i++) {
                    log(TAG, mMemInfoFields[i] + " = " + mMemInfoSizes[i]
                            / 1024 + "M");
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
