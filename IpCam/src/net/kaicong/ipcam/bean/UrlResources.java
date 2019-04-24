package net.kaicong.ipcam.bean;

/**
 * Created by LingYan on 2014/9/3.
 */
public class UrlResources {

	/**
	 * 测试机
	 */
	private static final String BASE_URL_TEST = "http://apitest.kaicongyun.com/";

	/**
	 * 正式版
	 */
	private static final String BASE_URL = "https://api.kaicongyun.com/";

//	private static final String BASE_URL = "http://apitest.kaicongyun.com/";

	// 登陆
	public static final String URL_LOGIN = BASE_URL + "v4/account/user/login";
	// 注册（邮箱）
	public static final String URL_REGISTER_EMAIL = BASE_URL
			+ "v4/account/user/RegisterByEmail";
	// 注册（手机号）
	public static final String URL_REGISTER_PHONE = BASE_URL
			+ "v4/account/user/RegisterByMobile";
	// 看世界
	public static final String URL_WORLD_VIEW = BASE_URL
			+ "v5/device/public/get_list";
	// 设备列表
	public static final String URL_DEVICES_LIST = BASE_URL + "v4/Device/List";
	// 查看智云的设备信息
	public static final String URL_CHECK_ZCLOUD_INDO = BASE_URL
			+ "v4/Device/public/DetailZ";
	// 绑定设备ip
	public static final String URL_BIND_DEVICE_IP = BASE_URL
			+ "v4/Device/AddByIp";
	// 绑定设备DDNS
	public static final String URL_BIND_DEVICE_DDNS = BASE_URL
			+ "v4/Device/AddByDdns";
	// 绑定设备智云
	public static final String URL_BIND_DEVICE_ZHIYUN = BASE_URL
			+ "v4/Device/AddByZCloud";
	// 删除列表中的设备
	public static final String URL_DELETE_DEVICE = BASE_URL
			+ "v4/Device/DelDeviceById";
	// 获取用户图像url
	public static final String URL_GET_USER_HEAD = BASE_URL
			+ "v3/account/user/loadHead";
	// 获取验证码（注册时）
	public static final String URL_GET_REGISTER_SMS_CODE = BASE_URL
			+ "v4/accounURL_WECHAT_LOGINURL_WECHAT_LOGINt/user/GetSmsValidateCodeForRegister";
	// 获取验证码（修改密码时）
	public static final String URL_GET_RESET_SMS_CODE = BASE_URL
			+ "v4/account/user/GetResetSmsValidateCodeForRegister";
	// 修改图像
	public static final String URL_UPLOAD_USER_HEAD = BASE_URL + "Default.aspx";
	// 通过邮箱找回密码
	public static final String URL_GET_BACK_PWD_BY_EMAIL = BASE_URL
			+ "v4/account/user/ResetPwdByEmail";
	// 通过手机号找回密码
	public static final String URL_GET_BACK_PWD_BY_PHONE = BASE_URL
			+ "v4/account/user/ResetPwdByMobile";
	// ddns检测
	public static final String URL_DDNS_CHECK = BASE_URL
			+ "v4/Device/public/DdnsCheck";
	// 修改密码
	public static final String URL_CHANGE_PASSWORD = BASE_URL
			+ "v4/account/user/ChangePassword";
	// 看世界评论
	public static final String URL_COMMENTS_LIST = BASE_URL
			+ "v5/device/public/get_review_list";
	// 获取分享的设备信息
	public static final String URL_GET_PUBLIC_DEVICE_INFO = BASE_URL
			+ "v5/device/public/get_info";
	// 提交评论
	public static final String URL_COMMIT_COMMENT = BASE_URL
			+ "v6/device/public/create_review";
	// 收藏设备
	public static final String URL_COLLECT_DEVICE = BASE_URL
			+ "v5/device/public/create_favorite";
	// 点赞
	public static final String URL_CREATE_PRAISE = BASE_URL
			+ "v5/device/public/create_praise";
	// 收藏列表
	public static final String URL_GET_COLLECT_LIST = BASE_URL
			+ "v5/device/public/get_favorite_list";
	// 删除评论
	public static final String URL_DELETE_COMMENT = BASE_URL
			+ "v5/device/public/delete_review";
	// 取消收藏
	public static final String URL_DELETE_COLLECT = BASE_URL
			+ "v5/device/public/remove_favorite";
	// 开启分享
	public static final String URL_OPEN_SHARE = BASE_URL
			+ "v5/device/public/create";
	// 关闭分享
	public static final String URL_CLOSE_SHARE = BASE_URL
			+ "v5/device/public/remove";
	// 更新设备（DDNS/IP）
	public static final String URL_UPDATE_DDNS_DEVICE = BASE_URL
			+ "v5/Device/public/Ddns/UpdateInfo";
	// 更新设备（智云）
	public static final String URL_UPDATE_ZHIYUN_DEVICE = BASE_URL
			+ "v5/Device/public/ZCloud/UpdateInfo";
	// 获取订单号
	public static final String URL_GET_ORDER_NO = BASE_URL
			+ "v4/CommitOrder/public/CommitOrderAndPay";
	// 加载用户反馈
	public static final String URL_GET_FEEDBACK = BASE_URL
			+ "v3/user/getfeedback";
	// 创建反馈
	public static final String URL_CREATE_FEEDBACK = BASE_URL
			+ "v3/user/createfeedback";
	// 加载反馈回复
	public static final String URL_GET_FEEDBACK_RETRY = BASE_URL
			+ "v3/user/getfeedbackreply";
	// 聊天回复
	public static final String URL_REPLY_FEEDBACK = BASE_URL
			+ "v3/user/replyfeedback";
	// 消息
	public static final String URL_MSG = BASE_URL + "/v3/Alarms/Get_Alarm_List";

