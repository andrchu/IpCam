package net.kaicong.ipcam.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import net.kaicong.ipcam.R;
import net.kaicong.ipcam.utils.LogUtil;

/**
 * Created by LingYan on 2014/11/20 0020.
 */
public class CommentReplyDialog extends Dialog {

    private LinearLayout takePhoto;
    private LinearLayout selectPhoto;
    private int listPosition = 0;
    private OnSelectionSelectListener onSelectionSelectListener;

    public CommentReplyDialog(Context context, int style, OnSelectionSelectListener onSelectionSelectListener) {
        super(context, style);
        init(context);
        this.onSelectionSelectListener = onSelectionSelectListener;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }

    private void init(Context context) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_comment_reply, null);
        setContentView(contentView);

        takePhoto = (LinearLayout) contentView.findViewById(R.id.take_photo_linear);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onSelectionSelectListener != null) {
                    onSelectionSelectListener.onSelectionSelect(listPosition, 1);
                }
                dismiss();
            }
        });
        final View rootView = contentView;
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(

                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect r = new Rect();
                        rootView.getWindowVisibleDisplayFrame(r);
                        int screenHeight = rootView.getRootView()
                                .getHeight();
                        int heightDifference = screenHeight - (r.bottom);
                        if (heightDifference > 100) {
                            LogUtil.d("chu", "--dialog height--" + heightDifference);
                        } else if (heightDifference == 0) {

                        }
                    }
                });
        selectPhoto = (LinearLayout) contentView.findViewById(R.id.select_photo_linear);
        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (onSelectionSelectListener != null) {
                    onSelectionSelectListener.onSelectionSelect(listPosition, 2);
                }
            }
        });
    }

    public interface OnSelectionSelectListener {

        public void onSelectionSelect(int listPosition, int position);

    }

}
