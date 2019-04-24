package net.kaicong.ipcam.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LingYan on 15/6/26.
 */
public class RewardRecord {

    public String headPath;
    public int deviceId;
    public String rewardTime;
    public String username;
    public int userId;
    public double rewardFee;
    public List<RewardRecord> data;
    public List<Double> feeList;

    public static RewardRecord getRewardRecord(JSONArray array) {
        RewardRecord result = new RewardRecord();
        result.data = new ArrayList<>();
        result.feeList = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                RewardRecord rewardRecord = new RewardRecord();
                rewardRecord.headPath = jsonObject.optString("reviewerIcon");
                rewardRecord.deviceId = jsonObject.optInt("device_id");
                rewardRecord.rewardTime = jsonObject.optString("showtime");
                rewardRecord.username = jsonObject.optString("username");
                rewardRecord.userId = jsonObject.optInt("user_id");
                rewardRecord.rewardFee = jsonObject.optDouble("rewardfee");
                result.feeList.add(rewardRecord.rewardFee);
                result.data.add(rewardRecord);
            }
        } catch (Exception e) {
        }
        return result;
    }


}
