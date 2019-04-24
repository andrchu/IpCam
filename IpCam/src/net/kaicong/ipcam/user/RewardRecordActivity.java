package net.kaicong.ipcam.user;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.R;
import net.kaicong.ipcam.adpater.RewardRecordAdapter;
import net.kaicong.ipcam.api.VolleyResponse;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.RewardRecord;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.utility.ApiClientUtility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by LingYan on 15/6/25.
 */
public class RewardRecordActivity extends BaseActivity {

    private RadioButton rewardToOther;
    private RadioButton rewardToMe;
    private SegmentedGroup segmentedGroup;

    private List<RewardRecord> toOtherData = new ArrayList<>();
    private List<RewardRecord> toMeData = new ArrayList<>();
    private RewardRecordAdapter adapter;
    private ListView listView;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_record);
        initTitle(getString(R.string.reward_info));
        showBackButton();
        getData(1);

        segmentedGroup = (SegmentedGroup) findViewById(R.id.segment_group_resolution);
        rewardToOther = (RadioButton) findViewById(R.id.radio_btn1);
        rewardToMe = (RadioButton) findViewById(R.id.radio_btn2);
        rewardToMe.setChecked(true);
//        segmentedGroup.setTintColor(Color.parseColor("#d2d5d2"));
        segmentedGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if (i == R.id.radio_btn1) {
                    rewardToOther.setChecked(true);
                    if (toOtherData.size() == 0) {
                        emptyView.setVisibility(View.VISIBLE);
                        adapter.setData(toOtherData);
                        adapter.notifyDataSetChanged();
                        return;
                    }
                    emptyView.setVisibility(View.GONE);
                    adapter.setData(toOtherData);
                    adapter.notifyDataSetChanged();
                } else if (i == R.id.radio_btn2) {
                    rewardToMe.setChecked(true);
                    if (toMeData.size() == 0) {
                        emptyView.setVisibility(View.VISIBLE);
                        adapter.setData(toMeData);
                        adapter.notifyDataSetChanged();
                        return;
                    }
                    emptyView.setVisibility(View.GONE);
                    adapter.setData(toMeData);
                    adapter.notifyDataSetChanged();
                }

            }

        });
        adapter = new RewardRecordAdapter();
        adapter.setData(toOtherData);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);
        emptyView = (TextView) findViewById(R.id.empty_view);
    }

    private void getData(int flag) {
        Map<String, String> map = new HashMap<>();
        map.put(CameraConstants.USER_ID, String.valueOf(UserAccount.getUserID()));
        if (flag == 0) {
            //我打赏别人
            doPost(UrlResources.URL_REWARD_TO_OTHER, ApiClientUtility.getParams(map), new VolleyResponse(this, true, getString(R.string.activity_base_progress_dialog_content)) {

                @Override
                protected void onTaskSuccess(JSONArray result) {
                    super.onTaskSuccess(result);
                    toOtherData.addAll(RewardRecord.getRewardRecord(result).data);
                }

            });
        } else if (flag == 1) {
            //别人打赏我
            doPost(UrlResources.URL_REWARD_TO_ME, ApiClientUtility.getParams(map), new VolleyResponse(this, true, getString(R.string.activity_base_progress_dialog_content)) {

                @Override
                protected void onTaskSuccess(JSONArray result) {
                    super.onTaskSuccess(result);
                    getData(0);
                    toMeData.addAll(RewardRecord.getRewardRecord(result).data);
                    if (toMeData.size() == 0) {
                        emptyView.setVisibility(View.VISIBLE);
                        return;
                    }
                    emptyView.setVisibility(View.GONE);
                    adapter.setData(toMeData);
                    adapter.notifyDataSetChanged();
                }

            });
        }
    }

}
