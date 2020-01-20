package com.labournet.nyrah.Utils.webviewclients;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Toast;

import static android.content.Context.DOWNLOAD_SERVICE;

public class WebViewDownloadListener implements DownloadListener {

    private Activity activity = null;

    public WebViewDownloadListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

        Toast.makeText(activity, "Downloading", Toast.LENGTH_LONG).show();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);

        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);


        //------------------------COOKIE!!------------------------
        String cookies = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie", cookies);


        try {
            //Check internet connection is available
            if (checkNetworkConnection()) {
                final String fileNamee = URLUtil.guessFileName(url, null, MimeTypeMap.getFileExtensionFromUrl(url));

                if (fileNamee.contains(".pdf") || fileNamee.contains(".doc") || fileNamee.contains(".docx") || fileNamee.contains(".txt") || fileName.contains(".xls")
                        || fileNamee.contains(".xlsx") || fileNamee.contains(".ppt") || fileNamee.contains(".pptx") || fileNamee.contains(".ppsx")
                        || fileNamee.contains(".pps") || fileNamee.contains(".bmp") || fileNamee.contains(".jpg") || fileNamee.contains(".jpeg")
                        || fileNamee.contains(".png") || fileNamee.contains(".gif") || fileNamee.contains(".svg") || fileNamee.contains(".tif")
                ) {
                    //do nothing.
                }

            } else {
                //Do nothing.
            }
        } catch (Exception e) {
            Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
        }


        DownloadManager dManager = (DownloadManager) activity.getSystemService(DOWNLOAD_SERVICE);
        dManager.enqueue(request);
    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
