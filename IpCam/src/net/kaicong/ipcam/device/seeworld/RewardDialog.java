package net.kaicong.ipcam.device.seeworld;

import net.kaicong.ipcam.R;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


/**
 * Author: lu_qwen
 * Intro:
 * Time: 2015/6/15.
 */
public class RewardDialog extends Dialog {
    private Context context;
    private onRewardClickListener listener;
    private EditText edt_num;
    private ImageView img_random; //骰子
    private Button btn_sure;//提交打赏

    public RewardDialog(Context context, int style, onRewardClickListener listener) {
        super(context, style);
        this.context = context;
        this.listener = listener;

        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_reward, null);
        setContentView(view);

        edt_num = (EditText) findViewById(R.id.edt_reward_num);
        edt_num.requestFocus();
        img_random = (ImageView) findViewById(R.id.img_reward_random);
        btn_sure = (Button) findViewById(R.id.btn_sureReward);

        img_random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onRandomSalary();
                }
            }
        });
        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && edt_num != null && edt_num.getText().length() > 0) {
                    listener.onSureReward(edt_num.getText().toString());
                }
            }
        });
        edt_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf('.') > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        edt_num.setText(s);
                        edt_num.setSelection(s.length());
                    }
                    if (s.toString().trim().substring(0).equals(".")) {
                        s = "0" + s;
                        edt_num.setText(s);
                        edt_num.setSelection(2);
                    }

                    if (s.toString().startsWith("0")
                            && s.toString().trim().length() > 1) {
                        if (!s.toString().substring(1, 2).equals(".")) {
                            edt_num.setText(s.subSequence(0, 1));
                            edt_num.setSelection(1);
                            return;
                        }
                    }
                    //限制
                    if (s.toString().indexOf('.') > 4) {
                        if (s.length() - 1 - s.toString().indexOf('.') == 2) {
                            s = s.toString().subSequence(0, 4) + "" + s.toString().subSequence(s.toString().indexOf("."), s.toString().indexOf(".") + 3);
                            edt_num.setText(s);
                            edt_num.setSelection(s.length());
                        }
                        if (s.length() - 1 - s.toString().indexOf('.') == 1) {
                            s = s.toString().subSequence(0, 4) + "" + s.toString().subSequence(s.toString().indexOf("."), s.toString().indexOf(".") + 2);
                            edt_num.setText(s);
                            edt_num.setSelection(s.length());
                        }
                        if (s.length() - 1 - s.toString().indexOf('.') == 0) {
                            s = s.toString().subSequence(0, 4) + "" + s.toString().subSequence(s.toString().indexOf("."), s.toString().indexOf("."));
                            edt_num.setText(s);
                            edt_num.setSelection(s.length());
                        }
                    }

                } else {
                    if (s.length() > 4) {
                        s = s.toString().subSequence(0,
                                4);
                        edt_num.setText(s);
                        edt_num.setSelection(s.length());
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 点击事件接口
     */
    public interface onRewardClickListener {
        //随机 1-10元以内的数字
        public void onRandomSalary();

        //确认打赏
        public void onSureReward(String money);
    }


    public void setRewardNum(String str) {
        edt_num.setText(str);
        edt_num.setSelection(str.length());//光标
    }

    public void clearCont() {
        edt_num.setText("");
    }
}
