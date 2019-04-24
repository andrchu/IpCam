package net.kaicong.ipcam.user;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.R;

import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.HttpRequestConstants;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.utils.StringUtils;

import com.loopj.android.http.RequestParams;

import net.kaicong.utility.ApiClientUtility;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LingYan on 2014/9/2.
 */
public class ResetPasswordActivity extends BaseActivity {

    private EditText account;
    private EditText password;
    private EditText passwordTwice;
    private TextView register;
    private LinearLayout switchLayout;
    private LinearLayout resetPasswordLayout;
    private LinearLayout resetPasswordTwiceLayout;
    private TextView registerByEmail;
    private TextView registerByPhone;
    private LinearLayout getSmsCodeLayout;
    private EditText smsCodeEdit;
    private Button getSmsCode;

    private boolean isRegisterByEmail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initTitle(getString(R.string.title_user_reset_password));
        initView();
    }

    private void initView() {
        showBackButton();
        account = (EditText) findViewById(R.id.register_edit_account);
        password = (EditText) findViewById(R.id.register_edit_password);
        passwordTwice = (EditText) findViewById(R.id.register_edit_password_twice);
        switchLayout = (LinearLayout) findViewById(R.id.switch_action);
        registerByEmail = (TextView) findViewById(R.id.reset_by_email);
        registerByEmail.setOnClickListener(this);
        registerByPhone = (TextView) findViewById(R.id.reset_by_phone);
        registerByPhone.setOnClickListener(this);
        register = (TextView) findViewById(R.id.text_register);
        register.setOnClickListener(this);
        getSmsCodeLayout = (LinearLayout) findViewById(R.id.get_sms_layout);
        smsCodeEdit = (EditText) findViewById(R.id.register_edit_phone);
        getSmsCode = (Button) findViewById(R.id.get_code);
        getSmsCode.setOnClickListener(this);
        resetPasswordLayout = (LinearLayout) findViewById(R.id.reset_password_layout);
        resetPasswordTwiceLayout = (LinearLayout) findViewById(R.id.reset_password_twice_layout);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.text_register:
                check();
                break;
            case R.id.reset_by_email:
                account.setText("");
                resetPasswordLayout.setVisibility(View.GONE);
                resetPasswordTwiceLayout.setVisibility(View.GONE);
                account.setHint(getString(R.string.register_user_please_input_email));
                isRegisterByEmail = true;
                getSmsCodeLayout.setVisibility(View.GONE);
                account.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                switchLayout.setBackgroundResource(R.drawable.matching_tab_two);
                registerByEmail.setTextColor(getResources().getColor(R.color.white));
                registerByPhone.setTextColor(getResources().getColor(R.color.black));
                break;
            case R.id.reset_by_phone:
                account.setText("");
                resetPasswordLayout.setVisibility(View.VISIBLE);
                resetPasswordTwiceLayout.setVisibility(View.VISIBLE);
                password.setText("");
                passwordTwice.setText("");
                smsCodeEdit.setText("");
                account.setHint(getString(R.string.register_user_please_input_phone));
                isRegisterByEmail = false;
                getSmsCodeLayout.setVisibility(View.VISIBLE);
                account.setInputType(InputType.TYPE_CLASS_NUMBER);
                switchLayout.setBackgroundResource(R.drawable.matching_tab_one);
                registerByEmail.setTextColor(getResources().getColor(R.color.black));
                registerByPhone.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.get_code:
                String accountStr = account.getText().toString();
                if (StringUtils.isEmpty(accountStr)) {
                    makeToast(getString(R.string.login_invalid_phone));
                    return;
                }
                Map<String, String> map = new HashMap<>();
                //获取验证码
                map.put(CameraConstants.USER_ID, UserAccount.getUserID() + "");
                map.put(HttpRequestConstants.USER_NAME, accountStr);
                doPost(UrlResources.URL_GET_RESET_SMS_CODE, ApiClientUtility.getParams(map), new VolleyResponse(this, false, getString(R.string.com_facebook_loading)) {

                    @Override
                    public void onSuccess(JSONObject result) {
                        getSmsCode.setClickable(false);
                        //一个每隔一秒的60秒倒计时
                        new CountDownTimer(60 * 1000, 1000) {

                            @Override
                            public void onTick(long l) {
                                getSmsCode.setText(getString(R.string.register_take_sms_time) + l / 1000);
                            }

                            @Override
                            public void onFinish() {
                                getSmsCode.setClickable(true);
                                getSmsCode.setText(getString(R.string.register_get_sms_code_btn));
                            }
                        }.start();
                        makeToast(getString(R.string.sms_code_success));
                    }

                });
                break;
        }
    }

    private void check() {

        final String accountStr = account.getText().toString();
        String passwordStr = password.getText().toString();
        String passwordTwiceStr = passwordTwice.getText().toString();
        String smsCodeStr = smsCodeEdit.getText().toString();
        if (isRegisterByEmail) {
            if (StringUtils.isEmpty(accountStr) || !StringUtils.isEmailValid(accountStr)) {
                makeToast(getString(R.string.login_invalid_email));
                return;
            }
        } else {
            if (StringUtils.isEmpty(accountStr)) {
                makeToast(getString(R.string.login_invalid_phone));
                return;
            }
            if (StringUtils.isEmpty(smsCodeStr)) {
                makeToast(getString(R.string.register_please_input_sms_code));
                return;
            }
            if (StringUtils.isEmpty(passwordStr)) {
                makeToast(getString(R.string.login_invalid_password));
                return;
            }
            if (StringUtils.isEmpty(passwordTwiceStr)) {
                makeToast(getString(R.string.login_invalid_password));
                return;
            }
            if (!passwordStr.equals(passwordTwiceStr)) {
                makeToast(getString(R.string.login_invalid_password));
                return;
            }
        }
        Map<String, String> map = new HashMap<>();
        if (isRegisterByEmail) {
            map.put(CameraConstants.USER_ID, UserAccount.getUserID() + "");
            map.put(HttpRequestConstants.USER_NAME, accountStr);
            doPost(UrlResources.URL_GET_BACK_PWD_BY_EMAIL, ApiClientUtility.getParams(map), new VolleyResponse(this, true, getString(R.string.com_facebook_loading)) {

                @Override
                protected void onTaskSuccess(JSONObject result) {
                    makeToast(getString(R.string.reset_by_email_success));
                    UserAccount.saveUserName(accountStr);
                    ResetPasswordActivity.this.finish();
                }
            });
        } else {
            map.put(CameraConstants.USER_ID, UserAccount.getUserID() + "");
            map.put(HttpRequestConstants.USER_NAME, accountStr);
            map.put(HttpRequestConstants.USER_NEW_PASSWORD, passwordStr);
            map.put(HttpRequestConstants.USER_CONFIRM_PASSWORD, passwordTwiceStr);
            map.put(HttpRequestConstants.USER_SMS_VALIDATE_CODE, smsCodeStr);
            doPost(UrlResources.URL_GET_BACK_PWD_BY_PHONE, ApiClientUtility.getParams(map), new VolleyResponse(this, true, getString(R.string.com_facebook_loading)) {

                @Override
                public void onSuccess(JSONObject result) {
                    makeToast(getString(R.string.register_by_phone_success));
                    UserAccount.saveUserName(accountStr);
                    ResetPasswordActivity.this.finish();
                }

            });
        }

    }

}
