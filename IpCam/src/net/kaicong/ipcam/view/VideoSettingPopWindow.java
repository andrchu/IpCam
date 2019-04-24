package net.kaicong.ipcam.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;

import net.kaicong.ipcam.R;
import net.kaicong.ipcam.device.cgi.CgiImageAttr;

import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by LingYan on 15-1-13.
 */
public class VideoSettingPopWindow extends PopupWindow {

    private Activity context;
    private CgiImageAttr cgiImageAttr;
    private OnVideoSettingListener onVideoSettingListener;

    private SegmentedGroup segmentedGroup01;
    private SegmentedGroup segmentedGroup02;
    private RadioButton radioButton01;
    private RadioButton radioButton02;
    private RadioButton radioButton03;
    private RadioButton radioButton04;
    private RadioButton radioButton05;
    private RadioButton radioButton06;
    private SeekBar seekBar01;
    private SeekBar seekBar02;
    private SeekBar seekBar03;
    private Switch aSwitch01;
    private Switch aSwitch02;

    public VideoSettingPopWindow(Activity context, CgiImageAttr cgiImageAttr, int layoutResId, OnVideoSettingListener onVideoSettingListener) {
        this.context = context;
        this.cgiImageAttr = cgiImageAttr;
        this.onVideoSettingListener = onVideoSettingListener;
        initView(context, layoutResId);
    }

    private void initView(Context context, int layoutResId) {
        View view = LayoutInflater.from(context).inflate(layoutResId, null);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);

        segmentedGroup01 = (SegmentedGroup) view.findViewById(R.id.segment_group_resolution);
        segmentedGroup02 = (SegmentedGroup) view.findViewById(R.id.segment_group_mode);
        radioButton01 = (RadioButton) view.findViewById(R.id.radio_btn1);
        radioButton02 = (RadioButton) view.findViewById(R.id.radio_btn2);
        radioButton03 = (RadioButton) view.findViewById(R.id.radio_btn3);
        radioButton04 = (RadioButton) view.findViewById(R.id.radio_btn4);
        radioButton05 = (RadioButton) view.findViewById(R.id.radio_btn5);
        radioButton06 = (RadioButton) view.findViewById(R.id.radio_btn6);
        seekBar01 = (SeekBar) view.findViewById(R.id.seek1);
        seekBar02 = (SeekBar) view.findViewById(R.id.seek2);
        seekBar03 = (SeekBar) view.findViewById(R.id.seek3);
        aSwitch01 = (Switch) view.findViewById(R.id.switch1);
        aSwitch02 = (Switch) view.findViewById(R.id.switch2);

        /**
         * state init
         */
        if (cgiImageAttr.resolution == 0) {
            if (radioButton01 != null) {
                radioButton01.setChecked(true);
            }
        } else if (cgiImageAttr.resolution == 1) {
            if (radioButton02 != null) {
                radioButton02.setChecked(true);
            }
        } else if (cgiImageAttr.resolution == 2) {
            if (radioButton03 != null) {
                radioButton03.setChecked(true);
            }
        }

        if (cgiImageAttr.mode == 0) {
            if (radioButton04 != null) {
                radioButton04.setChecked(true);
            }
        } else if (cgiImageAttr.mode == 1) {
            if (radioButton05 != null) {
                radioButton05.setChecked(true);
            }
        } else if (cgiImageAttr.mode == 2) {
            if (radioButton06 != null) {
                radioButton06.setChecked(true);
            }
        }
        if (seekBar01 != null) {
            seekBar01.setProgress(cgiImageAttr.brightness);
            seekBar01.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (onVideoSettingListener != null) {
                        onVideoSettingListener.onVideoBrightnessSet(seekBar.getProgress());
                        cgiImageAttr.brightness = seekBar.getProgress();
                    }
                }
            });
        }
        if (seekBar02 != null) {
            seekBar02.setProgress(cgiImageAttr.saturation);
            seekBar02.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (onVideoSettingListener != null) {
                        onVideoSettingListener.onVideoSaturationSet(seekBar.getProgress());
                        cgiImageAttr.saturation = seekBar.getProgress();
                    }
                }
            });
        }
        if (seekBar03 != null) {
            seekBar03.setProgress(cgiImageAttr.contrast);
            seekBar03.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (onVideoSettingListener != null) {
                        onVideoSettingListener.onVideoContrastSet(seekBar.getProgress());
                        cgiImageAttr.contrast = seekBar.getProgress();
                    }
                }
            });
        }
        aSwitch01.setChecked(cgiImageAttr.flip);
        aSwitch02.setChecked(cgiImageAttr.mirror);

        /**
         * state setting
         */
        if (segmentedGroup01 != null) {
            segmentedGroup01.setTintColor(Color.parseColor("#d2d5d2"));
            segmentedGroup01.
                    setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                   @Override
                                                   public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                                       int result = 0;
                                                       if (i == R.id.radio_btn1) {
                                                           result = 0;
                                                       } else if (i == R.id.radio_btn2) {
                                                           result = 1;
                                                       } else if (i == R.id.radio_btn3) {
                                                           result = 2;
                                                       }

                                                       if (result == cgiImageAttr.resolution) {
                                                           return;
                                                       }
                                                       if (onVideoSettingListener != null) {
                                                           onVideoSettingListener.onVideoResolutionSet(result);
                                                           cgiImageAttr.resolution = result;
                                                       }
                                                   }
                                               }
                    );
        }
        if (segmentedGroup02 != null) {
            segmentedGroup02.setTintColor(Color.parseColor("#d2d5d2"));
            segmentedGroup02.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()

                                                        {
                                                            @Override
                                                            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                                                int result = 0;
                                                                if (i == R.id.radio_btn4) {
                                                                    result = 0;
                                                                } else if (i == R.id.radio_btn5) {
                                                                    result = 1;
                                                                } else if (i == R.id.radio_btn6) {
                                                                    result = 2;
                                                                }
                                                                if (result == cgiImageAttr.mode) {
                                                                    return;
                                                                }
                                                                if (onVideoSettingListener != null) {
                                                                    onVideoSettingListener.onVideoModeSet(result);
                                                                    cgiImageAttr.mode = result;
                                                                }
                                                            }
                                                        }

            );
        }

        aSwitch01.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()

                                             {
                                                 @Override
                                                 public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                     if (onVideoSettingListener != null) {
                                                         onVideoSettingListener.onVideoFlipSet(b);
                                                     }
                                                 }
                                             }

        );

        aSwitch02.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()

                                             {
                                                 @Override
                                                 public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                                     if (onVideoSettingListener != null) {
                                                         onVideoSettingListener.onVideoMirrorSet(b);
                                                     }
                                                 }
                                             }

        );

        setContentView(view);
    }

    public interface OnVideoSettingListener {

        /**
         * 分辨率设置
         */
        public void onVideoResolutionSet(int position);

        /**
         * 模式设置
         */
        public void onVideoModeSet(int position);

        /**
         * 明亮度设置
         */
        public void onVideoBrightnessSet(int num);

        /**
         * 饱和度设置
         */
        public void onVideoSaturationSet(int num);

        /**
         * 对比度设置
         */
        public void onVideoContrastSet(int num);

        /**
         * 翻转设置
         */
        public void onVideoFlipSet(boolean on);

        /**
         * 镜像设置
         */
        public void onVideoMirrorSet(boolean on);

    }

}
