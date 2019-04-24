package net.kaicong.ipcam.device.cgi;

import net.kaicong.ipcam.utils.LogUtil;

import org.json.JSONObject;

/**
 * Created by LingYan on 2014/11/20 0020.
 */
public class CgiUtils {

    /**
     * 将cgi返回的字符串转化为json对象
     * 例如var display_mode="1"; var brightness="69"; var saturation="94";
     * var sharpness="80"; var contrast="38"; var hue="50"; var wdr="off";
     * var night="off"; var shutter="65535"; var flip="off"; var mirror="on";
     * var gc="31744"; var ae="8"; var targety="150"; var noise="off"; var gamma="1";
     * var aemode="0"; var imgmode="0";
     *
     * @param cgiStr
     * @return
     */
    public static JSONObject convertCgiStr2Json(String cgiStr) {
        JSONObject jsonObject = null;
        try {
            String result = cgiStr.replace("var ", "\"").replace(";", ",").replace("=", "\":");
            result = result.substring(0, result.lastIndexOf(","));
            String temp = "{" + result + "}";
            jsonObject = new JSONObject(temp);
        } catch (Exception e) {
            LogUtil.d("chu", "exception" + "CGI TO JSON FAILED");
        }
        return jsonObject;
    }

}
