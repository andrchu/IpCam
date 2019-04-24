package net.kaicong.ipcam.user;

import android.content.Context;
import android.widget.Toast;

import net.kaicong.ipcam.KCApplication;
import net.kaicong.ipcam.R;

import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.utils.PreferenceUtils;

/**
 * Created by LingYan on 2014/11/19 0019.
 */
public class UserAccount {

	public static final long DAY = 24 * 60 * 60;

	public static boolean isUserLogin() {
		return PreferenceUtils.loadBooleanPreference(
				KCApplication.getContext(), CameraConstants.LOGIN_STATE, false);
	}

	public static void saveLoginSate(boolean loginState) {
		PreferenceUtils.savePreference(KCApplication.getContext(),
				CameraConstants.LOGIN_STATE, loginState);
	}

	public static void saveUserID(int userID) {
		PreferenceUtils.savePreference(KCApplication.getContext(),
				CameraConstants.USER_ID, userID);
	}

	public static int getUserID() {
		return PreferenceUtils.loadIntPreference(KCApplication.getContext(),
				CameraConstants.USER_ID, 0);
	}

	public static void saveUserName(String email) {
		PreferenceUtils.savePreference(KCApplication.getContext(),
				CameraConstants.USER_EMAIL, email);
	}

	public static String getUserName() {
		return PreferenceUtils.loadStringPreference(KCApplication.getContext(),
				CameraConstants.USER_EMAIL);
	}

	public static void saveUserKey(String key) {
		PreferenceUtils.savePreference(KCApplication.getContext(),
				CameraConstants.USER_KEY, key);
	}

	public static void saveUserHeadUrl(String url) {
		PreferenceUtils.savePreference(KCApplication.getContext(),
				CameraConstants.USER_HEAD, url);
	}

	public static String getUserHeadUrl() {
		return PreferenceUtils.loadStringPreference(KCApplication.getContext(),
				CameraConstants.USER_HEAD, "");
	}

	public static void saveLoginTypeEmail(boolean isByEmail) {
		PreferenceUtils.savePreference(KCApplication.getContext(),
				CameraConstants.IS_BY_EMAIL, isByEmail);
	}

	public static boolean getLoginTypeEmail() {
		return PreferenceUtils.loadBooleanPreference(
				KCApplication.getContext(), CameraConstants.IS_BY_EMAIL, true);
	}

	public static void saveLoginUserChanged(boolean isChanged) {
		PreferenceUtils.savePreference(KCApplication.getContext(),
				CameraConstants.LOGIN_CHANGED, isChanged);
	}

	public static boolean getLoginUserChanged() {
		return PreferenceUtils
				.loadBooleanPreference(KCApplication.getContext(),
						CameraConstants.LOGIN_CHANGED, true);
	}

	public static void saveRegType(int regType) {
		PreferenceUtils.savePreference(KCApplication.getContext(),
				CameraConstants.REG_TYPE, regType);
	}

	public static boolean isOtherLogin() {
		int regType = PreferenceUtils.loadIntPreference(
				KCApplication.getContext(), CameraConstants.REG_TYPE, 10);
		if (regType == 30 || regType == 40 || regType == 50) {
			return true;
		}
		return false;
	}

	public static void saveVirtualcurrency(int num) {
		PreferenceUtils.savePreference(KCApplication.getContext(),
				CameraConstants.VIRTUAL_CURRENCY, num);
	}

	public static int getVirtualcurrency() {
		return PreferenceUtils.loadIntPreference(KCApplication.getContext(),
				CameraConstants.VIRTUAL_CURRENCY, 0);
	}

	public static void saveCurrentTime() {
		PreferenceUtils
				.savePreference(KCApplication.getContext(),
						CameraConstants.CURRENT_TIME,
						System.currentTimeMillis() / 1000);
	}

	public static long getCurrentTime() {
		return PreferenceUtils.loadLongPreference(KCApplication.getContext(),
				CameraConstants.CURRENT_TIME, 0);
	}

