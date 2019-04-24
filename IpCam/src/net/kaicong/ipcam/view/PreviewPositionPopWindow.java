package net.kaicong.ipcam.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import net.kaicong.ipcam.R;

/**
 * Created by LingYan on 2014/8/27.
 */
public class PreviewPositionPopWindow extends PopupWindow {

    private LinearLayout addContentViews;
    private List<Integer> previewData = null;
    private OnPreViewItemClickListener onPreViewItemClickListener = null;
    private OnPreviewSettingClickListener onPreviewSettingClickListener = null;

    public void setOnPreViewItemClickListener(OnPreViewItemClickListener onPreViewItemClickListener) {
        this.onPreViewItemClickListener = onPreViewItemClickListener;
    }

    public void setOnPreviewSettingClickListener(OnPreviewSettingClickListener onPreviewSettingClickListener) {
        this.onPreviewSettingClickListener = onPreviewSettingClickListener;
    }

    public PreviewPositionPopWindow(Context context, List<Integer> previewData) {
        this.previewData = previewData;
        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_preview_position, null);
        setContentView(contentView);
        addContentViews = (LinearLayout) contentView.findViewById(R.id.preview_content);
        setWidth(context.getResources().getDimensionPixelOffset(R.dimen.pop_preview_width));
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        for (int i = 0; i < previewData.size(); i++) {
            addContentViews.addView(getItemLayout(context, i));
            if (i == (previewData.size() - 1)) {
                //最后一行下面不添加横线
                break;
            }
            addContentViews.addView(getLineView(context));
        }
    }

    private RelativeLayout getItemLayout(Context context, final int position) {
        RelativeLayout layout = new RelativeLayout(context);
        //左边的图片
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView previewImage = new ImageView(context);
        previewImage.setImageResource(R.drawable.see_launcher);
        params1.addRule(RelativeLayout.CENTER_VERTICAL);
        params1.setMargins(context.getResources().getDimensionPixelOffset(R.dimen.item_preview_margin), 0, 0, 0);
        layout.addView(previewImage, params1);
        //预置位编号
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView previewText = new TextView(context);
        previewText.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.item_preview_text_size));
        params2.addRule(RelativeLayout.CENTER_IN_PARENT);
        previewText.setText(context.getResources().getString(R.string.text_video_preview) + previewData.get(position));
        layout.addView(previewText, params2);
        //设置按钮
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView previewSettingImage = new ImageView(context);
        previewSettingImage.setImageResource(R.drawable.see_launcher);
        params3.addRule(RelativeLayout.CENTER_VERTICAL);
        params3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params3.setMargins(0, 0, context.getResources().getDimensionPixelOffset(R.dimen.item_preview_margin), 0);
        layout.addView(previewSettingImage, params3);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPreViewItemClickListener != null) {
                    onPreViewItemClickListener.onPreviewItemClick(view, position);
                }
            }
        });

        previewSettingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPreviewSettingClickListener != null) {
                    onPreviewSettingClickListener.OnPreviewSettingClick(view, position);
                }
            }
        });
        return layout;
    }

    //底部横线view
    private View getLineView(Context context) {
        View line = new View(context);
        line.setBackgroundColor(Color.BLACK);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        params.setMargins(context.getResources().getDimensionPixelOffset(R.dimen.item_preview_margin), 3, context.getResources().getDimensionPixelOffset(R.dimen.item_preview_margin), 0);
        line.setLayoutParams(params);
        return line;
    }

    //listView点击事件
    public interface OnPreViewItemClickListener {
        public void onPreviewItemClick(View view, int position);
    }

    //listView预置位设置按钮点击事件
    public interface OnPreviewSettingClickListener {
        public void OnPreviewSettingClick(View view, int position);
    }

}
