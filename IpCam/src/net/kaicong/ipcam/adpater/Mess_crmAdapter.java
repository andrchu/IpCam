package net.kaicong.ipcam.adpater;

import java.util.List;

import net.kaicong.ipcam.R;
import net.kaicong.ipcam.bean.Mess_CrmRecord;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class Mess_crmAdapter extends BaseAdapter {

	private Context context;
	private List<Mess_CrmRecord> list;

	public Mess_crmAdapter(Context context) {
		this.context = context;
	}

	public void setData(List<Mess_CrmRecord> list) {
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int i) {
		return list.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_mess_crm, null);
			holder = new ViewHolder();
			holder.time = (TextView) convertView
					.findViewById(R.id.tev_crm_Time);
			holder.from = (TextView) convertView
					.findViewById(R.id.tev_crm_from);
			holder.content = (TextView) convertView
					.findViewById(R.id.tev_crm_content);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		holder.time.setText(list.get(position).createTime);
		if (list.get(position).from.toLowerCase().equals("system")) {
			holder.from.setText(context.getString(R.string.mess_crmMess) + ":");
		}
		holder.content.setText(list.get(position).content);

		return convertView;
	}

	private class ViewHolder {
		private TextView time;
		private TextView from;
		private TextView content;
	}
}
