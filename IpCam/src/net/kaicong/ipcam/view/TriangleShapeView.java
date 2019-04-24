package net.kaicong.ipcam.view;

import net.kaicong.ipcam.R;
import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

/**
 * Created by LingYan on 15/7/13.
 */

public class TriangleShapeView extends View {

    public TriangleShapeView(Context context) {
        super(context);
    }

    public TriangleShapeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TriangleShapeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth() / 2;

        Path path = new Path();
        path.moveTo(w, 0);
        path.lineTo(2 * w, 0);
        path.lineTo(2 * w, w);
        path.lineTo(w, 0);
        path.close();

        Paint p = new Paint();
        p.setColor(getResources().getColor(R.color.kaicong_orange));

        canvas.drawPath(path, p);
    }
}
