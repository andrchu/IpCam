package net.kaicong.ipcam.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.kaicong.ipcam.KCApplication;
import net.kaicong.ipcam.R;

import net.kaicong.ipcam.bean.AnimateFirstDisplayListener;
import net.kaicong.ipcam.bean.GetCameraModel;
import net.kaicong.ipcam.bean.SharedCamera;
import net.kaicong.ipcam.utils.ImageUtils;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;
import java.util.Timer;

/**
 * Created by LingYan on 2014/9/4.
 */
public class WorldViewAdapter extends BaseAdapter {
	private List<SharedCamera> data;
	private OnWorldViewItemClickListener onWorldViewItemClickListener;
	private Timer timer;
	/**
	 * 透明动画*
	 */
	Animation mAnimation = null;
	private ImageLoader imageLoader = ImageLoader.getInstance();

	public void setData(List<SharedCamera> data) {
		this.data = data;
	}

	public WorldViewAdapter(Context context) {
		super();
		/** 加载透明动画 **/
		mAnimation = AnimationUtils.loadAnimation(context,
				R.anim.world_view_alpha_change);
		timer = new Timer();
	}

	public void setOnWorldViewItemClickListener(
			OnWorldViewItemClickListener onWorldViewItemClickListener) {
		this.onWorldViewItemClickListener = onWorldViewItemClickListener;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int i) {
		return data.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(final int i, View view, ViewGroup viewGroup) {
		ViewHolder viewHolder;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(viewGroup.getContext()).inflate(
					R.layout.item_world_view, viewGroup, false);
			viewHolder.itemLayout = (LinearLayout) view
					.findViewById(R.id.world_view_item);
			viewHolder.sharedNameTextView = (TextView) view
					.findViewById(R.id.shared_name);
			viewHolder.cameraImageView = (ImageView) view
					.findViewById(R.id.imageview);
			viewHolder.imageViewZhiyun = (ImageView) view
					.findViewById(R.id.image_zhiyun);
			int mImageWidth = KCApplication.getWindowWidth();
			int mImageHeight = mImageWidth * 2 / 3;
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					mImageWidth, mImageHeight);
			viewHolder.cameraImageView.setLayoutParams(params);
			viewHolder.alphaImageView = (ImageView) view
					.findViewById(R.id.alpha_imageview);
			viewHolder.praiseTextView = (TextView) view
					.findViewById(R.id.item_praise);
			viewHolder.commentTextView = (TextView) view
					.findViewById(R.id.item_comment);
			viewHolder.popularityTextView = (TextView) view
					.findViewById(R.id.item_popularity);
			viewHolder.shareDateTextView = (TextView) view
					.findViewById(R.id.item_share_date);
			viewHolder.imageViewPosition = (ImageView) view
					.findViewById(R.id.image_position);
			view.setTag(viewHolder);
		}
		viewHolder = (ViewHolder) view.getTag();
		viewHolder.sharedNameTextView.setText(data.get(i).shareName);
		imageLoader.displayImage(data.get(i).imageUrl,
				viewHolder.cameraImageView, ImageUtils.getDisplayOptions(
						R.drawable.world_view_common_image, null),
				new AnimateFirstDisplayListener());
		viewHolder.praiseTextView.setText(data.get(i).praiseCount);
		viewHolder.commentTextView.setText(data.get(i).commentCount);
		viewHolder.popularityTextView.setText(data.get(i).popularity);
		if (data.get(i).date.contains("T")) {
			String date = data.get(i).date.substring(0,
					data.get(i).date.indexOf("T"));
			viewHolder.shareDateTextView.setText(date);
		} else {
			viewHolder.shareDateTextView.setText(data.get(i).date);
		}
		if (data.get(i).ddnsModelId == GetCameraModel.CAMERA_MODEL_ZHIYUN) {
			// 智云
			viewHolder.imageViewZhiyun.setVisibility(View.VISIBLE);
		} else {
			viewHolder.imageViewZhiyun.setVisibility(View.GONE);
		}
		if (data.get(i).IsVerify == 1) {
			// 地标认证成功
			viewHolder.imageViewPosition.setVisibility(View.VISIBLE);
		} else {
			viewHolder.imageViewPosition.setVisibility(View.GONE);
		}
		final ImageView alImageView = viewHolder.alphaImageView;
		viewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// alImageView.startAnimation(mAnimation);
				// final View mView = view;
				// timer.schedule(new TimerTask() {
				// @Override
				// public void run() {
				// if (onWorldViewItemClickListener != null) {
				// onWorldViewItemClickListener.onWorldViewItemClick(mView, i);
				// }
				// }
				// }, 500);
				if (onWorldViewItemClickListener != null) {
					onWorldViewItemClickListener.onWorldViewItemClick(view, i);
				}
			}
		});

		return view;
	}

	private class ViewHolder {
		public LinearLayout itemLayout;
		public TextView sharedNameTextView;
		public ImageView cameraImageView;
		public ImageView alphaImageView;
		public TextView praiseTextView;
		public TextView commentTextView;
		public TextView popularityTextView;
		public TextView shareDateTextView;
		public ImageView imageViewZhiyun;
		public ImageView imageViewPosition;
	}

	// 取消动画
	public void cancelAnimation() {
		if (timer != null && mAnimation != null) {
			timer.cancel();
			timer = null;
			mAnimation.cancel();
			mAnimation = null;
		}
	}

	public interface OnWorldViewItemClickListener {

		public void onWorldViewItemClick(View view, int position);

	}

}
