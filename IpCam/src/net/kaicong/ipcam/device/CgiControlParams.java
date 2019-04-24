package net.kaicong.ipcam.device;

import net.kaicong.ipcam.device.cgi.CgiImageAttr;
import net.kaicong.ipcam.device.cgi.CgiUtils;
import net.kaicong.ipcam.utils.LogUtil;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * cgi控制命令
 * Created by LingYan on 2014/10/24 0024.
 */
public class CgiControlParams {

    public interface OnCgiTaskListener<T> {

        public void onCgiTaskFinished(T result);

    }

    public interface OnCgiTaskFinishedListener {

        public void onTaskFinished();

    }

    private static final String HTTP_PROTOCOL = "http://";

    //PTZ UP
    private static final String PTZ_UP_URL = "/cgi-bin/hi3510/ptzup.cgi";
    //PTZ DOWN
    private static final String PTZ_DOWN_URL = "/cgi-bin/hi3510/ptzdown.cgi";
    //PTZ LEFT
    private static final String PTZ_LEFT_URL = "/cgi-bin/hi3510/ptzleft.cgi";
    //PTZ RIGHT
    private static final String PTZ_RIGHT_URL = "/cgi-bin/hi3510/ptzright.cgi";
    //snap host
    private static final String CGI_SNAP_HOST = "/web/tmpfs/snap.jpg";
    //reinit 1018 camera
    private static final String CGI_REINIT_1018_CAMERA = "/set_params.cgi";
    //get image attr
    private static final String CGI_GET_IMAGE_ATTR = "/cgi-bin/hi3510/param.cgi?cmd=getimageattr";
    //set image attr
    private static final String CGI_SET_IMAGE_ATTR = "/cgi-bin/hi3510/param.cgi?cmd=setimageattr";
    //get our ddns attr
    private static final String CGI_GET_DDNS_ATTR = "/cgi-bin/hi3510/param.cgi?cmd=getourddnsattr";
    //水品巡航
    private static final String CGI_SET_HSCAN = "/cgi-bin/hi3510/param.cgi?cmd=ptzctrl&-step=0&-act=hscan&-speed=45";
    //垂直巡航
    private static final String CGI_SET_VSCAN = "/cgi-bin/hi3510/param.cgi?cmd=ptzctrl&-step=0&-act=vscan&-speed=45";
    //停止巡航
    private static final String CGI_SET_SCAN_STOP = "/cgi-bin/hi3510/param.cgi?cmd=ptzctrl&-step=0&-act=stop&-speed=45";
    //1601设备检测
    private static final String CGI_CHECK_SIP1601 = "/get_status.cgi";
    //1601 PTZ
    private static final String CGI_1601_PTZ = "/decoder_control.cgi?onestep=&sit=&next_url=&command=";

    /**
     * 1303 cgi向上步进
     *
     * @param ip
     * @param port
     * @return
     */
    public static String getPtzUpUrl(String ip, int port) {
        return HTTP_PROTOCOL + ip + ":" + port + PTZ_UP_URL;
    }

    /**
     * 1303 cgi向下步进
     *
     * @param ip
     * @param port
     * @return
     */
    public static String getPtzDownUrl(String ip, int port) {
        return HTTP_PROTOCOL + ip + ":" + port + PTZ_DOWN_URL;
    }

    /**
     * 1303 cgi向左步进
     *
     * @param ip
     * @param port
     * @return
     */
    public static String getPtzLeftUrl(String ip, int port) {
        return HTTP_PROTOCOL + ip + ":" + port + PTZ_LEFT_URL;
    }

    /**
     * 1303 cgi向有右步进
     *
     * @param ip
     * @param port
     * @return
     */
    public static String getPtzRightUrl(String ip, int port) {
        return HTTP_PROTOCOL + ip + ":" + port + PTZ_RIGHT_URL;
    }

    public static String getReinitCameraUrl(String ip, int port) {
        return HTTP_PROTOCOL + ip + ":" + port + CGI_REINIT_1018_CAMERA;
    }

    /**
     * 1303 cgi 获取设备播放参数
     *
     * @param ip
     * @param port
     * @return
     */
    public static String getImageAttrUrl(String ip, int port) {
        return HTTP_PROTOCOL + ip + ":" + port + CGI_GET_IMAGE_ATTR;
    }

