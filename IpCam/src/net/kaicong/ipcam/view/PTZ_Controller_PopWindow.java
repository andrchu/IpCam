package net.kaicong.ipcam.view;

import net.kaicong.ipcam.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

/**
 * Created by LingYan on 2014/9/28 0028.
 */
public class PTZ_Controller_PopWindow extends PopupWindow {

    private Context context;
    private OnPTZClickListener onPTZClickListener = null;
    public static final int PTZ_CLICK_POSITION_UP = 0;
    public static final int PTZ_CLICK_POSITION_LEFT = PTZ_CLICK_POSITION_UP + 1;
    public static final int PTZ_CLICK_POSITION_RIGHT = PTZ_CLICK_POSITION_LEFT + 1;
    public static final int PTZ_CLICK_POSITION_DOWN = PTZ_CLICK_POSITION_RIGHT + 1;
    public static final int PTZ_CLICK_POSITION_LEFT_RIGHT = PTZ_CLICK_POSITION_DOWN + 1;
    public static final int PTZ_CLICK_POSITION_UP_DOWN = PTZ_CLICK_POSITION_LEFT_RIGHT + 1;
    public static final int PTZ_CLICK_POSITION_STOP = PTZ_CLICK_POSITION_UP_DOWN + 1;
    public static final int PTZ_CLICK_POSITION_ZOOM_IN = PTZ_CLICK_POSITION_STOP + 1;
    public static final int PTZ_CLICK_POSITION_ZOOM_OUT = PTZ_CLICK_POSITION_ZOOM_IN + 1;

    public void setOnPTZClickListener(OnPTZClickListener onPTZClickListener) {
        this.onPTZClickListener = onPTZClickListener;
    }

    public PTZ_Controller_PopWindow(Context context) {
        this.context = context;
        final View contentView = LayoutInflater.from(context).inflate(R.layout.pop_ptz_controller, null);
        setContentView(contentView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置popwindow以外的地方点击，popwindow消失
        setOutsideTouchable(true);
        initButtons(contentView);
        LinearLayout layoutRoot = (LinearLayout) contentView.findViewById(R.id.root);
        layoutRoot.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int height = contentView.findViewById(R.id.control_layout).getTop();
                int y = (int) motionEvent.getY();
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }

        });
    }

    private void initButtons(View contentView) {
        final ImageView firstButton = (ImageView) contentView.findViewById(R.id.ptz_up);
        firstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPTZClickListener != null) {
                    //第一个按钮点击
                    onPTZClickListener.onPTZClick(firstButton, PTZ_CLICK_POSITION_UP);
                }
            }
        });
        final ImageView secondButton = (ImageView) contentView.findViewById(R.id.ptz_left);
        secondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPTZClickListener != null) {
                    //第二个按钮点击
                    onPTZClickListener.onPTZClick(secondButton, PTZ_CLICK_POSITION_LEFT);
                }
            }
        });
        final ImageView thirdButton = (ImageView) contentView.findViewById(R.id.ptz_right);
        thirdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPTZClickListener != null) {
                    //第三个按钮点击
                    onPTZClickListener.onPTZClick(thirdButton, PTZ_CLICK_POSITION_RIGHT);
                }
            }
        });
        final ImageView firthButton = (ImageView) contentView.findViewById(R.id.ptz_down);
        firthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPTZClickListener != null) {
                    //第四个按钮点击
                    onPTZClickListener.onPTZClick(firthButton, PTZ_CLICK_POSITION_DOWN);
                }
            }
        });
        final ImageView ptzLeftRight = (ImageView) contentView.findViewById(R.id.ptz_left_right);
        ptzLeftRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPTZClickListener != null) {
                    //第四个按钮点击
                    onPTZClickListener.onPTZClick(ptzLeftRight, PTZ_CLICK_POSITION_LEFT_RIGHT);
                }
            }
        });
        final ImageView ptzUpDown = (ImageView) contentView.findViewById(R.id.ptz_up_down);
        ptzUpDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPTZClickListener != null) {
                    //第四个按钮点击
                    onPTZClickListener.onPTZClick(ptzUpDown, PTZ_CLICK_POSITION_UP_DOWN);
                }
            }
        });
        final ImageView ptzStop = (ImageView) contentView.findViewById(R.id.ptz_stop);
        ptzStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPTZClickListener != null) {
                    //第四个按钮点击
                    onPTZClickListener.onPTZClick(ptzStop, PTZ_CLICK_POSITION_STOP);
                }
            }
        });
        final Button cancelButton = (Button) contentView.findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        final ImageView ptzZoomIn = (ImageView) contentView.findViewById(R.id.ptz_zoom_in);
        ptzZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPTZClickListener != null) {
                    //第四个按钮点击
                    onPTZClickListener.onPTZClick(ptzZoomIn, PTZ_CLICK_POSITION_ZOOM_IN);
                }
            }
        });
        final ImageView ptzZoomOut = (ImageView) contentView.findViewById(R.id.ptz_zoom_out);
        ptzZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onPTZClickListener != null) {
                    //第四个按钮点击
                    onPTZClickListener.onPTZClick(ptzZoomOut, PTZ_CLICK_POSITION_ZOOM_OUT);
                }
            }
        });
    }

    public interface OnPTZClickListener {

        public void onPTZClick(View view, int position);

    }

}
