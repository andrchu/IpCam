package net.kaicong.ipcam.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.kaicong.ipcam.BaseActivity;
import net.kaicong.ipcam.KCApplication;
import net.kaicong.ipcam.R;

import net.kaicong.ipcam.MainActivity;
import net.kaicong.ipcam.bean.CameraConstants;
import net.kaicong.ipcam.bean.ErrorResponse;
import net.kaicong.ipcam.bean.UrlResources;
import net.kaicong.ipcam.utils.ImageUtils;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.ipcam.utils.ToolUtil;
import net.kaicong.ipcam.view.UploadPhotoSelectionDialog;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.soundcloud.android.crop.Crop;

import net.kaicong.utility.ApiClientUtility;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by LingYan on 2014/11/19 0019.
 */
public class UserAccountInfoActivity extends BaseActivity {

    //从拍照来
    private static final int RESULT_LOAD_IMAGE_FROM_CAMERA = 0x10;
    //从相册来
    private static final int PHOTO_PICKED_WITH_DATA = 0x11;

    //头像改变
    private int MESS_INDEX = 5;

    private File mPhotoFile;
    private RelativeLayout imgLayout;
    private LinearLayout changePwd;
    private ImageView userImageView;
    private UploadPhotoSelectionDialog uploadPhotoSelectionDialog;
    private AsyncHttpClient asyncHttpClient;
    //云豆数
    private TextView tevYDSum;
    private LinearLayout rewardInfo;

    //邮箱或手机号
    private TextView tev_account;

    //退出
    private Button btn_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initTitle(getString(R.string.main_tab_more));
        showBackButton();
        imgLayout = (RelativeLayout) findViewById(R.id.img_layout);
        imgLayout.setOnClickListener(this);
        changePwd = (LinearLayout) findViewById(R.id.lel_changePwd);
        changePwd.setOnClickListener(this);
        //yundou
        tevYDSum = (TextView) findViewById(R.id.tev_yundouSum);
        tevYDSum.setText(UserAccount.getVirtualcurrency() + "");

        userImageView = (ImageView) findViewById(R.id.user_img);

        tev_account = (TextView) findViewById(R.id.tev_mailAccount);
        tev_account.setText(UserAccount.getUserName());

        btn_out = (Button) findViewById(R.id.btn_loginOut);
        btn_out.setOnClickListener(this);

        String photoName = "crop_temp.jpg";
        mPhotoFile = new File(KCApplication.getAppCacheDir(), photoName);
        if (!mPhotoFile.exists()) {
            try {
                mPhotoFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        asyncHttpClient = new AsyncHttpClient();

        uploadPhotoSelectionDialog = new UploadPhotoSelectionDialog(this, R.style.ZhiYunVideoSettingDialog, new UploadPhotoSelectionDialog.OnSelectionSelectListener() {
            @Override
            public void onSelectionSelect(int position) {
                if (position == 1) {
                    //拍照获取
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
                    startActivityForResult(intent, RESULT_LOAD_IMAGE_FROM_CAMERA);
                } else if (position == 2) {
                    Crop.pickImage(UserAccountInfoActivity.this);
                }
            }
        });

        imageLoader.displayImage(UserAccount.getUserHeadUrl(), userImageView, ImageUtils.getRoundedDisplayOptions(R.drawable.common_head_bg, null));


        rewardInfo = (LinearLayout) findViewById(R.id.lel_reward_info);
        rewardInfo.setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == Crop.REQUEST_PICK) {
            //选择照片成功
            beginCrop(data.getData());
        }
        if (requestCode == RESULT_LOAD_IMAGE_FROM_CAMERA) {
            //拍照成功
            beginCrop(Uri.fromFile(mPhotoFile));
        }
        if (requestCode == Crop.REQUEST_CROP) {
            //裁剪成功
            handleCrop(resultCode, data);
        }
    }

