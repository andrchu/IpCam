package net.kaicong.ipcam.device.seeworld;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.R;
import net.kaicong.ipcam.UmengShareUtils;
import net.kaicong.ipcam.utils.StringUtils;

/**
 * Created by LingYan on 15/6/23.
 */
public class RewardShareActivity extends BaseActivity {

    private Button rewardOkAgain;
    private EditText rewardOkEdit;
    private ImageView rewardOkShare;
    private String price;

    protected UmengShareUtils umengShareUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle(getString(R.string.seeworld_reward_title));
        showBackButton();
        setContentView(R.layout.activity_reward_share);
        price = getIntent().getStringExtra("price");

        rewardOkAgain = (Button) findViewById(R.id.reward_ok_again);
        rewardOkAgain.setOnClickListener(this);
        rewardOkEdit = (EditText) findViewById(R.id.reward_ok_edit);
        rewardOkShare = (ImageView) findViewById(R.id.reward_ok_share);
        rewardOkShare.setOnClickListener(this);

        rewardOkEdit.setHint(getString(R.string.seeworld_reward_edit_hint, price));

        umengShareUtils = new UmengShareUtils(this);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.reward_ok_again:

                break;
            case R.id.reward_ok_share:
                String editText = rewardOkEdit.getText().toString();
                if (StringUtils.isEmpty(editText)) {
                    editText = getString(R.string.seeworld_reward_edit_hint, price);
                }
                umengShareUtils.share(null, editText);
                break;

        }
    }

//    @Override
//    public void doBackButtonAction() {
//        super.doBackButtonAction();
//        setResult(RESULT_OK);
//        finish();
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            setResult(RESULT_OK);
//            finish();
//        }
//        return true;
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        umengShareUtils.doSSOHandler(requestCode, resultCode, data);
    }

}
