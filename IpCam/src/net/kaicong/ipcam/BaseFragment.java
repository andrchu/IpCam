package net.kaicong.ipcam;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.kaicong.ipcam.api.VolleyHttpUtil;
import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.utils.ToolUtil;

import com.kaicong.myprogresshud.ProgressHUD;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Map;

/**
 * Created by LingYan on 2014/9/1.
 */
public class BaseFragment extends Fragment implements View.OnClickListener,
        DialogInterface.OnCancelListener {

    private View fragmentView;
    public DisplayMetrics displayMetrics;
    private ProgressHUD progressHUD;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    protected VolleyHttpUtil volleyHttpUtil;

    //资源文件
    public int getLayoutId() {
        return 0;
    }

    protected void initView(View convertView) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        volleyHttpUtil = new VolleyHttpUtil();
    }

    //post请求
    public void doPost(String url, Map<String,String> params, VolleyResponse volleyResponse) {
        volleyHttpUtil.doJsonObjectRequest(url, params, volleyResponse);
    }

    public void makeToast(String msg) {
        Toast.makeText(this.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(getLayoutId(), container, false);
        initView(fragmentView);
        return fragmentView;
    }

    //默认的内容
    public void showProgressDialog() {
        if (progressHUD == null) {
            progressHUD = ProgressHUD.show(this.getActivity(), "");
        }
        progressHUD.show();
    }

    public void setProgressText(String cotent) {
        if (progressHUD != null) {
            progressHUD.setMessage(cotent);
        }
    }

    //移除对话框
    public void removeProgressDialog() {
        if (progressHUD != null && progressHUD.isShowing()) {
            progressHUD.dismiss();
        }
    }

    /**
     * Unbind all the drawables.
     *
     * @param view
     */
    private void unbindDrawables(View view) {
        ToolUtil.unbindDrawables(view);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressHUD != null && progressHUD.isShowing()) {
            progressHUD.dismiss();
            progressHUD = null;
        }
        unbindDrawables(fragmentView.findViewById(R.id.root));
        System.gc();
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {

    }

}
