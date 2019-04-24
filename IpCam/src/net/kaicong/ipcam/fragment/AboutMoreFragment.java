package net.kaicong.ipcam.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import net.kaicong.ipcam.AboutUsUrlActivity;
import net.kaicong.ipcam.BaseFragment;
import net.kaicong.ipcam.R;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import net.kaicong.ipcam.adpater.UserBindingAdapter;
import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.BindingModel;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.bean.WechatLoginProperty;
import net.kaicong.ipcam.user.UserAccountInfoActivity;
import net.kaicong.ipcam.user.UserBindingActivity;
import net.kaicong.ipcam.user.UserFeedbackActivity;
import net.kaicong.ipcam.utils.ImageUtils;
import net.kaicong.ipcam.user.LoginActivity;
import net.kaicong.ipcam.user.UserAccount;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.ipcam.utils.StringUtils;
import net.kaicong.ipcam.utils.ToastUtil;
import net.kaicong.ipcam.utils.ToolUtil;
import net.kaicong.utility.ApiClientUtility;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LingYan on 2014/8/29.
 */
public class AboutMoreFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private static final int REQUEST_CODE_DETAIL = 233;

    private static final int REQUEST_CODE_LOGIN = 1000;
    private LinearLayout lel_head;
    private LinearLayout lel_aboutUs;
    private LinearLayout lel_feedback;
    private LinearLayout lel_socialLogin;
    private LinearLayout lel_weixinXd;

    private ImageView img_head;
    private TextView tev_account;

    private Bitmap bitmap;

    private boolean isLogined = false;


    private ListView listView;
    private UserBindingAdapter userBindingAdapter;
    private List<BindingModel> data = new ArrayList<>();
    private boolean isBinding[] = new boolean[3];

    public UMSocialService mController;
    private boolean isWXInstalled = false;
    private Map<String, Object> resParams = new HashMap<>();
    private WechatLoginProperty wxPro;
    //QQ_uid/openid
    private String qq_uid = "";
    private String qq_openid = "";
    private String titles[] = new String[3];
    private int icons[] = new int[3];

    private boolean isShow = true;
    private ImageView imgTag;

    @Override
    protected void initView(View convertView) {
        super.initView(convertView);

        lel_head = (LinearLayout) convertView.findViewById(R.id.lel_head);
        lel_aboutUs = (LinearLayout) convertView.findViewById(R.id.lel_aboutUs);
        lel_feedback = (LinearLayout) convertView.findViewById(R.id.lel_feedback);
        lel_socialLogin = (LinearLayout) convertView.findViewById(R.id.lel_socialLogin);
        lel_weixinXd = (LinearLayout) convertView.findViewById(R.id.lel_weixin_xd);
        lel_weixinXd.setVisibility(View.GONE);
        lel_socialLogin.setEnabled(false);

        imgTag = (ImageView) convertView.findViewById(R.id.img_showTag);

        lel_head.setOnClickListener(this);
        lel_aboutUs.setOnClickListener(this);
        lel_feedback.setOnClickListener(this);
        lel_socialLogin.setOnClickListener(this);
        lel_weixinXd.setOnClickListener(this);

        img_head = (ImageView) convertView.findViewById(R.id.img_userHead);
        bitmap = ImageUtils.drawableToBitmap(getResources().getDrawable(R.drawable.default_boy));
        img_head.setImageBitmap(ImageUtils.toRoundBitmap(bitmap));
        tev_account = (TextView) convertView.findViewById(R.id.tev_accountName);


        listView = (ListView) convertView.findViewById(R.id.list);
        userBindingAdapter = new UserBindingAdapter();
        userBindingAdapter.setData(data);
        listView.setAdapter(userBindingAdapter);

        listView.setOnItemClickListener(this);

        titles[0] = getString(R.string.umeng_socialize_text_weixin_key);
        titles[1] = getString(R.string.umeng_socialize_text_sina_key);
        titles[2] = getString(R.string.umeng_socialize_text_qq_key);
        icons[0] = R.drawable.umeng_socialize_wechat;
        icons[1] = R.drawable.umeng_socialize_sina_on;
        icons[2] = R.drawable.umeng_socialize_qq_on;

        wxPro = new WechatLoginProperty();

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
        UMWXHandler wxHandler = new UMWXHandler(getActivity(), appID, appSecret);
        wxHandler.addToSocialSDK();
        wxHandler.setRefreshTokenAvailable(false);
        isWXInstalled = wxHandler.isClientInstalled();//判断是否安装客户端
        mController.getConfig().setSsoHandler(wxHandler);

        //参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(), "1104240300",
                "8h2gdOXdWm5O2Yh7");
        qqSsoHandler.addToSocialSDK();
        mController.getConfig().setSsoHandler(qqSsoHandler);

        if (UserAccount.isUserLogin()) {
            isLogin();
        }

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            //重新显示的时候,检查登录状态，来更新view
            if (UserAccount.isUserLogin()) {
                isLogin();
            } else {
                isOut();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode != Activity.RESULT_OK) {
//            return;
//        }
        if (requestCode == REQUEST_CODE_LOGIN && resultCode == Activity.RESULT_OK) {
            isLogin();
        }
        if (requestCode == REQUEST_CODE_DETAIL && !UserAccount.isUserLogin()) {
            isOut();
        }
        //更改头像
        if (requestCode == REQUEST_CODE_DETAIL && resultCode == 233) {
            imageLoader.displayImage(UserAccount.getUserHeadUrl(), img_head, ImageUtils.getRoundedDisplayOptions(R.drawable.common_head_bg, null));
        }
        /**使用SSO授权必须添加如下代码 */
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.lel_head:
                if (!isLogined) {
                    Intent loginIntent = new Intent();
                    loginIntent.setClass(this.getActivity(), LoginActivity.class);
                    startActivityForResult(loginIntent, REQUEST_CODE_LOGIN);
                } else {
                    //点击进入用户信息界面
                    Intent intent = new Intent();
                    intent.setClass(this.getActivity(), UserAccountInfoActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_DETAIL);
                }
                break;
            case R.id.lel_aboutUs:
                //关于我们
                Intent aboutIntent = new Intent();
                aboutIntent.setClass(this.getActivity(), AboutUsUrlActivity.class);
                startActivity(aboutIntent);
                break;
            case R.id.lel_feedback:
                //反馈
                if (UserAccount.isUserLogin()) {
                    Intent feedbackIntent = new Intent();
                    feedbackIntent.setClass(this.getActivity(), UserFeedbackActivity.class);
                    startActivity(feedbackIntent);
                } else {
                    makeToast(getString(R.string.login_user_please_login_first));
                    Intent intent = new Intent();
                    intent.setClass(this.getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.lel_socialLogin:
                if (isShow) {
                    isShow = false;
                    imgTag.setRotation(90);
                    listView.setVisibility(View.GONE);
                } else {
                    isShow = true;
                    imgTag.setRotation(270);
                    listView.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.lel_weixin_xd:
                String url = "http://mp.weixin.qq.com/bizmall/mallshelf?id=&t=mall/list&biz=MjM5NTMyODYyMA==&shelf_id=1&showwxpaytitle=1#wechat_redirect";
                if (!StringUtils.isEmpty(url)) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    Uri content_url = Uri.parse(url);
                    intent.setData(content_url);
                    startActivity(intent);
                }
                break;
        }
    }

    private void isLogin() {
        isLogined = true;
        lel_socialLogin.setEnabled(true);
        tev_account.setText(UserAccount.getUserName());
        imageLoader.displayImage(UserAccount.getUserHeadUrl(), img_head, ImageUtils.getRoundedDisplayOptions(R.drawable.common_head_bg, null));
        lel_socialLogin.setClickable(true);
        //
        listView.setEnabled(true);
        getBindingData();

    }

    private void isOut() {
        isLogined = false;
        lel_socialLogin.setEnabled(false);
        tev_account.setText(getResources().getText(R.string.login_user_login_btn));
        img_head.setImageBitmap(ImageUtils.toRoundBitmap(bitmap));
        lel_socialLogin.setClickable(false);

        data.clear();
        for (int i = 0; i < 3; i++) {
            BindingModel bindingModel = new BindingModel();
            bindingModel.imgId = icons[i];
            bindingModel.title = titles[i];
            bindingModel.isBinding = false;
            data.add(bindingModel);
        }
        userBindingAdapter.setData(data);
        ToolUtil.setListViewHeightBasedOnChildren(listView);
        userBindingAdapter.notifyDataSetChanged();
        listView.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_about_more;
    }

    private void getBindingData() {
        Map<String, String> map = new HashMap<>();
        map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
        doPost(UrlResources.URL_BINDING_INFO, ApiClientUtility.getParams(map), new VolleyResponse(getActivity(), false, getString(R.string.activity_base_progress_dialog_content)) {

            @Override
            protected void onTaskSuccessRoot(JSONObject jsonObject) {
                super.onTaskSuccessRoot(jsonObject);
                data.clear();
                isBinding[0] = jsonObject.optBoolean("iswechat_binging");
                isBinding[1] = jsonObject.optBoolean("isweibo_binging");
                isBinding[2] = jsonObject.optBoolean("isqq_binging");
                for (int i = 0; i < 3; i++) {
                    BindingModel bindingModel = new BindingModel();
                    bindingModel.imgId = icons[i];
                    bindingModel.title = titles[i];
                    bindingModel.isBinding = isBinding[i];
                    data.add(bindingModel);
                }
                userBindingAdapter.setData(data);
                ToolUtil.setListViewHeightBasedOnChildren(listView);
                userBindingAdapter.notifyDataSetChanged();

                if (UserAccount.isOtherLogin()) {
                    //使用第三方无法再绑定
                    listView.setEnabled(false);
                }
            }

        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        boolean isBinding = data.get(i).isBinding;
        switch (i) {
            case 0:
                if (isBinding) {
                    //取消绑定
                    cancelBinding(1);
                } else {
                    register(1);
                }
                break;
            case 1:
                if (isBinding) {
                    //取消绑定
                    cancelBinding(2);
                } else {
                    register(2);
                }
                break;
            case 2:
                if (isBinding) {
                    //取消绑定
                    cancelBinding(3);
                } else {
                    register(3);
                }
                break;
        }
    }

    public void register(int flag) {
        if (flag == 2) {//新浪微博
            getPower(SHARE_MEDIA.SINA);
        } else if (flag == 1) {//微信
            getPower(SHARE_MEDIA.WEIXIN);
        } else {
            getPower(SHARE_MEDIA.QQ);
        }
    }

    /**
     * 授权
     */

    private void getPower(final SHARE_MEDIA media) {
        if (media == SHARE_MEDIA.WEIXIN && !isWXInstalled) {
            makeToast("未安装微信客户端,无法登录");
        } else {
            mController.doOauthVerify(getActivity(), media, new SocializeListeners.UMAuthListener() {

                @Override
                public void onStart(SHARE_MEDIA platform) {
                }

                @Override
                public void onError(SocializeException e, SHARE_MEDIA platform) {
                    makeToast("授权失败");
                }

                @Override
                public void onComplete(Bundle value, SHARE_MEDIA platform) {
                    if (media == SHARE_MEDIA.SINA) {
                        if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
                            getData(media);
                        } else {
                            makeToast("授权失败");
                        }
                    } else if (media == SHARE_MEDIA.WEIXIN) {
                        getData(media);
                    } else {//QQ
                        LogUtil.d("qq_bundle", value.toString());
                        qq_uid = value.getString("uid");
                        qq_openid = value.getString("openid");
                        getData(media);
                    }
                }

                @Override
                public void onCancel(SHARE_MEDIA platform) {
                    makeToast("授权取消");
                }

            });
        }
    }

    private void getData(final SHARE_MEDIA media) {
        //获取相关授权信息
        mController.getPlatformInfo(getActivity(), media, new SocializeListeners.UMDataListener() {
            @Override
            public void onStart() {
                //Toast.makeText(context, "获取平台数据开始...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(int status, Map<String, Object> info) {
                if (status == 200 && info != null) {
                    LogUtil.d("info", info.toString());
                    resParams.clear();
                    resParams.putAll(info);
                    bind(media);
                } else {
                    LogUtil.d("info_error", "error：" + status);
                }
            }

        });
    }

    private void bind(SHARE_MEDIA media) {
        Map<String, String> map = new HashMap<>();
        String url = "";
        map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
        if (media == SHARE_MEDIA.SINA) {
            //微博
            url = UrlResources.URL_BINDING_WEIBO;
            //拆分location --sina 返回无country 传值 “”
            String[] location = resParams.get("location").toString().split(" ");
            if (location != null && location.length == 2) {
            } else {
                location = new String[]{"", ""};
            }
            map.put("uid", resParams.get("uid").toString());
            map.put("screen_name", resParams.get("screen_name").toString());
            map.put("gender", resParams.get("gender").toString());
            map.put("country", "");
            map.put("province", location[0]);
            map.put("city", location[1]);
            map.put("profile_image_url", resParams.get("profile_image_url").toString());
        } else if (media == SHARE_MEDIA.WEIXIN) {
            //微信
            url = UrlResources.URL_BINDING_WEIXIN;
            map.put("sex", resParams.get("sex").toString());
            map.put("nickname", resParams.get("nickname").toString());
            map.put("unionid", resParams.get("unionid").toString());
            map.put("openid", resParams.get("openid").toString());
            map.put("headimgurl", resParams.get("headimgurl").toString());
            map.put("city", resParams.get("city").toString());
            map.put("province", resParams.get("province").toString());
            map.put("country", resParams.get("country").toString());
        } else if (media == SHARE_MEDIA.QQ) {
            //QQ
            url = UrlResources.URL_BINDING_QQ;
            map.put("uid", qq_uid);
            map.put("openid", qq_openid);
            map.put("screen_name", resParams.get("screen_name").toString());
            map.put("gender", resParams.get("gender").toString());
            map.put("profile_image_url", resParams.get("profile_image_url").toString());
        }
        doPost(url, ApiClientUtility.getParams(map), new VolleyResponse(getActivity(), false, getString(R.string.activity_base_progress_dialog_content)) {

            @Override
            protected void onTaskSuccess(JSONObject result) {
                super.onTaskSuccess(result);
                getBindingData();
            }

        });
    }

    private void cancelBinding(final int position) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.cancel_banding))
                .setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Map<String, String> map = new HashMap<>();
                                map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
                                if (position == 1) {
                                    //微信
                                    map.put("binding_type", "10");
                                } else if (position == 2) {
                                    //微博
                                    map.put("binding_type", "20");
                                } else if (position == 3) {
                                    //微博
                                    map.put("binding_type", "30");
                                }
                                doPost(UrlResources.URL_CANCEL_BINDING, ApiClientUtility.getParams(map), new VolleyResponse(getActivity(), false, getString(R.string.activity_base_progress_dialog_content)) {
                                    @Override
                                    protected void onTaskSuccessRoot(JSONObject jsonObject) {
                                        super.onTaskSuccessRoot(jsonObject);
                                        getBindingData();
                                    }
                                });

                            }
                        }, 1000);
                    }
                }).setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();

    }



}
