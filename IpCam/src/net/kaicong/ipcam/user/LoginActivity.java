package net.kaicong.ipcam.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.KCApplication;
import net.kaicong.ipcam.R;

import com.facebook.FacebookSdk;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.facebook.controller.UMFacebookHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.HttpRequestConstants;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.bean.WechatLoginProperty;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.ipcam.utils.PreferenceUtils;
import net.kaicong.ipcam.utils.StringUtils;

import net.kaicong.utility.ApiClientUtility;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by LingYan on 2014/9/1.
 */
public class LoginActivity extends BaseActivity {
	public static final String INTENT_ACCOUNT_DATA = "INTENT_ACCOUNT_DATA";
	private EditText userAccount;
	private EditText userPassword;
	private TextView login;
	private TextView register;
	private TextView forgetPassword;
	private ImageView img_sina;
	private ImageView img_wchat;
	private ImageView img_qq;

	UMSocialService mController;
	private boolean flag = false;// 删除返回标识
	private boolean isWXInstalled = false;

	private Map<String, Object> resParams = new HashMap<>();
	private WechatLoginProperty wxPro;

	// QQ_uid/openid
	private String qq_uid = "";
	private String qq_openid = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FacebookSdk.sdkInitialize(getApplicationContext());
		setContentView(R.layout.activity_user_login);
		initTitle(getString(R.string.title_user_login));
		initView();
	}

	private void initView() {
		showBackButton();
		userAccount = (EditText) findViewById(R.id.login_edit_account);
		userAccount.setText(PreferenceUtils.loadStringPreference(this,
				CameraConstants.USER_EMAIL, ""));
		userPassword = (EditText) findViewById(R.id.login_edit_password);
		login = (TextView) findViewById(R.id.text_login);
		login.setOnClickListener(this);
		register = (TextView) findViewById(R.id.text_register);
		register.setOnClickListener(this);
		forgetPassword = (TextView) findViewById(R.id.tv_forget_pwd);
		forgetPassword.setOnClickListener(this);

		img_sina = (ImageView) findViewById(R.id.img_sina);
		img_wchat = (ImageView) findViewById(R.id.img_wchat);
		img_qq = (ImageView) findViewById(R.id.img_qq);
		img_sina.setOnClickListener(this);
		img_wchat.setOnClickListener(this);
		img_qq.setOnClickListener(this);

		wxPro = new WechatLoginProperty();

		mController = UMServiceFactory.getUMSocialService("com.umeng.login");
		// 设置新浪SSO handler
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		/**
		 * 添加微信平台 appId appSecret
		 */
		String appID = "wxc330def075ca1297";
		String appSecret = "2d14a54859111bc6d12c2c29feccac33";
		UMWXHandler wxHandler = new UMWXHandler(this, appID, appSecret);
		wxHandler.addToSocialSDK();
		wxHandler.setRefreshTokenAvailable(false);
		isWXInstalled = wxHandler.isClientInstalled();// 判断是否安装客户端
		mController.getConfig().setSsoHandler(wxHandler);

		// 参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(LoginActivity.this,
				"1104240300", "8h2gdOXdWm5O2Yh7");
		qqSsoHandler.addToSocialSDK();
		mController.getConfig().setSsoHandler(qqSsoHandler);

		UMFacebookHandler mFacebookHandler = new UMFacebookHandler(this);
		mFacebookHandler.addToSocialSDK();
	}

	private void getUserHeadUrl() {
		Map<String, String> map = new HashMap<>();
		map.put(CameraConstants.USER_ID, UserAccount.getUserID() + "");
		doPost(UrlResources.URL_GET_USER_HEAD, ApiClientUtility.getParams(map),
				new VolleyResponse(this, false, "") {

					@Override
					protected void onTaskSuccessRoot(JSONObject jsonObject) {
						super.onTaskSuccessRoot(jsonObject);
						String headPath = "";
						int count = 0;
						if (jsonObject.has("Head_Path")) {
							headPath = jsonObject.optString("Head_Path");
							KCApplication.userHeadUrl = headPath;
							UserAccount.saveUserHeadUrl(headPath);
							// 云豆
							count = jsonObject.optInt("virtualcurrency");
							KCApplication.Virtualcurrency = count;
							UserAccount.saveVirtualcurrency(count);
						}
						LogUtil.d("Head_Path", headPath);
						LogUtil.d("virtualcurrency", count + "");
						setResult(RESULT_OK);
						finish();
					}

				});

	}

	@Override
	protected void onResume() {
		super.onResume();
		userAccount.setText(UserAccount.getUserName());
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.text_login:
			// 登录操作
			check();
			break;
		case R.id.text_register:
			// 注册
			Intent intent = new Intent();
			intent.setClass(this, RegisterActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_forget_pwd:
			Intent intent1 = new Intent();
			intent1.setClass(LoginActivity.this, ResetPasswordActivity.class);
			startActivity(intent1);
			break;
		case R.id.img_sina:
			register(1);
			break;
		case R.id.img_wchat:
			register(2);
			break;
		case R.id.img_qq:
			register(3);
			break;
		}
	}

	private void check() {
		if (StringUtils.isEmpty(userAccount.getText().toString())) {
			makeToast(getString(R.string.login_invalid_email));
			return;
		}
		if (StringUtils.isEmpty(userPassword.getText().toString())) {
			makeToast(getString(R.string.login_invalid_password));
			return;
		}
		Map<String, String> map = new HashMap<>();
		map.put(CameraConstants.USER_ID, UserAccount.getUserID() + "");
		map.put(HttpRequestConstants.USER_NAME, userAccount.getText()
				.toString());
		map.put(HttpRequestConstants.USER_PASSWORD, userPassword.getText()
				.toString());
		doPost(UrlResources.URL_LOGIN, ApiClientUtility.getParams(map),
				new VolleyResponse(this, true,
						getString(R.string.com_facebook_loading)) {

					@Override
					public void onTaskSuccess(JSONObject result) {
						UserAccount.saveLoginSate(true);
						if (UserAccount.getUserID() != result.optInt("id")) {
							// 当已存入的userId与当前新登录的userId不一致时，记录新的账户登录状态
							UserAccount.saveLoginUserChanged(true);
							Log.i("id", result.optInt("id") + "");
						}
						UserAccount.saveRegType(result.optInt("regtype"));
						UserAccount.saveUserID(result.optInt("id"));
						UserAccount.saveUserKey(result.optString("key"));
						if (StringUtils.isEmailValid(result.optString("name"))) {
							// 邮箱登陆的
							UserAccount.saveLoginTypeEmail(true);
						} else {
							// 手机号登陆的
							UserAccount.saveLoginTypeEmail(false);
						}
						UserAccount.saveUserName(result.optString("name"));
						// 登陆之后设置alias
						setLoginAlias(result.optInt("id"));
						getUserHeadUrl();
					}

					@Override
					protected void onTaskFailure() {
						super.onTaskFailure();
						LogUtil.i("error", "login_fail");
					}
				});
	}

	public void register(int flag) {

		if (flag == 1) {// 新浪微博
			getPower(SHARE_MEDIA.SINA);
		} else if (flag == 2) {// 微信
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
			mController.doOauthVerify(this, media,
					new SocializeListeners.UMAuthListener() {

						@Override
						public void onStart(SHARE_MEDIA platform) {
						}

						@Override
						public void onError(SocializeException e,
								SHARE_MEDIA platform) {
							makeToast("授权失败");
						}

						@Override
						public void onComplete(Bundle value,
								SHARE_MEDIA platform) {
							if (media == SHARE_MEDIA.SINA) {
								if (value != null
										&& !TextUtils.isEmpty(value
												.getString("uid"))) {
									getData(media);
								} else {
									makeToast("授权失败");
								}
							} else if (media == SHARE_MEDIA.WEIXIN) {
								getData(media);
							} else {// QQ
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
		// 获取相关授权信息
		mController.getPlatformInfo(this, media,
				new SocializeListeners.UMDataListener() {
					@Override
					public void onStart() {
						// Toast.makeText(context, "获取平台数据开始...",
						// Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onComplete(int status, Map<String, Object> info) {
						if (status == 200 && info != null) {
							LogUtil.d("info", info.toString());
							resParams.clear();
							resParams.putAll(info);
							newLogin(media);
						} else {
							Log.d("info_error", "error：" + status);
						}
					}

				});
	}

	/**
	 * @param index
	 *            1--微博
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
		mController.deleteOauth(this, media,
				new SocializeListeners.SocializeClientListener() {
					@Override
					public void onStart() {
					}

					@Override
					public void onComplete(int status, SocializeEntity entity) {
						if (status == 200) {
							makeToast("删除成功");
							flag = true;
						} else {
							makeToast("删除失败");
							flag = false;
						}
					}
				});
		return flag;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	/**
	 * 第三方登陆
	 */
	private void newLogin(SHARE_MEDIA media) {
		Map<String, String> map = new HashMap<>();
		String url = "";
		if (media == SHARE_MEDIA.WEIXIN) {
			url = UrlResources.URL_WECHAT_LOGIN;
			map.put("sex", resParams.get("sex").toString());
			map.put("nickname", resParams.get("nickname").toString());
			map.put("unionid", resParams.get("unionid").toString());
			map.put("openid", resParams.get("openid").toString());
			map.put("headimgurl", resParams.get("headimgurl").toString());
			map.put("city", resParams.get("city").toString());
			map.put("province", resParams.get("province").toString());
			map.put("country", resParams.get("country").toString());
		} else if (media == SHARE_MEDIA.SINA) {
			url = UrlResources.URL_SINA_LOGIN;
			// 拆分location --sina 返回无country 传值 “”
			String[] location = resParams.get("location").toString().split(" ");
			if (location != null && location.length == 2) {
			} else {
				location = new String[] { "", "" };
			}
			map.put("uid", resParams.get("uid").toString());
			map.put("screen_name", resParams.get("screen_name").toString());
			map.put("gender", resParams.get("gender").toString());
			map.put("country", "");
			map.put("province", location[0]);
			map.put("city", location[1]);
			map.put("profile_image_url", resParams.get("profile_image_url")
					.toString());
		} else {// -----QQ
			url = UrlResources.URL_QQ_LOGIN;
			map.put("uid", qq_uid);
			map.put("openid", qq_openid);
			map.put("screen_name", resParams.get("screen_name").toString());
			map.put("gender", resParams.get("gender").toString());
			map.put("profile_image_url", resParams.get("profile_image_url")
					.toString());
		}
		map = ApiClientUtility.getParams(map);
		map.put("regfrom", "40");
		LogUtil.d("param", map.toString());
		// TO_DO
		doPost(url, map, new VolleyResponse(this, true,
				getString(R.string.com_facebook_loading)) {
			@Override
			protected void onTaskSuccess(JSONObject result) {
				super.onTaskSuccess(result);
				UserAccount.saveLoginSate(true);
				if (UserAccount.getUserID() != result.optInt("id")) {
					// 当已存入的userId与当前新登录的userId不一致时，记录新的账户登录状态
					UserAccount.saveLoginUserChanged(true);
					LogUtil.i("id", result.optInt("id") + "");
				}
				UserAccount.saveUserID(result.optInt("id"));
				UserAccount.saveUserKey(result.optString("key"));
				UserAccount.saveUserName(result.optString("nickname"));
				UserAccount.saveRegType(result.optInt("regtype"));
				// 登陆之后设置alias
				setLoginAlias(result.optInt("id"));
				// 客户端不管头像
				getUserHeadUrl();
			}

			@Override
			protected void onTaskFailure() {
				super.onTaskFailure();
				LogUtil.i("error", "login_fail");
			}
		});
	}

	private void setLoginAlias(int _id) {
		JPushInterface.setAlias(getApplicationContext(), _id + "",
				new TagAliasCallback() {
					@Override
					public void gotResult(int i, String s, Set<String> strings) {
						String logs;
						switch (i) {
						case 0:
							logs = "Set tag and alias success";
							LogUtil.d("chu", logs);
							break;

						case 6002:
							logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
							LogUtil.d("chu", logs);
							// if
							// (ExampleUtil.isConnected(getApplicationContext()))
							// {
							// mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS,
							// tags), 1000 * 60);
							// } else {
							// Log.i(TAG, "No network");
							// }
							break;

						default:
							logs = "Failed with errorCode = " + i;
							LogUtil.e("chu", logs);
						}
					}
				});
	}

}
