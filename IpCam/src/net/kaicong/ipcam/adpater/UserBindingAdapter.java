package net.kaicong.ipcam.adpater;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;


import net.kaicong.ipcam.R;
import net.kaicong.ipcam.bean.BindingModel;

import java.util.List;

/**
 * Created by LingYan on 15/5/27.
 */
public class UserBindingAdapter extends BaseAdapter {

    private List<BindingModel> data;

    public void setData(List<BindingModel> data) {
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
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user_binding, viewGroup, false);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.item_img);
            viewHolder.title = (TextView) view.findViewById(R.id.item_title);
            viewHolder.bindingText = (TextView) view.findViewById(R.id.item_binding_text);
            viewHolder.bindingCheckBox = (CheckBox) view.findViewById(R.id.item_check_box);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) view.getTag();
        viewHolder.imageView.setImageResource(data.get(i).imgId);
        viewHolder.title.setText(data.get(i).title);
        viewHolder.bindingText.setText(data.get(i).isBinding ? viewGroup.getContext().getString(R.string.has_binding) : viewGroup.getContext().getString(R.string.not_binding));
        viewHolder.bindingCheckBox.setChecked(data.get(i).isBinding);
        return view;
    }

    private class ViewHolder {
        public ImageView imageView;
        public TextView title;
        public TextView bindingText;
        public CheckBox bindingCheckBox;
    }

}
