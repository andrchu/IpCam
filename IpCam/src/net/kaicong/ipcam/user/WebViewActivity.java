package net.kaicong.ipcam.user;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.R;

/**
 * Created by LingYan on 15/6/30.
 */
public class WebViewActivity extends BaseActivity {

    private WebView webView;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle(getString(R.string.user_agreement));
        showBackButton();
        setContentView(R.layout.activity_wap_pay);
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
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

    }

}