    /**
     * 1303 设置设备播放参数
     *
     * @param ip
     * @param port
     * @return
     */
    public static String getSetImageAttrUrl(String ip, int port) {
        return HTTP_PROTOCOL + ip + ":" + port + CGI_SET_IMAGE_ATTR;
    }

    /**
     * 1303 cgi检测设备是否在线
     *
     * @param ip
     * @param port
     * @return
     */
    public static String getDDNSAttr(String ip, int port) {
        return HTTP_PROTOCOL + ip + ":" + port + CGI_GET_DDNS_ATTR;
    }

    /**
     * 1303 cgi水平巡航
     *
     * @param ip
     * @param port
     * @return
     */
    public static String getHSCANUrl(String ip, int port) {
        return HTTP_PROTOCOL + ip + ":" + port + CGI_SET_HSCAN;
    }

    /**
     * 1303 cgi垂直巡航
     *
     * @param ip
     * @param port
     * @return
     */
    public static String getVSCANUrl(String ip, int port) {
        return HTTP_PROTOCOL + ip + ":" + port + CGI_SET_VSCAN;
    }

    /**
     * 1303 cgi停止巡航
     *
     * @param ip
     * @param port
     * @return
     */
    public static String getSCANStopUrl(String ip, int port) {
        return HTTP_PROTOCOL + ip + ":" + port + CGI_SET_SCAN_STOP;
    }

    /**
     * 检查1601设备是否在线
     *
     * @param ip
     * @param port
     * @return
     */
    public static String checkSip1601Device(String ip, int port) {
        return HTTP_PROTOCOL + ip + ":" + port + CGI_CHECK_SIP1601;
    }

    public static String checkSip1211Device(String ip, int port, String account, String password) {
        return HTTP_PROTOCOL + ip + ":" + port + "/cgi-bin/check_user.cgi?user=" + account + "&pwd=" + password;
    }

    public static String checkSip1406Device(String ip, int port, String account, String password) {
        return HTTP_PROTOCOL + ip + ":" + port + "/get_camera_params.cgi";
    }

    public static String checkSip1120Device(String ip, int port, String account, String password) {
        return HTTP_PROTOCOL + ip + ":" + port + "/cgi-bin/hi3510/param.cgi?cmd=getimageattr";
    }

    private AsyncHttpClient httpClient = null;

    public CgiControlParams(String username, String password) {
        httpClient = new AsyncHttpClient();
        //http basic认证
        httpClient.setBasicAuth(username, password);
        //超时10秒
        httpClient.setTimeout(10 * 1000);
    }

    public CgiControlParams() {
        httpClient = new AsyncHttpClient();
        //超时10秒
        httpClient.setTimeout(10 * 1000);
    }

    public AsyncHttpClient getHttpClient() {
        return httpClient;
    }

