/**
 * Copyright (C) 2012 finger.
 * This file write by finger in 2012-9-4,mail:luweifeng_2000@126.com
 */
package net.kaicong.ipcam.utils;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * a simple use for PreferenceManager
 *
 * @author finger
 * @version V1.0
 */
public class PreferenceUtils {

    /**
     * save int Preference
     *
     * @param context context
     * @param key     key
     * @param values  value
     */
    public static void savePreference(Context context, String key, int values) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(key, values).commit();
    }

    /**
     * save boolean Preference
     *
     * @param context context
     * @param key     key
     * @param values  value
     */
    public static void savePreference(Context context, String key,
                                      boolean values) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(key, values).commit();
    }

    /**
     * save float Preference
     *
     * @param context context
     * @param key     key
     * @param values  value
     */
    public static void savePreference(Context context, String key, float values) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putFloat(key, values).commit();
    }

    /**
     * save String Preference
     *
     * @param context context
     * @param key     key
     * @param values  value
     */
    public static void savePreference(Context context, String key, String values) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(key, values).commit();
    }

    /**
     * save long Preference
     *
     * @param context context
     * @param key     key
     * @param values  value
     */
    public static void savePreference(Context context, String key, long values) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putLong(key, values).commit();
    }

    /**
     * read int Preference
     *
     * @param context context
     * @param key     key
     * @return default 0
     */
    public static int loadIntPreference(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(
                key, 0);
    }

    /**
     * read integer Preference
     *
     * @param context      context
     * @param key          key
     * @param defaultValue default
     * @return default 0
     */
    public static int loadIntPreference(Context context, String key,
                                        int defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(
                key, defaultValue);
    }

    /**
     * read boolean Preference
     *
     * @param context context
     * @param key     key
     * @return default false
     */
    public static boolean loadBooleanPreference(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(key, false);
    }

    /**
     * 读取boolean型Preference
     *
     * @param context      context
     * @param key          key
     * @param defaultValue default
     * @return default false
     */
    public static boolean loadBooleanPreference(Context context, String key,
                                                boolean defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(key, defaultValue);
    }

    /**
     * read float Preference
     *
     * @param context context
     * @param key     key
     * @return default 0.0f
     */
    public static float loadFloatPreference(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getFloat(
                key, 0.0f);
    }

    /**
     * read float Preference
     *
     * @param context      context
     * @param key          key
     * @param defaultValue default
     * @return default 0.0f
     */
    public static float loadFloatPreference(Context context, String key,
                                            float defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getFloat(
                key, defaultValue);
    }

    /**
     * read String Preference
     *
     * @param context context
     * @param key     key
     * @return default a blank String
     */
    public static String loadStringPreference(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(key, "");
    }

    /**
     * read String Preference
     *
     * @param context      context
     * @param key          key
     * @param defaultValue default
     * @return default a blank String
     */
    public static String loadStringPreference(Context context, String key,
                                              String defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(key, defaultValue);
    }

    /**
     * read long Preference
     *
     * @param context context
     * @param key     key
     * @return default 0
     */
    public static long loadLongPreference(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(
                key, 0);
    }

    /**
     * read long Preference
     *
     * @param context      context
     * @param key          key
     * @param defaultValue default
     * @return default 0
     */
    public static long loadLongPreference(Context context, String key,
                                          long defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(
                key, defaultValue);
    }
}
