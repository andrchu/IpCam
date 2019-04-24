package net.kaicong.ipcam.user;

import android.os.Bundle;
import android.view.View;

import android.widget.EditText;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.R;

import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.HttpRequestConstants;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.utils.StringUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.kaicong.utility.ApiClientUtility;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LingYan on 2014/11/28 0028.
 */
public class ChangePasswordActivity extends BaseActivity {

    private EditText oldPassword;
    private EditText newPassword;
    private EditText againPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initTitle(getString(R.string.about_more_change_pwd));
        showBackButton();
        showRightButton(getString(R.string.btn_save));

        oldPassword = (EditText) findViewById(R.id.edit_old_pwd);
        newPassword = (EditText) findViewById(R.id.edit_new_pwd);
        againPassword = (EditText) findViewById(R.id.edit_again_pwd);

    }

    @Override
    public void doRightButtonAction(View view) {
        super.doRightButtonAction(view);
        String oldStr = oldPassword.getText().toString();
        String newStr = newPassword.getText().toString();
        String againStr = againPassword.getText().toString();

        if (StringUtils.isEmpty(oldStr) ||
                StringUtils.isEmpty(newStr) ||
                StringUtils.isEmpty(againStr)) {
            makeToast(getString(R.string.common_input_not_empty));
            return;
        }
        if (!newStr.equals(againStr)) {
            makeToast(getString(R.string.common_input_not_equal));
            return;
        }
        if (newStr.equals(oldStr)) {
            makeToast(getString(R.string.common_input_new_old_equal));
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put(HttpRequestConstants.USER_NAME, UserAccount.getUserName());
        map.put(CameraConstants.USER_ID, UserAccount.getUserID() + "");
        map.put(HttpRequestConstants.USER_OLD_PASSWORD, oldStr);
        map.put(HttpRequestConstants.USER_NEW_PASSWORD, newStr);
        map.put(HttpRequestConstants.USER_CONFIRM_PASSWORD, againStr);
        doPost(UrlResources.URL_CHANGE_PASSWORD, ApiClientUtility.getParams(map),  new VolleyResponse(this, true, getString(R.string.com_facebook_loading)) {


            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog();
            }

            @Override
            public void onSuccess(JSONObject jsonObject) {
                removeProgressDialog();
                try {
                    if (jsonObject.has("code")) {
                        int code = jsonObject.optInt("code");
                        if (code == 1) {
                            makeToast(getString(R.string.change_pwd_success));
                            ChangePasswordActivity.this.finish();
                        } else if (code == 8) {
                            makeToast(getString(R.string.tips_old_password_error));
                        }
                    }
                } catch (Exception e) {

                }
            }

            @Override
            protected void onTaskFailure() {
                super.onTaskFailure();
                removeProgressDialog();
                makeToast(getString(R.string.socket_connect_error));
            }

        });

    }
}
