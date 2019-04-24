package net.kaicong.ipcam.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: lu_qwen
 * Intro:
 * Time: 2015/7/23.
 */
public class Mess_ComRecord {
    public String id;//评论ID
    public String deviceId;//设备ID
    public String title;//设备名称
    public String username;//评论者名字
    public String headUrl;//头像地址
    public String time;//评论时间
    public String connent;//评论内容
    public String picUrl_s;//评论图片地址_xiaotu
    public String picUrl_b;//评论图片地址_xiaotu

    public List<String> imglist;//图片地址集合
    public List<String> conlist;//评论内容集合

    public List<Mess_ComRecord> data;

    public static Mess_ComRecord getData(JSONArray array) {
        Mess_ComRecord records = new Mess_ComRecord();
        records.data = new ArrayList<>();
        records.imglist = new ArrayList<>();
        records.conlist = new ArrayList<>();

        try {
            for (int i = 0; i < array.length(); i++) {
                Mess_ComRecord record = new Mess_ComRecord();
                JSONObject item = array.getJSONObject(i);
                record.id = item.optString("id");
                record.title = item.optString("share_title");
                record.username = item.optString("username");
                record.headUrl = item.optString("reviewerIcon");
                record.time = item.optString("showtime");
                record.connent = item.optString("content");
                record.picUrl_s = item.optString("smallimage");
                record.picUrl_b = item.optString("image");
                record.deviceId = item.optString("device_id");
                if (record.picUrl_s.length() >= 1) {
                    records.imglist.add(record.picUrl_b);
                    records.conlist.add(record.connent);
                }
                records.data.add(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return records;
    }

}
