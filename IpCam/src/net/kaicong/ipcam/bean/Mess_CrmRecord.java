package net.kaicong.ipcam.bean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Mess_CrmRecord {
	public String id;//消息ID
	public String from;//消息来源
	public String content;//消息内容
	public String createTime;//创建时间

	public List<Mess_CrmRecord> data;

	public static Mess_CrmRecord getData(JSONArray array) {
		Mess_CrmRecord records = new Mess_CrmRecord();
		records.data = new ArrayList<>();
		try {
			for (int i = 0; i < array.length(); i++) {
				Mess_CrmRecord record = new Mess_CrmRecord();
				JSONObject item = (JSONObject) array.get(i);
				record.id = item.optString("Id");
				record.from = item.optString("CreatedBy");
				record.content = item.optString("Content");
				record.createTime = item.optString("CreatedTime");
				
				records.data.add(record);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return records;
	}

}
