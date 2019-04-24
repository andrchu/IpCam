package net.kaicong.ipcam.device;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.R;

/**
 * Created by LingYan on 15-1-9.
 */
public class WapPayActivity extends BaseActivity {

    private WebView webView;
    private String mUrl;
    private boolean isPaySuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wap_pay);
        initTitle(getString(R.string.device_property_wap_pay));
        showBackButton();
        mUrl = getIntent().getStringExtra("load_url");
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new MyBrowser());

        CookieManager.getInstance().setAcceptCookie(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(mUrl);
    }

    private class MyBrowser extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //回调地址如果等于http://www.kaicongyun.com/AliPayWap/ReturnUrl，表示支付已经成功
            if (url.contains("http://www.kaicongyun.com/AliPayWap/ReturnUrl")) {
                isPaySuccess = true;
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            quit();
        }
        return true;
    }

    @Override
    public void doBackButtonAction() {
        quit();
    }

    private void quit() {
        if (isPaySuccess) {
            setResult(RESULT_OK);
        }
        this.finish();
    }

}
