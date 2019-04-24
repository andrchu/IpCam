package net.kaicong.ipcam.view;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

/**
 * Created by LingYan on 15-1-6.
 */
public class StateDrawable extends LayerDrawable {


    public StateDrawable(Drawable[] layers) {
        super(layers);
    }

    @Override
    protected boolean onStateChange(int[] states) {
        for (int state : states) {
            if (state == android.R.attr.state_selected ||
                    state == android.R.attr.state_focused ||
                    state == android.R.attr.state_pressed) {
                super.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
            } else {
                super.setColorFilter(Color.parseColor("#F95913"), PorterDuff.Mode.SRC_ATOP);
            }
        }
        return super.onStateChange(states);
    }

    @Override
    public boolean isStateful() {
        return true;
    }


}
