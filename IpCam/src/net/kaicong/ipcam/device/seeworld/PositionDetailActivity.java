package net.kaicong.ipcam.device.seeworld;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.R;
import net.kaicong.ipcam.bean.PositionModel;

public class PositionDetailActivity extends BaseActivity {

	private String title;

	private TextView positionApplyDate;
	private TextView positionApplySuccDate;
	private TextView desc_rejectReseaonTextView;
	private TextView desc_rejectReseaon;
	private ImageView positionPic;

	private PositionModel positionModel;
	private ImageLoader imageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_position_detail);
		imageLoader = ImageLoader.getInstance();
		positionModel = (PositionModel) getIntent().getSerializableExtra(
				"position_model");
		initTitle(positionModel.LandmarkName);
		showBackButton();

		positionApplyDate = (TextView) findViewById(R.id.apply_date);
		if (positionModel.ApplyTime.contains("T")) {
			positionApplyDate.setText(positionModel.ApplyTime.substring(0,
					positionModel.ApplyTime.indexOf("T")));
		} else {
			positionApplyDate.setText(positionModel.ApplyTime);
		}
		positionApplySuccDate = (TextView) findViewById(R.id.apply_success_date);
		if (positionModel.YesOrNoTime.contains("T")) {
			positionApplySuccDate.setText(positionModel.YesOrNoTime.substring(
					0, positionModel.ApplyTime.indexOf("T")));
		} else {
			positionApplySuccDate.setText(positionModel.YesOrNoTime);
		}
		desc_rejectReseaonTextView = (TextView) findViewById(R.id.desc_text);
		desc_rejectReseaon = (TextView) findViewById(R.id.desc);
		positionPic = (ImageView) findViewById(R.id.position_pic);
		if (positionModel.IsVerify == 1) {
			// 已通过
			desc_rejectReseaonTextView
					.setText(getString(R.string.device_position_desc));
			desc_rejectReseaon.setText(positionModel.LandmarkDesc);
			imageLoader.displayImage(positionModel.LandmarkPic, positionPic);
		} else {
			// 未通过
			desc_rejectReseaonTextView
					.setText(getString(R.string.device_position_reject_reason));
			desc_rejectReseaon.setText(positionModel.Reason);
		}

	}

}
