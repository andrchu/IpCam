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
import net.kaicong.ipcam.bean.Mess_ComRecord;
import net.kaicong.ipcam.utils.ImageUtils;

import java.util.List;

/**
 * Author: lu_qwen
 * Intro:
 * Time: 2015/7/23.
 */
public class Mess_CommentAdapter extends BaseAdapter {

    private Context context;
    private List<Mess_ComRecord> list;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    private onMyClickListener listener;

    public Mess_CommentAdapter(Context context, onMyClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setData(List<Mess_ComRecord> list) {
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
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_mess_commentlist, null);
            holder.title = (TextView) view.findViewById(R.id.device_name);
            holder.name = (TextView) view.findViewById(R.id.user_name);
            holder.time = (TextView) view.findViewById(R.id.comment_time);
            holder.connent = (TextView) view.findViewById(R.id.comment_content);
            holder.head = (ImageView) view.findViewById(R.id.user_head);
            holder.snap = (ImageView) view.findViewById(R.id.comment_img);
            view.setTag(holder);
        }
        holder = (ViewHolder) view.getTag();
        holder.title.setText(list.get(position).title);
        holder.name.setText(list.get(position).username);
        holder.time.setText(list.get(position).time);
        holder.connent.setText(list.get(position).connent);
        imageLoader.displayImage(list.get(position).headUrl,
                holder.head,
                ImageUtils.getRoundedDisplayOptions(R.drawable.common_head_bg, null));

        if (null != list.get(position).picUrl_s && list.get(position).picUrl_s.length() > 0) {
            holder.snap.setVisibility(View.VISIBLE);
            imageLoader.displayImage(list.get(position).picUrl_s,
                    holder.snap,
                    ImageUtils.getDisplayOptions(R.drawable.common_no_image, null));
        } else {
            holder.snap.setVisibility(View.GONE);

        }
        holder.snap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onImageClick(position);
            }
        });
        return view;
    }

    private class ViewHolder {
        private TextView title;
        private ImageView head;
        private TextView name;
        private TextView time;
        private TextView connent;
        private ImageView snap;
    }

   public interface onMyClickListener {
        public void onImageClick(int position);
    }
}
