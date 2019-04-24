package net.kaicong.ipcam;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;

import net.kaicong.ipcam.device.seeworld.SearchMyDeviceActivity;
import net.kaicong.ipcam.device.seeworld.SearchSeeWorldActivity;
import net.kaicong.ipcam.device.zhiyun.MyCamera;
import net.kaicong.ipcam.fragment.AboutMoreFragment;
import net.kaicong.ipcam.fragment.MyDeviceFragment;
import net.kaicong.ipcam.fragment.RecentFragment;
import net.kaicong.ipcam.fragment.WorldViewFragment;
import net.kaicong.ipcam.user.LoginActivity;
import net.kaicong.ipcam.user.MyCollectDeviceActivity;
import net.kaicong.ipcam.user.UserAccount;
import net.kaicong.ipcam.view.BadgeView;

import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.update.UmengUpdateAgent;

import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 项目的主Activity，所有的Fragment都嵌入在这里。
 *
 * @author LingYan
 */
public class MainActivity extends BaseActivity {


    //切换的四个fragment
    private MyDeviceFragment myDeviceFragment;
    private WorldViewFragment worldViewFragment;
    private RecentFragment recentFragment;
    private AboutMoreFragment aboutMoreFragment;
    //底部四个tab的所属的view
    private View tabMyDevice;
    private View tabWorldView;
    private RelativeLayout tabRecent;
    private View tabMore;
    //tab的图标
    private ImageView imageMyDevice;
    private ImageView imageWorldView;
    private ImageView imageRecent;
    private ImageView imageMore;
    //在Tab布局上显示消息标题的控件
    private TextView textMyDevice;
    private TextView textWorldView;
    private TextView textRecent;
    private TextView textMore;
    //用于对Fragment进行管理
    private FragmentManager fragmentManager;
    //当前tab的位置
    private int currentIndex = 0;
    private long startMillions = 0;

