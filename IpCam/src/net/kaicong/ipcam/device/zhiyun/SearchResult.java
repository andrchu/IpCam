package net.kaicong.ipcam.device.zhiyun;

import java.io.Serializable;

/**
 * Created by LingYan on 2014/9/28 0028.
 */
public class SearchResult implements Serializable {

    private String UID;
    private String IP;

    public boolean isExists() {
        return isExists;
    }

    public void setExists(boolean isExists) {
        this.isExists = isExists;
    }

    private boolean isExists;

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }
}
