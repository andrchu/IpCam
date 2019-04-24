package net.kaicong.ipcam.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestParams;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.R;
import net.kaicong.ipcam.adpater.UserFeedbackAdapter;
import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.utility.ApiClientUtility;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LingYan on 15/2/25.
 */
public class UserFeedbackActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        PullToRefreshBase.OnRefreshListener2 {

    public static final int REQUEST_CODE_REFRESH = 999;
    private ListView listView;
    private PullToRefreshListView mPullRefreshListView;
    private UserFeedbackAdapter userFeedbackAdapter;
    private List<UserFeedback> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feedback);
        initTitle(getString(R.string.about_more_feedback));
        showBackButton();
        showRightButton(R.drawable.add_device_add);
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.list);
        mPullRefreshListView.setOnRefreshListener(this);
        listView = mPullRefreshListView.getRefreshableView();
        listView.setOnItemClickListener(this);
        userFeedbackAdapter = new UserFeedbackAdapter(this);
        userFeedbackAdapter.setData(data);
        listView.setAdapter(userFeedbackAdapter);
        getFeedback();
    }

    @Override
    public void doRightButtonAction(View view) {
        super.doRightButtonAction(view);
        Intent intent = new Intent();
        intent.setClass(this, UserFeedbackListActivity.class);
        startActivityForResult(intent, REQUEST_CODE_REFRESH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_REFRESH) {
            getFeedback();
        }
    }

    //获取反馈信息
    private void getFeedback() {
        Map<String, String> map = new HashMap<>();
        map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
        doPost(UrlResources.URL_GET_FEEDBACK, ApiClientUtility.getParams(map), new VolleyResponse(this, true, getString(R.string.com_facebook_loading)) {


            @Override
            protected void onTaskSuccess(JSONArray result) {
                super.onTaskSuccess(result);
                int size = result.length();
                if (size == 0) {
                    //暂无反馈
                } else if (size > 0) {
                    data.clear();
                    mPullRefreshListView.onRefreshComplete();
                    data.addAll(UserFeedback.getFeedback(result).data);
                    userFeedbackAdapter.setData(data);
                    userFeedbackAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(VolleyError error) {
                super.onError(error);
                mPullRefreshListView.onRefreshComplete();
                LogUtil.i("error", "feedback-error");
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent();
        intent.setClass(this, UserFeedbackListActivity.class);
        intent.putExtra("listIndex", 1);
        intent.putExtra("user_feedback", data.get(i - 1));
        intent.putExtra("content", data.get(i - 1).content);
        startActivity(intent);
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        getFeedback();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {

    }

}