    public static BadgeView badgeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tab_item);

        UmengUpdateAgent.setUpdateOnlyWifi(false);
        UmengUpdateAgent.update(this);

        initTitle(getString(R.string.main_tab_my_device));
        // 初始化布局元素
        initViews();
        fragmentManager = getSupportFragmentManager();
        // 第一次启动时选中第0个tab
        setTabSelection();

        MyCamera.init();

        /**
         * 检测是否安装了旧版本,如果安装了将其卸载
         */
        if (isAvailable("com.kaicong.ipcam")) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle(getString(R.string.app_update_title))
                    .setMessage(getString(R.string.app_update_message))
                    .setPositiveButton(getString(R.string.app_update_uninstall), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            Uri packageURI = Uri.parse("package:com.kaicong.ipcam");
                            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
                            startActivity(uninstallIntent);
                        }

                    }).create().show();
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
    //-----------------------推送相关  end-------------------------

    private boolean isAvailable(String packageName) {
        final PackageManager packageManager = getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }

    /**
     * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
     */
    private void initViews() {
        tabMyDevice = findViewById(R.id.my_device_layout);
        tabWorldView = findViewById(R.id.world_view_layout);
        tabRecent = (RelativeLayout) findViewById(R.id.recent_layout);
        tabMore = findViewById(R.id.more_layout);
        imageMyDevice = (ImageView) findViewById(R.id.tab_my_device);
        imageWorldView = (ImageView) findViewById(R.id.tab_world_view);
        imageRecent = (ImageView) findViewById(R.id.tab_recent);
        imageMore = (ImageView) findViewById(R.id.tab_more);

        //角标
        badgeView = new BadgeView(this, imageRecent);
        badgeView.setText("1");
        badgeView.setTextSize(8);
        badgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        badgeView.setBadgeMargin(2,2);
        badgeView.setBadgeBackgroundColor(getResources().getColor(R.color.kaicong_orange));
        badgeView.hide();

        textMyDevice = (TextView) findViewById(R.id.text_my_device);
        textWorldView = (TextView) findViewById(R.id.text_world_view);
        textRecent = (TextView) findViewById(R.id.text_recent);
        textMore = (TextView) findViewById(R.id.text_more);
        tabMyDevice.setOnClickListener(this);
        tabWorldView.setOnClickListener(this);
        tabRecent.setOnClickListener(this);
        tabMore.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyCamera.uninit();
        KCApplication.isRefreshDevices = false;
        BitmapAjaxCallback.clearCache();
        AQUtility.cleanCache(AQUtility.getCacheDir(this), 0, 0);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.my_device_layout:
                // 当点击了消息tab时，选中第2个tab
                if (currentIndex == 1) {
                    return;
                }
                currentIndex = 1;
                setTabSelection();
                break;
            case R.id.world_view_layout:
                // 当点击了联系人tab时，选中第1个tab
                if (currentIndex == 0) {
                    return;
                }
                currentIndex = 0;
                setTabSelection();
                break;
            case R.id.recent_layout:
                // 当点击了动态tab时，选中第3个tab
                if (currentIndex == 2) {
                    return;
                }
                currentIndex = 2;
                setTabSelection();
                break;
            case R.id.more_layout:
                // 当点击了设置tab时，选中第4个tab
                if (currentIndex == 3) {
                    return;
                }
                currentIndex = 3;
                setTabSelection();
                break;
            default:
                break;
        }
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     */
    private void setTabSelection() {
        // 每次选中之前先清楚掉上次的选中状态
        clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (currentIndex) {
            case 0:
                setBaseTitle(getString(R.string.main_tab_world_view));
                showLeftButton(R.drawable.add_device_search);
                showRightButton(R.drawable.common_collect_device);
                // 当点击了联系人tab时，改变控件的图片和文字颜色
                imageWorldView.setColorFilter(getResources().getColor(R.color.kaicong_orange));
                textWorldView.setTextColor(getResources().getColor(R.color.kaicong_orange));
                if (worldViewFragment == null) {
                    // 如果ContactsFragment为空，则创建一个并添加到界面上
                    worldViewFragment = new WorldViewFragment();
                    transaction.add(R.id.content, worldViewFragment);
                } else {
                    // 如果ContactsFragment不为空，则直接将它显示出来
                    transaction.show(worldViewFragment);
                }
                break;
            case 1:
                setBaseTitle(getString(R.string.main_tab_my_device));
                showLeftButton(R.drawable.add_device_search);
                showRightButton(R.drawable.add_device_add);
                // 当点击了消息tab时，改变控件的图片和文字颜色
                imageMyDevice.setColorFilter(getResources().getColor(R.color.kaicong_orange));
                textMyDevice.setTextColor(getResources().getColor(R.color.kaicong_orange));
                if (myDeviceFragment == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    myDeviceFragment = new MyDeviceFragment();
                    transaction.add(R.id.content, myDeviceFragment);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(myDeviceFragment);
                }
                break;
            case 2:
                setBaseTitle(getString(R.string.main_tab_recent));
                hideLeftButton();
                hideRightButton();
                // 当点击了动态tab时，改变控件的图片和文字颜色
                imageRecent.setColorFilter(getResources().getColor(R.color.kaicong_orange));
                textRecent.setTextColor(getResources().getColor(R.color.kaicong_orange));
                if (recentFragment == null) {
                    // 如果NewsFragment为空，则创建一个并添加到界面上
                    recentFragment = new RecentFragment();
                    transaction.add(R.id.content, recentFragment);
                } else {
                    // 如果NewsFragment不为空，则直接将它显示出来
                    transaction.show(recentFragment);
                }
                break;
            case 3:
                setBaseTitle(getString(R.string.main_tab_more));
                hideLeftButton();
                hideRightButton();
                // 当点击了设置tab时，改变控件的图片和文字颜色
                imageMore.setColorFilter(getResources().getColor(R.color.kaicong_orange));
                textMore.setTextColor(getResources().getColor(R.color.kaicong_orange));
                if (aboutMoreFragment == null) {
                    // 如果SettingFragment为空，则创建一个并添加到界面上
                    aboutMoreFragment = new AboutMoreFragment();
                    transaction.add(R.id.content, aboutMoreFragment);
                } else {
                    // 如果SettingFragment不为空，则直接将它显示出来
                    transaction.show(aboutMoreFragment);
                }
                break;
            default:
                break;
        }
        transaction.commit();
    }

    /**
     * 清除掉所有的选中状态。
     */
    private void clearSelection() {
        int commonColor = getResources().getColor(R.color.my_device_text_color);
        imageMyDevice.setColorFilter(commonColor);
        textMyDevice.setTextColor(commonColor);
        imageWorldView.setColorFilter(commonColor);
        textWorldView.setTextColor(commonColor);
        imageRecent.setColorFilter(commonColor);
        textRecent.setTextColor(commonColor);
        imageMore.setColorFilter(commonColor);
        textMore.setTextColor(commonColor);
    }

    @Override
    public void doLeftButtonAction(View view) {
        Intent intent = new Intent();
        switch (currentIndex) {
            case 0:
                //搜索看世界
                intent.setClass(MainActivity.this, SearchSeeWorldActivity.class);
                startActivity(intent);
                break;
            case 1:
                //搜索我的设备
                intent.setClass(MainActivity.this, SearchMyDeviceActivity.class);
                startActivity(intent);
                break;
            case 2:

                break;
            case 3:

                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //这里不继承父类的方法，当切换系统语言时，不会保存当前view的数据
        //这样解决了当切换到非默认fragment时，设置系统语言再回来，fragment切换错乱的问题
//        super.onSaveInstanceState(outState);
    }

    @Override
    public void doRightButtonAction(View view) {
        super.doRightButtonAction(view);
        switch (currentIndex) {
            case 0:
                //看世界收藏
                if (!UserAccount.isUserLogin()) {
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.see_world_notice))
                            .setMessage(getString(R.string.see_world_notice_message))
                            .setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent();
                                    intent.setClass(MainActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    dialogInterface.dismiss();
                                }
                            }).setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();
                } else {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, MyCollectDeviceActivity.class);
                    startActivity(intent);
                }
                break;
            case 1:
                if (myDeviceFragment != null) {
                    myDeviceFragment.addDeviceAction();
                }
                break;
            case 2:

                break;
            case 3:

                break;
        }

    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction 用于对Fragment执行操作的事务
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (myDeviceFragment != null) {
            transaction.hide(myDeviceFragment);
        }
        if (worldViewFragment != null) {
            transaction.hide(worldViewFragment);
        }
        if (recentFragment != null) {
            transaction.hide(recentFragment);
        }
        if (aboutMoreFragment != null) {
            transaction.hide(aboutMoreFragment);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - startMillions > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                startMillions = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