	// 标记 消息页面是否需要刷新
	public static void isMessNeedRefresh(boolean need) {
		PreferenceUtils.savePreference(KCApplication.getContext(),
				"is_need_refresh", need);
	}

	public static boolean isToRefresh() {
		return PreferenceUtils.loadBooleanPreference(
				KCApplication.getContext(), "is_need_refresh", false);
	}

	public static void showErrStrByCode(Context context, int code) {

		String errStr = context.getResources().getString(
				R.string.operation_failed);

		switch (code) {
		case 1003:
			errStr = context.getResources().getString(
					R.string.tips_invalid_ddns);
			break;
		case 1004:
			errStr = context.getResources().getString(
					R.string.tips_ddns_name_password_not_match);
			break;
		case 1101:
			errStr = context.getResources().getString(
					R.string.login_error_account);
			break;
		case 1102:
			errStr = context.getResources().getString(
					R.string.login_error_not_enabled);
			break;
		case 1103:
			errStr = context.getResources().getString(
					R.string.login_error_account_pass_not_match);
			break;
		case 1201:
			errStr = context.getResources().getString(
					R.string.register_account_exists);
			break;
		case 1301:
			errStr = context.getResources().getString(
					R.string.register_sms_code_error);
			break;
		case 1302:
			errStr = context.getResources().getString(
					R.string.register_sms_max_get);
			break;
		case 1303:
			errStr = context.getResources().getString(
					R.string.register_sms_max_get);
			break;
		case 1304:
			errStr = context.getResources().getString(
					R.string.register_phone_num_exists);
			break;
		case 1305:
			errStr = context.getResources().getString(
					R.string.reset_pwd_get_sms_failed);
			break;
		case 1404:
			errStr = context.getResources().getString(
					R.string.tips_zcloud_invalid);
			break;
		case 2001:
			errStr = context.getResources()
					.getString(R.string.tips_ddns_locked);
			break;
		case 2002:
			errStr = context.getResources().getString(
					R.string.tips_not_band_ddns);
			break;
		case 2003:
			errStr = context.getResources().getString(
					R.string.tips_ddns_has_banded);
			break;
		case 2005:
			errStr = context.getResources().getString(
					R.string.tips_this_ddns_not_exist);
			break;
		case 2010:
			errStr = context.getResources().getString(
					R.string.tips_has_banded_device);
			break;
		case 2201:
			errStr = context.getResources().getString(
					R.string.tips_invalid_ddns);
			break;
		case 2202:
			errStr = context.getResources().getString(
					R.string.tips_ddns_pwd_error);
			break;
		case 2203:
			errStr = context.getResources().getString(
					R.string.tips_ddns_sms_not_open);
			break;
		case 2204:
			errStr = context.getResources().getString(
					R.string.tips_not_band_ddns);
			break;
		case 4001:
			errStr = context.getResources().getString(
					R.string.tips_sms_net_error);
			break;
		case 4002:
			errStr = context.getResources().getString(
					R.string.tips_phone_invalid);
			break;
		case 4003:
			errStr = context.getResources().getString(
					R.string.tips_not_band_ddns);
			break;
		case 4004:
			errStr = context.getResources().getString(
					R.string.tips_balance_short);
			break;
		case 3383:
			errStr = context.getResources().getString(
					R.string.register_sms_code_error);
			break;
		case 3314:
			errStr = context.getResources().getString(
					R.string.see_world_has_collect);
			break;
		case 3315:
			errStr = context.getString(R.string.see_world_has_praise);
			break;
		case 3319:
			errStr = context.getString(R.string.tips_device_is_not_shared);
			break;
		case 3320:
			errStr = context.getString(R.string.tips_device_has_ownered);
			break;
		case 3321:
			errStr = context.getString(R.string.tips_device_has_apply_position);
			break;

		}
		Toast.makeText(context, errStr, Toast.LENGTH_LONG).show();
	}

}