    private void beginCrop(Uri source) {
        Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
        new Crop(source).output(outputUri).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            try {
                InputStream image_stream = getContentResolver().openInputStream(Crop.getOutput(result));
                Bitmap bitmap = BitmapFactory.decodeStream(image_stream);
                //制作缩略图
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, 150, 150);
                userImageView.setImageBitmap(ImageUtils.toRoundBitmap(bitmap));
                FileOutputStream fos = new FileOutputStream(new File(KCApplication.getAppCacheDir() + File.separator + "temp_upload"));
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                //上传图像
                Map<String, String> map = new HashMap<>();
                map.put(CameraConstants.USER_ID, UserAccount.getUserID() + "");
                RequestParams params = new RequestParams(ApiClientUtility.getParams(map));
                params.put("img", new File(KCApplication.getAppCacheDir() + File.separator + "temp_upload"));
                asyncHttpClient.post(UrlResources.URL_UPLOAD_USER_HEAD, params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        showProgressDialog();
                    }

                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        removeProgressDialog();
                        String result = new String(bytes);
                        if (result.equals("Success")) {
                            //上传成功
                            makeToast(getString(R.string.upload_head_success));
                            //更新图片链接
                            getUserHeadUrl();
                        }

                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        removeProgressDialog();
                        LogUtil.d("chu", throwable.toString());
                        makeToast(getString(R.string.upload_head_failed));
                    }
                });


            } catch (Exception e) {

            }
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * 更新图片
     */
    private void getUserHeadUrl() {
        Map<String, String> map = new HashMap<>();
        map.put(CameraConstants.USER_ID, UserAccount.getUserID() + "");
        RequestParams params = new RequestParams(ApiClientUtility.getParams(map));
        asyncHttpClient.post(UrlResources.URL_GET_USER_HEAD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String result = new String(bytes);
                LogUtil.d("chu", "head path" + result);
                ErrorResponse errorResponse = new ErrorResponse();
                if (!errorResponse.IsError(result)) {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        if (jsonObject.has("Head_Path")) {
                            String headPath = jsonObject.optString("Head_Path");
                            KCApplication.userHeadUrl = headPath;
                            UserAccount.saveUserHeadUrl(headPath);
                            MESS_INDEX = 233;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }

        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.img_layout:
                uploadPhotoSelectionDialog.show();
                WindowManager.LayoutParams params = uploadPhotoSelectionDialog.getWindow().getAttributes();
                params.width = displayMetrics.widthPixels * 5 / 6;
                uploadPhotoSelectionDialog.getWindow().setAttributes(params);
                break;
            case R.id.lel_changePwd:
                Intent intent = new Intent();
                intent.setClass(this, ChangePasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.lel_reward_info:
                Intent intentInfo = new Intent();
                intentInfo.setClass(this, RewardRecordActivity.class);
                startActivity(intentInfo);
                break;
            case R.id.btn_loginOut:
                //退出登录
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.about_more_login_out_text))
                        .setPositiveButton(getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, final int i) {
                                dialogInterface.dismiss();
                                showProgressDialog();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        UserAccount.saveUserID(0);
                                        UserAccount.saveLoginSate(false);
                                        //清空alias
                                        setOutalias();
                                        removeProgressDialog();
                                        setResult(RESULT_OK);
                                        UserAccountInfoActivity.this.finish();

                                    }
                                }, 1000);
                            }
                        }).setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
                break;
        }
    }

    private String getMyString(InputStream stream) {
        StringBuffer sb = new StringBuffer();
        String readLine;
        BufferedReader responseReader;
        try {
            //处理响应流，必须与服务器响应流输出的编码一致
            responseReader = new BufferedReader(new InputStreamReader(stream));
            while ((readLine = responseReader.readLine()) != null) {
                sb.append(readLine).append("\n");
            }
            responseReader.close();
        } catch (IOException e) {
            return "";
        }

        return sb.toString();
    }

    private class myAscynTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                //上传图像
                URL url = new URL(params[0]);
                HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                httpCon.setDoOutput(true);//使用 URL 连接进行输出
                httpCon.setDoInput(true);//使用 URL 连接进行输入
                httpCon.setUseCaches(false);//忽略缓存
                httpCon.setRequestMethod("POST");//设置URL请求方法

                httpCon.setRequestProperty(CameraConstants.USER_ID, UserAccount.getUserID() + "");
                //建立输出流，并写入数据
                OutputStream outputStream = httpCon.getOutputStream();
                //文件变成字节数据
                byte[] requestStringBytes = ToolUtil.getBytesFromFile(new File(KCApplication.getAppCacheDir() + File.separator + "temp_upload"));
                outputStream.write(requestStringBytes);
                outputStream.close();

                //获得响应状态
                int responseCode = httpCon.getResponseCode();
                if (HttpURLConnection.HTTP_OK == responseCode) {//连接成功
                    return getMyString(httpCon.getInputStream());
                } else {
                    return "";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            removeProgressDialog();
            if (s.equals("Success")) {
                //上传成功
                makeToast(getString(R.string.upload_head_success));

                //更新图片链接
                getUserHeadUrl();
            } else {
                makeToast(getString(R.string.upload_head_failed));
            }
        }

    }

    private void setOutalias() {
        JPushInterface.setAlias(this.getApplicationContext(), "0", new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> strings) {
                LogUtil.d("alias:", s);
            }
        });
    }

    public void doBackButtonAction() {
        setResult(MESS_INDEX);
        super.doBackButtonAction();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(MESS_INDEX);
            finish();
        }
        return true;
    }
}
