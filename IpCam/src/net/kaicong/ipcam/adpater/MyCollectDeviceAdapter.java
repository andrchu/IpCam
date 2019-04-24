package net.kaicong.ipcam.adpater;

import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import net.kaicong.ipcam.R;
import net.kaicong.ipcam.bean.AnimateFirstDisplayListener;
import net.kaicong.ipcam.bean.MyCollect;
import net.kaicong.ipcam.utils.ImageUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by LingYan on 14-12-22.
 */
public class MyCollectDeviceAdapter extends BaseAdapter {

    private List<MyCollect> data;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public void setData(List<MyCollect> data) {
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
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_my_collect_device, viewGroup, false);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.imageview);
            viewHolder.deviceTitle = (TextView) view.findViewById(R.id.device_title);
            TextPaint tPaint = viewHolder.deviceTitle.getPaint();
            tPaint.setFakeBoldText(true);//设置粗体
            viewHolder.collectTime = (TextView) view.findViewById(R.id.collect_time);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) view.getTag();
        imageLoader.displayImage(data.get(i).deviceIcon, viewHolder.imageView, ImageUtils.getFilletDisplayOptions(8, R.drawable.world_view_common_image, null), new AnimateFirstDisplayListener());
        viewHolder.deviceTitle.setText(data.get(i).deviceName);
        int tIndex = data.get(i).favoriteTime.indexOf("T");
        String result = viewGroup.getContext().getString(R.string.see_world_collect_on, data.get(i).favoriteTime.substring(tIndex - 5, tIndex) + " " + data.get(i).favoriteTime.substring(tIndex + 1, tIndex + 6));
        viewHolder.collectTime.setText(result);
        return view;
    }

    private class ViewHolder {
        private ImageView imageView;
        private TextView deviceTitle;
        private TextView collectTime;
    }

}
