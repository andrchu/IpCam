package net.kaicong.ipcam.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: lu_qwen Intro: Time: 2015/7/21.
 */
public class MessRecord {

	public String id;
	public String time;
	public String type;
	public String from;
	public String imgUrl;

	public List<MessRecord> data;

	public List<String> urlList;

	public static MessRecord getData(JSONArray array) {
		MessRecord records = new MessRecord();
		records.data = new ArrayList<>();
		records.urlList = new ArrayList<>();
		try {
			for (int i = 0; i < array.length(); i++) {
				MessRecord record = new MessRecord();
				JSONObject item = array.getJSONObject(i);
				record.id = item.optString("msg_id");
				record.time = item.optString("msg_time");
				record.type = item.optString("msg_type");
				record.imgUrl = item.optString("snapshot_url");
				record.from = item.optString("dev_ddnsname");

				if (record.imgUrl != null) {
					records.urlList.add(record.imgUrl);
				}

				records.data.add(record);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return records;
	}
}
