package net.kaicong.ipcam.view;

import net.kaicong.ipcam.R;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by LingYan on 2014/11/20 0020.
 */
public class UploadPhotoSelectionDialog extends Dialog {

    private LinearLayout takePhoto;
    private LinearLayout selectPhoto;
    private OnSelectionSelectListener onSelectionSelectListener;

    public UploadPhotoSelectionDialog(Context context, int style, OnSelectionSelectListener onSelectionSelectListener) {
        super(context, style);
        init(context);
        this.onSelectionSelectListener = onSelectionSelectListener;
    }

    private void init(Context context) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_upload_photo_selection, null);
        setContentView(contentView);

        takePhoto = (LinearLayout) contentView.findViewById(R.id.take_photo_linear);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onSelectionSelectListener != null) {
                    onSelectionSelectListener.onSelectionSelect(1);
                }
                dismiss();
            }
        });
        selectPhoto = (LinearLayout) contentView.findViewById(R.id.select_photo_linear);
        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onSelectionSelectListener != null) {
                    onSelectionSelectListener.onSelectionSelect(2);
                }
                dismiss();
            }
        });
    }

    public interface OnSelectionSelectListener {

        public void onSelectionSelect(int position);

    }

}
