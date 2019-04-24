package net.kaicong.ipcam.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.kaicong.ipcam.BaseFragment;
import net.kaicong.ipcam.KCApplication;
import net.kaicong.ipcam.R;

import net.kaicong.ipcam.adpater.CommentsAdapter;
import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.device.seeworld.BaseSeeWorldActivity;
import net.kaicong.ipcam.device.seeworld.Comments;
import net.kaicong.ipcam.device.seeworld.Preview_PicCommentActivity;
import net.kaicong.ipcam.device.seeworld.Summary;
import net.kaicong.ipcam.user.UserAccount;
import net.kaicong.ipcam.utils.LocationUtil;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.ipcam.utils.ToolUtil;
import net.kaicong.ipcam.view.CommentReplyDialog;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import net.kaicong.ipcam.view.PicCommentDialog;
import net.kaicong.utility.ApiClientUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by LingYan on 14-12-9.
 */
public class CommentsFragment extends BaseFragment implements
		View.OnClickListener, PullToRefreshBase.OnRefreshListener2,
		PullToRefreshBase.OnLastItemVisibleListener,
		AdapterView.OnItemClickListener,
		CommentReplyDialog.OnSelectionSelectListener,
		PicCommentDialog.OnPicCommitListener,
		PicCommentDialog.onPreviewListener, CommentsAdapter.onMyClick {

	/**
	 * for comment
	 */
	public static final int REFRESH_MODE_COMMENT = 100;
	private PullToRefreshListView mPullRefreshListView;
	private CommentsAdapter mAdapter;
	private int pageIndex = 1;
	private List<Comments> data = new ArrayList<>();
	private ProgressBar progressBar;
	private EditText postComment;
	private LinearLayout parentLayout;
	private TextView emptyView;

	protected Summary summary;
	private boolean isCommentToShowKeyboard = false;
	private boolean isPicCommentToShowKeyboard = false;

	private CommentReplyDialog commentReplyDialog;

	private PicCommentDialog picCommentDialog;
	// 快照图片
	// private Bitmap bitmap;

	private UploadManager uploadManager;

	// 定义临时图片存储路径
	private String tempDir = "";
	private String tempUrl = "";
	// 七牛存储 保存文件名 token
	private String picName = "";
	private String token = "";

	// 图片地址集合
	private ArrayList<String> imageList = new ArrayList<>();
	private ArrayList<String> contentLlist = new ArrayList<>();

	private boolean isDataLoaded = false;

	@Override
	protected void initView(View convertView) {
		super.initView(convertView);
		mPullRefreshListView = (PullToRefreshListView) convertView
				.findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setOnRefreshListener(this);
		mPullRefreshListView.setOnLastItemVisibleListener(this);
		mPullRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
		mAdapter = new CommentsAdapter(getActivity(), this);
		ListView actualListView = mPullRefreshListView.getRefreshableView();
		progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
		parentLayout = (LinearLayout) convertView
				.findViewById(R.id.parent_layout);
		postComment = (EditText) convertView.findViewById(R.id.post_comment);
		emptyView = (TextView) convertView.findViewById(R.id.empty_view);
		postComment.setFocusableInTouchMode(false);
		postComment.setOnClickListener(this);
		uploadManager = new UploadManager();
		mAdapter.setData(data);
		actualListView.setAdapter(mAdapter);
		actualListView.setOnItemClickListener(this);

		// getCommentsData();

	}

	public void setSummary(Summary summary) {
		this.summary = summary;
	}

	@Override
	public int getLayoutId() {
		return R.layout.fragment_comments;
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		if (view.getId() == R.id.post_comment) {
			if (!UserAccount.isUserLogin()) {
				makeToast(getString(R.string.see_world_login_first_when_comment));
				return;
			}
			doPicComment();
		}
	}

	@Override
	public void onLastItemVisible() {

	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase refreshView) {
		pageIndex = 1;
		getCommentsData();
		progressBar.setVisibility(View.VISIBLE);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase refreshView) {
		pageIndex++;
		getCommentsData();
		progressBar.setVisibility(View.VISIBLE);
	}

	private void getCommentsData() {
		Map<String, String> map = new HashMap<>();
		map.put(CameraConstants.USER_ID,
				String.valueOf(UserAccount.getUserID()));
		map.put(CameraConstants.DEVICE_ID, String.valueOf(summary.deviceId));
		map.put(CameraConstants.PAGE_SIZE, CameraConstants.NORMAL_PAGE_SIZE);
		map.put(CameraConstants.PAGE_INDEX, String.valueOf(pageIndex));

		doPost(UrlResources.URL_GET_COMMENT_IMG,
				ApiClientUtility.getParams(map), new VolleyResponse(
						CommentsFragment.this.getActivity(), false,
						getString(R.string.com_facebook_loading)) {

					@Override
					protected void onTaskSuccess(JSONArray result) {
						super.onTaskSuccess(result);
						mPullRefreshListView.onRefreshComplete();
						Comments comments = Comments.getAllComments(result);
						progressBar.setVisibility(View.GONE);
						if (mPullRefreshListView.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_START
								|| (int) mPullRefreshListView.getTag() == REFRESH_MODE_COMMENT) {
							// 下拉刷新
							if (comments.data.size() == 0) {
								emptyView.setVisibility(View.VISIBLE);
								return;
							}
							emptyView.setVisibility(View.GONE);
							data.clear();
							data.addAll(comments.data);
							// 图片地址集合
							imageList.clear();
							imageList.addAll(comments.list);
							// 图片内容集合
							contentLlist.clear();
							contentLlist.addAll(comments.con_list);

							mAdapter.setData(data);
							mAdapter.setDeviceBelongHost(summary.userId == UserAccount
									.getUserID() ? true : false);
							mAdapter.notifyDataSetChanged();
							mPullRefreshListView.getRefreshableView()
									.setSelection(0);
							mPullRefreshListView.setTag(0);
						} else if (mPullRefreshListView.getCurrentMode() == PullToRefreshBase.Mode.PULL_FROM_END) {
							// 上拉加载
							data.addAll(comments.data);
							// **图片地址集合**
							imageList.addAll(comments.list);
							contentLlist.addAll(comments.con_list);
							mAdapter.setData(data);
							mAdapter.setDeviceBelongHost(summary.userId == UserAccount
									.getUserID() ? true : false);
							mAdapter.notifyDataSetChanged();
						}

					}

					@Override
					protected void onTaskFailure() {
						super.onTaskFailure();
						mPullRefreshListView.onRefreshComplete();
						progressBar.setVisibility(View.GONE);
					}

					@Override
					protected void onTaskError(int code) {
						super.onTaskError(code);
						mPullRefreshListView.onRefreshComplete();
						progressBar.setVisibility(View.GONE);
					}
				}

		);
	}

	/**
	 * 此处做放开全部评论
	 * 
	 * @param adapterView
	 * @param view
	 * @param i
	 * @param l
	 */
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		// 当前设备由当前登录用户分享，可以进行回复和删除评论操作
		if (summary.userId == UserAccount.getUserID() ? true : false) {
			if (commentReplyDialog == null) {
				commentReplyDialog = new CommentReplyDialog(this.getActivity(),
						R.style.ZhiYunVideoSettingDialog, this);
			}
			commentReplyDialog.show();
			WindowManager.LayoutParams params = commentReplyDialog.getWindow()
					.getAttributes();
			params.width = KCApplication.getWindowWidth() * 5 / 6;
			commentReplyDialog.getWindow().setAttributes(params);
			commentReplyDialog.setListPosition(i - 1);
		}// 非本人回复
		else {
			if (!UserAccount.isUserLogin()) {
				makeToast(getString(R.string.see_world_login_first_when_reply));
				return;
			}
			// 不能自己回复自己
			if (!data.get(i - 1).userName.equals(UserAccount.getUserName())) {
				if (picCommentDialog == null) {
					picCommentDialog = new PicCommentDialog(this.getActivity(),
							R.style.ZhiYunVideoSettingDialog, this, this);
				}
				picCommentDialog.getWindow().setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
				WindowManager.LayoutParams params = picCommentDialog
						.getWindow().getAttributes();
				params.width = KCApplication.getWindowWidth();
				params.height = parentLayout.getContext().getResources()
						.getDimensionPixelSize(R.dimen.common_edittext_height);
				picCommentDialog.setMode(PicCommentDialog.MODE_REPLY,
						data.get(i - 1).userName);
				picCommentDialog.setCursorPosition(data.get(i - 1).userName
						.length() + 4);
				picCommentDialog.show();
				isPicCommentToShowKeyboard = true;

			}
		}
	}

	/*
	 * 本人回复 删除/恢复选择框
	 */
	@Override
	public void onSelectionSelect(final int listPosition, int position) {
		if (position == 1) {
			// 回复
			if (!UserAccount.isUserLogin()) {
				makeToast(getString(R.string.see_world_login_first_when_reply));
				return;
			}
			if (picCommentDialog == null) {
				picCommentDialog = new PicCommentDialog(this.getActivity(),
						R.style.ZhiYunVideoSettingDialog, this, this);
			}
			picCommentDialog.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			WindowManager.LayoutParams params = picCommentDialog.getWindow()
					.getAttributes();
			params.width = KCApplication.getWindowWidth();
			params.height = parentLayout.getContext().getResources()
					.getDimensionPixelSize(R.dimen.common_edittext_height);
			picCommentDialog.show();
			picCommentDialog.setPosition(listPosition);
			picCommentDialog.setMode(PicCommentDialog.MODE_REPLY,
					data.get(listPosition).userName);
			picCommentDialog.setCursorPosition(data.get(listPosition).userName
					.length() + 4);
			isCommentToShowKeyboard = true;
		}
		if (position == 2) {
			// 删除
			if (!UserAccount.isUserLogin()) {
				makeToast(getString(R.string.see_world_login_first_when_delete));
				return;
			}
			new AlertDialog.Builder(this.getActivity())
					.setTitle(getString(R.string.see_world_delete_comment))
					.setPositiveButton(getString(R.string.btn_ok),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialogInterface, int i) {
									Map<String, String> map = new HashMap<>();
									map.put(CameraConstants.USER_ID, String
											.valueOf(UserAccount.getUserID()));
									map.put(CameraConstants.DEVICE_ID,
											String.valueOf(summary.deviceId));
									map.put(CameraConstants.REVIEW_ID, String
											.valueOf(data.get(listPosition).id));
									// RequestParams params = new
									// RequestParams(ApiClientUtility.getParams(map));
									doPost(UrlResources.URL_DELETE_COMMENT,
											ApiClientUtility.getParams(map),
											new VolleyResponse(
													CommentsFragment.this
															.getActivity(),
													false, "loading...") {

												@Override
												protected void onTaskSuccess(
														JSONObject result) {
													super.onTaskSuccess(result);
													makeToast(getString(R.string.see_world_delete_success));
													mPullRefreshListView
															.setTag(REFRESH_MODE_COMMENT);
													pageIndex = 1;
													getCommentsData();
												}
											});
									dialogInterface.dismiss();
								}
							})
					.setNegativeButton(getString(R.string.btn_cancel),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialogInterface, int i) {
									dialogInterface.dismiss();
								}
							}).create().show();
		}
	}

	/**
	 * 评论
	 */
	public void postComment(String editStr, int flag) {
		// 提交评论
		if (flag == 0) {
			Map<String, String> map = new HashMap<>();
			map.put(CameraConstants.USER_ID,
					String.valueOf(UserAccount.getUserID()));
			map.put(CameraConstants.DEVICE_ID, String.valueOf(summary.deviceId));
			map.put(CameraConstants.CONTENT, editStr);
			map.put("Longitude", LocationUtil.getLatitude(this.getActivity()));
			map.put("Latitude", LocationUtil.getLongitude(this.getActivity()));
			map.put("Image", "");
			map = ApiClientUtility.getParams(map);
			// 推送类型
			map.put("TerminalSystemType", "20");
			// 无图模式提交评论
			doPost(UrlResources.URL_COMMIT_COMMENT, map, new VolleyResponse(
					CommentsFragment.this.getActivity(), true,
					getString(R.string.activity_base_progress_dialog_content)) {
				@Override
				public void onTaskSuccessRoot(JSONObject obj) {
					mPullRefreshListView.setTag(REFRESH_MODE_COMMENT);
					pageIndex = 1;
					getCommentsData();
					summary.reviewCount++;
					if (picCommentDialog != null) {
						picCommentDialog.clearText();
					}
				}
			});
		} else {
			showProgressDialog();
			setProgressText(getString(R.string.activity_base_progress_dialog_content));
			// 有图模式
			// 升成文件名 YYYY/MM/DD/32位GUID
			picName = ToolUtil.fileRule();
			try {
				// picName = Etag.file(tempUrl) + (System.currentTimeMillis() /
				// 1000) + ".jpg";
				if (picName.length() > 1 && token.length() > 0) {
					// 转换成字节数组 再传
					// byte[] btb = ImageUtils.getByteArrayFromBitmap(bitmap);
					// 路径 -- 指定骑牛服务器上的文件名 -- token -- 回调 -- 进度通知等
					if (token.length() > 1) {
						Map<String, String> map = new HashMap<>();
						map.put(CameraConstants.USER_ID,
								String.valueOf(UserAccount.getUserID()));
						map.put(CameraConstants.DEVICE_ID,
								String.valueOf(summary.deviceId));
						map.put(CameraConstants.CONTENT, editStr);
						map.put("Longitude",
								LocationUtil.getLatitude(this.getActivity()));
						map.put("Latitude",
								LocationUtil.getLongitude(this.getActivity()));
						map.put("Image", picName);
						map = ApiClientUtility.getParams(map);
						// 推送类型
						map.put("TerminalSystemType", "20");
						final Map<String, String> myMap = map;
						uploadManager.put(tempUrl, picName, token,
								new UpCompletionHandler() {

									@Override
									public void complete(String s,
											ResponseInfo responseInfo,
											JSONObject jsonObject) {
										LogUtil.i("qn_upload", s + "//"
												+ responseInfo + "//"
												+ jsonObject);
										// 等待七牛的图片上传完成，再上传至服务器
										doPost(UrlResources.URL_CREATE_COMMENT_IMG,
												myMap,
												new VolleyResponse(
														CommentsFragment.this
																.getActivity(),
														false, "loading...") {

													@Override
													public void onTaskSuccessRoot(
															JSONObject obj) {
														mPullRefreshListView
																.setTag(REFRESH_MODE_COMMENT);
														pageIndex = 1;
														postPicComment();
														getCommentsData();
														summary.reviewCount++;
														removeProgressDialog();
														if (picCommentDialog != null) {
															picCommentDialog
																	.clearText();
														}
													}

													@Override
													protected void onTaskFailure() {
														super.onTaskFailure();
														removeProgressDialog();
													}

												});
									}

								}, null);
					}
				} else {
					removeProgressDialog();
				}
			} catch (Exception e) {
				removeProgressDialog();
				e.printStackTrace();
			}
		}
	}

	public void sharedComment(String money) {
		Map<String, String> map = new HashMap<>();
		map.put(CameraConstants.USER_ID,
				String.valueOf(UserAccount.getUserID()));
		map.put(CameraConstants.DEVICE_ID, String.valueOf(summary.deviceId));
		map = ApiClientUtility.getParams(map);
		
		map.put("Longitude", LocationUtil.getLatitude(this.getActivity()));
		map.put("Latitude", LocationUtil.getLongitude(this.getActivity()));
		map.put("totalfee", money);

		// 无图模式提交评论
		doPost("https://api.kaicongyun.com/v4/device/public/reward_review", map, new VolleyResponse(
				CommentsFragment.this.getActivity(), true,
				getString(R.string.activity_base_progress_dialog_content)) {
			@Override
			public void onTaskSuccessRoot(JSONObject obj) {
				mPullRefreshListView.setTag(REFRESH_MODE_COMMENT);
				pageIndex = 1;
				getCommentsData();
				summary.reviewCount++;
				if (picCommentDialog != null) {
					picCommentDialog.clearText();
				}
			}
		});
	}

	// 截图评论
	public void doPicComment() {
		// 弹出 截图品论对话框 获取 bitmap
		final Bitmap bitmap = ((BaseSeeWorldActivity) this.getActivity())
				.getBitmap();
		if (bitmap == null) {
			// 文字评论
			if (picCommentDialog == null) {
				picCommentDialog = new PicCommentDialog(this.getActivity(),
						R.style.ZhiYunVideoSettingDialog, this, this);
			}
			picCommentDialog.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
			WindowManager.LayoutParams params = picCommentDialog.getWindow()
					.getAttributes();
			params.width = KCApplication.getWindowWidth();
			params.height = parentLayout.getContext().getResources()
					.getDimensionPixelSize(R.dimen.common_edittext_height);
			picCommentDialog.clearText();
			picCommentDialog.show();
			picCommentDialog.setMode(PicCommentDialog.MODE_COMMENT_TEXT, "");
			isPicCommentToShowKeyboard = true;
		} else {
			if (checkDirExit()) {// 存在目录
				new Thread(new Runnable() {
					@Override
					public void run() {// 存本地
						deleteBitMap();
						saveBitMap(bitmap);
					}
				}).start();
				if (token.length() <= 1) {// 获取token
					getToken();
				}

				if (picCommentDialog == null) {
					picCommentDialog = new PicCommentDialog(this.getActivity(),
							R.style.ZhiYunVideoSettingDialog, this, this);
				}
				picCommentDialog.setImage(bitmap);
				picCommentDialog.getWindow().setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
				WindowManager.LayoutParams params = picCommentDialog
						.getWindow().getAttributes();
				params.width = KCApplication.getWindowWidth();
				params.height = parentLayout.getContext().getResources()
						.getDimensionPixelSize(R.dimen.common_edittext_height);
				picCommentDialog.clearText();
				picCommentDialog.show();
				picCommentDialog.setMode(PicCommentDialog.MODE_COMMENT_IMAGE,
						"");
				isPicCommentToShowKeyboard = true;
			}
		}

	}

	/**
	 * 获取token
	 */
	private void getToken() {
		Map<String, String> map = new HashMap<>();
		map.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
		map.put("guid", UUID.randomUUID().toString());
		map.put("scope", "kaicong-img5");
		map.put("deadline",
				String.valueOf(System.currentTimeMillis() / 1000 + 7200));
		doPost(UrlResources.URL_GET_TOKEN,
				ApiClientUtility.getParams(map),
				new VolleyResponse(
						CommentsFragment.this.getActivity(),
						true,
						getString(R.string.activity_base_progress_dialog_content)) {

					@Override
					public void onTaskSuccessRoot(JSONObject response) {
						try {
							token = response.getString("uploadToken");
							LogUtil.e("token", token);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					protected void onTaskFailure() {
						super.onTaskFailure();
					}
				});
	}

	public void showPicCommentDialog(int heightDifference) {
		if (isPicCommentToShowKeyboard && picCommentDialog != null) {
			WindowManager.LayoutParams params = picCommentDialog.getWindow()
					.getAttributes();
			params.width = KCApplication.getWindowWidth();
			params.height = parentLayout.getContext().getResources()
					.getDimensionPixelSize(R.dimen.common_edittext_height);
			int yPosition = KCApplication.getWindowHeight()
					- heightDifference
					- parentLayout
							.getContext()
							.getResources()
							.getDimensionPixelSize(
									R.dimen.common_edittext_height) / 2;
			params.y = yPosition / 2;
			picCommentDialog.getWindow().setAttributes(params);
			LogUtil.d("chu", "keyboard length=" + heightDifference);
			picCommentDialog.show();
			isPicCommentToShowKeyboard = false;
		}
	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public void onPicCommentCommit(int mode, String editStr, int position) {
		if (mode == PicCommentDialog.MODE_DISMISS) {
			dismissPicCommentDialog();
		} else if (mode == PicCommentDialog.MODE_COMMENT_TEXT) {
			postComment(editStr.replace(" ", ""), 0);
		} else if (mode == PicCommentDialog.MODE_COMMENT_IMAGE) {
			postComment(editStr.replace(" ", ""), 2);
		} else if (mode == PicCommentDialog.MODE_REPLY) {
			replyComments(data.get(position).id, editStr.replace(" ", ""));
		}
	}

	/**
	 * 评论回复
	 */
	private void replyComments(int reviewId, String editStr) {
		Map<String, String> map = new HashMap<>();
		map.put(CameraConstants.USER_ID,
				String.valueOf(UserAccount.getUserID()));
		map.put("review_id", String.valueOf(reviewId));
		map.put(CameraConstants.CONTENT, editStr);
		map.put("Longitude", KCApplication.Longitude);
		map.put("Latitude", KCApplication.Latitude);
		map.put("Image", "");
		map = ApiClientUtility.getParams(map);
		// 无图模式提交评论
		doPost(UrlResources.URL_REPLY_COMMENTS, map, new VolleyResponse(
				CommentsFragment.this.getActivity(), true,
				getString(R.string.activity_base_progress_dialog_content)) {
			@Override
			public void onTaskSuccessRoot(JSONObject obj) {
				mPullRefreshListView.setTag(REFRESH_MODE_COMMENT);
				pageIndex = 1;
				getCommentsData();
				summary.reviewCount++;
				if (picCommentDialog != null) {
					picCommentDialog.clearText();
				}
			}
		});
	}

	@Override
	public void doPreview() {
		Intent intent = new Intent(getActivity(),
				Preview_PicCommentActivity.class);
		intent.putExtra("flag", 1);
		intent.putExtra("url", tempUrl);
		BaseSeeWorldActivity.clickRoute = 1;
		// 传递 url集合
		intent.putStringArrayListExtra("imageList", null);
		intent.putStringArrayListExtra("contentLlist", null);
		startActivityForResult(intent, 2);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void dismissPicCommentDialog() {
		if (picCommentDialog != null) {
			picCommentDialog.dismiss();
		}
	}

	public void setShowDialog() {
		isPicCommentToShowKeyboard = true;
	}

	// 上传
	public void postPicComment() {

	}

	private void saveBitMap(Bitmap bm) {
		if (bm != null) {
			File f = new File(tempUrl);
			try {
				f.createNewFile();
				FileOutputStream out = new FileOutputStream(f);
				boolean b = bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				LogUtil.e("picComment", e + "");
			}
		}
	}

	// 判断是否存在文件夹
	private boolean checkDirExit() {
		if (KCApplication.isSDExist()) {
			tempDir = Environment.getExternalStorageDirectory().getPath()
					+ "/Android";
			File destDir = new File(tempDir);
			if (!destDir.exists()) {
				destDir.mkdirs();
			}
			tempUrl = tempDir + "/tempPicComment.jpg";
			return true;
		} else {
			return false;
		}
	}

	private void deleteBitMap() {
		File f = new File(tempUrl);
		if (f.exists()) {
			f.delete();
		}
	}

	// 适配列表 图片预览
	@Override
	public void clickImage(int i) {
		Intent intent = new Intent(getActivity(),
				Preview_PicCommentActivity.class);
		intent.putExtra("flag", 3);
		intent.putExtra("device_id", summary.deviceId);
		intent.putExtra("current_index", pageIndex);
		BaseSeeWorldActivity.clickRoute = 2;
		// 传递 url集合
		intent.putStringArrayListExtra("imageList", imageList);
		intent.putStringArrayListExtra("contentLlist", contentLlist);
		intent.putExtra("url", data.get(i).prevUrl);
		// intent.putExtra("currentLocation", i);
		startActivityForResult(intent, 2);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (!isDataLoaded && isVisibleToUser) {
			getCommentsData();
		} else {
			if (progressBar != null) {
				progressBar.setVisibility(View.GONE);
			}
		}
	}

}
