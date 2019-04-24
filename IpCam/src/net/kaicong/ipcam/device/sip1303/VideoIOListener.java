package net.kaicong.ipcam.device.sip1303;

/**
 * Created by LingYan on 2014/11/5 0005.
 */
public interface VideoIOListener<T> {

    public void videoSocketConnected(T t);

    public void videoSocketConnectFailed(T t);

    public void videoSocketDisconnected(T t);

    public void getVideoInfoSuccess(T t, int videoInfo[]);

    public void getPacketException();

}
