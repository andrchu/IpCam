package net.kaicong.ipcam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.kaicong.ipcam.utils.PreferenceUtils;

import cn.jpush.android.api.JPushInterface;


public class WelcomeActivity extends Activity implements OnPageChangeListener, View.OnTouchListener {
    /**
     * ViewPager
     */
    private ViewPager viewPager;
    private ImageView[] tips;
    private ImageView[] mImageViews;
    private int[] imgIdArray;

    private boolean isFirstLaunch = true;
    private boolean isStartLaunch = false;
    private int currentPosition = 0;
    private int lastX = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        isFirstLaunch = PreferenceUtils.loadBooleanPreference(this, "isFirstLaunch", true);

        if (isFirstLaunch) {
            //加载欢迎界面的三张图
            setContentView(R.layout.activity_welcome);
            ViewGroup group = (ViewGroup) findViewById(R.id.viewGroup);
            viewPager = (ViewPager) findViewById(R.id.viewPager);
            imgIdArray = new int[]{R.drawable.welcome_01, R.drawable.welcome_02, R.drawable.welcome_03};
            tips = new ImageView[imgIdArray.length];
            for (int i = 0; i < tips.length; i++) {
                ImageView imageView = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(12, 12);
                params.leftMargin = 10;
                imageView.setLayoutParams(params);
                tips[i] = imageView;
                if (i == 0) {
                    tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
                } else {
                    tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
                }
                group.addView(imageView);
            }
            mImageViews = new ImageView[imgIdArray.length];
            for (int i = 0; i < mImageViews.length; i++) {
                ImageView imageView = new ImageView(this);
                mImageViews[i] = imageView;
                imageView.setBackgroundResource(imgIdArray[i]);
            }
            viewPager.setAdapter(new MyAdapter());
            viewPager.setOnPageChangeListener(this);
            viewPager.setOnTouchListener(this);
            viewPager.setCurrentItem(0);
            PreferenceUtils.savePreference(this, "isFirstLaunch", false);

        } else {
            //加载启动图
            setContentView(R.layout.splash);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent mainIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                    // mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    // Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    WelcomeActivity.this.startActivity(mainIntent);
                    WelcomeActivity.this.finish();
                    overridePendingTransition(R.anim.mainfadein, R.anim.splashfadeout);
                }
            }, 1500);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) motionEvent.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if ((lastX - motionEvent.getX()) > 100 && (currentPosition == imgIdArray.length - 1) && !isStartLaunch) {
                    Intent intent = new Intent();
                    intent.setClass(WelcomeActivity.this, MainActivity.class);
                    startActivity(intent);
                    WelcomeActivity.this.finish();
                    isStartLaunch = true;
                }
                break;
            default:
                break;
        }
        return false;
    }

    public class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imgIdArray.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(mImageViews[position]);
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(mImageViews[position], 0);
            return mImageViews[position];
        }

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        setImageBackground(arg0);
        currentPosition = arg0;
    }

    private void setImageBackground(int selectItems) {
        for (int i = 0; i < tips.length; i++) {
            if (i == selectItems) {
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            } else {
                tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

}