	// 上传凭证
	public static final String URL_GET_TOKEN = BASE_URL
			+ "v3/qiniu/uploadToken";
	// 下载对应私有资源url
	public static final String URL_TOKEN_URL = BASE_URL
			+ "v3/qiniu/downloadToken";

	// 获取评论列表
	public static final String URL_GET_COMMENT_IMG = BASE_URL
			+ "v6/device/public/get_review_list";
	// 创建评论
	public static final String URL_CREATE_COMMENT_IMG = BASE_URL
			+ "v6/device/public/create_review";
	// 看世界搜索
	public static final String URL_SEARCH_SEE_WORLD_LIST = BASE_URL
			+ "v5/device/public/search_list";
	// 我的设备搜索
	public static final String URL_SEARCH_MY_DEVICE_LIST = BASE_URL
			+ "v4/Device/SearchList";

	// 获取分享的设备信息
	public static final String URL_GET_PUBLIC_DEVICE_INFO_NEW = BASE_URL
			+ "v6/device/public/get_info";

	// 第三方登陆
	public static final String URL_WECHAT_LOGIN = BASE_URL
			+ "v3/account/user/wechatlogin";
	// Sina
	public static final String URL_SINA_LOGIN = BASE_URL
			+ "v3/account/user/weibologin";
	// QQ
	public static final String URL_QQ_LOGIN = BASE_URL
			+ "v3/account/user/qqlogin";
	// 统一支付接口
	public static final String URL_PAY_INTERFACE = BASE_URL
			+ "v5/CommitOrder/public/CommitOrder";
	// 微信支付接口
	public static final String URL_PAY_WEIXIN = BASE_URL
			+ "v5/CommitOrder/public/CommitOrderForWeChat";
	// 微博绑定
	public static final String URL_BINDING_WEIBO = BASE_URL
			+ "v3/account/user/weibobinding";
	// 微信绑定
	public static final String URL_BINDING_WEIXIN = BASE_URL
			+ "v3/account/user/wechatbinding";
	// QQ绑定
	public static final String URL_BINDING_QQ = BASE_URL
			+ "v3/account/user/qqbinding";
	// 查询绑定信息
	public static final String URL_BINDING_INFO = BASE_URL
			+ "v3/account/user/isbinding";
	// 取消绑定
	public static final String URL_CANCEL_BINDING = BASE_URL
			+ "v3/account/user/removebinding";

	// 生成微信支付打赏订单
	public static final String URL_RewardOrderForWeChat = BASE_URL
			+ "v5/CommitOrder/public/RewardOrderForWeChat";
	// 生成支付宝打赏订单
	public static final String URL_RewardOrderForAlipay = BASE_URL
			+ "v5/CommitOrder/public/RewardOrderForAlipay";
	// 获取打赏记录
	public static final String URL_RewardRecords = BASE_URL
			+ "v5/CommitOrder/public/RewardRecords";
	// 根据智云号获取设备型号
	public static final String URL_GET_MODEL_BY_ZCLOUD = BASE_URL
			+ "v4/zcloud/GetZCloud";
	// 根据设备型号检索最新的可用固件，用于App端在线升级
	public static final String URL_GET_FIRMWIRE = BASE_URL
			+ "/v3/Device/Firmware";
	// 评论回复
	public static final String URL_REPLY_COMMENTS = BASE_URL
			+ "v5/device/public/reply_review";
	// 我打赏别人
	public static final String URL_REWARD_TO_OTHER = BASE_URL
			+ "v5/CommitOrder/public/RewardRecords_To";
	// 别人打赏我
	public static final String URL_REWARD_TO_ME = BASE_URL
			+ "v5/CommitOrder/public/RewardRecords_From";
	// 根据用户id获取用户已分享设备
	public static final String URL_GET_SHARED_DEVICES = BASE_URL
			+ "v4/Device/ShareList";

	// 拉取移动侦测消息
	public static final String URL_WARN_LIST = BASE_URL
			+ "/v3/Alarms/Get_Alarm_List";
	// 标记所有消息为已读
	public static final String URL_WARN_READALL = BASE_URL
			+ "v3/Alarms/UpdateAlarms";
	// 点击某条消息后形成已读消息API
	public static final String URL_WARN_READONE = BASE_URL
			+ "v3/Alarms/UpdateAlarm";

	// 获取某个用户公共设备评论列表(公共版)
	public static final String URL_MESS_COMMENT_LIST = BASE_URL
			+ "v5/device/public/device_reviews";
	// 标记所有评论为已读
	public static final String URL_MESS_COMMENT_READALL = BASE_URL
			+ "v4/device/public/update_reviews";
	// 点击某条评论后形成已读消息API
	public static final String URL_MESS_COMMENT_READONE = BASE_URL
			+ "v4/device/public/update_review";
	// 提交评论
	public static final String URL_COMMIT_COMMENT_2 = BASE_URL
			+ "v6/device/public/create_review";
	// 新获取某个用户设备打赏列表
	public static final String URL_MESS_REWARD = BASE_URL
			+ "v5/Device/public/reward_reviews";

	// CRM消息
	public static final String URL_MESS_CRM = BASE_URL + "v4/public/crm_msgs";
	// 申请地标
	public static final String URL_APPLY_POSITION = BASE_URL
			+ "v4/device/public/landmark";
	// 成为主人
	public static final String URL_BECOME_OWNER = BASE_URL
			+ "v5/Device/DeviceOwner";
	// 取消主人
	public static final String URL_CANCEL_OWNER = BASE_URL
			+ "v5/Device/UnDeviceOwner";

}
