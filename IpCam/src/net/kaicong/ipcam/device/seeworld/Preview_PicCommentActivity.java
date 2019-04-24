package net.kaicong.ipcam.device.seeworld;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import net.kaicong.ipcam.R;
import net.kaicong.ipcam.UmengShareUtils;
import net.kaicong.ipcam.adpater.ExtendedViewPager;
import net.kaicong.ipcam.api.VolleyHttpUtil;
import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.user.UserAccount;
import net.kaicong.ipcam.utils.CapturePhotoUtils;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.ipcam.utils.ToolUtil;
import net.kaicong.utility.ApiClientUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: lu_qwen Intro: Time: 2015/3/25.
 * <p/>
 * Modified：LingYan Time：2015年07月03日15:43:45 Content：优化截图滑动
 */
public class Preview_PicCommentActivity extends Activity implements
		ViewPager.OnPageChangeListener, View.OnTouchListener {
	private int window_width, window_height;// 控件宽度
	private int state_height;// 状态栏的高度

	private RelativeLayout layout;
	// 下标 及 内容
	private TextView tev_current;
	private TextView tev_total;
	private TextView tev_content;

	// 定义临时图片存储路径
	String tempUrl = "/sdcard/tmp/" + "tempPicComment" + ".jpg";

	private String real_URL = "";
	private VolleyHttpUtil httpUtil;
	// POP
	private PopupWindow myPop;
	private LinearLayout lel_pop;
	private ImageView picPreviewShare;
	private ImageView picPreviewSave;
	// ImageLoader
	private ImageLoader loader;
	// progress bar
	private ProgressBar progressBar;

	private Bitmap mBitmap;
	private UmengShareUtils umengShareUtils;
	// viewPager
	private ExtendedViewPager viewPager;
	// 图片 地址集合
	private List<String> list;
	private List<String> conList;
	// 当前图片 所在的位置
	private int c_pos = 0;

	private PreViewAdapter adapter;
	private LayoutInflater inflater;
	private View poplayout;
	// 标记只初始化一次适配器
	private int index = 0;
	// 子控件数组
	List<ProgressBar> list_pro = new ArrayList<>();

	private VClick vClick;
	private int flag;

	// 设备id
	private int deviceId;
	// 当前滑动索引
	private int currentIndex;
	private List<Boolean> isFirstClick = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_preview_piccomment);
		WindowManager manager = getWindowManager();
		window_width = manager.getDefaultDisplay().getWidth();
		window_height = manager.getDefaultDisplay().getHeight();

		layout = (RelativeLayout) findViewById(R.id.lel_preview);
		tev_current = (TextView) findViewById(R.id.tev_page_current);
		tev_total = (TextView) findViewById(R.id.tev_pageTotal);
		tev_content = (TextView) findViewById(R.id.tev_pre_content);

		// viewpage
		list = new ArrayList<>();
		conList = new ArrayList<>();
		viewPager = (ExtendedViewPager) findViewById(R.id.pre_viewpage);
		setVPParam();
		adapter = new PreViewAdapter();
		viewPager.setOnPageChangeListener(this);
		viewPager.setOnTouchListener(this);

		// item布局
		inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		loader = ImageLoader.getInstance();
		umengShareUtils = new UmengShareUtils(this);

		httpUtil = new VolleyHttpUtil();
		Intent intent = getIntent();
		flag = intent.getIntExtra("flag", 0);
		real_URL = intent.getStringExtra("url");

		deviceId = intent.getIntExtra("device_id", 0);
		currentIndex = intent.getIntExtra("current_index", 0);
		// 获取集合 与当前图片所在集合的位置
		if (intent.getStringArrayListExtra("imageList") != null) {
			list.clear();
			list.addAll(intent.getStringArrayListExtra("imageList"));
			conList.addAll(intent.getStringArrayListExtra("contentLlist"));
			c_pos = list.indexOf(real_URL);

			/**
			 * 数组赋初始值
			 */
			Boolean[] array = new Boolean[list.size()];
			Arrays.fill(array, false);
			List<Boolean> tempList = Arrays.asList(array);
			isFirstClick.addAll(tempList);

		}

		if (flag == 1) {// 单张预览
			list.add(0, real_URL);
			viewPager.setAdapter(adapter);
			viewPager.setCurrentItem(0);
		} else {// 列表预览
			viewPager.setAdapter(adapter);
			viewPager.setCurrentItem(c_pos);
			if (list.size() == 1) {
				// 当pageindex=1时，如果此时只有一张图片，因为onPageSelected方法不会调用，所以
				// 在这里调用一次获取oageindex=2的数据
				if (list.size() == 1) {
					getCommentsData();
				}
			}
		}

		// 位置标识
		tev_total.setText(list.size() + "");
		tev_current.setText(c_pos + 1 + "");
		if (conList.size() > 0) {
			tev_content.setVisibility(View.VISIBLE);
			tev_content.setText("\"" + conList.get(c_pos) + "\"");
		} else {
			tev_content.setVisibility(View.GONE);
		}
		// POP
		initPopWindow();
		vClick = new VClick();

		layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (myPop != null && myPop.isShowing()) {
					myPop.dismiss();
				} else if (myPop != null && !myPop.isShowing()) {
					myPop.showAtLocation(layout, Gravity.TOP, 0,
							getStatusBarHeight());
				}
			}
		});
	}

	private void getDownUrl() {
		Map<String, String> map = new HashMap<>();
		map.put("downloadUrl", real_URL);
		httpUtil.doJsonObjectRequest(UrlResources.URL_TOKEN_URL,
				ApiClientUtility.getParams(map), new VolleyResponse(this,
						false, "loading...") {

					@Override
					public void onTaskSuccess(JSONObject response) {
						super.onSuccess(response);
						try {
							real_URL = response.getString("realdownloadurl");
							LogUtil.e("real_URL", real_URL);
							// downPic();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (myPop != null) {
			myPop.dismiss();
		}
	}

	// 图viewpage参数设置
	private void setVPParam() {
		/** 获取可見区域高度 **/
		ViewGroup.LayoutParams params = viewPager.getLayoutParams();
		params.width = window_width;
		params.height = window_height;
		viewPager.setLayoutParams(params);
	}

	private void setTImgParam(TouchImageView v) {
		ViewGroup.LayoutParams params = v.getLayoutParams();
		params.width = window_width;
		params.height = window_height;
		v.setLayoutParams(params);
	}

	private void initPopWindow() {
		poplayout = inflater.inflate(R.layout.activity_picpreview_popwindow,
				null);
		myPop = new PopupWindow(poplayout, ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		myPop.setFocusable(true);
		lel_pop = (LinearLayout) poplayout
				.findViewById(R.id.lel_preview_popwindow);
		lel_pop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Preview_PicCommentActivity.this.finish();
			}
		});
		picPreviewShare = (ImageView) poplayout
				.findViewById(R.id.pic_preview_share);
		picPreviewSave = (ImageView) poplayout
				.findViewById(R.id.pic_preview_save);
		picPreviewShare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mBitmap != null) {
					umengShareUtils.share(mBitmap);
				}
			}
		});
		picPreviewSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// 保存截图
				if (mBitmap != null) {
					String name = ToolUtil.getNowTimeStr();
					CapturePhotoUtils.insertImage(getContentResolver(),
							mBitmap, name, name);
					Toast.makeText(Preview_PicCommentActivity.this,
							getString(R.string.tips_snapshot_ok),
							Toast.LENGTH_SHORT).show();
				}

			}
		});
		myPop.setOutsideTouchable(true);
		myPop.setBackgroundDrawable(new BitmapDrawable());// BUG
	}

	protected int getStatusBarHeight() {
		Rect rectangle = new Rect();
		Window window = getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
		int statusBarHeight = rectangle.top;
		return statusBarHeight;
	}

	/**
	 * ******************************* 滑动 ********************************
	 */
	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		return false;
	}

	public class PreViewAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object o) {
			return view == o;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		// -- 初始化position处的item
		@Override
		public Object instantiateItem(final ViewGroup container,
				final int position) {

			View i_layout = inflater.inflate(R.layout.activity_preview_item,
					container, false);
			final TouchImageView imageView = (TouchImageView) i_layout
					.findViewById(R.id.img_preview_picCommment);
			setTImgParam(imageView);
			if (flag != 1) {
				loader.displayImage(list.get(position), imageView,
						new ImageLoadingListener() {

							@Override
							public void onLoadingStarted(String imageUri,
									View view) {

							}

							@Override
							public void onLoadingFailed(String imageUri,
									View view, FailReason failReason) {

							}

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								imageView.setImageBitmap(loadedImage);
								mBitmap = loadedImage;
							}

							@Override
							public void onLoadingCancelled(String imageUri,
									View view) {

							}

						});
			} else {
				mBitmap = BitmapFactory.decodeFile(real_URL);
				imageView.setImageBitmap(mBitmap);
			}
			container.addView(i_layout);
			imageView.setOnClickListener(vClick);
			return i_layout;

		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		LogUtil.d("pageSelected", "pageSelected：" + arg0);
		c_pos = arg0;
		tev_current.setText(c_pos + 1 + "");
		tev_content.setText(conList.get(c_pos));

		if ((arg0 + 1) == list.size() && !isFirstClick.get(arg0)) {
			// 当滑动到最后一张时并且是第一次滑动到此处，进行网络请求
			getCommentsData();
		}
		isFirstClick.set(arg0, true);
	}

	/**
	 * 内部View的点击事件 解决 点击区域和 翻页点击
	 */
	private class VClick implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if (myPop != null && myPop.isShowing()) {
				myPop.dismiss();
			} else if (myPop != null && !myPop.isShowing()) {
				myPop.showAtLocation(layout, Gravity.TOP, 0,
						getStatusBarHeight());
			}
		}

	}

	private void getCommentsData() {
		Map<String, String> map = new HashMap<>();
		map.put(CameraConstants.USER_ID,
				String.valueOf(UserAccount.getUserID()));
		map.put(CameraConstants.DEVICE_ID, String.valueOf(deviceId));
		map.put(CameraConstants.PAGE_SIZE, CameraConstants.NORMAL_PAGE_SIZE);
		currentIndex++;
		map.put(CameraConstants.PAGE_INDEX, String.valueOf(currentIndex));

		httpUtil.doJsonObjectRequest(UrlResources.URL_GET_COMMENT_IMG,
				ApiClientUtility.getParams(map), new VolleyResponse(this,
						false, getString(R.string.com_facebook_loading)) {

					@Override
					protected void onTaskSuccess(JSONArray result) {
						super.onTaskSuccess(result);

						Comments comments = Comments.getAllComments(result);
						list.addAll(comments.list);
						conList.addAll(comments.con_list);
						for (int i = 0; i < comments.list.size(); i++) {
							// 添加新状态
							isFirstClick.add(false);
						}
						adapter.notifyDataSetChanged();
						tev_total.setText(list.size() + "");
					}

					@Override
					protected void onTaskFailure() {
						super.onTaskFailure();
					}

					@Override
					protected void onTaskError(int code) {
						super.onTaskError(code);
					}

				}

		);
	}

}
