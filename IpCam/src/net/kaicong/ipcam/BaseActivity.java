package net.kaicong.ipcam;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.kaicong.ipcam.api.VolleyHttpUtil;
import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.utils.ToastUtil;
import net.kaicong.ipcam.utils.ToolUtil;

import com.kaicong.myprogresshud.ProgressHUD;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Map;

import cn.jpush.android.api.JPushInterface;

/**
 * @author LianYan
 */
public class BaseActivity extends ActionBarActivity implements View.OnClickListener,

        DialogInterface.OnCancelListener {

    private RelativeLayout leftBtnTextLayout;
    private RelativeLayout leftBtnImageLayout;
    private RelativeLayout rightBtnTextLayout;
    private RelativeLayout rightBtnImageLayout;
    private TextView leftBtnText;
    private ImageView leftBtnImage;
    private TextView rightBtnText;
    private ImageView rightBtnImage;
    private TextView baseTitle;

    protected VolleyHttpUtil volleyHttpUtil;
    private ProgressHUD progressHUD;
    public DisplayMetrics displayMetrics;
    //初始化图片加载器
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    //左边按钮是否是返回按钮
    private boolean isLeftButtonBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_base_title);
        volleyHttpUtil = new VolleyHttpUtil();
    }

    //post请求
    public void doPost(String url, Map<String, String> params, VolleyResponse volleyResponse) {
        volleyHttpUtil.doJsonObjectRequest(url, params, volleyResponse);
    }

    //默认的内容
    public void showProgressDialog() {
        if (progressHUD == null) {
            progressHUD = ProgressHUD.show(this, "");
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

    //初始化标题
    public void initTitle(String title) {
        baseTitle = (TextView) findViewById(R.id.base_title);
        baseTitle.setText(title);
        leftBtnTextLayout = (RelativeLayout) findViewById(R.id.base_left_text_layout);
        leftBtnTextLayout.setOnClickListener(this);
        leftBtnText = (TextView) findViewById(R.id.base_left_btn_text);
        leftBtnImageLayout = (RelativeLayout) findViewById(R.id.base_left_image_layout);
        leftBtnImageLayout.setOnClickListener(this);
        leftBtnImage = (ImageView) findViewById(R.id.base_left_btn_image);
        rightBtnTextLayout = (RelativeLayout) findViewById(R.id.base_right_text_layout);
        rightBtnTextLayout.setOnClickListener(this);
        rightBtnText = (TextView) findViewById(R.id.base_right_btn_text);
        rightBtnImageLayout = (RelativeLayout) findViewById(R.id.base_right_image_layout);
        rightBtnImageLayout.setOnClickListener(this);
        rightBtnImage = (ImageView) findViewById(R.id.base_right_btn_image);
    }

    public void makeToast(String msg) {
        ToastUtil.showToast(this, msg);
        // Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void setBaseTitle(String title) {
        if (baseTitle != null) {
            baseTitle.setText(title);
        }
    }

    //显示title左边按钮(默认文字)
    public void showLeftButton(String btnText) {
        if (leftBtnText != null && leftBtnTextLayout != null && leftBtnImageLayout != null) {
            leftBtnTextLayout.setVisibility(View.VISIBLE);
            leftBtnImageLayout.setVisibility(View.GONE);
            leftBtnText.setText(btnText);
        }
    }

    //显示title左边按钮(背景图片)
    public void showLeftButton(int resId) {
        if (leftBtnImage != null && leftBtnImageLayout != null && leftBtnTextLayout != null) {
            leftBtnImageLayout.setVisibility(View.VISIBLE);
            leftBtnTextLayout.setVisibility(View.GONE);
//            addImageState(leftBtnImage, resId);
            leftBtnImage.setImageResource(resId);
            leftBtnImage.setColorFilter(getResources().getColor(R.color.kaicong_orange));
        }
    }

    public void hideLeftButton() {
        if (leftBtnTextLayout != null && leftBtnImageLayout != null) {
            leftBtnTextLayout.setVisibility(View.GONE);
            leftBtnImageLayout.setVisibility(View.GONE);
        }
    }

    public void showRightButton(String btnText) {
        if (rightBtnText != null && rightBtnTextLayout != null && rightBtnImageLayout != null) {
            rightBtnTextLayout.setVisibility(View.VISIBLE);
            rightBtnImageLayout.setVisibility(View.GONE);
            rightBtnText.setText(btnText);
        }
    }

    public void showRightButton(int resId) {
        if (rightBtnImage != null && rightBtnImageLayout != null && rightBtnTextLayout != null) {
            rightBtnImageLayout.setVisibility(View.VISIBLE);
            rightBtnTextLayout.setVisibility(View.GONE);
//            rightBtnImage.setImageDrawable(new StateDrawable(new Drawable[]{getResources().getDrawable(resId)}));
            rightBtnImage.setImageResource(resId);
            rightBtnImage.setColorFilter(getResources().getColor(R.color.kaicong_orange));
        }
    }

    public void hideRightButton() {
        if (rightBtnTextLayout != null && rightBtnImageLayout != null) {
            rightBtnTextLayout.setVisibility(View.GONE);
            rightBtnImageLayout.setVisibility(View.GONE);
        }
    }

    public void showBackButton() {
        if (leftBtnImage != null && leftBtnImageLayout != null && leftBtnTextLayout != null) {
            leftBtnImageLayout.setVisibility(View.VISIBLE);
//            addImageState(leftBtnImage, R.drawable.video_play_back);
//            leftBtnImage.setImageDrawable(new StateDrawable(new Drawable[]{getResources().getDrawable(R.drawable.video_play_back)}));
            leftBtnImage.setImageResource(R.drawable.video_play_back);
            leftBtnImage.setColorFilter(getResources().getColor(R.color.kaicong_orange));
            isLeftButtonBack = true;
        }
    }

    public void doBackButtonAction() {
        //返回按钮默认是页面销毁
        finish();
    }

    public void doLeftButtonAction(View view) {

    }

    public void doRightButtonAction(View view) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.base_left_text_layout:
            case R.id.base_left_image_layout:
                if (isLeftButtonBack) {
                    doBackButtonAction();
                } else {
                    doLeftButtonAction(view);
                }
                break;
            case R.id.base_right_text_layout:
            case R.id.base_right_image_layout:
                doRightButtonAction(view);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressHUD != null && progressHUD.isShowing()) {
            progressHUD.dismiss();
            progressHUD = null;
        }
        //layout资源文件的最外层layout设置id为root，这样在Activity销毁时能及时释放资源文件的内存
        unbindDrawables(findViewById(R.id.root));
        System.gc();
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
    public void onCancel(DialogInterface dialogInterface) {

    }

    protected void addImageState(ImageView imageView, int imageResId) {
        //正常显示时
        Drawable normalDrawable = getResources().getDrawable(imageResId);
        normalDrawable.setColorFilter(getResources().getColor(R.color.kaicong_orange), PorterDuff.Mode.SRC_ATOP);
        //被选中时
        Drawable selectedDrawable = getResources().getDrawable(imageResId);
        selectedDrawable.setColorFilter(getResources().getColor(R.color.kaicong_orange_selected), PorterDuff.Mode.SRC_ATOP);
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{}, normalDrawable);
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_selected, android.R.attr.state_focused}, selectedDrawable);
        imageView.setImageDrawable(stateListDrawable);
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            if (ToolUtil.isFastDoubleClick()) {
//                return true;
//            }
//        }
//        return super.dispatchTouchEvent(ev);
//    }

    public void hiddenBar(){
        getSupportActionBar().hide();
    }


    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

}
