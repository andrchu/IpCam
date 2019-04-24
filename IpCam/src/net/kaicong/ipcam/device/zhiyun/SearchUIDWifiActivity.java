package net.kaicong.ipcam.device.zhiyun;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.R;

import com.tutk.IOTC.Camera;
import com.tutk.IOTC.st_LanSearchInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LingYan on 2014/9/23 0023.
 */
public class SearchUIDWifiActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    public static final String INTENT_RESULT_DATA = "INTENT_RESULT_DATA";

    private ListView listView = null;
    private TextView resultTextView;
    private ArrayList<String> uidList = null;
    private List<SearchResult> searchData = new ArrayList<SearchResult>();
    private SearchResultAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle(getString(R.string.activity_search_uid_wifi_title));
        showBackButton();
        showRightButton(getString(R.string.activity_search_camera_right_btn_text));
        setContentView(R.layout.activity_search_uid_wifi);
        uidList = getIntent().getStringArrayListExtra("uidList");
        listView = (ListView) findViewById(R.id.list);
        resultTextView = (TextView) findViewById(R.id.search_result_text);
        listView.setOnItemClickListener(this);
        adapter = new SearchResultAdapter();
        adapter.setData(searchData);
        listView.setAdapter(adapter);
        SearchDeviceTask task = new SearchDeviceTask();
        task.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SearchResult result = searchData.get(position);
        if (result.isExists()) {
            return;
        }
        Intent data = new Intent();
        data.putExtra("dev_uid", result.getUID());
        this.setResult(RESULT_OK, data);
        this.finish();
    }

    private class SearchDeviceTask extends AsyncTask<Void, Void, st_LanSearchInfo[]> {

        @Override
        protected void onPostExecute(st_LanSearchInfo[] arrResp) {
//            LogUtil.d("chu", "result---" + arrResp.toString());
            if (arrResp != null && arrResp.length > 0) {
                for (st_LanSearchInfo resp : arrResp) {
                    SearchResult result = new SearchResult();
                    result.setUID(new String(resp.UID).trim());
                    result.setIP(new String(resp.IP).trim());
                    if (uidList != null) {
                        for (String uid : uidList) {
                            if (uid.equals(new String(resp.UID).trim())) {
                                result.setExists(true);
                                break;
                            }
                        }
                    }
                    searchData.add(result);
                }
                resultTextView.setText(getString(R.string.add_device_search_result, searchData.size()));
                adapter.setData(searchData);
                adapter.notifyDataSetChanged();
            } else {
                makeToast(getString(R.string.search_uid_no_devices));
            }
            removeProgressDialog();
            super.onPostExecute(arrResp);
        }

        @Override
        protected st_LanSearchInfo[] doInBackground(Void... voids) {
            //执行搜索方法
            st_LanSearchInfo[] arrResp = Camera.SearchLAN();
            return arrResp;
        }

        @Override
        protected void onPreExecute() {
            searchData.clear();
            showProgressDialog();
            super.onPreExecute();
        }
    }

    @Override
    public void doRightButtonAction(View view) {
        super.doRightButtonAction(view);
        SearchDeviceTask task = new SearchDeviceTask();
        task.execute();
    }

    private class SearchResultAdapter extends BaseAdapter {

        private List<SearchResult> data = null;

        public void setData(List<SearchResult> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_uid_wifi, parent, false);
                viewHolder.textViewUID = (TextView) convertView.findViewById(R.id.item_uid);
                viewHolder.textViewIP = (TextView) convertView.findViewById(R.id.item_ip);
                viewHolder.linearLayout = (LinearLayout) convertView.findViewById(R.id.search_background);
                convertView.setTag(viewHolder);
            }
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.textViewUID.setText(data.get(position).getUID());
            viewHolder.textViewIP.setText(data.get(position).getIP());
            viewHolder.textViewUID.setTextColor(parent.getContext().getResources().getColor(data.get(position).isExists() ? R.color.me_item_not_clickable : R.color.black));
            viewHolder.textViewIP.setTextColor(parent.getContext().getResources().getColor(data.get(position).isExists() ? R.color.me_item_not_clickable : R.color.black));
            return convertView;
        }

        @Override
        public boolean isEnabled(int position) {
            if (data.get(position).isExists()) {
                return false;
            }
            return super.isEnabled(position);
        }

        private class ViewHolder {
            public LinearLayout linearLayout;
            public TextView textViewUID;
            public TextView textViewIP;
        }

    }

}
