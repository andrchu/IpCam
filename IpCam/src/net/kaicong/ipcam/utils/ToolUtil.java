package net.kaicong.ipcam.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class ToolUtil {
    /*
     * create random 6-digit password
     */
    private static Map<String, SimpleDateFormat> formatMap = new HashMap<String, SimpleDateFormat>();
    private static long lastClickTime = 0;

    public static String createRandomPassword() {
        Random random = new Random();
        StringBuilder num = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            num.append(random.nextInt(10));
        }
        return num.toString();
    }

    public static Properties getUrlProperties() {
        return getProperties("/data/data/com.kaicong.ipcam/url.properties");
    }

    /**
     * 读取properties配置文件
     *
     * @param propertiesUri
     * @return
     */
    public static Properties getProperties(String propertiesUri) {
        Properties props = new Properties();
        try {
            InputStream in = new FileInputStream(new File(propertiesUri));
            props.load(in);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return props;
    }

    //生成图片名
    public static String getNowTimeStr() {
        Time t = new Time();
        t.setToNow();
        String filename = String.format(
                "%04d%02d%02d%02d%02d%02d.jpg",
                t.year, t.month + 1, t.monthDay,
                t.hour, t.minute, t.second);
        return filename;
    }

    //生成日期文件名
    public static String getNowTimeStrZip() {
        Time t = new Time();
        t.setToNow();
        String filename = String.format(
                "%04d%02d%02d%02d%02d%02d.zip",
                t.year, t.month + 1, t.monthDay,
                t.hour, t.minute, t.second);
        return filename;
    }

    /**
     * 检查字符长度
     *
     * @param tv
     * @param min
     * @param max
     * @param hint
     * @return
     */
    public static boolean checkLength(EditText tv, int min, int max, String hint) {
        if (tv.length() < min || tv.length() > max) {
            tv.setError(hint);
            return false;
        }
        return true;
    }

    /**
     * Unbind all the drawables.
     *
     * @param view
     */
    public static void unbindDrawables(View view) {
        if (view != null) {
            if (view.getBackground() != null) {
                view.getBackground().setCallback(null);
            }
            if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    unbindDrawables(((ViewGroup) view).getChildAt(i));
                }
                ((ViewGroup) view).removeAllViews();
            }
        }
    }

    public static boolean isEmpty(String str) {
        if ("null".equalsIgnoreCase(str) || TextUtils.isEmpty(str)) {
            return true;
        }
        return false;
    }

    /**
     * 将指定格式的时间字符串转化为时间戳
     *
     * @param timeStr
     * @return
     */
    public static long getTimestamp(String timeStr) {
        SimpleDateFormat simpleDateFormat;
        if (timeStr.contains(".")) {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS");
        } else {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        }
        try {
            return simpleDateFormat.parse(timeStr).getTime();
        } catch (Exception e) {

        }
        return 0;
    }

    public static String fixTimeDisplay(String timeStr) {
        // long timestamp = 0;
        // try {
        // timeStr = timeStr.replace("+08:00", "").replace("T", " ");
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //
        // Date date = sdf.parse(timeStr);
        // timestamp = date.getTime() / 1000;
        // } catch (ParseException e) {
        // e.printStackTrace();
        // }

        long timestamp = Long.parseLong(timeStr);
        long currentSeconds = System.currentTimeMillis() / 1000;
        long timeGap = currentSeconds - timestamp;// 与现在时间相差秒数

        if (timeGap > 24 * 60 * 60) {// 1天以上
            timeStr = timeGap / (24 * 60 * 60) + "天前";
        } else if (timeGap > 60 * 60) {// 1小时-24小时
            timeStr = timeGap / (60 * 60) + "小时前";
        } else if (timeGap > 60) {// 1分钟-59分钟
            timeStr = timeGap / 60 + "分钟前";
        } else {// 1秒钟-59秒钟
            timeStr = "刚刚";
        }
        return timeStr;
    }

    public static String fixTimeStandardDisplay(String timeStr) {
        long time = Long.parseLong(timeStr) * 1000;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(new Date(time));
    }

    public static String fixAgeDisplay(String birthday) {
        Date CurrentSeconds = new Date();
        long birthdaySeconds = Long.parseLong(birthday);
        long age = (CurrentSeconds.getTime() / 1000 - birthdaySeconds)
                / (60 * 60 * 24 * 365);
        return age + "";
    }

    public static Calendar getCalendarInstance(String birthday) {
        long birthdaySeconds = Long.parseLong(birthday) * 1000;
        Date dd = new Date(birthdaySeconds);
        Calendar cal = Calendar.getInstance();
        cal.setTime(dd);
        return cal;
    }

    //上传文件名命名规则
    public static String fileRule() {
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String part1 = sdf.format(new Date());//日期
        String part2 = UUID.randomUUID().toString().toLowerCase().replace("-", "");
        return part1 + "-" + part2 + ".jpg";
    }


    public static String fixBirthdayDisplay(String birthday) {
        Calendar cal = getCalendarInstance(birthday);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        StringBuilder sb = new StringBuilder();
        sb.append(year).append("年").append(month).append("月").append(day)
                .append("日");
        return sb.toString();
    }

    public static String setBirthdayBySeconds(int year, int month, int day) {
        StringBuilder sb = new StringBuilder();
        sb.append(year).append("年").append(month).append("月").append(day)
                .append("日");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        try {
            long birthday = sdf.parse(sb.toString()).getTime() / 1000;
            return birthday + "";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String fixTitleDisplay(String title) {
        if (title.length() > 15) {
            title = title.substring(0, 12) + "...";
        }
        return title;
    }

    public static int reckonThumbnail(int oldWidth, int oldHeight, int newWidth, int newHeight) {
        if ((oldHeight > newHeight && oldWidth > newWidth)
                || (oldHeight <= newHeight && oldWidth > newWidth)) {
            int be = (int) (oldWidth / (float) newWidth);
            if (be <= 1)
                be = 1;
            return be;
        } else if (oldHeight > newHeight && oldWidth <= newWidth) {
            int be = (int) (oldHeight / (float) newHeight);
            if (be <= 1)
                be = 1;
            return be;
        }
        return 1;
    }

    public static Bitmap PicZoom(Bitmap bmp, int width, int height) {
        int bmpWidth = bmp.getWidth();
        int bmpHeght = bmp.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale((float) width / bmpWidth, (float) height / bmpHeght);

        return Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeght, matrix, true);
    }

    public static Date parseDate(String str, String format) {
        if (str == null || "".equals(str)) {
            return null;
        }
        SimpleDateFormat sdf = formatMap.get(format);
        if (null == sdf) {
            sdf = new SimpleDateFormat(format, Locale.ENGLISH);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            formatMap.put(format, sdf);
        }
        try {
            synchronized (sdf) {
                // SimpleDateFormat is not thread safe
                return sdf.parse(str);
            }
        } catch (ParseException pe) {
            return null;
        }
    }

    public static String formatSource(String name) {
        if (name == null || "".equals(name)) {
            return name;
        }
        int start = name.indexOf(">");
        int end = name.lastIndexOf("<");
        if (start == -1 || end == -1) {
            return name;
        }
        return name.substring(start + 1, end);
    }

    public static String extractLineName(String name) {
        if (name == null || "".equals(name)) {
            return name;
        }
        int start = name.indexOf("(");
        if (start == -1) {
            return name;
        }
        return name.substring(0, start);
    }

    /**
     * @param str
     * @return 如果是符合网址格式的字符串, 返回<b>true</b>,否则为<b>false</b>
     */
    public static boolean isHomepage(String str) {
        String regex = "(http|https)://(([a-zA-z0-9]|-){1,}\\.)[\\S]{1,}";
        return match(regex, str);
    }

    /**
     * @param regex 正则表达式字符串
     * @param str   要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 判断是否为英文字符串
     *
     * @param charaString
     * @return
     */
    public static boolean isEnglish(String charaString) {
        return charaString.matches("^[a-zA-Z]*");
    }

    /**
     * 判断字符串是否为中文
     *
     * @param str
     * @return
     */
    public static boolean isChinese(String str) {
        String regEx = "[\\u4e00-\\u9fa5]+";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        if (m.find())
            return true;
        else
            return false;
    }

    public static Map<String, String> getParamsFromUrl(String url) {
        Map<String, String> map = null;
        if (url != null && url.indexOf('?') != -1) {
            map = splitUrlQuery(url.substring(url.indexOf('?') + 1));
        }
        if (map == null) {
            map = new HashMap<String, String>();
        }
        return map;
    }

    /**
     * 从URL中提取所有的参数。
     *
     * @param query URL地址
     * @return 参数映射
     */
    public static Map<String, String> splitUrlQuery(String query) {
        Map<String, String> result = new HashMap<String, String>();

        String[] pairs = query.split("&");
        if (pairs != null && pairs.length > 0) {
            for (String pair : pairs) {
                String[] param = pair.split("=", 2);
                if (param != null && param.length == 2) {
                    result.put(param[0], param[1]);
                }
            }
        }

        return result;
    }

    /**
     * 文件转化为字节数组
     *
     * @param file
     * @return
     */
    public static byte[] getBytesFromFile(File file) {
        byte[] ret = null;
        try {
            if (file == null) {
                // log.error("helper:the file is null!");
                return null;
            }
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            byte[] b = new byte[1024];
            int n;
            while ((n = in.read(b)) != -1) {
                out.write(b, 0, n);
            }
            in.close();
            out.close();
            ret = out.toByteArray();
        } catch (IOException e) {
            // log.error("helper:get bytes from file process error!");
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 把字节数组保存为一个文件
     *
     * @param b
     * @param outputFile
     * @return
     */
    public static File getFileFromBytes(byte[] b, String outputFile) {
        File ret = null;
        BufferedOutputStream stream = null;
        try {
            ret = new File(outputFile);
            FileOutputStream fstream = new FileOutputStream(ret);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            // log.error("helper:get file from byte process error!");
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // log.error("helper:get file from byte process error!");
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (timeD >= 0 && timeD <= 1000) {
            return true;
        } else {
            lastClickTime = time;
            return false;
        }
    }


    public static String getLocalIpAddress() {
        String ipAddress = null;
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface iface : interfaces) {
                if (iface.getDisplayName().equals("eth0")) {
                    List<InetAddress> addresses = Collections.list(iface.getInetAddresses());
                    for (InetAddress address : addresses) {
                        if (address instanceof Inet4Address) {
                            ipAddress = address.getHostAddress();
                        }
                    }
                } else if (iface.getDisplayName().equals("wlan0")) {
                    List<InetAddress> addresses = Collections.list(iface.getInetAddresses());
                    for (InetAddress address : addresses) {
                        if (address instanceof Inet4Address) {
                            ipAddress = address.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipAddress;
    }

    public static String decompress(String filePath) {
        String decompressedPath = "";
        try {
            ZipInputStream Zin = new ZipInputStream(new FileInputStream(filePath));//输入源zip路径
            BufferedInputStream Bin = new BufferedInputStream(Zin);
            String Parent = filePath.substring(0, filePath.lastIndexOf(File.separator)) + File.separator; //输出路径（文件夹目录）
            File Fout = null;
            ZipEntry entry;
            try {
                while ((entry = Zin.getNextEntry()) != null && !entry.isDirectory()) {
                    Fout = new File(Parent, entry.getName());
                    decompressedPath = Parent + entry.getName();
                    if (!Fout.exists()) {
                        (new File(Fout.getParent())).mkdirs();
                    }
                    FileOutputStream out = new FileOutputStream(Fout);
                    BufferedOutputStream Bout = new BufferedOutputStream(out);
                    int b;
                    while ((b = Bin.read()) != -1) {
                        Bout.write(b);
                    }
                    Bout.flush();
                    Bout.close();
                    out.close();
                }
                Bin.close();
                Zin.close();
                LogUtil.d("chu", "--解压成功--");
                return decompressedPath;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
