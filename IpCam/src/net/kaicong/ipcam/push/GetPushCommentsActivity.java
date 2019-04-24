package net.kaicong.ipcam.push;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.KCApplication;
import net.kaicong.ipcam.R;
import net.kaicong.ipcam.WelcomeActivity;
import net.kaicong.ipcam.adpater.CommentsAdapter;
import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.device.seeworld.Comments;
import net.kaicong.ipcam.device.seeworld.Summary;
import net.kaicong.ipcam.user.UserAccount;
import net.kaicong.ipcam.utils.LocationUtil;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.ipcam.view.PicCommentDialog;
import net.kaicong.utility.ApiClientUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by LingYan on 15/5/29.
 */
public class GetPushCommentsActivity extends BaseActivity implements CommentsAdapter.onMyClick,
        AdapterView.OnItemClickListener,
        PicCommentDialog.OnPicCommitListener {

    private ListView listView;
    private LinearLayout rootLayout;

    private int deviceId;
    private List<Comments> data = new ArrayList<>();
    private CommentsAdapter mAdapter;
    // 图片地址集合
    private ArrayList<String> imageList = new ArrayList<>();
    private ArrayList<String> contentLlist = new ArrayList<>();

    //摘要信息
    private ImageView imageView;
    private TextView title;
    private TextView user;
    private TextView praise;
    private TextView comment;
    private TextView popularity;
    private TextView date;
    private Summary summary = new Summary();

    private PicCommentDialog picCommentDialog;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initTitle(getString(R.string.title_push_commnet));
        showBackButton();

        setContentView(R.layout.activity_get_push_comments);

        rootLayout = (LinearLayout) findViewById(R.id.root);
        imageView = (ImageView) findViewById(R.id.image);
        title = (TextView) findViewById(R.id.title);
        user = (TextView) findViewById(R.id.user);
        praise = (TextView) findViewById(R.id.item_praise);
        comment = (TextView) findViewById(R.id.item_comment);
        popularity = (TextView) findViewById(R.id.item_popularity);
        date = (TextView) findViewById(R.id.item_share_date);

        listView = (ListView) findViewById(R.id.list);
        mAdapter = new CommentsAdapter(this, this);
        mAdapter.setData(data);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);

        Intent intent = getIntent();
        if (null != intent) {
            Bundle bundle = getIntent().getExtras();
            deviceId = bundle.getInt("deviceId");
            getSummaryData();
        }

        checkKeyboardHeight(rootLayout);

    }

    @Override
    public void doBackButtonAction() {
        //启动welcome activity
        Intent intent = new Intent();
        intent.setClass(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    protected void getSummaryData() {
        Map<String, String> map = new HashMap<>();
        map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
        map.put(CameraConstants.DEVICE_ID, String.valueOf(deviceId));
        doPost(UrlResources.URL_GET_PUBLIC_DEVICE_INFO_NEW, ApiClientUtility.getParams(map), new VolleyResponse(this, false, getString(R.string.com_facebook_loading)) {
            @Override
            public void onTaskSuccess(JSONObject result) {
                super.onTaskSuccess(result);
                getData();
                summary.userId = result.optInt("user_id");
                summary.shareTitle = result.optString("share_title");
                summary.shareUser = result.optString("username");
                summary.shareTime = result.optString("shared_time");
                summary.shareHotNum = result.optString("visit_count");
                summary.modelId = result.optInt("ddns_modelid");
                summary.praiseCount = result.optInt("praise_count");
                summary.reviewCount = result.optInt("review_count");
                title.setText(summary.shareTitle);
                user.setText(summary.shareUser);
                praise.setText(String.valueOf(summary.praiseCount));
                comment.setText(String.valueOf(summary.reviewCount));
                popularity.setText(summary.shareHotNum);
                date.setText(summary.shareTime);
                imageLoader.displayImage(result.optString("last_snapshot"), imageView);
            }
        });
    }

    private void getData() {
        Map<String, String> map = new HashMap<>();
        map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
        map.put(CameraConstants.DEVICE_ID, String.valueOf(deviceId));
        map.put(CameraConstants.PAGE_SIZE, CameraConstants.NORMAL_PAGE_SIZE);
        map.put(CameraConstants.PAGE_INDEX, String.valueOf(1));

        doPost(UrlResources.URL_GET_COMMENT_IMG, ApiClientUtility.getParams(map), new VolleyResponse(this, true, getString(R.string.com_facebook_loading)) {

                    @Override
                    protected void onTaskSuccess(JSONArray result) {
                        super.onTaskSuccess(result);
                        Comments comments = Comments.getAllComments(result);
                        data.clear();
                        data.addAll(comments.data);
                        // 图片地址集合
                        imageList.addAll(comments.list);
                        //图片内容集合
                        contentLlist.addAll(comments.con_list);

                        mAdapter.setData(data);
                        mAdapter.notifyDataSetChanged();
                    }

                }
        );
    }

    @Override
    public void clickImage(int i) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (picCommentDialog == null) {
            picCommentDialog = new PicCommentDialog(this, R.style.ZhiYunVideoSettingDialog, this, null);
        }
        picCommentDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        WindowManager.LayoutParams params = picCommentDialog.getWindow().getAttributes();
        params.width = KCApplication.getWindowWidth();
        params.height = getResources().getDimensionPixelSize(R.dimen.common_edittext_height);
        picCommentDialog.show();
        picCommentDialog.setPosition(i);
        picCommentDialog.setMode(PicCommentDialog.MODE_REPLY, data.get(i).userName);
        picCommentDialog.setCursorPosition(data.get(i).userName.length() + 4);
    }

    @Override
    public void onPicCommentCommit(int mode, String editStr, int position) {
        if (mode == PicCommentDialog.MODE_DISMISS) {
            dismissPicCommentDialog();
        } else if (mode == PicCommentDialog.MODE_COMMENT_TEXT) {
            postComment(editStr, 0);
        } else if (mode == PicCommentDialog.MODE_COMMENT_IMAGE) {
            postComment(editStr, 2);
        } else if (mode == PicCommentDialog.MODE_REPLY) {
            replyComments(data.get(position).id, editStr);
        }
    }

    /**
     * 评论回复
     */
    private void replyComments(int reviewId, String editStr) {
        Map<String, String> map = new HashMap<>();
        map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
        map.put("review_id", String.valueOf(reviewId));
        map.put(CameraConstants.CONTENT, editStr);
        map.put("Longitude", KCApplication.Longitude);
        map.put("Latitude", KCApplication.Latitude);
        map.put("Image", "");
        map = ApiClientUtility.getParams(map);
        //无图模式提交评论
        doPost(UrlResources.URL_REPLY_COMMENTS, map, new VolleyResponse(this, true, getString(R.string.activity_base_progress_dialog_content)) {
            @Override
            public void onTaskSuccessRoot(JSONObject obj) {
                getData();
                summary.reviewCount++;
                if (picCommentDialog != null) {
                    picCommentDialog.clearText();
                }
            }
        });
    }

    public void dismissPicCommentDialog() {
        if (picCommentDialog != null) {
            picCommentDialog.dismiss();
        }
    }

    /**
     * 评论
     */
    private void postComment(String editStr, int flag) {
        //提交评论
        Map<String, String> map = new HashMap<>();
        map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
        map.put(CameraConstants.DEVICE_ID, String.valueOf(deviceId));
        map.put(CameraConstants.CONTENT, editStr);
        map.put("Longitude", LocationUtil.getLatitude(this));
        map.put("Latitude", LocationUtil.getLongitude(this));
        map.put("Image", "");
        map = ApiClientUtility.getParams(map);
        //推送类型
        map.put("TerminalSystemType", "20");
        final Map<String, String> myMap = map;
        if (flag == 0) {
            //无图模式提交评论
            doPost(UrlResources.URL_COMMIT_COMMENT, myMap, new VolleyResponse(this, true, getString(R.string.activity_base_progress_dialog_content)) {
                @Override
                public void onTaskSuccessRoot(JSONObject obj) {
                    getData();
                    summary.reviewCount++;
                    if (picCommentDialog != null) {
                        picCommentDialog.clearText();
                    }
                }
            });
        }
    }

    private void checkKeyboardHeight(final View parentLayout) {
        parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(

                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect r = new Rect();
                        parentLayout.getWindowVisibleDisplayFrame(r);
                        int screenHeight = parentLayout.getRootView()
                                .getHeight();
                        int heightDifference = screenHeight - (r.bottom);
                        if (heightDifference > 100) {
                            showPicCommentDialog(heightDifference);
                        } else if (heightDifference == 0) {

                        }
                    }
                });
    }

    public void showPicCommentDialog(int heightDifference) {
        if (picCommentDialog != null) {
            WindowManager.LayoutParams params = picCommentDialog.getWindow().getAttributes();
            params.width = KCApplication.getWindowWidth();
            params.height = getResources().getDimensionPixelSize(R.dimen.common_edittext_height);
            int yPosition = KCApplication.getWindowHeight() - heightDifference - getResources().getDimensionPixelSize(R.dimen.common_edittext_height) / 2;
            params.y = yPosition / 2;
            picCommentDialog.getWindow().setAttributes(params);
            LogUtil.d("chu", "keyboard length=" + heightDifference);
            picCommentDialog.show();
        }
    }

}
