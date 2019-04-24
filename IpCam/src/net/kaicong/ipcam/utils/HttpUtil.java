package net.kaicong.ipcam.utils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.security.KeyStore;

/**
 * @author LingYan
 */

public class HttpUtil {

    private static HttpUtil httpUtil;
    private AsyncHttpClient client;

    public static HttpUtil getInstance() {
        if (httpUtil == null) {
            httpUtil = new HttpUtil();
        }
        return httpUtil;
    }

    private AsyncHttpClient getClient() {
        if (httpUtil.client == null) {
            /**
             * Trusting all certificates using HttpClient over HTTPS
             * @see http://stackoverflow.com/questions/11573108/self-signed-certificate-and-loopj-for-android?nsukey=qhocs5k6GCqSc%2BzdQiifBY3My9pBUOpfWUUwMQpOv5zvO6cbEONqUBuasonbvUkU8C2jpYdzvwFFDhtoBoEt0w%3D%3D
             */
            try {
                httpUtil.client = new AsyncHttpClient();
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                trustStore.load(null, null);
                MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
                sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                httpUtil.client.setSSLSocketFactory(sf);
                //超时10s
                httpUtil.client.setTimeout(10000);
            } catch (Exception e) {
            }
        }
        return httpUtil.client;
    }

    //get请求
    public void doGet(String url, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        getClient().get(url, asyncHttpResponseHandler);
    }

    //get请求
    public void doGet(String url, RequestParams params, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        getClient().get(url, params, asyncHttpResponseHandler);
    }

    //post请求
    public void doPost(String url, RequestParams params, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        getClient().post(url, params, asyncHttpResponseHandler);
    }

}
