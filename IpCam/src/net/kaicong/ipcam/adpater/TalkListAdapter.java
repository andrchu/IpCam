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
import net.kaicong.ipcam.user.UserFeedbackRetry;
import net.kaicong.ipcam.utils.StringUtils;

import java.util.List;

/**
 * Created by LingYan on 2/28/14.
 */
public class TalkListAdapter extends BaseAdapter {

    private Context mContext;
    public List<UserFeedbackRetry> mDataset;
    private ImageLoader imageLoader;

    public TalkListAdapter(Context context) {
        mContext = context;
        imageLoader = ImageLoader.getInstance();
    }

    public void setData(List<UserFeedbackRetry> items) {
        mDataset = items;
    }

    @Override
    public int getCount() {
        return mDataset == null ? 0 : mDataset.size();
    }

    @Override
    public UserFeedbackRetry getItem(int position) {
        return mDataset.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_talkrecord, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.iv_income_img = (ImageView) convertView
                    .findViewById(R.id.iv_income_img);

            viewHolder.iv_out_img = (ImageView) convertView
                    .findViewById(R.id.iv_out_img);

            viewHolder.tv_income_date = (TextView) convertView
                    .findViewById(R.id.tv_income_date);
            viewHolder.tv_out_date = (TextView) convertView
                    .findViewById(R.id.tv_out_date);

            viewHolder.tv_income_msg = (TextView) convertView
                    .findViewById(R.id.tv_income_msg);
            viewHolder.tv_out_msg = (TextView) convertView
                    .findViewById(R.id.tv_out_msg);

            viewHolder.ll_income = convertView.findViewById(R.id.ll_income);
            viewHolder.ll_out = convertView.findViewById(R.id.ll_out);

            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        UserFeedbackRetry item = getItem(position);
        if (item.IsCustomer)// 自己的发言
        {
            viewHolder.ll_income.setVisibility(View.GONE);
            viewHolder.ll_out.setVisibility(View.VISIBLE);
            if (!StringUtils.isEmpty(item.Content)) {
                viewHolder.tv_out_msg.setVisibility(View.VISIBLE);
                viewHolder.tv_out_msg.setText(item.Content);
            } else {
                viewHolder.tv_out_msg.setVisibility(View.GONE);
            }
            viewHolder.iv_out_img.setVisibility(View.GONE);
        } else {
            //我们后台的回复
            viewHolder.ll_income.setVisibility(View.VISIBLE);
            viewHolder.ll_out.setVisibility(View.GONE);
            if (!StringUtils.isEmpty(item.Content)) {
                viewHolder.tv_income_msg.setVisibility(View.VISIBLE);
                viewHolder.tv_income_msg.setText(item.Content);
            } else {
                viewHolder.tv_income_msg.setVisibility(View.GONE);
            }
            viewHolder.iv_income_img.setVisibility(View.GONE);
        }
        return convertView;
    }

    private class ViewHolder {
        public View ll_income;
        public View ll_out;
        public TextView tv_income_date;
        public TextView tv_out_date;
        public TextView tv_income_msg;
        public TextView tv_out_msg;
        public ImageView iv_income_img;
        public ImageView iv_out_img;
    }

}