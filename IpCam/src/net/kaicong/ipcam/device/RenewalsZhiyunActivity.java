package net.kaicong.ipcam.device;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.R;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.user.UserAccount;

import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.ipcam.view.PaySelectDialog;
import net.kaicong.ipcam.wxpay.Constants;
import net.kaicong.ipcam.wxpay.MD5;
import net.kaicong.ipcam.wxpay.Util;
import net.kaicong.kcalipay.PayResultListener;
import net.kaicong.kcalipay.PayUtils;
import net.kaicong.utility.ApiClientUtility;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by LingYan on 15-1-6.
 */
public class RenewalsZhiyunActivity extends BaseActivity {

    public static final int RESULT_WAP_PAY = 1999;

    private TextView renewalsZCloudNum;
    private Button renewalsDate;
    private TextView renewalsPrice;
    private Button renewalsBtnOk;

    private String zCloudNum;
    private PaySelectDialog paySelectDialog;
    private PayUtils payUtils;

    private PayReq req;
    private final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
    private String ourOrderId = "";
    public static int WX_PAY_RESULT_CODE = -99;
    private boolean isWxPay = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_renewals_zhiyun);
        initTitle(getString(R.string.device_property_renew));
        showBackButton();
        zCloudNum = getIntent().getStringExtra("zcloud");

        renewalsZCloudNum = (TextView) findViewById(R.id.renewals_zcloud_num);
        renewalsZCloudNum.setText(zCloudNum);
        renewalsDate = (Button) findViewById(R.id.renewals_zcloud_date);
        renewalsPrice = (TextView) findViewById(R.id.renewals_zcloud_price);
        renewalsBtnOk = (Button) findViewById(R.id.btn_ok);
        renewalsBtnOk.setOnClickListener(this);
        payUtils = new PayUtils(this);

        req = new PayReq();
        msgApi.registerApp(Constants.APP_ID);

    }

    @Override
    protected void onPause() {
        super.onPause();
        WX_PAY_RESULT_CODE = -99;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isWxPay) {
            if (WX_PAY_RESULT_CODE == 0) {
                //支付成功
                LogUtil.d("chu", "支付成功");
                makeToast("支付成功");
                Intent resultData = new Intent();
                resultData.putExtra("number", 12);
                RenewalsZhiyunActivity.this.setResult(RESULT_OK, resultData);
                RenewalsZhiyunActivity.this.finish();
            } else if (WX_PAY_RESULT_CODE == -1) {
                //支付失败（签名或者其它错误）
                makeToast("支付失败");
            } else if (WX_PAY_RESULT_CODE == -2) {
                //支付取消
                makeToast("支付取消");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WX_PAY_RESULT_CODE = -99;
    }

    /**
     * 微信支付
     */
    private void getWXOrderNo() {
        Map<String, String> map = new HashMap<>();
        map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
        map.put(CameraConstants.ZCLOUD_Id, zCloudNum);
        map.put("ServiceId", "9200");
        map.put("TotalQty", "1");/*!< 消费次数 */
        map.put("PayType", "50");
        doPost(UrlResources.URL_PAY_WEIXIN, ApiClientUtility.getParams(map), new VolleyResponse(this, true, getString(R.string.com_facebook_loading)) {

            @Override
            protected void onTaskSuccessRoot(JSONObject jsonObject) {
                super.onTaskSuccessRoot(jsonObject);
//                ourOrderId = jsonObject.optString("Order_Id");
//                GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
//                getPrepayId.execute();
                //获得签名
                req.appId = jsonObject.optString("appId");
                req.partnerId = jsonObject.optString("partnerId");
                req.prepayId = jsonObject.optString("prepayId");
                req.packageValue = jsonObject.optString("package");
                req.nonceStr = jsonObject.optString("nonceStr");
                req.timeStamp = jsonObject.optString("timeStamp");

                List<NameValuePair> signParams = new LinkedList<NameValuePair>();
                signParams.add(new BasicNameValuePair("appid", req.appId));
                signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
                signParams.add(new BasicNameValuePair("package", req.packageValue));
                signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
                signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
                signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

                req.sign = genAppSign(signParams);
//                req.sign = jsonObject.optString("sign");

                //发起支付
                msgApi.registerApp(Constants.APP_ID);
                msgApi.sendReq(req);
                isWxPay = true;
            }
        });
    }

    private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>> {

        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected void onPostExecute(Map<String, String> result) {

            //获得签名
            req.appId = Constants.APP_ID;
            req.partnerId = Constants.MCH_ID;
            req.prepayId = result.get("prepay_id");
            req.packageValue = "Sign=WXPay";
            req.nonceStr = genNonceStr();
            req.timeStamp = String.valueOf(genTimeStamp());

            List<NameValuePair> signParams = new LinkedList<NameValuePair>();
            signParams.add(new BasicNameValuePair("appid", req.appId));
            signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
            signParams.add(new BasicNameValuePair("package", req.packageValue));
            signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
            signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
            signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

            req.sign = genAppSign(signParams);

            //发起支付
            msgApi.registerApp(Constants.APP_ID);
            msgApi.sendReq(req);
            removeProgressDialog();
            isWxPay = true;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            removeProgressDialog();
        }

        @Override
        protected Map<String, String> doInBackground(Void... params) {
            String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
            String entity = genProductArgs();
            byte[] buf = Util.httpPost(url, entity);
            String content = new String(buf);
            Map<String, String> xml = decodeXml(content);
            return xml;
        }
    }

    private String genProductArgs() {
        StringBuffer xml = new StringBuffer();

        try {
            String nonceStr = genNonceStr();
            xml.append("</xml>");
            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            packageParams.add(new BasicNameValuePair("appid", Constants.APP_ID));
            packageParams.add(new BasicNameValuePair("body", "weixin"));
            packageParams.add(new BasicNameValuePair("mch_id", Constants.MCH_ID));
            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
            packageParams.add(new BasicNameValuePair("notify_url", "http://alipay.kaicongyun.com/WeChatMobile/NotifyUrl"));
            packageParams.add(new BasicNameValuePair("out_trade_no", ourOrderId));
            packageParams.add(new BasicNameValuePair("spbill_create_ip", "127.0.0.1"));
            packageParams.add(new BasicNameValuePair("total_fee", "1"));
            packageParams.add(new BasicNameValuePair("trade_type", "APP"));
            String sign = genPackageSign(packageParams);
            packageParams.add(new BasicNameValuePair("sign", sign));

            return toXml(packageParams);

        } catch (Exception e) {
            LogUtil.e("chu", "genProductArgs fail, ex = " + e.getMessage());
            return null;
        }

    }

    private void getOrderNo(final int mode) {
//        showProgressDialog();
        Map<String, String> map = new HashMap<>();
        map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
        map.put(CameraConstants.ZCLOUD_Id, zCloudNum);
        map.put("ServiceId", "9012");
        map.put("TotalQty", "1");/*!< 消费次数 */
        if (mode == 1) {
            map.put("PayType", "70");/*!< app支付宝 */
        } else if (mode == 2) {
            map.put("PayType", "60");/*!< wap支付宝 */
        }
        doPost(UrlResources.URL_GET_ORDER_NO, ApiClientUtility.getParams(map), new VolleyResponse(this, true, getString(R.string.com_facebook_loading)) {

            @Override
            protected void onTaskSuccessRoot(JSONObject result) {
                super.onTaskSuccess(result);
                String orderId = result.optString("Order_Id");
                if (mode == 1) {
                    //支付宝钱包支付
                    String subject = result.optString("ServiceName");
                    String body = result.optString("ServiceIntro");
                    String price = result.optString("UnitPrice");
//                    String price = "0.01";
                    payUtils.pay(subject, body, orderId, price, new PayResultListener() {
                        @Override
                        public void payResult(String resultCode) {
                            // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                            if (TextUtils.equals(resultCode, "9000")) {
                                Intent data = new Intent();
                                data.putExtra("number", 12);
                                RenewalsZhiyunActivity.this.setResult(RESULT_OK, data);
                                RenewalsZhiyunActivity.this.finish();
                            } else {
                                // 判断resultStatus 为非“9000”则代表可能支付失败
                                // “8000” 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                                if (TextUtils.equals(resultCode, "8000")) {
                                    makeToast(getString(R.string.tips_pay_confirm));
                                }
                            }
                        }
                    });
                } else if (mode == 2) {
                    //wap支付
                    String url = "http://www.kaicongyun.com/AliPayWap/Pay?orderid=" + orderId;
                    Intent intent = new Intent();
                    intent.setClass(RenewalsZhiyunActivity.this, WapPayActivity.class);
                    intent.putExtra("load_url", url);
                    startActivityForResult(intent, RESULT_WAP_PAY);
                }
            }

            @Override
            protected void onTaskError(int code) {
                super.onTaskError(code);
                makeToast(getString(R.string.tips_get_order_info_error));
            }

            @Override
            protected void onTaskFailure() {
                super.onTaskFailure();
                removeProgressDialog();
                makeToast(getString(R.string.tips_get_order_info_error));
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        Intent resultData = new Intent();
        resultData.putExtra("number", 12);
        RenewalsZhiyunActivity.this.setResult(RESULT_OK, resultData);
        RenewalsZhiyunActivity.this.finish();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_ok:
                if (paySelectDialog == null) {
                    paySelectDialog = new PaySelectDialog(this, R.style.ZhiYunVideoSettingDialog, new PaySelectDialog.OnPayListener() {
                        @Override
                        public void onPay(int payStyle) {
                            if (payStyle == PAY_STYLE_APP) {
                                getOrderNo(1);
                            } else if (payStyle == PAY_STYLE_WEIXIN) {
                                getWXOrderNo();
                            }
                        }
                    });
                }
                paySelectDialog.show();
                break;
        }
    }

    public Map<String, String> decodeXml(String content) {

        try {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:
                        if ("xml".equals(nodeName) == false) {
                            //实例化student对象
                            xml.put(nodeName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            Log.e("orion", e.toString());
        }
        return null;

    }

    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }


    private String genOutTradNo() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    /**
     * 生成签名
     */

    private String genPackageSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Constants.API_KEY);

        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        Log.e("orion", packageSign);
        return packageSign;
    }

    private String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<" + params.get(i).getName() + ">");
            sb.append(params.get(i).getValue());
            sb.append("</" + params.get(i).getName() + ">");
        }
        sb.append("</xml>");
        LogUtil.e("chu", sb.toString());
        return sb.toString();
    }

    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(Constants.API_KEY);

        String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        return appSign;
    }

}
