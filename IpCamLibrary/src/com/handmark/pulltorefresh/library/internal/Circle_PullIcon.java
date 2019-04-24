package com.handmark.pulltorefresh.library.internal;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;


import net.kaicong.R;

import java.util.Random;

/**
 * Author: lu_qwen
 * Intro: 下拉圆弧
 * Time: 2015/3/19.
 */
public class Circle_PullIcon extends ImageView {
    private Bitmap bitmap;
    private int pro_ = 22;
    private Paint paint;
    private RectF rectc_arc;
    private boolean isRun = false;

    public Circle_PullIcon(Context context) {
        super(context);
        Resources resources = context.getResources();
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.kclogo_25);

    }

    public Circle_PullIcon(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources resources = context.getResources();
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.kclogo_25);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint = new Paint();
        paint.setAntiAlias(true);//抗锯齿
        paint.setColor(Color.rgb(242, 103, 33));
        paint.setStrokeWidth(2.1f);//粗度
        paint.setStyle(Paint.Style.STROKE);//空心

        int width = bitmap.getWidth();
        //int height = bitmap.getHeight();

        rectc_arc = new RectF(2, 2, width - 2, width - 2);
        canvas.drawArc(rectc_arc, -90, pro_, false, paint);

        if (isRun)
            runCircle();
    }

    //根据 下拉向下移动距离 画进度弧
    public void setProgress(double progress) {
        this.pro_ = (int) (progress * 360);
        this.invalidate();
    }

    //设置 自动旋转
    public void setAutoRun(boolean isRun) {
        this.isRun = isRun;
        this.invalidate();
    }

    //无限转动圆弧
    private void runCircle() {
        Random random = new Random();
        int s = random.nextInt(9) + 1;
        pro_ += s;
        if (pro_ <= 360) {

        } else {
            pro_ = 2;
        }
        this.invalidate();
    }


}
