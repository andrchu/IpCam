package net.kaicong.ipcam.wxapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.kaicong.ipcam.R;
import net.kaicong.ipcam.device.RenewalsZhiyunActivity;
import net.kaicong.ipcam.device.seeworld.BaseSeeWorldActivity;
import net.kaicong.ipcam.device.seeworld.ReWardActivity;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.ipcam.wxpay.Constants;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            RenewalsZhiyunActivity.WX_PAY_RESULT_CODE = resp.errCode;
            ReWardActivity.WX_PAY_RESULT_CODE = resp.errCode;
            finish();
            LogUtil.d("chu", "错误信息----" + resp.errStr);
        }
    }
}