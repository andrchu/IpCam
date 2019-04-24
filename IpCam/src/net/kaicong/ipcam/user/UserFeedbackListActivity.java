package net.kaicong.ipcam.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.R;
import net.kaicong.ipcam.adpater.TalkListAdapter;
import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.utils.StringUtils;
import net.kaicong.utility.ApiClientUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LingYan on 15/2/25.
 */
public class UserFeedbackListActivity extends BaseActivity {

    private int listIndex = 0;//为0默认是新建反馈页，为1表示是反馈聊天页
    private int selectedFeedbackType = -1;

    private ImageView postComment;
    private ListView listView;
    private EditText editText;
    private Button feedbackPicker;
    private String[] feedbackTypes;
    private NumberPicker aNumberPicker;
    private AlertDialog.Builder alertBw;
    private AlertDialog alertDw;
    private UserFeedback mUserFeedback;

    private TalkListAdapter talkListAdapter;
    private List<UserFeedbackRetry> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_feedback_list);

        feedbackTypes = getResources().getStringArray(R.array.feedback_types);
        listIndex = getIntent().getIntExtra("listIndex", 0);

        postComment = (ImageView) findViewById(R.id.post_comment);
        editText = (EditText) findViewById(R.id.common_edittext);
        feedbackPicker = (Button) findViewById(R.id.feedback_pick);
        feedbackPicker.setOnClickListener(new View.OnClickListener() {

                                              @Override
                                              public void onClick(View view) {
                                                  alertDw.show();
                                              }

                                          }

        );
        postComment.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               if (selectedFeedbackType < 0 && listIndex == 0) {
                                                   //请选择反馈类型
                                                   makeToast(getString(R.string.about_more_select_feedback));
                                                   return;
                                               }
                                               if (StringUtils.isEmpty(editText.getText().toString())) {
                                                   makeToast(getString(R.string.common_input_not_empty));
                                                   return;
                                               }
                                               if (editText.getText().toString().length() > 200) {
                                                   makeToast(getString(R.string.about_more_feedback_max_length));
                                                   return;
                                               }
                                               postFeedback(editText.getText().toString());
                                           }

                                       }

        );

        if (listIndex == 0) {//新建反馈页面
            initTitle(getString(R.string.about_more_new_feedback));
        } else if (listIndex == 1) {//反馈列表页面
            feedbackPicker.setVisibility(View.GONE);
            mUserFeedback = (UserFeedback) getIntent().getSerializableExtra("user_feedback");
            // LogUtil.e("获取的数据", String.valueOf(mUserFeedback));
            initTitle(getString(R.string.about_more_my_feedback_talk));
            talkListAdapter = new TalkListAdapter(this);
            talkListAdapter.setData(data);
            listView = (ListView) findViewById(R.id.list);
            listView.setAdapter(talkListAdapter);
            getRetryContent();//获取反馈内容
        }
        showBackButton();
        setAlert();
    }

    public void setAlert() {
        RelativeLayout linearLayout = new RelativeLayout(UserFeedbackListActivity.this);
        aNumberPicker = new NumberPicker(UserFeedbackListActivity.this);
        aNumberPicker.setMaxValue(feedbackTypes.length);
        aNumberPicker.setMinValue(1);
        aNumberPicker.setDisplayedValues(feedbackTypes);
        aNumberPicker.setWrapSelectorWheel(false);
        aNumberPicker.setClickable(false);
        aNumberPicker.setEnabled(true);
        aNumberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
        RelativeLayout.LayoutParams numPickerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        numPickerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        linearLayout.setLayoutParams(params);
        linearLayout.addView(aNumberPicker, numPickerParams);
        linearLayout.isClickable();

        aNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                selectedFeedbackType = newVal;
            }

        });

        alertBw = new AlertDialog.Builder(this);
        alertBw.setTitle(getString(R.string.about_more_feedback_type));
        alertBw.setView(linearLayout);
        alertBw.setCancelable(false);
        alertBw.setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                if (selectedFeedbackType < 0) {
                    selectedFeedbackType = 0;
                }
                feedbackPicker.setText(feedbackTypes[selectedFeedbackType - 1 < 0 ? 0 : selectedFeedbackType - 1]);
            }
        });
        alertBw.setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDw = alertBw.create();

    }

    /**
     * 获取反馈的回复
     */
    public void getRetryContent() {
        Map<String, String> map = new HashMap<>();
        map.put(CameraConstants.USER_ID, UserAccount.getUserID() + "");
        map.put("feedback_id", mUserFeedback.id + "");
        Log.e("feedback_id", mUserFeedback.id + "");
        Log.e("USER_id", UserAccount.getUserID() + "");
        doPost(UrlResources.URL_GET_FEEDBACK_RETRY, ApiClientUtility.getParams(map), new VolleyResponse(this, true, getString(R.string.com_facebook_loading)) {


            @Override
            protected void onTaskSuccess(JSONArray result) {
                super.onTaskSuccess(result);
                editText.setText("");
                data.clear();
                data.add(createOne());
                data.addAll(UserFeedbackRetry.getUserFeedbackRetry(result).data);
//                if (result.length() <= 0) {
//                    data.add(createOne());
//                }
                talkListAdapter.setData(data);
                talkListAdapter.notifyDataSetChanged();
            }

        });
    }

    public void postFeedback(String content) {
        if (listIndex == 0) {
            //新建反馈
            Map<String, String> map = new HashMap<>();
            map.put(CameraConstants.USER_ID, UserAccount.getUserID() + "");
            map.put("apptype", "看看看");
            map.put("content", content);//反馈内容
            map.put("mobilephone_system", "Android" + Build.VERSION.RELEASE);//当前系统版本
            map.put("mobilephone_type", Build.MODEL);//手机型号
            map.put("feedback_type", String.valueOf(selectedFeedbackType));//反馈类型
            map.put("versionnum", getAppVersion());//app当前版本
            doPost(UrlResources.URL_CREATE_FEEDBACK, ApiClientUtility.getParams(map), new VolleyResponse(this, true, getString(R.string.com_facebook_loading)) {

                @Override
                protected void onTaskSuccessRoot(JSONObject jsonObject) {
                    super.onTaskSuccessRoot(jsonObject);
                    //提交成功
                    makeToast(getString(R.string.about_more_feedback_success));
                    setResult(RESULT_OK);
                    finish();
                }


            });
        } else if (listIndex == 1) {
            //回复反馈
            Map<String, String> map = new HashMap<>();
            map.put(CameraConstants.USER_ID, UserAccount.getUserID() + "");
            map.put("feedback_id", mUserFeedback.id + "");
            map.put("content", content);
            doPost(UrlResources.URL_REPLY_FEEDBACK, ApiClientUtility.getParams(map), new VolleyResponse(this, true, getString(R.string.com_facebook_loading)) {

                @Override
                protected void onTaskSuccessRoot(JSONObject result) {
                    super.onTaskSuccessRoot(result);
                    //刷新数据
                    getRetryContent();
                }

            });
        }
    }

    private String getAppVersion() {
        // 获取packagemanager的实例
        try {
            PackageManager packageManager = getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            String version = packInfo.versionName;
            return version;
        } catch (Exception e) {

        }
        return null;
    }

    public UserFeedbackRetry createOne() {
        UserFeedbackRetry retry = new UserFeedbackRetry();
        retry.id = 2;
        retry.Content = getIntent().getStringExtra("content");
        retry.HeadPortrait = "";
        retry.Feedback_Id = 2;
        retry.IsCustomer = true;
        return retry;
    }

}
