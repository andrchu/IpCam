package net.kaicong.ipcam.adpater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import net.kaicong.ipcam.R;
import net.kaicong.ipcam.bean.RewardRecord;
import net.kaicong.ipcam.utils.ImageUtils;

import java.util.List;

/**
 * Created by LingYan on 15/6/26.
 */
public class RewardRecordAdapter extends BaseAdapter {

    private List<RewardRecord> data;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public void setData(List<RewardRecord> data) {
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
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_reward_record, viewGroup, false);
            viewHolder.userHead = (ImageView) view.findViewById(R.id.user_head);
            viewHolder.userName = (TextView) view.findViewById(R.id.user_name);
            viewHolder.rewardTime = (TextView) view.findViewById(R.id.reward_time);
            viewHolder.rewardFe = (TextView) view.findViewById(R.id.reward_fee);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) view.getTag();
        imageLoader.displayImage(data.get(i).headPath, viewHolder.userHead, ImageUtils.getRoundedDisplayOptions(R.drawable.common_head_bg, null));
        viewHolder.userName.setText(data.get(i).username);
        viewHolder.rewardTime.setText(data.get(i).rewardTime);
        viewHolder.rewardFe.setText(viewGroup.getContext().getResources().getString(R.string.reward_record_fee, data.get(i).rewardFee));
        return view;
    }

    private class ViewHolder {
        public ImageView userHead;
        public TextView userName;
        public TextView rewardTime;
        public TextView rewardFe;
    }


}
