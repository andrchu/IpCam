package net.kaicong.ipcam.bean;

import net.kaicong.ipcam.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LingYan on 2014/9/4.
 */
public class SharedCamera extends ErrorResponse {

	public int id;// 列表id
	public int userId;// 用户id
	public String userName;// 用户名(邮箱)
	public String shareName;// 名称
	public int usagePattern;
	public int productModelId;// 摄像头产品类型(例如1018)
	public String account;// 登录名(admin)
	public String password;// 密码
	public String ip;// ip
	public int port;// 端口
	public String ddnsName;// 序列号
	public String ddnsWanIp;
	public String ddnsLanIp;
	public int ddnsTcpPort;
	public int ddnsModelId;
	public String imageUrl;// 图片链接
	public String praiseCount;// 赞数
	public String commentCount;// 评论数
	public String popularity;// 人气
	public String date;// 分享日期
	public String longitude;// 经度
	public String latitude;// 纬度
	public String zCloud;
	public int IsVerify;
	public List<SharedCamera> data;

	public static SharedCamera getSharedCameraInfo(JSONArray array) {
		SharedCamera sharedCameras = new SharedCamera();
		sharedCameras.data = new ArrayList<>();
		try {

			for (int i = 0; i < array.length(); i++) {
				SharedCamera sharedCamera = new SharedCamera();
				JSONObject camera = array.getJSONObject(i);
				sharedCamera.id = camera.optInt("id");
				sharedCamera.userId = camera.optInt("user_id");
				sharedCamera.userName = camera.optString("username");
				sharedCamera.shareName = camera.optString("share_title");
				sharedCamera.usagePattern = camera.optInt("usage_pattern");
				sharedCamera.productModelId = camera.optInt("product_modelid");
				sharedCamera.account = camera.optString("account");
				sharedCamera.password = camera.optString("password");
				sharedCamera.ip = camera.optString("ip");
				sharedCamera.port = camera.optInt("port");
				sharedCamera.ddnsName = camera.optString("ddns_name");
				sharedCamera.ddnsWanIp = camera.optString("ddns_wanip");
				sharedCamera.ddnsLanIp = camera.optString("ddns_lanip");
				sharedCamera.ddnsTcpPort = camera.optInt("ddns_tcpport");
				sharedCamera.ddnsModelId = camera.optInt("ddns_modelid");
				sharedCamera.imageUrl = camera.optString("last_snapshot");
				sharedCamera.praiseCount = camera.optString("praise_count");
				sharedCamera.commentCount = camera.optString("review_count");
				sharedCamera.popularity = camera.optString("favorite_count");
				sharedCamera.date = camera.optString("shared_time");
				sharedCamera.longitude = camera.optString("longitude");
				sharedCamera.latitude = camera.optString("latitude");
				sharedCamera.zCloud = camera.optString("zcloud");

				if (StringUtils.isEmpty(camera.optString("IsVerify"))) {
					// 为空，说明没有提交过申请
					sharedCamera.IsVerify = -1;
				} else if (camera.optBoolean("IsVerify")) {
					// 申请通过
					sharedCamera.IsVerify = 1;
				} else if (!camera.optBoolean("IsVerify")) {
					// 申请暂未通过或者拒绝
					sharedCamera.IsVerify = 0;
				}

				sharedCameras.data.add(sharedCamera);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sharedCameras;
	}
}
