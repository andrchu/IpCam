package net.kaicong.ipcam.device.seeworld;

import android.R.integer;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.R;
import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.user.UserAccount;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.ipcam.view.PaySelectDialog;
import net.kaicong.ipcam.wxpay.Constants;
import net.kaicong.ipcam.wxpay.MD5;
import net.kaicong.kcalipay.PayResultListener;
import net.kaicong.kcalipay.PayUtils;
import net.kaicong.utility.ApiClientUtility;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Author: lu_qwen Intro: Time: 2015/7/10.
 */
@SuppressWarnings("deprecation")
public class ReWardActivity extends BaseActivity {

	private RewardDialog.onRewardClickListener listener;
	private EditText edt_num;
	private ImageView img_random_min; // 小随即
	private ImageView img_random_max;// 大随即
	private Button btn_sure;// 提交打赏

	private Random random;

	//
	private PaySelectDialog paySelectDialog;
	// 微信
	private PayReq req;
	private final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
	public static int WX_PAY_RESULT_CODE = -99;
	private boolean isWxPay = false;
	// 支付宝
	private PayUtils payUtils;

	private int aplayMode = 1;

	private String rewMoney = "";
	protected boolean isRewardSuccess = false;

	private String bUserId;
	private String bDeviceId;

	private Intent bcakIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_reward);
		initTitle(getString(R.string.seeworld_reward_title));
		initView();
	}

	private void initView() {
		showBackButton();
		edt_num = (EditText) findViewById(R.id.edt_reward_num);
		edt_num.requestFocus();
		if (random == null) {
			random = new Random();
		}
		String s = String.format("%.2f", random.nextDouble() * 0.99 + 0.01);
		edt_num.setText(s);
		edt_num.setSelection(4);
		rewMoney = s;
		img_random_min = (ImageView) findViewById(R.id.img_reward_random);
		img_random_max = (ImageView) findViewById(R.id.img_reward_random_max);
		btn_sure = (Button) findViewById(R.id.btn_sureReward);

		img_random_min.setOnClickListener(this);
		img_random_max.setOnClickListener(this);
		btn_sure.setOnClickListener(this);

		if (getIntent().getStringExtra("T_UserId") != null) {
			bUserId = getIntent().getStringExtra("T_UserId");
		}
		if (getIntent().getStringExtra("T_DeviceId") != null) {
			bDeviceId = getIntent().getStringExtra("T_DeviceId");
		}

		payUtils = new PayUtils(this);
		req = new PayReq();
		msgApi.registerApp(Constants.APP_ID);

		bcakIntent = new Intent();

		edt_num.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.toString().contains(".")) {
					if (s.length() - 1 - s.toString().indexOf('.') > 2) {
						s = s.toString().subSequence(0,
								s.toString().indexOf(".") + 3);
						edt_num.setText(s);
						edt_num.setSelection(s.length());
					}
					if (s.toString().trim().substring(0).equals(".")) {
						s = "0" + s;
						edt_num.setText(s);
						edt_num.setSelection(2);
					}

					// 限制
					if (s.toString().indexOf('.') > 4) {
						if (s.length() - 1 - s.toString().indexOf('.') == 2) {
							s = s.toString().subSequence(0, 4)
									+ ""
									+ s.toString().subSequence(
											s.toString().indexOf("."),
											s.toString().indexOf(".") + 3);
							edt_num.setText(s);
							edt_num.setSelection(s.length());
						}
						if (s.length() - 1 - s.toString().indexOf('.') == 1) {
							s = s.toString().subSequence(0, 4)
									+ ""
									+ s.toString().subSequence(
											s.toString().indexOf("."),
											s.toString().indexOf(".") + 2);
							edt_num.setText(s);
							edt_num.setSelection(s.length());
						}
						if (s.length() - 1 - s.toString().indexOf('.') == 0) {
							s = s.toString().subSequence(0, 4)
									+ ""
									+ s.toString().subSequence(
											s.toString().indexOf("."),
											s.toString().indexOf("."));
							edt_num.setText(s);
							edt_num.setSelection(s.length());
						}
					}

				} else {
					if (s.toString().startsWith("0")
							&& s.toString().trim().length() > 1) {
						if (!s.toString().substring(1, 2).equals(".")) {
							edt_num.setText(s.subSequence(0, 1));
							edt_num.setSelection(1);
							return;
						}
					}

					if (s.length() > 4) {
						s = s.toString().subSequence(0, 4);
						edt_num.setText(s);
						edt_num.setSelection(s.length());
					}
				}
				rewMoney = s.toString();
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.img_reward_random:
			onRandomSalary(0);
			break;
		case R.id.img_reward_random_max:
			onRandomSalary(1);
			break;
		case R.id.btn_sureReward:
			// 进行输入校验
			if (checkSalary()) {
				onSureReward();
			}

			break;
		}
	}

	// 随机金额 0.01-0.20
	private void onRandomSalary(int judge) {
		String sal;

		if (judge == 0) {
			sal = String.format("%.2f", random.nextDouble() * 0.99 + 0.01);
		} else {
			sal = String.format("%.2f", random.nextDouble() * 9.00 + 1.00);
		}
		edt_num.setText(sal);
		edt_num.setSelection(sal.length());

		rewMoney = sal;
	}

	private boolean checkSalary() {
		if (rewMoney.equals("0.00")) {
			return false;
		}
		if (getPointNum(rewMoney) > 1) {
			return false;
		}
		return true;
	}

	private int getPointNum(String str) {
		LogUtil.d("lqw", str);
		int count = 0;
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '.') {
				count++;
			}
		}
		LogUtil.d("lqw", count + "");
		return count;
	}

	// 确认提交
	private void onSureReward() {

		if (paySelectDialog == null) {
			paySelectDialog = new PaySelectDialog(this,
					R.style.ZhiYunVideoSettingDialog,
					new PaySelectDialog.OnPayListener() {
						@Override
						public void onPay(int payStyle) {
							if (payStyle == PAY_STYLE_APP) {
								// 支付宝
								toOrder(rewMoney, 2);
							} else if (payStyle == PAY_STYLE_WEIXIN) {
								// 微信
								toOrder(rewMoney, 1);
							}
						}
					});
		}
		paySelectDialog.show();
	}

	/**
	 * 生成订单 TradeFromType: [Description("微信")] WeChat = 40,
	 * [Description("Android看看看")] Android_KanKanKan = 50,
	 */

	private void toOrder(String money, int flag) {
		isRewardSuccess = false;
		Map<String, String> map = new HashMap<>();
		map.put(CameraConstants.USER_ID,
				String.valueOf(UserAccount.getUserID()));// 打赏者id
		map.put("T_UserId", bUserId);// 被打赏者id
		map.put("T_DeviceId", bDeviceId);// 被打赏者分享的设备id
		map.put("TotalFee", money);// 打赏总金额
		map.put("TradeFromType", "50");// 打赏来源
		if (flag == 1) {// WX
			doPost(UrlResources.URL_RewardOrderForWeChat,
					ApiClientUtility.getParams(map), new VolleyResponse(
							ReWardActivity.this, true,
							getString(R.string.com_facebook_loading)) {

						@Override
						protected void onTaskSuccessRoot(JSONObject jsonObject) {
							super.onTaskSuccessRoot(jsonObject);
							// 获得签名
							req.appId = jsonObject.optString("appId");
							req.partnerId = jsonObject.optString("partnerId");
							req.prepayId = jsonObject.optString("prepayId");
							req.packageValue = jsonObject.optString("package");
							req.nonceStr = jsonObject.optString("nonceStr");
							req.timeStamp = jsonObject.optString("timeStamp");

							List<NameValuePair> signParams = new LinkedList<NameValuePair>();
							signParams.add(new BasicNameValuePair("appid",
									req.appId));
							signParams.add(new BasicNameValuePair("noncestr",
									req.nonceStr));
							signParams.add(new BasicNameValuePair("package",
									req.packageValue));
							signParams.add(new BasicNameValuePair("partnerid",
									req.partnerId));
							signParams.add(new BasicNameValuePair("prepayid",
									req.prepayId));
							signParams.add(new BasicNameValuePair("timestamp",
									req.timeStamp));

							req.sign = genAppSign(signParams);
							// 发起支付
							boolean a = msgApi.registerApp(Constants.APP_ID);
							boolean b = msgApi.sendReq(req);
							LogUtil.d("a/b", "regirst:" + a + "|| send:" + b);
							isWxPay = true;
						}

					});
		} else {
			doPost(UrlResources.URL_RewardOrderForAlipay,
					ApiClientUtility.getParams(map), new VolleyResponse(
							ReWardActivity.this, true,
							getString(R.string.com_facebook_loading)) {

						@Override
						protected void onTaskSuccessRoot(JSONObject result) {
							super.onTaskSuccess(result);
							String orderId = result.optString("Order_Id");
							// 支付宝钱包支付
							try {
								String subject = result
										.optString("ServiceName");
								String body = result.optString("ServiceIntro");
								final String price = result.get("TotalFee")
										.toString();
								payUtils.setCallbackUrl("http://www.kaicongyun.com/AliPayReWard/NotifyUrl");
								payUtils.pay(subject, body, orderId, price,
										new PayResultListener() {
											@Override
											public void payResult(
													String resultCode) {
												// 判断resultStatus
												// 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
												if (TextUtils.equals(
														resultCode, "9000")) {
													makeToast("支付成功");
													isRewardSuccess = true;
													bcakIntent.putExtra(
															"money", rewMoney);
													LogUtil.d("chu", rewMoney);
													setResult(001, bcakIntent);
													ReWardActivity.this
															.finish();

												} else {
													// 判断resultStatus
													// 为非“9000”则代表可能支付失败
													// “8000”
													// 代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
													if (TextUtils.equals(
															resultCode, "8000")) {
														makeToast(getString(R.string.tips_pay_confirm));
													}
												}
											}
										});
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					});
		}
	}

	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);

		String appSign = MD5.getMessageDigest(sb.toString().getBytes())
				.toUpperCase();
		return appSign;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isWxPay) {
			if (WX_PAY_RESULT_CODE == 0) {
				// 支付成功
				LogUtil.d("chu", "--微信成功回调--");
				makeToast("支付成功");
				isRewardSuccess = true;
				bcakIntent.putExtra("money", rewMoney);
				LogUtil.d("chu", rewMoney);
				setResult(001, bcakIntent);
				finish();
			} else if (WX_PAY_RESULT_CODE == -1) {
				// 支付失败（签名或者其它错误）
				makeToast("支付失败");
			} else if (WX_PAY_RESULT_CODE == -2) {
				// 支付取消
				makeToast("支付取消");
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		WX_PAY_RESULT_CODE = -99;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		WX_PAY_RESULT_CODE = -99;
	}

}
