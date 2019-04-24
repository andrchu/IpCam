package net.kaicong.ipcam.device.sip1303;

import java.io.Serializable;

/**
 * Created by LingYan on 2014/10/23 0023.
 */
public class Sip1303Camera implements Serializable{

    public String ip;
    public int port;
    public String account;
    public String password;
    public int position;

    public Sip1303Camera(String ip, int port, String account, String password, int position){
        this.ip = ip;
        this.port = port;
        this.account = account;
        this.password = password;
        this.position = position;
    }

}
