package net.kaicong.ipcam.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import net.kaicong.ipcam.BaseFragment;
import net.kaicong.ipcam.R;

import com.viewpagerindicator.TabPageIndicator;

/**
 * Created by LingYan on 2014/8/29.
 */
public class WorldViewFragment extends BaseFragment {

    /**
     * for viewpager tab indicator
     */
    private FragmentPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private TabPageIndicator tabPageIndicator;
    private String titles[] = new String[4];

    private RecommendFragment recommendFragment;
    private PraiseFragment praiseFragment;
    private HotFragment hotFragment;
    private LatestFragment latestFragment;

    @Override
    protected void initView(View convertView) {
        super.initView(convertView);

        titles[0] = getString(R.string.see_world_keyword_recommend);
        titles[2] = getString(R.string.see_world_keyword_hot);
        titles[1] = getString(R.string.see_world_keyword_praise);
        titles[3] = getString(R.string.see_world_keyword_latest);

        pagerAdapter = new SwitchTabAdapter(this.getActivity().getSupportFragmentManager());
        viewPager = (ViewPager) convertView.findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        tabPageIndicator = (TabPageIndicator) convertView.findViewById(R.id.indicator);
        tabPageIndicator.setViewPager(viewPager);

    }

    private class SwitchTabAdapter extends FragmentPagerAdapter {

        public SwitchTabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i == 0) {
                //推荐
                if (recommendFragment == null) {
                    recommendFragment = new RecommendFragment();
                }
                return recommendFragment;
            } else if (i == 1) {
                //好评
                if (praiseFragment == null) {
                    praiseFragment = new PraiseFragment();
                }
                return praiseFragment;
            } else if (i == 2) {
                //人气
                if (hotFragment == null) {
                    hotFragment = new HotFragment();
                }
                return hotFragment;
            } else if (i == 3) {
                //最新
                if (latestFragment == null) {
                    latestFragment = new LatestFragment();
                }
                return latestFragment;
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_world_view;
    }
}
