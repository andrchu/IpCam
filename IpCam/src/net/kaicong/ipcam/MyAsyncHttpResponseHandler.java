package net.kaicong.ipcam;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import net.kaicong.ipcam.user.UserAccount;
import net.kaicong.ipcam.utils.LogUtil;

import com.kaicong.myprogresshud.ProgressHUD;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 封装http请求的相应处理
 * Created by LingYan on 2014/9/2.
 */
public abstract class MyAsyncHttpResponseHandler extends JsonHttpResponseHandler implements DialogInterface.OnCancelListener {

    private Context context;
    private boolean isShowDialog = true;
    private ProgressHUD progressHUD;

    public MyAsyncHttpResponseHandler(Context context) {
        this.context = context;
    }

    //服务器返回正常的数据(has item)
    protected void onTaskSuccess(JSONObject result) {

    }

    protected void onTaskError(int code) {

    }

    //服务器返回正常的数据(has items)
    protected void onTaskSuccess(JSONArray result) {

    }

    //code =1
    protected void onTaskSuccess() {

    }

    protected void onTaskFailure() {

    }

    @Override
    public void onStart() {
        super.onStart();
        if (isShowDialog) {
            progressHUD = ProgressHUD.show(context, context.getString(R.string.activity_base_progress_dialog_content));
        }
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        this.onCancel();
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        super.onSuccess(statusCode, headers, response);
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
                        onTaskSuccess();
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
    public void onRetry(int retryNo) {
        super.onRetry(retryNo);
        LogUtil.d("chu", "retry num" + retryNo);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject
            errorResponse) {
        super.onFailure(statusCode, headers, throwable, errorResponse);
        if (isShowDialog) {
            progressHUD.dismiss();
        }
        Toast.makeText(context, context.getResources().getString(R.string.socket_connect_error), Toast.LENGTH_SHORT).show();
        if (throwable != null) {
            LogUtil.d("chu", "throwable exception--" + throwable.toString());
        }
        onTaskFailure();
    }

}
