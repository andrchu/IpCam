package net.kaicong.ipcam.message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.R;
import net.kaicong.ipcam.UmengShareUtils;
import net.kaicong.ipcam.adpater.ExtendedViewPager;
import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.MessRecord;
import net.kaicong.ipcam.bean.Mess_ComRecord;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.device.seeworld.TouchImageView;
import net.kaicong.ipcam.user.UserAccount;
import net.kaicong.ipcam.utils.CapturePhotoUtils;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.ipcam.utils.ToolUtil;
import net.kaicong.utility.ApiClientUtility;

/**
 * Author: lu_qwen Intro: 信息详情 Time: 2015/7/21.
 */
public class MessDetailActivity extends BaseActivity implements
		ViewPager.OnPageChangeListener, View.OnTouchListener {

	private RelativeLayout root;

	private TextView save;
	private TextView share;

	private ImageLoader loader;
	private int window_width;
	private int window_height;

	private Bitmap mBitmap;

	private UmengShareUtils umengShareUtils;

	// 下标 及 内容
	private TextView tev_current;
	private TextView tev_total;

	private LayoutInflater inflater;

	private String real_URL = "";

	// viewPager
	private ExtendedViewPager viewPager;
	// 图片 地址集合
	private List<String> list;
	// 当前图片 所在的位置
	private int c_pos = 0;
	private PreViewAdapter adapter;

	private VClick vClick;
	// 当前滑动索引
	private int currentIndex;
	private List<Boolean> isFirstClick = new ArrayList<>();

	// 类别 0:警报 -- 1：评论
	private int messKind = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hiddenBar();
		setContentView(R.layout.activity_messdetail);
		init();
	}

	private void init() {
		loader = ImageLoader.getInstance();
		umengShareUtils = new UmengShareUtils(this);
		WindowManager manager = getWindowManager();
		window_width = manager.getDefaultDisplay().getWidth();
		window_height = manager.getDefaultDisplay().getHeight();

		root = (RelativeLayout) findViewById(R.id.rel_root);
		// img = (ImageView) findViewById(R.id.img_messDetail_img);
		save = (TextView) findViewById(R.id.tev_messDetail_save);
		share = (TextView) findViewById(R.id.tev_messDetail_share);

		root.setOnClickListener(this);
		save.setOnClickListener(this);
		share.setOnClickListener(this);

		tev_current = (TextView) findViewById(R.id.tev_page_current);
		tev_total = (TextView) findViewById(R.id.tev_pageTotal);

		// viewpage
		list = new ArrayList<>();
		viewPager = (ExtendedViewPager) findViewById(R.id.pre_viewpage);
		setVPParam();
		adapter = new PreViewAdapter();
		viewPager.setOnPageChangeListener(this);
		viewPager.setOnTouchListener(this);

		// item布局
		inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		vClick = new VClick();

		Intent intent = getIntent();
		currentIndex = intent.getIntExtra("pageIndex", 0);
		real_URL = intent.getStringExtra("url");
		messKind = intent.getIntExtra("kind", 0);
		// 获取集合 与当前图片所在集合的位置
		if (intent.getStringArrayListExtra("url_list") != null) {
			list.clear();
			list.addAll(intent.getStringArrayListExtra("url_list"));
			c_pos = list.indexOf(real_URL);

			/**
			 * 数组赋初始值
			 */
			Boolean[] array = new Boolean[list.size()];
			Arrays.fill(array, false);
			List<Boolean> tempList = Arrays.asList(array);
			isFirstClick.addAll(tempList);

		}

		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(c_pos);

		// 位置标识
		tev_total.setText(list.size() + "");
		tev_current.setText(c_pos + 1 + "");

		// 当pageindex=1时，如果此时只有一张图片，因为onPageSelected方法不会调用，所以
		// 在这里调用一次获取oageindex=2的数据
		if (list.size() == 1) {
			getData();
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

	private void setImgParam(ImageView v) {
		ViewGroup.LayoutParams params = v.getLayoutParams();
		params.width = window_width;
		params.height = window_height;
		v.setLayoutParams(params);
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.rel_root:
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.tev_messDetail_save:
			// 保存
			if (mBitmap != null) {
				String name = ToolUtil.getNowTimeStr();
				CapturePhotoUtils.insertImage(getContentResolver(), mBitmap,
						name, name);
				makeToast(getString(R.string.tips_snapshot_ok));
			}
			break;
		case R.id.tev_messDetail_share:
			// 分享
			if (mBitmap != null) {
				umengShareUtils.share(mBitmap);
			}
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(RESULT_OK);
			finish();
		}
		return true;
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
			setImgParam(imageView);
			loader.displayImage(list.get(position), imageView,
					new ImageLoadingListener() {

						@Override
						public void onLoadingStarted(String imageUri, View view) {

						}

						@Override
						public void onLoadingFailed(String imageUri, View view,
								FailReason failReason) {

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

		if ((arg0 + 1) == list.size() && !isFirstClick.get(arg0)) {
			// 当滑动到最后一张时并且是第一次滑动到此处，进行网络请求
			getData();
		}
		isFirstClick.set(arg0, true);
	}

	/**
	 * 内部View的点击事件 解决 点击区域和 翻页点击
	 */
	private class VClick implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			setResult(RESULT_OK);
			finish();
		}

	}

	private void getData() {
		if (messKind == 0) {
			// 移动侦测
			Map<String, String> map = new HashMap<>();
			map.put(CameraConstants.USER_ID,
					String.valueOf(UserAccount.getUserID()));
			map.put(CameraConstants.PAGE_SIZE, CameraConstants.NORMAL_PAGE_SIZE);
			currentIndex++;
			map.put(CameraConstants.PAGE_INDEX, currentIndex + "");
			doPost(UrlResources.URL_WARN_LIST, ApiClientUtility.getParams(map),
					new VolleyResponse(MessDetailActivity.this, true,
							getString(R.string.com_facebook_loading)) {

						@Override
						protected void onTaskSuccess(JSONArray result) {
							super.onTaskSuccess(result);
							MessRecord record = MessRecord.getData(result);
							list.addAll(record.urlList);
							for (int i = 0; i < record.urlList.size(); i++) {
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
					});
		} else {
			// 评论
			Map<String, String> map = new HashMap<>();
			map.put(CameraConstants.USER_ID,
					String.valueOf(UserAccount.getUserID()));
			currentIndex++;
			map.put(CameraConstants.PAGE_INDEX, currentIndex + "");
			doPost(UrlResources.URL_MESS_COMMENT_LIST,
					ApiClientUtility.getParams(map), new VolleyResponse(
							MessDetailActivity.this, true,
							getString(R.string.com_facebook_loading)) {
						@Override
						protected void onTaskSuccess(JSONArray result) {
							super.onTaskSuccess(result);
							Mess_ComRecord record = Mess_ComRecord
									.getData(result);
							list.addAll(record.imglist);
							for (int i = 0; i < record.imglist.size(); i++) {
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
					});
		}
	}
}
