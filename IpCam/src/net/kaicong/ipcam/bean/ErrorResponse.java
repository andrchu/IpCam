package net.kaicong.ipcam.bean;


import net.kaicong.ipcam.utils.StringUtils;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by LingYan on 2014/9/1.
 */
public class ErrorResponse implements Serializable {

    private int ErrCode = 0;
    private boolean hasError = false;

    //根据code的值来判断返回的json数据
    public boolean IsError(String json) {
        try {
            JSONObject object = new JSONObject(json);
            if (object.has("code")) {
                int code = object.optInt("code");
                if (code != 1) {
                    hasError = true;
                }
                if (object.has("item")) {
                    if (StringUtils.isEmpty(object.optString("item"))) {
                        hasError = true;
                    }
                }
                this.setErrCode(code);
            }
        } catch (Exception e) {
            hasError = true;
        }
        return hasError;
    }

    public int getErrCode() {
        return this.ErrCode;
    }

    public void setErrCode(int errCode) {
        this.ErrCode = errCode;
    }

    public boolean hasError() {
        return hasError;
    }

}
