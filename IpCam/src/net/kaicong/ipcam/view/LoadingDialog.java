package net.kaicong.ipcam.view;

import net.kaicong.ipcam.R;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by LingYan on 2014/9/1.
 */
public class LoadingDialog extends Dialog {

    private TextView loadingText;

    public LoadingDialog(Context context, int style, String text) {
        super(context, style);
        init(context, text, R.drawable.tag_radius_loading_diolog);
    }

    public LoadingDialog(Context context, int style, String text, int bgColor) {
        super(context, style);
        init(context, text, bgColor);
    }

    private void init(Context context, String text, int color) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_loading_dialog, null);
        loadingText = (TextView) contentView.findViewById(R.id.text_loading);
        loadingText.setText(text);
        LinearLayout bgLinearLayout = (LinearLayout) contentView.findViewById(R.id.loading_bg_color);
        bgLinearLayout.setBackgroundResource(color);
        setContentView(contentView);
        setCanceledOnTouchOutside(false);
    }

    public void setLoadingText(String text) {
        if (loadingText != null) {
            loadingText.setText(text);
        }
    }


}
