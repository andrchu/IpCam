package net.kaicong.ipcam.view;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.kaicong.ipcam.R;
import net.kaicong.ipcam.bean.DownloadManagerPro;
import net.kaicong.ipcam.utils.LogUtil;
import net.kaicong.ipcam.utils.PreferenceUtils;
import net.kaicong.ipcam.utils.ToastUtil;
import net.kaicong.ipcam.utils.ToolUtil;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by LingYan on 15/6/3.
 */
public class DownloadDialog extends Dialog {

    static final DecimalFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("0.##");
    public static final String DOWNLOAD_FOLDER_NAME = "UpdateRoot";
    public static final String DOWNLOAD_FILE_NAME = ToolUtil.getNowTimeStrZip();

    public static final int MB_2_BYTE = 1024 * 1024;
    public static final int KB_2_BYTE = 1024;
    public static final String KEY_NAME_DOWNLOAD_ID = "downloadId";

    private Context context;
    private ProgressBar progressBar;
    private TextView progressBarPercent;
    private TextView progressBarSize;

    private DownloadManager downloadManager;
    private DownloadManagerPro downloadManagerPro;
    private long downloadId = 0;

    private MyHandler handler;
    private DownloadChangeObserver downloadObserver;
    private CompleteReceiver completeReceiver;

    private String downloadUrl;
    private IDownloadComplete iDownloadComplete;

    public DownloadDialog(Context context, int style, String downloadUrl, IDownloadComplete iDownloadComplete) {
        super(context, style);
        this.context = context;
        this.downloadUrl = downloadUrl;
        this.iDownloadComplete = iDownloadComplete;
        init();
    }

    private void init() {
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_download, null);
        progressBar = (ProgressBar) contentView.findViewById(R.id.progress_bar);
        progressBarPercent = (TextView) contentView.findViewById(R.id.progress_bar_percent);
        progressBarSize = (TextView) contentView.findViewById(R.id.progress_bar_size);
        setContentView(contentView);

        handler = new MyHandler();
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManagerPro = new DownloadManagerPro(downloadManager);
        initData();

        downloadObserver = new DownloadChangeObserver();
        completeReceiver = new CompleteReceiver();
        /** register download success broadcast **/
        context.registerReceiver(completeReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        /** observer download change **/
        context.getContentResolver().registerContentObserver(DownloadManagerPro.CONTENT_URI, true, downloadObserver);
        updateView();

    }

    /**
     * MyHandler
     *
     * @author Trinea 2012-12-18
     */
    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    int status = (Integer) msg.obj;
                    if (isDownloading(status)) {
                        progressBar.setMax(0);
                        progressBar.setProgress(0);
                        if (msg.arg2 < 0) {
                            progressBar.setIndeterminate(true);
                            progressBarPercent.setText("0%");
                            progressBarSize.setText("0M/0M");
                        } else {
                            progressBar.setIndeterminate(false);
                            progressBar.setMax(msg.arg2);
                            progressBar.setProgress(msg.arg1);
                            progressBarPercent.setText(getNotiPercent(msg.arg1, msg.arg2));
                            progressBarSize.setText(getAppSize(msg.arg1) + "/" + getAppSize(msg.arg2));
                        }
                    } else {
                        if (status == DownloadManager.STATUS_FAILED) {
//                            ToastUtil.showToast(context, "下载失败");
                        } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
//                            ToastUtil.showToast(context, "下载完成");
                        } else {
//                            ToastUtil.showToast(context, "正在下载");
                        }
                    }
                    break;
            }
        }
    }

    class DownloadChangeObserver extends ContentObserver {

        public DownloadChangeObserver() {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            updateView();
        }

    }

    public void updateView() {
        int[] bytesAndStatus = downloadManagerPro.getBytesAndStatus(downloadId);
        handler.sendMessage(handler.obtainMessage(0, bytesAndStatus[0], bytesAndStatus[1], bytesAndStatus[2]));
    }

    class CompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            /**
             * get the id of download which have download success, if the id is my id and it's status is successful,
             * then install it
             **/
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (completeDownloadId == downloadId) {
//                initData();
                updateView();
                // if download successful, install apk
                if (downloadManagerPro.getStatusById(downloadId) == DownloadManager.STATUS_SUCCESSFUL) {
                    String downloadFilePath = new StringBuilder(Environment.getExternalStorageDirectory().getAbsolutePath())
                            .append(File.separator).append(DOWNLOAD_FOLDER_NAME).append(File.separator)
                            .append(DOWNLOAD_FILE_NAME).toString();

                    dismiss();
                    if (iDownloadComplete != null) {
                        iDownloadComplete.downloadComplete(downloadFilePath);
                    }

                }
            }
        }
    }

    /**
     * @param size
     * @return
     */
    public static CharSequence getAppSize(long size) {
        if (size <= 0) {
            return "0M";
        }

        if (size >= MB_2_BYTE) {
            return new StringBuilder(16).append(DOUBLE_DECIMAL_FORMAT.format((double) size / MB_2_BYTE)).append("M");
        } else if (size >= KB_2_BYTE) {
            return new StringBuilder(16).append(DOUBLE_DECIMAL_FORMAT.format((double) size / KB_2_BYTE)).append("K");
        } else {
            return size + "B";
        }
    }

    public static String getNotiPercent(long progress, long max) {
        int rate = 0;
        if (progress <= 0 || max <= 0) {
            rate = 0;
        } else if (progress > max) {
            rate = 100;
        } else {
            rate = (int) ((double) progress / max * 100);
        }
        return new StringBuilder(16).append(rate).append("%").toString();
    }

    public static boolean isDownloading(int downloadManagerStatus) {
        return downloadManagerStatus == DownloadManager.STATUS_RUNNING
                || downloadManagerStatus == DownloadManager.STATUS_PAUSED
                || downloadManagerStatus == DownloadManager.STATUS_PENDING;
    }

    private void initData() {
        /**
         * get download id from preferences.<br/>
         * if download id bigger than 0, means it has been downloaded, then query status and show right text;
         */
        downloadId = PreferenceUtils.loadLongPreference(context, KEY_NAME_DOWNLOAD_ID);
        updateView();

        File folder = Environment.getExternalStoragePublicDirectory(DOWNLOAD_FOLDER_NAME);
        if (!folder.exists() || !folder.isDirectory()) {
            folder.mkdirs();
        }

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        request.setDestinationInExternalPublicDir(DOWNLOAD_FOLDER_NAME, DOWNLOAD_FILE_NAME);
//        request.setTitle("下载通知");
//        request.setDescription("meilishuo desc");
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(false);
        // request.allowScanningByMediaScanner();
        // request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        // request.setShowRunningNotification(false);
        // request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setMimeType("application/cn.trinea.download.file");
        downloadId = downloadManager.enqueue(request);
        /** save download id to preferences **/
        PreferenceUtils.savePreference(context, KEY_NAME_DOWNLOAD_ID, downloadId);
        updateView();

    }

    @Override
    public void dismiss() {
        super.dismiss();
        context.getContentResolver().unregisterContentObserver(downloadObserver);
        context.unregisterReceiver(completeReceiver);
    }

    /**
     * 取消下载
     */
    public void cancelDownload() {
        downloadManager.remove(downloadId);
        updateView();
    }

    public interface IDownloadComplete {

        void downloadComplete(String zipPath);

    }


}