    //cgi get request
    public void doCgiGetRequest(String url, RequestParams params) {

        httpClient.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                LogUtil.d("chu", "result" + new String(bytes));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.d("chu", "send failure" + throwable.toString());
            }
        });
    }

    /**
     * 获取cgi图像属性(1303)
     *
     * @param url
     * @param onCgiTaskFinished
     */
    public void getImageAttr(String url, final OnCgiTaskListener<CgiImageAttr> onCgiTaskFinished) {

        RequestParams params = new RequestParams();
        httpClient.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                JSONObject object = CgiUtils.convertCgiStr2Json(new String(bytes));
                if (object != null) {
                    CgiImageAttr cgiImageAttr = new CgiImageAttr();
                    cgiImageAttr.displayMode = object.optInt("display_mode");
                    cgiImageAttr.saturation = object.optInt("saturation");
                    cgiImageAttr.brightness = object.optInt("brightness");
                    cgiImageAttr.contrast = object.optInt("contrast");
                    cgiImageAttr.sharpness = object.optInt("sharpness");
                    cgiImageAttr.flip = object.optString("flip").equalsIgnoreCase("on");
                    cgiImageAttr.mirror = object.optString("mirror").equalsIgnoreCase("on");
                    cgiImageAttr.night = object.optString("night").equalsIgnoreCase("on");
                    if (onCgiTaskFinished != null) {
                        onCgiTaskFinished.onCgiTaskFinished(cgiImageAttr);
                    }
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.d("chu", "send failure" + throwable.toString());
            }
        });
    }

    /**
     * cgi设置亮度(1303)
     */
    public void setCgiBrightness(String ip, int port, int mBrightness) {
        RequestParams params = new RequestParams();
        String urlStr = HTTP_PROTOCOL + ip + ":" + port + CGI_SET_IMAGE_ATTR + "&-brightness=" + mBrightness;
        httpClient.get(urlStr, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                LogUtil.d("chu", "send success" + new String(bytes));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.d("chu", "send failure" + throwable.toString());
            }
        });
    }

    /**
     * cgi设置饱和度(1303)
     */
    public void setCgiSaturation(String ip, int port, int mSaturation) {
        RequestParams params = new RequestParams();
        String urlStr = HTTP_PROTOCOL + ip + ":" + port + CGI_SET_IMAGE_ATTR + "&-saturation=" + mSaturation;
        httpClient.get(urlStr, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                LogUtil.d("chu", "send success" + new String(bytes));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.d("chu", "send failure" + throwable.toString());
            }
        });
    }

    /**
     * cgi设置对比度(1303)
     */
    public void setCgiContrast(String ip, int port, int mContrast) {
        RequestParams params = new RequestParams();
        String urlStr = HTTP_PROTOCOL + ip + ":" + port + CGI_SET_IMAGE_ATTR + "&-contrast=" + mContrast;
        httpClient.get(urlStr, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                LogUtil.d("chu", "send success" + new String(bytes));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.d("chu", "send failure" + throwable.toString());
            }
        });
    }

    /**
     * cgi设置翻转(1303)
     */
    public void setCgiFlip(String ip, int port, String isOnStr) {
        RequestParams params = new RequestParams();
        String urlStr = HTTP_PROTOCOL + ip + ":" + port + CGI_SET_IMAGE_ATTR + "&-flip=" + isOnStr;
        httpClient.get(urlStr, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                LogUtil.d("chu", "send success" + new String(bytes));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.d("chu", "send failure" + throwable.toString());
            }
        });
    }

    /**
     * cgi设置镜像(1303)
     */
    public void setCgiMirror(String ip, int port, String isOnStr) {
        RequestParams params = new RequestParams();
        String urlStr = HTTP_PROTOCOL + ip + ":" + port + CGI_SET_IMAGE_ATTR + "&-mirror=" + isOnStr;
        httpClient.get(urlStr, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                LogUtil.d("chu", "send success" + new String(bytes));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.d("chu", "send failure" + throwable.toString());
            }
        });
    }

    /**
     * cgi设置夜间模式(1303)
     */
    public void setCgiNightMode(String ip, int port, String isOnStr) {
        RequestParams params = new RequestParams();
        String urlStr = HTTP_PROTOCOL + ip + ":" + port + CGI_SET_IMAGE_ATTR + "&-night=" + isOnStr;
        httpClient.get(urlStr, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                LogUtil.d("chu", "send success" + new String(bytes));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.d("chu", "send failure" + throwable.toString());
            }
        });
    }

    /**
     * 1601 cgi control
     *
     * @param ip
     * @param port
     * @param user
     * @param pwd
     * @param ptzCmd
     */
    public void set1601PTZControl(String ip, int port, String user, String pwd, int ptzCmd) {
        RequestParams params = new RequestParams();
        String urlStr = HTTP_PROTOCOL + ip + ":" + port + CGI_1601_PTZ + ptzCmd + "&user=" + user + "&pwd=" + pwd;
        httpClient.get(urlStr, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                LogUtil.d("chu", "send success" + new String(bytes));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.d("chu", "send failure" + throwable.toString());
            }
        });
    }

    /**
     * 1601 cgi control
     *
     * @param ip
     * @param port
     * @param user
     * @param pwd
     * @param ptzCmd
     */
    public void set1601PTZControlOneStep(String ip, int port, String user, String pwd, int ptzCmd) {
        RequestParams params = new RequestParams();
        String urlStr = HTTP_PROTOCOL + ip + ":" + port + "/decoder_control.cgi?onestep=0&sit=&next_url=&command=" + ptzCmd + "&user=" + user + "&pwd=" + pwd;
        httpClient.get(urlStr, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                LogUtil.d("chu", "send success" + new String(bytes));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.d("chu", "send failure" + throwable.toString());
            }
        });
    }

    /**
     * 获取1601图像参数
     *
     * @param ip
     * @param port
     * @param user
     * @param pwd
     */
    public void get1601ImageAttr(String ip, int port, String user, String pwd, final OnCgiTaskListener<CgiImageAttr> onCgiTaskListener) {
        RequestParams params = new RequestParams();
        String urlStr = HTTP_PROTOCOL + ip + ":" + port + "/get_camera_params.cgi?user=" + user + "&pwd=" + pwd;
        httpClient.get(urlStr, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                JSONObject object = CgiUtils.convertCgiStr2Json(new String(bytes));
                if (object != null) {
                    CgiImageAttr cgiImageAttr = new CgiImageAttr();
                    cgiImageAttr.resolution = object.optInt("resolution");
                    cgiImageAttr.displayMode = object.optInt("mode");
                    cgiImageAttr.saturation = object.optInt("vsaturation");
                    cgiImageAttr.brightness = object.optInt("vbright");
                    cgiImageAttr.contrast = object.optInt("vcontrast");
                    cgiImageAttr.flip1601 = object.optInt("flip");
                    if (onCgiTaskListener != null) {
                        onCgiTaskListener.onCgiTaskFinished(cgiImageAttr);
                    }
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.d("chu", "send failure" + throwable.toString());
            }

        });
    }

    /**
     * 1211图像参数获取
     *
     * @param ip
     * @param port
     * @param user
     * @param pwd
     * @param onCgiTaskListener
     */
    public void get1211ImageAttr(String ip, int port, String user, String pwd, final OnCgiTaskListener<CgiImageAttr> onCgiTaskListener) {
        RequestParams params = new RequestParams();
        String urlStr = HTTP_PROTOCOL + ip + ":" + port + "/cgi-bin/get_camera_vars.cgi?user=" + user + "&pwd=" + pwd;
        httpClient.get(urlStr, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                JSONObject object = CgiUtils.convertCgiStr2Json(new String(bytes));
                if (object != null) {
                    CgiImageAttr cgiImageAttr = new CgiImageAttr();
                    cgiImageAttr.displayMode = object.optInt("aec_value") - 1;
                    cgiImageAttr.saturation = object.optInt("saturation");
                    cgiImageAttr.brightness = object.optInt("brightness");
                    cgiImageAttr.contrast = object.optInt("contrast");
                    cgiImageAttr.flip = object.optInt("flip") == 1;
                    cgiImageAttr.mirror = object.optInt("mirror") == 1;
                    if (onCgiTaskListener != null) {
                        onCgiTaskListener.onCgiTaskFinished(cgiImageAttr);
                    }
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.d("chu", "send failure" + throwable.toString());
            }

        });
    }


    /**
     * 设置1601图像参数
     *
     * @param ip
     * @param port
     * @param user
     * @param pwd
     */
    public void set1601ImageAttr(String ip, int port, String user, String pwd, String key, String value, final OnCgiTaskFinishedListener onCgiTaskFinishedListener) {
        RequestParams params = new RequestParams();
        String urlStr = HTTP_PROTOCOL + ip + ":" + port + "/camera_control.cgi?param=" + key + "&value=" + value + "&user=" + user + "&pwd=" + pwd;
        httpClient.get(urlStr, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                LogUtil.d("chu", "send success");
                if (onCgiTaskFinishedListener != null) {
                    onCgiTaskFinishedListener.onTaskFinished();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.d("chu", "send failure" + throwable.toString());
            }

        });
    }

    /**
     * 设置1211图像参数
     *
     * @param ip
     * @param port
     * @param user
     * @param pwd
     */
    public void set1211ImageAttr(String ip, int port, String user, String pwd, String key, String value, final OnCgiTaskFinishedListener onCgiTaskFinishedListener) {
        RequestParams params = new RequestParams();
        String urlStr = HTTP_PROTOCOL + ip + ":" + port + "/cgi-bin/set_camera_vars.cgi?type=" + key + "&value=" + value + "&user=" + user + "&pwd=" + pwd + "&next_url=";
        httpClient.get(urlStr, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                LogUtil.d("chu", "send success");
                if (onCgiTaskFinishedListener != null) {
                    onCgiTaskFinishedListener.onTaskFinished();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.d("chu", "send failure" + throwable.toString());
            }

        });
    }

    /**
     * 1211 ptz control
     *
     * @param ip
     * @param port
     * @param user
     * @param pwd
     * @param ptzCmd
     */
    public void set1211PTZControl(String ip, int port, String user, String pwd, int ptzCmd, boolean isOneStep) {
        RequestParams params = new RequestParams();
        String urlStr;
        if (isOneStep) {
            urlStr = HTTP_PROTOCOL + ip + ":" + port + "/cgi-bin/decoder_control.cgi?type=0&cmd=" + ptzCmd + "&user=" + user + "&pwd=" + pwd + "&degree=&onestep=1&next_url=";
        } else {
            urlStr = HTTP_PROTOCOL + ip + ":" + port + "/cgi-bin/decoder_control.cgi?type=0&cmd=" + ptzCmd + "&user=" + user + "&pwd=" + pwd + "&degree=&onestep=&next_url=";
        }
        httpClient.get(urlStr, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                LogUtil.d("chu", "send success" + new String(bytes));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.d("chu", "send failure" + throwable.toString());
            }
        });
    }

    /**
     * 设置1212镜头拉伸
     *
     * @param ip
     * @param port
     * @param user
     * @param pwd
     */
    public void set1212Zoom(String ip, int port, String user, String pwd, int zoom) {
        RequestParams params = new RequestParams();
        String urlStr = "";
        if (zoom == 1) {
            //zoom in
            urlStr = HTTP_PROTOCOL + ip + ":" + port + "/cgi-bin/hi3510/param.cgi?cmd=ptzctrl&-step=0&-act=zoomin&-speed=45";
        } else if (zoom == 2) {
            //zoom out
            urlStr = HTTP_PROTOCOL + ip + ":" + port + "/cgi-bin/hi3510/param.cgi?cmd=ptzctrl&-step=0&-act=zoomout&-speed=45";
        } else if (zoom == -1) {
            //stop
            urlStr = HTTP_PROTOCOL + ip + ":" + port + CGI_SET_SCAN_STOP;
        }
        httpClient.get(urlStr, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                LogUtil.d("chu", "send success");
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                LogUtil.d("chu", "send failure" + throwable.toString());
            }

        });
    }

    //cgi post request
    public void doCgiPostRequest(String url, RequestParams params, AsyncHttpResponseHandler asyncHttpResponseHandler) {

        httpClient.post(url, params, asyncHttpResponseHandler);

    }

    /**
     * 生成cgi访问的url,以下的图片访问均需要添加Authorization验证
     * B1系列的IPCAM SIP1016不需要验证
     *
     * @param cameraMode
     * @param ip
     * @param port
     * @param user
     * @param password
     * @return
     */

    public static String getCgiSnapShotUrl(int cameraMode, String ip, int port, String user, String password) {
        String resultUrl = "";

        /**
         *信息采集
         */
        switch (cameraMode) {
            /**
             *B系列的IPCAM SIP1018技术方案
             *sip428                  428                 SIP1018
             *sip1017                 1017                SIP1018
             *sip1018                 1018                SIP1018
             *sip1018w                10180000            SIP1018
             *sip1018l                10180011            SIP1018
             *sip1019                 1019                SIP1018
             *sip1020                 1020                SIP1018
             *sip1021                 1021                SIP1018
             *sip1022                 1022                SIP1018
             *                        902                 SIP1018         //902是为了兼容老版本
             *                        910                 SIP1018         //910是为了兼容老版本
             */
            case 428:
            case 1017:
            case 1018:
            case 10180000:
            case 10180011:
            case 1019:
            case 1020:
            case 1021:
            case 1022:
            case 902://902是为了兼容老版本
            case 910://910是为了兼容老版本
            {
                resultUrl = "http://" + ip + ":" + port + "/snapshot.cgi";
            }
            break;

            /**
             *F系列的IPCAM SIP1406技术方案
             *sip1306                 1306                SIP1406
             *sip1306w                13060000            SIP1406
             *sip1406                 1406                SIP1406
             *                        916                 SIP1406         //916是为了兼容老版本
             *                        918                 SIP1406         //918是为了兼容老版本
             */
            case 1306:
            case 13060000:
            case 1406:
            case 916://916是为了兼容老版本
            case 918://918是为了兼容老版本
            {
                resultUrl = "http://" + ip + ":" + port + "/snapshot.cgi?user=" + user + "&pwd=" + password;
            }
            break;

            /**
             *H系列的IPCAM SIP1120技术方案
             *sip264                  264                 SIP1120
             *sip1113                 1113                SIP1120
             *sip1118                 1118                SIP1120
             *sip1119                 1119                SIP1120
             *sip1120                 1120                SIP1120
             *sip1121                 1121                SIP1120
             *sip1128                 1128                SIP1120
             *sip1308                 1308                SIP1120
             *                        912                 SIP1120         //912是为了兼容老版本
             */
            case 264:
            case 1113:
            case 1118:
            case 1119:
            case 1120:
            case 1121:
            case 1128:
            case 1308:
            case 912://912是为了兼容老版本
            {
                resultUrl = "http://" + ip + ":" + port + "/tmpfs/auto.jpg";
            }
            break;

            /**
             *M系列的IPCAM SIP1201技术方案
             *sip1201                 1201                SIP1201
             *sip1202w                12020000            SIP1201
             *sip1203                 1203                SIP1201
             *sip1204                 1204                SIP1201
             *sip1205                 1205                SIP1201
             *sip1206                 1206                SIP1201
             *sip1207                 1207                SIP1201
             *sip1210                 1210                SIP1201
             *sip1213                 1213                SIP1201
             *sip1214                 1214                SIP1201
             *sip1215                 1215                SIP1201
             *sip1202w                1202                SIP1201
             *                        914                 SIP1201         //914是为了兼容老版本
             */

            case 1201:
            case 1202:
            case 12020000:
            case 1203://新增M系列设备六台
            case 1204:
            case 1205:
            case 1206:
            case 1207:
            case 1210:
            case 1213:
            case 1214:
            case 1215:
            case 914://914是为了兼容老版本
            case 1212: {
                resultUrl = "http://" + ip + ":" + port + "/tmpfs/auto.jpg";
            }
            break;

            /**
             *B1系列的IPCAM SIP1016技术方案
             *sip1016                 1016                SIP1016
             *                        908                 SIP1016         //908是为了兼容老版本
             */
            case 908:
            case 1016: {
                resultUrl = "http://" + ip + ":" + port + "/snap.jpg?user=" + user + "&password=" + password;
            }
            break;


            /**
             *新增的1601系列设备 SIP1601技术方案
             *sip1601                 1601                SIP1601
             *sip1602                 1602                SIP1601
             *sip1603                 1603                SIP1601
             *sip1604                 1604                SIP1601
             *sip1605                 1605                SIP1601
             *sip1606w                16060000            SIP1601
             *sip1606w                1606                SIP1601
             */
            case 1601:
            case 1602:
            case 1603:
            case 1604:
            case 1605:
            case 16060000:
            case 1606:
            case 913: {
                resultUrl = "http://" + ip + ":" + port + "/snapshot.cgi";
            }
            break;

            /**
             *新增的1211系列设备 SIP1211技术方案
             *sip1211                 1211                SIP1211
             */
            case 1211: {
                resultUrl = "http://" + ip + ":" + port + "/cgi-bin/video_snapshot.cgi?user=" + user + "&pwd=" + password;
            }
            break;

            /**
             *新增的1303系列设备 SIP1303技术方案
             *SIP1303                 1303                SIP1303
             */
            case 1305:
            case 1303: {
                resultUrl = "http://" + ip + ":" + port + "/tmpfs/auto.jpg";
            }
            break;

            /**
             *智云设备
             */
            case 0: {
            }
//            break;

            default: {
                resultUrl = "http://" + ip + ":" + port + "/snapshot.cgi";
            }
            break;
        }

        return resultUrl;
    }

    public void doCgiGet(String url, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        httpClient.get(url, asyncHttpResponseHandler);
    }

}
