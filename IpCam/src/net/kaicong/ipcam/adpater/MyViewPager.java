package net.kaicong.ipcam.adpater;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Author: lu_qwen
 * Intro:
 * Time: 2015/4/29.
 */
public class MyViewPager extends ViewPager {

    private Context context;
    private boolean willIntercept;

    public MyViewPager(Context context) {
        super(context);
        this.context = context;
        this.willIntercept = true;
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.willIntercept = true;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (this.willIntercept) {
            //这个地方直接返回true会很卡
            // return true;
            return super.onInterceptTouchEvent(arg0);
        }
        return false;
    }


    /**
     * 设置ViewPager是否拦截点击事件
     *
     * @param value if true, ViewPager拦截点击事件
     *              if false, ViewPager将不能滑动，ViewPager的子View可以获得点击事件
     *              主要受影响的点击事件为横向滑动
     */
    public void setTouchIntercept(boolean value) {
        this.willIntercept = value;

    }

}
