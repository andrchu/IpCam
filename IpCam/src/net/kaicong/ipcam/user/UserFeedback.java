package net.kaicong.ipcam.user;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LingYan on 15/2/27.
 */
public class UserFeedback implements Serializable {

    public int id;
    public String appVersionNum;
    public String mobilePhoneSystem;
    public String mobilePhoneType;
    public String content;
    public boolean IsCompleted;
    public boolean IsReply;
    public int FeedbackType;
    public String createTime;
    public List<UserFeedback> data;

    public static UserFeedback getFeedback(JSONArray jsonArray) {
        UserFeedback userFeedback = new UserFeedback();
        userFeedback.data = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                UserFeedback jsonFeedback = new UserFeedback();
                jsonFeedback.id = jsonArray.getJSONObject(i).optInt("Id");
                jsonFeedback.FeedbackType = jsonArray.getJSONObject(i).optInt("feedbacktype");
                jsonFeedback.appVersionNum = jsonArray.getJSONObject(i).optString("VersionNum");
                jsonFeedback.content = jsonArray.getJSONObject(i).optString("Content");
                jsonFeedback.mobilePhoneSystem = jsonArray.getJSONObject(i).optString("MobilePhoneSystem");
                jsonFeedback.mobilePhoneType = jsonArray.getJSONObject(i).optString("MobilePhoneType");
                jsonFeedback.IsCompleted = jsonArray.getJSONObject(i).optBoolean("IsCompleted");
                jsonFeedback.IsReply = jsonArray.getJSONObject(i).optBoolean("IsReply");
                jsonFeedback.createTime = jsonArray.getJSONObject(i).optString("CreatedTime");
                userFeedback.data.add(jsonFeedback);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userFeedback;
    }

}
