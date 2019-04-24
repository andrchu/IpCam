package net.kaicong.ipcam.adpater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import net.kaicong.ipcam.R;

/**
 * Created by LingYan on 2014/11/14 0014.
 */
public class DrawerLayoutAdapter extends BaseAdapter {

    private List<HashMap<String, Object>> data;

    public void setData(List<HashMap<String, Object>> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_drawer_layout, viewGroup, false);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.imageview);
            viewHolder.textView = (TextView) view.findViewById(R.id.drawer_text);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) view.getTag();
        viewHolder.imageView.setImageResource((int) data.get(i).get("img_res"));
        viewHolder.textView.setText((String) data.get(i).get("txt_res"));
        return view;
    }

    private class ViewHolder {
        public ImageView imageView;
        public TextView textView;
    }

}
