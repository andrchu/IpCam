package net.kaicong.ipcam;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.facebook.controller.UMFacebookHandler;
import com.umeng.socialize.facebook.media.FaceBookShareContent;
import com.umeng.socialize.media.MailShareContent;
import com.umeng.socialize.media.SinaShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.EmailHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;


/**
 * Created by LingYan on 14-12-15.
 */
public class UmengShareUtils {

    private Activity activity;
    private UMSocialService mController;

    public UmengShareUtils(Activity activity) {
        this.activity = activity;
        init();
    }

    private void init() {
        // 首先在您的Activity中添加如下成员变量
        mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        // 移除分享
        mController.getConfig().removePlatform(
                SHARE_MEDIA.DOUBAN,//豆瓣
                SHARE_MEDIA.QQ,//QQ
                SHARE_MEDIA.QZONE,//QQ空间
                SHARE_MEDIA.TENCENT,//腾讯微博
                SHARE_MEDIA.RENREN);//人人网
        // 添加email
        EmailHandler emailHandler = new EmailHandler();
        emailHandler.addToSocialSDK();
        // wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
        String appID = "wxc330def075ca1297";
        String appSecret = "2d14a54859111bc6d12c2c29feccac33";
        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(activity, appID, appSecret);
        wxHandler.addToSocialSDK();
        // 支持微信朋友圈
        UMWXHandler wxCircleHandler = new UMWXHandler(activity, appID, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
        // 设置新浪SSO handler
        mController.getConfig().setSsoHandler(new SinaSsoHandler());

        // FaceBook.
        UMFacebookHandler mFacebookHandler = new UMFacebookHandler(activity);
        mFacebookHandler.addToSocialSDK();
        //Twitter
        mController.getConfig().supportAppPlatform(activity, SHARE_MEDIA.TWITTER,
                "com.umeng.share", true);

        // 排列顺序
        mController.getConfig().setPlatformOrder(
                SHARE_MEDIA.WEIXIN,
                SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.SINA,
                SHARE_MEDIA.EMAIL,
                SHARE_MEDIA.FACEBOOK,
                SHARE_MEDIA.TWITTER);
    }

    /**
     * 分享操作
     *
     * @param shareBitmap
     */
    public void share(Bitmap shareBitmap, String content) {

        String targetUrl = "http://app.kaicongyun.com/seever3/";
        UMImage umImage;
        if (shareBitmap != null && !shareBitmap.isRecycled()) {
            umImage = new UMImage(activity, shareBitmap);
        } else {
            umImage = new UMImage(activity, R.drawable.see_launcher);
        }
        //设置facebook分享的内容

        FaceBookShareContent fbContent = new FaceBookShareContent();
        fbContent.setShareImage(umImage);
        fbContent.setShareContent(content);
        fbContent.setTargetUrl(targetUrl);
        mController.setShareMedia(fbContent);

        // 设置邮件分享内容， 如果需要分享图片则只支持本地图片
        MailShareContent mail = new MailShareContent();
        mail.setTitle(activity.getString(R.string.see_world_share_from));
        mail.setShareImage(umImage);
        mail.setShareContent(content);
        mController.setShareMedia(mail);

        // 设置微信好友分享内容
        WeiXinShareContent weixinContent = new WeiXinShareContent();
        weixinContent.setShareImage(umImage);
        weixinContent.setShareContent(content);
        weixinContent.setTargetUrl(targetUrl);
        mController.setShareMedia(weixinContent);

        // 设置朋友圈分享的内容
        CircleShareContent circleMedia = new CircleShareContent();
        circleMedia.setShareImage(umImage);
        circleMedia.setShareContent(content);
        circleMedia.setTargetUrl(targetUrl);
        mController.setShareMedia(circleMedia);

        SinaShareContent sinaShareContent = new SinaShareContent();
        sinaShareContent.setShareImage(umImage);
        sinaShareContent.setShareContent(content);
        sinaShareContent.setTargetUrl(targetUrl);
        mController.setShareMedia(sinaShareContent);

        mController.openShare(activity, false);
    }

    public void share(Bitmap shareBitmap) {
        share(shareBitmap, "");
    }

    public void doSSOHandler(int requestCode, int resultCode, Intent data) {
        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 隐藏面板
     */
    public void dismissShare() {
        mController.dismissShareBoard();
    }

}
