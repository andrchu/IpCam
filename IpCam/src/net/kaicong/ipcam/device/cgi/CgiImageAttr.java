package net.kaicong.ipcam.device.cgi;

import java.io.Serializable;

/**
 * Created by LingYan on 2014/11/19 0019.
 */
public class CgiImageAttr implements Serializable {

    public int displayMode;//显示模式
    public int brightness;//明亮度
    public int saturation;//饱和度
    public int contrast;//对比度
    public int sharpness;//锐度
    public int hue;//色度
    public boolean flip;//翻转(开关)
    public boolean mirror;//镜像(开关)
    public boolean night;//夜间模式(开关)
    public int shutter;//快门饱和度
    public boolean wdr;//宽动态(开关)
    public boolean noise;//平滑模式(开关)
    public int gc;//夜视照度，取值范围[0-255],0代表自动调节夜视照度，大于0代表手动调节夜视照度。
    public int resolution;//码流分辨率
    public int mode;//模式(50 60 室外 夜间)
    /**
     * for 1601 flip
     */
    public int flip1601;

}
