package net.kaicong.ipcam;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.kaicong.ipcam.user.WebViewActivity;
import net.kaicong.ipcam.utils.StringUtils;

/**
 * Created by LingYan on 2014/12/2 0002. git
 */
public class AboutUsUrlActivity extends BaseActivity {

	private String officialUrl = "http://www.kaicong.net";
	private String technologyUrl = "http://www.kaicong.cc";
	private String mallUrl = "http://www.kaicongshop.com";
	private String progressUrl = "http://jb.kaicong.net";

	private TextView textOfficial;
	private TextView textTechnology;
	private TextView textMall;
	private TextView textProgress;

	private TextView content;
	private TextView protoctol;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us_url);

		initTitle(getString(R.string.about_more_about_us));
		showBackButton();

		textOfficial = (TextView) findViewById(R.id.text_kaicong_official);
		textOfficial.setOnClickListener(this);
		textTechnology = (TextView) findViewById(R.id.text_technology);
		textTechnology.setOnClickListener(this);
		textMall = (TextView) findViewById(R.id.text_mall);
		textMall.setOnClickListener(this);
		textProgress = (TextView) findViewById(R.id.text_progress);
		textProgress.setOnClickListener(this);

		protoctol = (TextView) findViewById(R.id.user_protocol);
		protoctol.setOnClickListener(this);
		
		content = (TextView) findViewById(R.id.tev_aboutUs_Content);
		content.setText("  "+getString(R.string.aboutUs_Content1) + "\n  "
				+ getString(R.string.aboutUs_Content2) + "\n  "
				+ getString(R.string.aboutUs_Content3) + "\n  "
				+ getString(R.string.aboutUs_Content4) + "\n  "
				+ getString(R.string.aboutUs_Content5));
		
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.text_kaicong_official:
			startBrowser(officialUrl);
			break;
		case R.id.text_technology:
			startBrowser(technologyUrl);
			break;
		case R.id.text_mall:
			startBrowser(mallUrl);
			break;
		case R.id.text_progress:
			startBrowser(progressUrl);
			break;
		case R.id.user_protocol:
			 Intent intent = new Intent();
             intent.setClass(AboutUsUrlActivity.this, WebViewActivity.class);
             intent.putExtra("load_url", getString(R.string.seeworld_user_agreement));
             startActivity(intent);
			break;
		}
	}

	/**
	 * 浏览器跳转
	 * 
	 * @param url
	 */
	private void startBrowser(String url) {
		if (!StringUtils.isEmpty(url)) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			Uri content_url = Uri.parse(url);
			intent.setData(content_url);
			startActivity(intent);
		}
	}

}
