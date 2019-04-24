package net.kaicong.ipcam.user;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import java.util.Map;

/**
 * Author: lu_qwen
 * Intro:
 * Time: 2015/5/6.
 */
public class Registrate {
    UMSocialService mController;
    private Context context;
    private static boolean flag = false;//删除返回标识
    private Activity activity;

    public Registrate(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        mController = UMServiceFactory.getUMSocialService("com.umeng.login");
        //设置新浪SSO handler
        mController.getConfig().setSsoHandler(new SinaSsoHandler());
        /**
         * 添加微信平台
         * appId
         * appSecret
         */
        String appID = "wxc330def075ca1297";
        String appSecret = "2d14a54859111bc6d12c2c29feccac33";
        UMWXHandler wxHandler = new UMWXHandler(context, appID, appSecret);
        wxHandler.addToSocialSDK();


        //参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(activity, "1103584563",
                "k1IbM3JCcSgNzdo4");
        qqSsoHandler.addToSocialSDK();

    }

    public void register(int flag) {

        if (flag == 1) {//新浪微博
            getPower(SHARE_MEDIA.SINA);
        } else if (flag == 2) {//微信
            getPower(SHARE_MEDIA.WEIXIN);
        } else {
            getPower(SHARE_MEDIA.QQ);
        }
    }

    /**
     * 授权
     */

    private void getPower(final SHARE_MEDIA media) {
        mController.doOauthVerify(context, media, new SocializeListeners.UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA platform) {
                Toast.makeText(context, "授权开始", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA platform) {
                Toast.makeText(context, "授权错误", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(Bundle value, SHARE_MEDIA platform) {
                if (media == SHARE_MEDIA.SINA) {
                    if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
                        Toast.makeText(context, "授权完成", Toast.LENGTH_SHORT).show();
                        getData(media);
                    } else {
                        Toast.makeText(context, "授权失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "授权完成", Toast.LENGTH_SHORT).show();
                    getData(media);
                }
            }

            @Override
            public void onCancel(SHARE_MEDIA platform) {
                Toast.makeText(context, "授权取消", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getData(SHARE_MEDIA media) {
        //获取相关授权信息
        mController.getPlatformInfo(context, media, new SocializeListeners.UMDataListener() {
            @Override
            public void onStart() {
                //Toast.makeText(context, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(int status, Map<String, Object> info) {
                if (status == 200 && info != null) {
                    Log.d("info", info.toString());


                } else {
                    Log.d("info_error", "error：" + status);
                }
            }

        });
    }

    /**
     * @param index 1--微博
     * @return
     */
    public boolean deleteAuth(int index) {
        SHARE_MEDIA media;
        if (index == 1) {
            media = SHARE_MEDIA.SINA;
        } else if (index == 2) {
            media = SHARE_MEDIA.WEIXIN;
        } else {
            media = SHARE_MEDIA.QQ;
        }
        mController.deleteOauth(context, media,
                new SocializeListeners.SocializeClientListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onComplete(int status, SocializeEntity entity) {
                        if (status == 200) {
                            Toast.makeText(context, "删除成功.",
                                    Toast.LENGTH_SHORT).show();
                            Registrate.flag = true;
                        } else {
                            Toast.makeText(context, "删除失败",
                                    Toast.LENGTH_SHORT).show();
                            Registrate.flag = false;
                        }
                    }
                });
        return flag;
    }
}
