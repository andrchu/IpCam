package net.kaicong.ipcam.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import net.kaicong.ipcam.R;
import net.kaicong.ipcam.user.UserFeedback;

import java.util.List;

/**
 * Created by LingYan on 15/2/25.
 */
public class UserFeedbackAdapter extends BaseAdapter {

    private List<UserFeedback> data;
    private Context context;
    private String[] feedbackTypes;

    public UserFeedbackAdapter(Context context) {
        this.context = context;
        feedbackTypes = context.getResources().getStringArray(R.array.feedback_types);
    }

    public void setData(List<UserFeedback> data) {
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
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user_feedback, viewGroup, false);
            viewHolder.feedbackContent = (TextView) view.findViewById(R.id.tev_feedback_content);
            viewHolder.feedbackDate = (TextView) view.findViewById(R.id.tev_fbCtime);
            viewHolder.feedbackTitle = (TextView) view.findViewById(R.id.tev_feedback_title);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) view.getTag();
        viewHolder.feedbackContent.setText(data.get(i).content);
        viewHolder.feedbackTitle.setText(feedbackTypes[(data.get(i).FeedbackType - 1 < 0) ? 0 : (data.get(i).FeedbackType - 1)]);
        viewHolder.feedbackDate.setText(data.get(i).createTime);
        return view;
    }

    private class ViewHolder {
        public TextView feedbackTitle;
        public TextView feedbackContent;
        public TextView feedbackDate;
    }

}
