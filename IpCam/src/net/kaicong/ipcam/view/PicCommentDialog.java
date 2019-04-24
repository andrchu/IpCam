package net.kaicong.ipcam.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.kaicong.ipcam.R;
import net.kaicong.ipcam.utils.StringUtils;

/**
 * Author: lu_qwen
 * Intro:
 * Time: 2015/3/24.
 */
public class PicCommentDialog extends Dialog {

    public static final int MAX_COUNT = 200;
    public static final int MODE_COMMENT = 1;//评论
    public static final int MODE_REPLY = 2;//回复
    public static final int MODE_DISMISS = 3;//取消
    public static final int MODE_COMMENT_TEXT = 4;//纯文字评论
    public static final int MODE_COMMENT_IMAGE = 5;//截图评论

    private EditText commonEditText;
    protected ImageView commentImage;
    private TextView commonHide;
    private TextView commonCommit;
    private OnPicCommitListener onPicCommitListener;
    private onPreviewListener onPreviewListener;
    private int mode;
    private String username;
    private Context context;
    private CheckBox checkBox;
    private TextView inputTextNum;
    //回复的字符长度
    private int re_Len = 0;
    private int position = 0;

    public PicCommentDialog(Context context, int style, OnPicCommitListener onCommitListener, onPreviewListener onPreviewListener) {
        super(context, style);
        this.onPicCommitListener = onCommitListener;
        this.onPreviewListener = onPreviewListener;
        this.context = context;

        init(context);
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setMode(int mode, String username) {
        this.mode = mode;
        this.username = username;
        if (mode == MODE_COMMENT_IMAGE) {
            commentImage.setVisibility(View.VISIBLE);
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setChecked(true);
        } else if (mode == MODE_COMMENT_TEXT) {
            commentImage.setVisibility(View.GONE);
            checkBox.setVisibility(View.INVISIBLE);
        } else if (mode == MODE_REPLY) {
            commentImage.setVisibility(View.GONE);
            checkBox.setVisibility(View.INVISIBLE);
            commonEditText.setText(Html.fromHtml(context.getString(R.string.see_world_reply_notice, username)));
        }
    }

    private void init(final Context context) {

        View convertView = LayoutInflater.from(context).inflate(R.layout.dialog_piccomment_edit_dialog, null);
        setContentView(convertView);
        commentImage = (ImageView) convertView.findViewById(R.id.picCommon_pic);
        commonEditText = (EditText) convertView.findViewById(R.id.picCommon_edittext);
        commonHide = (TextView) convertView.findViewById(R.id.picCommon_hide);
        commonCommit = (TextView) convertView.findViewById(R.id.picCommon_commit);
        checkBox = (CheckBox) convertView.findViewById(R.id.picCheckBox);
        inputTextNum = (TextView) convertView.findViewById(R.id.edit_text_input_num);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    commentImage.setVisibility(View.VISIBLE);
                    mode = MODE_COMMENT_IMAGE;
                } else {
                    commentImage.setVisibility(View.GONE);
                    mode = MODE_COMMENT_TEXT;
                }

            }

        });

        commentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (onPreviewListener != null) {
                    onPreviewListener.doPreview();
                }

            }
        });

        commonCommit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (StringUtils.isEmpty(commonEditText.getText().toString())) {
                    Toast.makeText(context, context.getString(R.string.common_input_not_empty), Toast.LENGTH_LONG).show();
                    return;
                } else if (mode == MODE_REPLY) {
                    if (commonEditText.getText().toString().length() <= re_Len) {
                        Toast.makeText(context, context.getString(R.string.common_input_not_empty), Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                if (onPicCommitListener != null) {
                    onPicCommitListener.onPicCommentCommit(mode, commonEditText.getText().toString(), position);
                }
                dismiss();
            }

        });

        commonHide.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (onPicCommitListener != null) {
                    onPicCommitListener.onPicCommentCommit(MODE_DISMISS, "", position);
                }
            }

        });

        commonEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int editStart = commonEditText.getSelectionStart();
                int editEnd = commonEditText.getSelectionEnd();

                // 先去掉监听器，否则会出现栈溢出
                commonEditText.removeTextChangedListener(this);

                // 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
                // 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
                while (calculateLength(editable.toString()) > MAX_COUNT) { // 当输入字符个数超过限制的大小时，进行截断操作
                    editable.delete(editStart - 1, editEnd);
                    editStart--;
                    editEnd--;
                }
                // mEditText.setText(s);将这行代码注释掉就不会出现后面所说的输入法在数字界面自动跳转回主界面的问题了，多谢@ainiyidiandian的提醒
                commonEditText.setSelection(editStart);

                // 恢复监听器
                commonEditText.addTextChangedListener(this);
                setLeftCount();
            }

        });

        commonEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (mode == MODE_REPLY && commonEditText.getText().toString().length() == re_Len) {
                        return true;
                    }
                }
                return false;
            }
        });

    }

    /**
     * 计算分享内容的字数，一个汉字=两个英文字母，一个中文标点=两个英文标点 注意：该函数的不适用于对单个字符进行计算，因为单个字符四舍五入后都是1
     *
     * @param c
     * @return
     */
    private long calculateLength(CharSequence c) {
        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            int tmp = (int) c.charAt(i);
            if (tmp > 0 && tmp < 127) {
                len += 0.5;
            } else {
                len++;
            }
        }
        return Math.round(len);
    }

    /**
     * 刷新剩余输入字数,最大值新浪微博是140个字，人人网是200个字
     */
    private void setLeftCount() {
        inputTextNum.setText(String.valueOf((MAX_COUNT - getInputCount())));
    }

    /**
     * 获取用户输入的分享内容字数
     *
     * @return
     */
    private long getInputCount() {
        return calculateLength(commonEditText.getText().toString());
    }

    public void setImage(Bitmap bitmap) {
        commentImage.setImageBitmap(bitmap);
    }

    public interface OnPicCommitListener {
        public void onPicCommentCommit(int mode, String editStr, int position);
    }

    public interface onPreviewListener {
        public void doPreview();
    }

    public void clearText() {
        if (commonEditText != null) {
            commonEditText.setText("");
        }
    }

    public void setCursorPosition(int index) {
        commonEditText.setSelection(index);
        re_Len = index;
    }

    @Override
    public void dismiss() {
        super.dismiss();
//        if (commonEditText != null) {
//            commonEditText.setText("");
//        }
    }

}
