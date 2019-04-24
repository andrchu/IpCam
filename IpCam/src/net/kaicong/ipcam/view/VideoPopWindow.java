package net.kaicong.ipcam.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import net.kaicong.ipcam.R;
import net.kaicong.ipcam.bean.VideoPopWindowIcon;

import java.util.List;

/**
 * Created by LingYan on 2014/8/27.
 */
public class VideoPopWindow extends PopupWindow {

    private View contentView;
    private LinearLayout rootLinearLayout = null;

    public VideoPopWindow(Context context, final List<VideoPopWindowIcon> icons, final OnVideoPopWindowClickListener onVideoPopWindowClickListener) {
        contentView = LayoutInflater.from(context).inflate(R.layout.pop_camera_video, null);
        rootLinearLayout = (LinearLayout) contentView.findViewById(R.id.root);
        for (int i = 0; i < icons.size(); i++) {
            final ImageView imageView = new ImageView(context);
            int imageBtnSize = context.getResources().getDimensionPixelSize(R.dimen.common_video_popwindow_size);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageBtnSize, imageBtnSize);
            params.weight = 1;
            imageView.setLayoutParams(params);
            imageView.setImageResource(icons.get(i).drawableId);
            imageView.setColorFilter(Color.WHITE);
            rootLinearLayout.addView(imageView);
            final int position = i;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onVideoPopWindowClickListener != null) {
                        onVideoPopWindowClickListener.onVideoPopWindowClick(rootLinearLayout, imageView, position, icons.get(position).isTop);
                    }
                }
            });
        }
        setContentView(contentView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(context.getResources().getDimensionPixelSize(R.dimen.pop_width));
        //设置popwindow以外的地方点击，popwindow消失
        setOutsideTouchable(true);
    }

    public void filterRedColor(int position) {
        if (rootLinearLayout != null) {
            ImageView imageView = (ImageView) rootLinearLayout.getChildAt(position);
            imageView.setColorFilter(Color.RED);
            imageView.setTag(true);
        }
    }

    public void filterWhiteColor(int position) {
        if (rootLinearLayout != null) {
            ImageView imageView = (ImageView) rootLinearLayout.getChildAt(position);
            imageView.setColorFilter(Color.WHITE);
            imageView.setTag(false);
        }
    }

    public interface OnVideoPopWindowClickListener {

        public void onVideoPopWindowClick(View parentView, View view, int position, boolean isTop);

    }

}
