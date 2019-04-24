package net.kaicong.umshare;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.EmailHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import net.kaicong.R;


/**
 * Created by LingYan on 15/4/28.
 */

public class UMShareWindow extends PopupWindow implements View.OnClickListener {

    private UMSocialService umSocialService = UMServiceFactory
            .getUMSocialService("com.umeng.share");
    private Activity activity;
    private View contentView;
    private LinearLayout layoutSeeWorld;
    private LinearLayout layoutWeixin;
    private LinearLayout layoutWeixinCircle;
    private LinearLayout layoutSina;
    private LinearLayout layoutEmail;
    private LinearLayout layoutRoot;
    private OnUMShareWindowClickListener onUMShareWindowClickListener;

    public UMShareWindow(Activity activity, OnUMShareWindowClickListener onUMShareWindowClickListener) {
        this.activity = activity;
        this.onUMShareWindowClickListener = onUMShareWindowClickListener;
        initSocialSDK();
        initView();
    }

    private void initView() {
        contentView = LayoutInflater.from(activity).inflate(R.layout.custom_um_share_board, null);
        layoutSeeWorld = (LinearLayout) contentView.findViewById(R.id.umeng_see_world);
        layoutSeeWorld.setOnClickListener(this);
        layoutWeixin = (LinearLayout) contentView.findViewById(R.id.umeng_weixin);
        layoutWeixin.setOnClickListener(this);
        layoutWeixinCircle = (LinearLayout) contentView.findViewById(R.id.umeng_weixin_circle);
        layoutWeixinCircle.setOnClickListener(this);
        layoutSina = (LinearLayout) contentView.findViewById(R.id.umeng_sina);
        layoutSina.setOnClickListener(this);
        layoutEmail = (LinearLayout) contentView.findViewById(R.id.umeng_email);
        layoutEmail.setOnClickListener(this);
        layoutRoot = (LinearLayout) contentView.findViewById(R.id.root);
        layoutRoot.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int height = contentView.findViewById(R.id.umeng_see_world).getTop();
                int y = (int) motionEvent.getY();
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }

        });
        setContentView(contentView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置popwindow以外的地方点击，popwindow消失
        setOutsideTouchable(true);
    }

    private void initSocialSDK() {
        String appID = "wxc330def075ca1297";
        String appSecret = "2d14a54859111bc6d12c2c29feccac33";

        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(activity, appID, appSecret);
        wxHandler.addToSocialSDK();
        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(activity, appID, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
        // 添加email
        EmailHandler emailHandler = new EmailHandler();
        emailHandler.addToSocialSDK();
        // 设置新浪SSO handler
        umSocialService.getConfig().setSsoHandler(new SinaSsoHandler());
    }

    @Override
    public void onClick(View view) {
        SHARE_MEDIA platform = null;
        if (view.getId() == R.id.umeng_see_world) {
            if (onUMShareWindowClickListener != null) {
                onUMShareWindowClickListener.onUMShareClick(umSocialService, true, null);
            }
            dismiss();
        } else if (view.getId() == R.id.umeng_weixin) {
            platform = SHARE_MEDIA.WEIXIN;
            if (onUMShareWindowClickListener != null) {
                onUMShareWindowClickListener.onUMShareClick(umSocialService, false, platform);
            }
            dismiss();
        } else if (view.getId() == R.id.umeng_weixin_circle) {
            platform = SHARE_MEDIA.WEIXIN_CIRCLE;
            if (onUMShareWindowClickListener != null) {
                onUMShareWindowClickListener.onUMShareClick(umSocialService, false, platform);
            }
            dismiss();
        } else if (view.getId() == R.id.umeng_sina) {
            platform = SHARE_MEDIA.SINA;
            if (onUMShareWindowClickListener != null) {
                onUMShareWindowClickListener.onUMShareClick(umSocialService, false, platform);
            }
            dismiss();
        } else if (view.getId() == R.id.umeng_email) {
            platform = SHARE_MEDIA.EMAIL;
            if (onUMShareWindowClickListener != null) {
                onUMShareWindowClickListener.onUMShareClick(umSocialService, false, platform);
            }
            dismiss();
        }
    }

    public void doSSOHandler(int requestCode, int resultCode, Intent data) {
        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = umSocialService.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    public interface OnUMShareWindowClickListener {

        public void onUMShareClick(UMSocialService umSocialService, boolean isSeeWorld, SHARE_MEDIA platform);

    }

}
