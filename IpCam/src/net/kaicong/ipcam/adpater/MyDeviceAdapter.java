package net.kaicong.ipcam.adpater;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;

import net.kaicong.ipcam.R;
import net.kaicong.ipcam.bean.DeviceCamera;
import net.kaicong.ipcam.device.CgiControlParams;

import java.util.List;

/**
 * Created by LingYan on 2014/9/29 0029.
 */
public class MyDeviceAdapter extends BaseAdapter {

    private Context context;
    private AQuery listQuery;

    public MyDeviceAdapter(Context context) {
        this.context = context;
        listQuery = new AQuery(context);
    }

    private List<DeviceCamera> data;

    public void setData(List<DeviceCamera> data) {
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
    public View getView(int i, View view, final ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_my_device, viewGroup, false);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.imageview);
            viewHolder.cameraName = (TextView) view.findViewById(R.id.camera_name);
            viewHolder.ddnsOrZhiyunNum = (TextView) view.findViewById(R.id.camera_ddns_or_zhiyun_num);
            viewHolder.overdueDate = (TextView) view.findViewById(R.id.camera_overdue_date);
            viewHolder.overdueProgressBar = (ProgressBar) view.findViewById(R.id.zhiyun_use_progressbar);
            viewHolder.overdueProgressText = (TextView) view.findViewById(R.id.zhiyun_use_progress_text);
            viewHolder.isZY = (ImageView)view.findViewById(R.id.img_isZY);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) view.getTag();
        /**
         * 通过IP或者DDNS添加的设备可以通过CGI的url来取到当前图像
         * 智云的设备第一次添加取不到当前图像(正在解决)，点击进入播放并且在退出时会将图像保存在sqlite数据库中
         * 下次进入设备列表智云的设备会从数据库中取出图像
         */
        int cameraMode = data.get(i).cameraModelId;
        String cameraIp = "";
        int cameraPort = 0;
        String user = data.get(i).cameraUser;
        String password = data.get(i).cameraPassword;
        if (data.get(i).cameraType == DeviceCamera.CAM_TYPE_DDNS) {
            cameraIp = data.get(i).wanIp;
            cameraPort = data.get(i).tcpPort;
        } else if (data.get(i).cameraType == DeviceCamera.CAM_TYPE_IP) {
            cameraIp = data.get(i).cameraIp;
            cameraPort = data.get(i).cameraPort;
        }
        final AQuery aQuery = listQuery.recycle(view);
        final ImageView imageView = viewHolder.imageView;
        if (data.get(i).cameraType == DeviceCamera.CAM_TYPE_ZHIYUN) {
            viewHolder.isZY.setVisibility(View.VISIBLE);
            if (data.get(i).bitmap == null) {
                aQuery.id(imageView).image(R.drawable.mydevice_zcloud_default);
            } else {
                aQuery.id(imageView).image(data.get(i).bitmap);
            }
        }
        /**
         * 通过cgi获取图像
         * 刚开始通过UniversalImageLoader添加BasicAuth验证，发现只支持1018系列
         * 后来重新添加新框架AndroidQuery，该框架支持图片缓存，BasicAuth验证
         */

        else {
            viewHolder.isZY.setVisibility(View.GONE);
            String visitUrl = CgiControlParams.getCgiSnapShotUrl(cameraMode, cameraIp, cameraPort, user, password);
            BasicHandle basicHandle = new BasicHandle(user, password);
//            int fallbackId = 0;
//            /**
//             * 通过图片名称获取图片资源id
//             */
//            int identify = context.getResources().getIdentifier(context.getPackageName() + ":drawable-hdpi/" + String.valueOf(data.get(i).modelId) + "_2", null, null);
//            if (identify > 0) {
//                fallbackId = identify;
//            } else if (identify == 0) {
//                //没有对应图片,取一个默认图片
//                switch (GetCameraModel.getCameraModel(data.get(i).modelId)) {
//                    case GetCameraModel.CAMERA_MODEL_SIP1018:
//                        fallbackId = R.drawable.sip1018_2;
//                        break;
//                    case GetCameraModel.CAMERA_MODEL_SIP1201:
//                        fallbackId = R.drawable.sip1201_2;
//                        break;
//                    case GetCameraModel.CAMERA_MODEL_SIP1601:
//                        fallbackId = R.drawable.sip1601_2;
//                        break;
//                    case GetCameraModel.CAMERA_MODEL_SIP1303:
//                        fallbackId = R.drawable.sip1303_2;
//                        break;
//                }
//            }
            ImageOptions options = new ImageOptions();
            options.memCache = true;
            options.fileCache = true;
            options.fallback = R.drawable.common_no_image;
            aQuery.id(imageView).auth(basicHandle).image(visitUrl, options);
        }

        if (data.get(i).cameraType == DeviceCamera.CAM_TYPE_DDNS) {
            viewHolder.overdueDate.setVisibility(View.INVISIBLE);
            viewHolder.overdueProgressBar.setVisibility(View.GONE);
            viewHolder.overdueProgressText.setVisibility(View.GONE);
            viewHolder.ddnsOrZhiyunNum.setText(context.getResources().getString(R.string
                    .add_device_adapter_ddns) + data.get(i).ddnsName);
        } else if (data.get(i).cameraType == DeviceCamera.CAM_TYPE_IP) {
            viewHolder.overdueDate.setVisibility(View.INVISIBLE);
            viewHolder.overdueProgressBar.setVisibility(View.GONE);
            viewHolder.overdueProgressText.setVisibility(View.GONE);
            viewHolder.ddnsOrZhiyunNum.setText(context.getResources().getString(R.string
                    .add_device_adapter_ip) + data.get(i).cameraIp);
        } else if (data.get(i).cameraType == DeviceCamera.CAM_TYPE_ZHIYUN) {
            viewHolder.ddnsOrZhiyunNum.setText(context.getResources().getString(R.string
                    .add_device_adapter_zhiyun) + data.get(i).zCloud);
            viewHolder.overdueDate.setVisibility(View.VISIBLE);
            viewHolder.overdueProgressBar.setVisibility(View.VISIBLE);
            viewHolder.overdueProgressText.setVisibility(View.VISIBLE);
            viewHolder.overdueDate.setText(context.getResources().getString(R.string.device_property_zhiyun_use_date, data.get(i).overDueDate));
            viewHolder.overdueProgressText.setText(data.get(i).progressText);
            viewHolder.overdueProgressText.setTextColor(context.getResources().getColor(R.color.kaicong_orange));
            //设置颜色
            viewHolder.overdueProgressBar.getProgressDrawable().setColorFilter(context.getResources().getColor(R.color.kaicong_orange), PorterDuff.Mode.MULTIPLY);
            viewHolder.overdueProgressBar.setProgress((int) data.get(i).progress);
        }
        viewHolder.cameraName.setText(data.get(i).displayName);
        return view;
    }

    private class ViewHolder {
        public ImageView imageView;
        public TextView cameraName;
        public TextView ddnsOrZhiyunNum;
        public TextView overdueDate;
        public ProgressBar overdueProgressBar;
        public TextView overdueProgressText;
        public ImageView isZY;
    }

}
