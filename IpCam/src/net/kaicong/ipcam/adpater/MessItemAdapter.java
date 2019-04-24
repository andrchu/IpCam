package net.kaicong.ipcam.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.kaicong.ipcam.R;
import net.kaicong.ipcam.bean.MessRecord;
import net.kaicong.ipcam.utils.ImageUtils;

import java.util.List;


/**
 * Author: lu_qwen
 * Intro:
 * Time: 2015/7/21.
 */
public class MessItemAdapter extends BaseAdapter {

    private Context context;

    private List<MessRecord> list;

    private ImageLoader loader = ImageLoader.getInstance();

    public MessItemAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<MessRecord> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_messlayout, null);
            viewHolder.time = (TextView) convertView.findViewById(R.id.tev_item_messTime);
            viewHolder.type = (TextView) convertView.findViewById(R.id.tev_item_messType);
            viewHolder.from = (TextView) convertView.findViewById(R.id.tev_item_messFrom);
            viewHolder.img = (ImageView) convertView.findViewById(R.id.img_item_messPic);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.time.setText(list.get(position).time);
        if (list.get(position).type == "1") {
            viewHolder.type.setText("Moving delection alarm");
        } else {
            viewHolder.type.setText("Moving delection alarm");
        }
        viewHolder.from.setText("from:(" + list.get(position).from + ")");

        loader.displayImage(list.get(position).imgUrl, viewHolder.img, ImageUtils.getDisplayOptions(R.drawable.common_no_image, null));

        return convertView;
    }

    private class ViewHolder {
        public TextView time;
        public TextView type;
        public TextView from;
        public ImageView img;
    }
}
