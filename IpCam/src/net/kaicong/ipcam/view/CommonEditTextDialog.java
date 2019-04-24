package net.kaicong.ipcam.view;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.kaicong.ipcam.R;
import net.kaicong.ipcam.utils.StringUtils;

/**
 * Created by LingYan on 14-12-17.
 */
public class CommonEditTextDialog extends Dialog {

    public static final int MODE_COMMENT = 1;
    public static final int MODE_REPLY = 2;
    public static final int MODE_DISMISS = 3;

    private EditText commonEditText;
    private TextView commonHide;
    private TextView commonCommit;
    private OnCommitListener onCommitListener;
    private int mode;
    private String username;
    private Context context;

    public CommonEditTextDialog(Context context, int style, OnCommitListener onCommitListener) {
        super(context, style);
        this.onCommitListener = onCommitListener;
        this.context = context;
        init(context);
    }

    public void setMode(int mode, String username) {
        this.mode = mode;
        this.username = username;
        if (mode == MODE_REPLY) {
            commonEditText.setText(Html.fromHtml(context.getString(R.string.see_world_reply_notice, username)));
        }
    }

    private void init(final Context context) {
        View convertView = LayoutInflater.from(context).inflate(R.layout.dialog_common_edittext_dialog, null);
        setContentView(convertView);
        commonEditText = (EditText) convertView.findViewById(R.id.common_edittext);

        commonHide = (TextView) convertView.findViewById(R.id.common_hide);
        commonCommit = (TextView) convertView.findViewById(R.id.common_commit);
        commonCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.isEmpty(commonEditText.getText().toString())) {
                    Toast.makeText(context, context.getString(R.string.common_input_not_empty), Toast.LENGTH_LONG).show();
                    return;
                }
                if (onCommitListener != null) {
                    onCommitListener.onCommit(mode, commonEditText.getText().toString());
                }
                dismiss();
            }
        });
        commonHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onCommitListener != null) {
                    onCommitListener.onCommit(MODE_DISMISS, "");
                }
            }
        });
    }

    public interface OnCommitListener {
        public void onCommit(int mode, String editStr);
    }


    @Override
    public void dismiss() {
        super.dismiss();
        if (commonEditText != null) {
            commonEditText.setText("");
        }
    }
}
