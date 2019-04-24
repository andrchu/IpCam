package net.kaicong.ipcam.view;

import net.kaicong.ipcam.R;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;

/**
 * Created by LingYan on 15-1-6.
 */
public class PaySelectDialog extends Dialog {

    private LinearLayout payAppLayout;
    private LinearLayout payWapLayout;
    private RadioButton payAppRadio;
    private RadioButton payWapRadio;
    private Button btnOk;
    private int checkIndex = 0;
    private OnPayListener onPayListener;

    public PaySelectDialog(Context context, int style, OnPayListener onPayListener) {
        super(context, style);
        this.onPayListener = onPayListener;
        init(context);
    }

    private void init(Context context) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_pay_select, null);
        setContentView(contentView);
        payAppLayout = (LinearLayout) contentView.findViewById(R.id.alipay_app_pay);
        payWapLayout = (LinearLayout) contentView.findViewById(R.id.alipay_wap_pay);
        payAppRadio = (RadioButton) contentView.findViewById(R.id.app_pay_radio);
        payAppRadio.setChecked(true);
        payWapRadio = (RadioButton) contentView.findViewById(R.id.wap_pay_radio);
        btnOk = (Button) contentView.findViewById(R.id.btn_ok);
        payAppLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appPayCheck();
            }
        });
        payWapLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wapPayCheck();
            }
        });
        payAppRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appPayCheck();
            }
        });
        payWapRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wapPayCheck();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPayListener != null) {
                    if (checkIndex == 0) {
                        onPayListener.onPay(OnPayListener.PAY_STYLE_APP);
                    } else if (checkIndex == 1) {
                        onPayListener.onPay(OnPayListener.PAY_STYLE_WEIXIN);
                    }
                    dismiss();
                }
            }
        });
    }

    private void appPayCheck() {
        payAppRadio.setChecked(true);
        payWapRadio.setChecked(false);
        checkIndex = 0;
    }

    private void wapPayCheck() {
        payAppRadio.setChecked(false);
        payWapRadio.setChecked(true);
        checkIndex = 1;
    }

    public interface OnPayListener {

        //app支付
        public int PAY_STYLE_APP = 0;
        //wap支付
        public int PAY_STYLE_WAP = 1;
        //微信支付
        int PAY_STYLE_WEIXIN = 2;

        public void onPay(int payStyle);

    }

}
