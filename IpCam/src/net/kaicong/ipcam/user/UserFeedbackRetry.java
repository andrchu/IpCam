package net.kaicong.ipcam.user;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LingYan on 15/2/27.
 */
public class UserFeedbackRetry {

    public boolean IsCustomer;
    public int id;
    public String HeadPortrait;
    public String Content;
    public int Feedback_Id;

    public List<UserFeedbackRetry> data;

    public static UserFeedbackRetry getUserFeedbackRetry(JSONArray jsonArray) {
        UserFeedbackRetry userFeedbackRetry = new UserFeedbackRetry();
        try {
            userFeedbackRetry.data = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                UserFeedbackRetry jsonRetry = new UserFeedbackRetry();
                jsonRetry.id = jsonArray.getJSONObject(i).optInt("Id");
                jsonRetry.Content = jsonArray.getJSONObject(i).optString("Content");
                jsonRetry.Feedback_Id = jsonArray.getJSONObject(i).optInt("Feedback_Id");
                jsonRetry.HeadPortrait = jsonArray.getJSONObject(i).optString("HeadPortrait");//头像
                jsonRetry.IsCustomer = jsonArray.getJSONObject(i).optBoolean("IsCustomer");//boolean顾客
                userFeedbackRetry.data.add(jsonRetry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userFeedbackRetry;
    }

}
