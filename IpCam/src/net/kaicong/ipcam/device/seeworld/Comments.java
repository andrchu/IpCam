package net.kaicong.ipcam.device.seeworld;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chu on 14-12-10.
 */
public class Comments {

    public String content;//评论内容
    public int id;//评论id
    public String userName;//用户名
    public String showTime;//时间(*天前)
    public String createTime;//时间(具体)
    public String reviewerHeadUrl;//用户头像url
    public String prevUrl;//大图路径
    public String prevUrl_s;//小图路径
    public List<Comments> data;
    public List<String> list;//图片地址集合
    public List<String> con_list;//评论内容集合

    public static Comments getAllComments(JSONArray jsonArray) {
        Comments comments = new Comments();
        comments.data = new ArrayList<>();
        comments.list = new ArrayList<>();
        comments.con_list = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                Comments result = new Comments();
                JSONObject resultObj = jsonArray.getJSONObject(i);
                result.content = resultObj.optString("content");
                result.id = resultObj.optInt("id");
                result.userName = resultObj.optString("username");
                result.showTime = resultObj.optString("showtime");
                result.createTime = resultObj.optString("created_time");
                result.reviewerHeadUrl = resultObj.optString("reviewerIcon");
                result.prevUrl = resultObj.optString("image");
                if (result.prevUrl.length() >= 1) {
                    comments.list.add(result.prevUrl);
                    comments.con_list.add(result.content);
                }
                result.prevUrl_s = resultObj.optString("smallimage");
                comments.data.add(result);
            }
        } catch (Exception e) {

        }
        return comments;
    }

}
