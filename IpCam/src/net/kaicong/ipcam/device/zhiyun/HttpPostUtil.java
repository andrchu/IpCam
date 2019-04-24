
package net.kaicong.ipcam.device.zhiyun;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;


public class HttpPostUtil {
    URL url;
    HttpURLConnection conn;
    String boundary = "---------------------------7de37494e0ab0";
    DataOutputStream ds;
    String filePath;
    String authedStr;

    public HttpPostUtil(String url, String filePath, String authedStr) {
        try {
            this.url = new URL(url);
        } catch (Exception e) {

        }
        this.filePath = filePath;
        this.authedStr = authedStr;
    }

    //重新设置要请求的服务器地址，即上传文件的地址。
    public void setUrl(String url) throws Exception {
        this.url = new URL(url);
    }

    // 发送数据到服务器，返回一个字节包含服务器的返回结果的数组
    public byte[] send() throws Exception {
        initConnection();
        try {
            conn.connect();
        } catch (SocketTimeoutException e) {
            // something
            throw new RuntimeException();
        }
        ds = new DataOutputStream(conn.getOutputStream());
        writeFileParams();
        InputStream in = conn.getInputStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int b;
        while ((b = in.read()) != -1) {
            out.write(b);
        }
        conn.disconnect();
        byte result[] = out.toByteArray();
        out.close();
        in.close();
        return result;
    }

    //文件上传的connection的一些必须设置
    private void initConnection() throws Exception {
        conn = (HttpURLConnection) this.url.openConnection();
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setConnectTimeout(10000); //连接超时为10秒
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "textml, application/xhtml+xml, */*");
        conn.setRequestProperty("User_Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; QQBrowser/7.6.21109.400");
        conn.setRequestProperty("Accept-Language", "zh-CN");
        conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
        conn.setRequestProperty("Authorization", authedStr);
    }

    //文件数据
    private void writeFileParams() throws Exception {
        ds.writeBytes("--" + boundary + "\r\n");
        ds.writeBytes("Content-Disposition: form-data; name=\"language\"\r\n\r\ncn\r\n");
        ds.writeBytes("--" + boundary + "\r\n");
        ds.writeBytes("Content-Disposition: form-data; name=\"type\"\r\n\r\nifu\r\n");
        ds.writeBytes("--" + boundary + "\r\n");
        ds.writeBytes("Content-Disposition: form-data; name=\"fileName\"; filename=\"" + filePath + "\"\r\nContent-Type: application/octet-stream\r\n\r\n");
        ds.write(getBytes(new File(filePath)));
        ds.writeBytes("\r\n--" + boundary + "--\r\n");
    }

    //把文件转换成字节数组
    private byte[] getBytes(File f) throws Exception {
        FileInputStream in = new FileInputStream(f);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] b = new byte[1024];
        int n;
        while ((n = in.read(b)) != -1) {
            out.write(b, 0, n);
        }
        in.close();
        return out.toByteArray();
    }

}
