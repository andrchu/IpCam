package net.kaicong.ipcam.device;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.R;

import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.user.UserAccount;
import net.kaicong.ipcam.user.WebViewActivity;
import net.kaicong.ipcam.utils.StringUtils;

import com.kaicong.myswitch.SwitchButton;

import net.kaicong.utility.ApiClientUtility;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LingYan on 14-12-23.
 */
public class ShareDeviceActivity extends BaseActivity {

	private SwitchButton shareSwitch;
	private EditText shareTitleEditText;
	private Button postShareTitleButton;
	// private TextView sharePositionConfirm;

	private boolean isShareOpen = false;
	private boolean isOpenShare = false;
	private int deviceId;

	private CheckBox checkBox;
	private TextView agreement;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_device);
		initTitle(getString(R.string.device_property_share_setting));
		showBackButton();
		isShareOpen = getIntent().getBooleanExtra("isShareOpen", false);
		deviceId = getIntent().getIntExtra("deviceId", 0);
		isOpenShare = isShareOpen;

		checkBox = (CheckBox) findViewById(R.id.checkbox);
		agreement = (TextView) findViewById(R.id.agreement);

		shareSwitch = (SwitchButton) findViewById(R.id.share_switch);
		shareSwitch
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton compoundButton,
							boolean b) {
						if (b) {
							isOpenShare = true;
							shareSwitch
									.setText(getString(R.string.device_property_has_open));
						} else {
							isOpenShare = false;
							shareSwitch
									.setText(getString(R.string.device_property_has_close));
						}
					}
				});
		shareTitleEditText = (EditText) findViewById(R.id.edit_share_title);
		postShareTitleButton = (Button) findViewById(R.id.post_share_title);
		postShareTitleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isShareOpen && isOpenShare || !isShareOpen && !isOpenShare) {
					// 没有任何改变
					finish();
					return;
				}
				if (!checkBox.isChecked()) {
					makeToast("请认真阅读看世界用户协议并同意");
					return;
				}
				if (isShareOpen && !isOpenShare) {
					// 关闭分享
					Map<String, String> map = new HashMap<>();
					map.put(CameraConstants.USER_ID,
							String.valueOf(UserAccount.getUserID()));
					map.put(CameraConstants.DEVICE_ID, String.valueOf(deviceId));
					doPost(UrlResources.URL_CLOSE_SHARE, ApiClientUtility
							.getParams(map), new VolleyResponse(
							ShareDeviceActivity.this, true,
							getString(R.string.com_facebook_loading)) {

						@Override
						public void onSuccess(JSONObject result) {
							super.onTaskSuccess(result);
							makeToast(getString(R.string.device_property_close_share_success));
							Intent dataIntent = new Intent();
							dataIntent.putExtra("is_shared", false);
							ShareDeviceActivity.this.setResult(RESULT_OK,
									dataIntent);
							ShareDeviceActivity.this.finish();
						}

					});
				}
				if (!isShareOpen && isOpenShare) {
					// 打开分享
					String shareTitle = shareTitleEditText.getText().toString();
					if (StringUtils.isEmpty(shareTitle)) {
						makeToast(getString(R.string.common_input_not_empty));
						return;
					}
					Map<String, String> map = new HashMap<>();
					map.put(CameraConstants.USER_ID,
							String.valueOf(UserAccount.getUserID()));
					map.put(CameraConstants.DEVICE_ID, String.valueOf(deviceId));
					map.put("share_title", shareTitle);
					doPost(UrlResources.URL_OPEN_SHARE, ApiClientUtility
							.getParams(map), new VolleyResponse(
							ShareDeviceActivity.this, true,
							getString(R.string.com_facebook_loading)) {

						@Override
						public void onSuccess(JSONObject result) {
							super.onTaskSuccess(result);
							makeToast(getString(R.string.device_property_open_share_success));
							Intent dataIntent = new Intent();
							dataIntent.putExtra("is_shared", true);
							ShareDeviceActivity.this.setResult(RESULT_OK,
									dataIntent);
							ShareDeviceActivity.this.finish();
						}

					});
				}
			}

		});
		shareSwitch.setChecked(isShareOpen);
		shareSwitch
				.setText(isShareOpen ? getString(R.string.device_property_has_open)
						: getString(R.string.device_property_has_close));
		agreement.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setClass(ShareDeviceActivity.this, WebViewActivity.class);
				intent.putExtra("load_url",
						getString(R.string.seeworld_user_agreement));
				startActivity(intent);
			}

		});

		// sharePositionConfirm = (TextView)
		// findViewById(R.id.position_confirm);
		// sharePositionConfirm.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View view) {
		// Intent intent = new Intent();
		// intent.setClass(ShareDeviceActivity.this,
		// CertificatePositionActivity.class);
		//
		// }
		//
		// });

	}

}
