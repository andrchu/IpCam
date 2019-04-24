package net.kaicong.ipcam.adpater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.kaicong.ipcam.R;
import net.kaicong.ipcam.bean.GetCameraModel;
import net.kaicong.ipcam.bean.SharedCamera;

import java.util.List;

/**
 * Created by LingYan on 15/7/28.
 */
public class SharedDevicesAdapter extends BaseAdapter {

    private List<SharedCamera> data;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public void setData(List<SharedCamera> data) {
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
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_shared_devices, viewGroup, false);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.imageview);
            viewHolder.title = (TextView) view.findViewById(R.id.device_title);
            viewHolder.shareTime = (TextView) view.findViewById(R.id.collect_time);
            viewHolder.imageViewZhiyun = (ImageView) view.findViewById(R.id.image_zhiyun);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) view.getTag();
        imageLoader.displayImage(data.get(i).imageUrl, viewHolder.imageView);
        viewHolder.title.setText(data.get(i).shareName);
        if (data.get(i).date.contains("T")) {
            String date = data.get(i).date.substring(0, data.get(i).date.indexOf("T"));
            viewHolder.shareTime.setText(date);
        }
        if (data.get(i).ddnsModelId == GetCameraModel.CAMERA_MODEL_ZHIYUN) {
            //智云
            viewHolder.imageViewZhiyun.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imageViewZhiyun.setVisibility(View.GONE);
        }
        return view;
    }

    private class ViewHolder {
        public ImageView imageView;
        public TextView title;
        public TextView shareTime;
        public ImageView imageViewZhiyun;
    }

}
