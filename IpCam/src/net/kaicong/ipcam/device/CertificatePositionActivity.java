package net.kaicong.ipcam.device;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.R;
import net.kaicong.ipcam.utils.StringUtils;

/**
 * Created by LingYan on 15/7/21.
 */
public class CertificatePositionActivity extends BaseActivity {

    private EditText sharedPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate_position);
        initTitle(getString(R.string.share_position_confirm));
        showBackButton();
        showRightButton(R.drawable.ic_action_accept);

        sharedPosition = (EditText) findViewById(R.id.edt_position);

    }

    @Override
    public void doRightButtonAction(View view) {
        super.doRightButtonAction(view);
        String positionStr = sharedPosition.getText().toString();
        if (!StringUtils.isEmpty(positionStr)) {
            Intent intent = new Intent();
            intent.putExtra("shared_position", positionStr);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
