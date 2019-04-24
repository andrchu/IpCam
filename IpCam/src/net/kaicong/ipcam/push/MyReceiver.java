package net.kaicong.ipcam.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import net.kaicong.ipcam.message.CommentListActivity;
import net.kaicong.ipcam.message.RewardMeActivity;
import net.kaicong.ipcam.user.UserAccount;
import net.kaicong.ipcam.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Author: lu_qwen 
 * Intro: 
 * Time: 2015/4/24.
 */
public class MyReceiver extends BroadcastReceiver {

	private String TAG = "chu";

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			// TO_DO
		}
		// 自定义消息不会展示在通知栏，完全要开发者写代码去处理
		else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			/**
			 * -- JPushInterface.EXTRA_TITLE 对应 API 消息内容的 title 字段。 --
			 * JPushInterface.EXTRA_MESSAGE 对应 API 消息内容的 message 字段。 --
			 * JPushInterface.EXTRA_EXTRA 对应 API 消息内容的 extras 字段。这是个 JSON 字符串 --
			 * JPushInterface.EXTRA_CONTENT_TYPE 对应 API 消息内容的 content_type 字段 --
			 * JPushInterface.EXTRA_MSG_ID 唯一标识消息的 ID, 可用于上报统计等。
			 */
		}
		// 在这里可以做些统计，或者做些其他工作
		else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {

			/**
			 * -- JPushInterface.EXTRA_NOTIFICATION_TITLE 对应 API 通知内容的 n_title
			 * 字段。 -- JPushInterface.EXTRA_ALERT 对应 API 通知内容的 n_content 字段。 --
			 * JPushInterface.EXTRA_NOTIFICATION_ID 通知栏的Notification
			 * ID，可以用于清除Notification -- JPushInterface.EXTRA_EXTRA 对应 API 通知内容的
			 * n_extras 字段。这是个 JSON 字符串
			 */
			// Log.d("RegistrationID",
			// JPushInterface.getRegistrationID(getApplicationContext()));
			// Log.d("Udid", JPushInterface.getUdid(getApplicationContext()));

		}
		// 在这里可以自己写代码去定义用户点击后的行为
		else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
			try {
				JSONObject jsonObject = new JSONObject(extras);
				int ExtType = jsonObject.optInt("ExtType");
				LogUtil.d("lqw_type", ExtType+"");
				if (ExtType == 1) {
					// 评论，点赞
					// 打开自定义的Activity
					// int deviceId = jsonObject.optInt("DeviceId");
					// Intent i = new Intent(context,
					// GetPushCommentsActivity.class);
					// putData.putInt("deviceId", deviceId);
					// i.putExtras(putData);
					// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					Intent i = new Intent(context, CommentListActivity.class);
					i.putExtra("from", 11);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					context.startActivity(i);
					UserAccount.isMessNeedRefresh(true);
				}else if (ExtType == 2){
					//打赏
					Intent i2 = new Intent(context, RewardMeActivity.class);
					i2.putExtra("from", 11);
					i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					context.startActivity(i2);
					UserAccount.isMessNeedRefresh(true);
				}
			} catch (Exception e) {

			}
		} else {
			Log.d(TAG, "Unhandled intent - " + intent.getAction());
		}

	}

	private String parseExtras(String str) {
		try {
			JSONObject object = new JSONObject(str);
			String type = object.getString("kankan_type");
			return type;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 通知的信息
	private void receivingNotification(Context context, Bundle bundle) {
		String title = bundle
				.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
		Log.d(TAG, " title : " + title);
		String message = bundle.getString(JPushInterface.EXTRA_ALERT);
		Log.d(TAG, "message : " + message);
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		Log.d(TAG, "extras : " + extras);
	}

}
