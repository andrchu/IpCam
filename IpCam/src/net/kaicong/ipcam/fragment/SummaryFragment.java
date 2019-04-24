package net.kaicong.ipcam.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.kaicong.ipcam.BaseFragment;
import net.kaicong.ipcam.R;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.PositionModel;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.device.seeworld.PositionDetailActivity;
import net.kaicong.ipcam.device.seeworld.Summary;
import net.kaicong.ipcam.bean.GetCameraModel;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.utility.ApiClientUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LingYan on 14-12-9.
 */
public class SummaryFragment extends BaseFragment {

	private TextView shareName;
	private TextView shareUser;
	private TextView shareTime;
	private TextView shareHotNum;
	private TextView shareModel;
	private ImageView shareDeviceIcon;
	private TextView positionName;
	private LinearLayout positionLayout;

	// add
	private TextView rewardCount;
	// 头像gridview
	private GridView gridview;

	private List<String> head_list = new ArrayList<>();

	private ImageLoader loader;
	//
	private int total;
	// //设备ID
	// private int devId = 0;
	// private String user = "";
	private Summary summary;
	// 地标信息
	private PositionModel positionModel;

	public void setSummary(Summary summary) {
		this.summary = summary;
	}

	public void setPositionModel(PositionModel positionModel) {
		this.positionModel = positionModel;
	}

	@Override
	protected void initView(View convertView) {
		super.initView(convertView);
		shareName = (TextView) convertView.findViewById(R.id.shared_name);
		shareUser = (TextView) convertView.findViewById(R.id.shared_user);
		shareTime = (TextView) convertView.findViewById(R.id.shared_time);
		shareHotNum = (TextView) convertView.findViewById(R.id.shared_hot_num);
		shareModel = (TextView) convertView.findViewById(R.id.shared_model);
		shareDeviceIcon = (ImageView) convertView
				.findViewById(R.id.shared_device_icon);

		rewardCount = (TextView) convertView
				.findViewById(R.id.tev_sumary_rewardCount);
		rewardCount.setText("0");
		gridview = (GridView) convertView
				.findViewById(R.id.grid_summry_reward_Heads);

		positionLayout = (LinearLayout) convertView
				.findViewById(R.id.position_layout);
		positionName = (TextView) convertView.findViewById(R.id.position_name);
		if (positionModel.IsVerify == -1) {
			// 没有申请过地标，隐藏
			positionLayout.setVisibility(View.GONE);
		}
		positionName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(SummaryFragment.this.getActivity(),
						PositionDetailActivity.class);
				intent.putExtra("position_model", positionModel);
				startActivity(intent);
			}
		});

		loader = ImageLoader.getInstance();
		// shareUser.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View view) {
		// Intent intent = new Intent();
		// intent.setClass(SummaryFragment.this.getActivity(),
		// GetSharedDevicesActivity.class);
		// intent.putExtra("title", summary.shareUser);
		// startActivity(intent);
		// SummaryFragment.this.getActivity().finish();
		// }
		//
		// });

		shareName.setText(summary.shareTitle);
		shareUser.setText(summary.shareUser);
		shareTime.setText(summary.shareTime.replace("T", "  "));
		shareHotNum.setText(String.valueOf(summary.reviewCount));
		shareModel.setText(GetCameraModel.getCameraName(this.getActivity(),
				summary.modelId));
		/**
		 * 通过图片名称获取图片资源id
		 */
		Resources resources = this.getActivity().getResources();
		int identify = resources.getIdentifier(
				this.getActivity().getPackageName() + ":drawable-hdpi/"
						+ String.valueOf(summary.modelId) + "_2", null, null);
		if (identify > 0) {
			shareDeviceIcon.setImageDrawable(resources.getDrawable(identify));
		} else if (identify == 0) {
			// 没有对应图片,取一个默认图片
			switch (GetCameraModel.getCameraModel(summary.modelId)) {
			case GetCameraModel.CAMERA_MODEL_SIP1018:
				shareDeviceIcon.setImageResource(R.drawable.sip1018_2);
				break;
			case GetCameraModel.CAMERA_MODEL_SIP1201:
				shareDeviceIcon.setImageResource(R.drawable.sip1201_2);
				break;
			case GetCameraModel.CAMERA_MODEL_SIP1601:
				shareDeviceIcon.setImageResource(R.drawable.sip1601_2);
				break;
			case GetCameraModel.CAMERA_MODEL_SIP1303:
				shareDeviceIcon.setImageResource(R.drawable.sip1303_2);
				break;
			case GetCameraModel.CAMERA_MODEL_SIP1211:
				shareDeviceIcon.setImageResource(R.drawable.sip1211_2);
				break;
			case GetCameraModel.CAMERA_MODEL_ZHIYUN:
				shareDeviceIcon.setImageResource(R.drawable.zcloud_log);
				break;
			}
		}
		// 获取打赏记录信息
		head_list.clear();
		// getRewardInfo();

	}

	@Override
	public int getLayoutId() {
		return R.layout.fragment_summary;
	}

	// 获取打赏该设备的人的信息 ---总打赏数//人头像
	private void getRewardInfo() {
		LogUtil.d("deviceId", summary.deviceId + "");
		Map<String, String> map = new HashMap<>();
		map.put("device_id", String.valueOf(summary.deviceId));
		doPost(UrlResources.URL_RewardRecords, ApiClientUtility.getParams(map),
				new VolleyResponse(this.getActivity(), false,
						getString(R.string.com_facebook_loading)) {
					@Override
					public void onSuccess(JSONObject response) {
						if (response != null) {
							if (response.has("code")) {
								int code = response.optInt("code");
								if (code == 1) {
									if (response.has("total")) {
										// 打赏总次数
										total = response.optInt("total");
									}
									if (response.has("items")) {
										JSONArray items = response
												.optJSONArray("items");
										if (items.length() > 0) {
											JSONObject jso;
											for (int i = 0; i < items.length(); i++) {
												jso = items.optJSONObject(i);
												if (jso.has("head_path")) {
													head_list.add(jso
															.optString("head_path"));
												}
											}

										}
									}
									LogUtil.d("tota//list", "total:" + total
											+ "// list:" + head_list);
									rewardCount.setText(total + "");
									if (head_list.size() > 0) {
										gridview.setVisibility(View.VISIBLE);
										gridview.setAdapter(new MyAdapter());
									} else {
										gridview.setVisibility(View.GONE);
									}
								}
							}
						}
					}
				});
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (head_list.size() >= 8) {
				return 8;
			} else {
				return head_list.size();
			}
		}

		@Override
		public Object getItem(int position) {
			return head_list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.layout_item_summry_head, null);
				viewHolder.img = (ImageView) convertView
						.findViewById(R.id.item_summry_head);
				convertView.setTag(viewHolder);
			}
			viewHolder = (ViewHolder) convertView.getTag();

			loader.displayImage(head_list.get(position), viewHolder.img);

			return convertView;
		}
	}

	class ViewHolder {
		private ImageView img;
	}

	/**
	 * fragment懒加载
	 * 
	 * @param isVisibleToUser
	 */
	// @Override
	// public void setUserVisibleHint(boolean isVisibleToUser) {
	// super.setUserVisibleHint(isVisibleToUser);
	// if (getUserVisibleHint()) {
	// if (devId > 0) {
	// getRewardInfo();
	// }
	// }
	// }
}
