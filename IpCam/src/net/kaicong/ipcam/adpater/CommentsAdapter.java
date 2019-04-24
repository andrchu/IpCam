package net.kaicong.ipcam.adpater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.kaicong.ipcam.R;
import net.kaicong.ipcam.device.seeworld.Comments;
import net.kaicong.ipcam.utils.ImageUtils;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by chu on 14-12-9.
 */
public class CommentsAdapter extends BaseAdapter {

    private List<Comments> data;
    private boolean isDeviceBelongHost = false;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private onMyClick myClick;

    private Context context;
    private LayoutInflater inflater;

    public void setData(List<Comments> data) {
        this.data = data;
    }

    public CommentsAdapter(Context context, onMyClick myClick) {

        this.context = context;
        this.myClick = myClick;

    }

    /**
     * 当前设备是当前登录用户所分享
     *
     * @param isDeviceBelongHost
     */
    public void setDeviceBelongHost(boolean isDeviceBelongHost) {
        this.isDeviceBelongHost = isDeviceBelongHost;
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.item_see_world_comments, null);
            viewHolder.userHead = (ImageView) view.findViewById(R.id.user_head);
            viewHolder.userName = (TextView) view.findViewById(R.id.user_name);
            viewHolder.commentContent = (TextView) view.findViewById(R.id.comment_content);
            viewHolder.commentTime = (TextView) view.findViewById(R.id.comment_time);
            viewHolder.replyArrow = (ImageView) view.findViewById(R.id.reply_arrow);
            viewHolder.snapComment = (ImageView) view.findViewById(R.id.comment_img);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) view.getTag();
        imageLoader.displayImage(data.get(i).reviewerHeadUrl,
                viewHolder.userHead,
                ImageUtils.getRoundedDisplayOptions(R.drawable.common_head_bg, null));
        viewHolder.userName.setText(data.get(i).userName);
        viewHolder.commentContent.setText(data.get(i).content);
        viewHolder.commentTime.setText(data.get(i).showTime);
        if (isDeviceBelongHost) {
            viewHolder.replyArrow.setVisibility(View.VISIBLE);
        } else {
            viewHolder.replyArrow.setVisibility(View.GONE);
        }
        //截图品论 之 图片k
        viewHolder.snapComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myClick.clickImage(i);
            }
        });
        if (null != data.get(i).prevUrl_s && data.get(i).prevUrl_s.length() > 0) {
            viewHolder.snapComment.setVisibility(View.VISIBLE);
            // String smallPic ="http://7xi6gi.com1.z0.glb.clouddn.com/s1s?imageView2/0/w/80/h/60";
            imageLoader.displayImage(data.get(i).prevUrl_s,
                    viewHolder.snapComment,
                    ImageUtils.getDisplayOptions(R.drawable.common_no_image, null));
        } else {
            viewHolder.snapComment.setVisibility(View.GONE);

        }
        // viewHolder.snapComment.setVisibility(View.GONE);
        return view;
    }

    private class ViewHolder {
        public ImageView userHead;
        public TextView userName;
        public TextView commentContent;
        public TextView commentTime;
        public ImageView replyArrow;
        public ImageView snapComment;
    }

    public interface onMyClick {
        public void clickImage(int i);
    }

}
