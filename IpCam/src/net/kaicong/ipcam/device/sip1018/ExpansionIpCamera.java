package net.kaicong.ipcam.device.sip1018;

import net.reecam.ipc.IpCamera;

/**
 * 扩展IpCamera类，因为IpCamera里没有一个标记自己的position(这个position在MyDeviceFragment类中有用到)
 * ，所以添加一个position来标记他
 * Created by LingYan on 2014/10/8 0008.
 */
public class ExpansionIpCamera extends IpCamera {

    public ExpansionIpCamera(String id, String name, String host, String port,
                             String user, String password) {
        super(id, name, host, port, user, password, MyIpCamera.DEFAULT_AUDIO_BUFFER_TIME);
    }

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
