package net.kaicong.ipcam.adpater;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

/**
 * Created by chu on 15-1-8.
 */
public class DropDownAdapter extends ArrayAdapter<String> {

    public DropDownAdapter(Context context, String[] objects) {
        super(context, android.R.layout.simple_list_item_single_choice, objects);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        final CheckedTextView view = (CheckedTextView) super.getView(position, convertView, parent);
        view.setChecked(position == 2);
        return view;
    }

}
