package net.kaicong.ipcam.api;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.kaicong.myprogresshud.ProgressHUD;

import net.kaicong.ipcam.R;
import net.kaicong.ipcam.user.UserAccount;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.ipcam.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by LingYan on 15/3/23.
 */

public class VolleyResponse implements IVolleyResponseListener {

    private Context context;
    private ProgressHUD progressHUD;
    private boolean isShowDialog = false;
    private String progressText;

    public VolleyResponse(Context context, boolean isShowDialog, String progressText) {
        this.context = context;
        this.isShowDialog = isShowDialog;
        this.progressText = progressText;
    }

    //服务器返回正常的数据(has item)
    protected void onTaskSuccess(JSONObject result) {

    }

    protected void onTaskSuccess(String result) {

    }

    protected void onTaskSuccessRoot(JSONObject jsonObject) {

    }

    protected void onTaskError(int code) {

    }

    //服务器返回正常的数据(has items)
    protected void onTaskSuccess(JSONArray result) {

    }

    protected void onTaskFailure() {

    }

    @Override
    public void onStart() {
        if (isShowDialog) {
            String text = StringUtils.isEmpty(progressText) ? context.getString(R.string.activity_base_progress_dialog_content) : progressText;
            progressHUD = ProgressHUD.show(context, text);
        }
    }

    @Override
    public void onSuccess(JSONObject response) {
        LogUtil.d("chu", response.toString());
        if (response != null) {
            if (response.has("code")) {
                int code = response.optInt("code");
                if (code == 1) {
                    //访问成功
                    if (response.has("item")) {
                        onTaskSuccess(response.optJSONObject("item"));
                    } else if (response.has("items")) {
                        onTaskSuccess(response.optJSONArray("items"));
                    } else {
                        onTaskSuccessRoot(response);
                    }
                } else {
                    //返回错误码
                    onTaskError(code);
                    UserAccount.showErrStrByCode(context, code);
                }
            }
        }
        if (isShowDialog) {
            progressHUD.dismiss();
        }
    }

    @Override
    public void onSuccess(String result) {
        if (isShowDialog) {
            progressHUD.dismiss();
        }
        if (!StringUtils.isEmpty(result)) {
            onTaskSuccess(result);
        }
    }

    @Override
    public void onError(VolleyError error) {
        if (isShowDialog) {
            progressHUD.dismiss();
        }
        Toast.makeText(context, context.getResources().getString(R.string.socket_connect_error), Toast.LENGTH_SHORT).show();
        if (error != null) {
            LogUtil.d("chu", "throwable exception--" + error.toString());
        }
        onTaskFailure();
    }

}
