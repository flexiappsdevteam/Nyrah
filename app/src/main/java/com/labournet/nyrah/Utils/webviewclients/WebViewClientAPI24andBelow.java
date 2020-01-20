package com.labournet.nyrah.Utils.webviewclients;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewClientAPI24andBelow extends WebViewClient {
    private Activity activity = null;

    public WebViewClientAPI24andBelow(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Log.d("UrlLoading", "shouldOverrideUrlLoading");

        if (url.startsWith("tel:")) {
            final Uri uri = Uri.parse(url);
            Log.d("UrlLoading", "Clicked on tel: ");
            final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(activity);
            alertDialogBuilder.setMessage(url)
                    .setPositiveButton("Call", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.startActivity(new Intent(Intent.ACTION_DIAL, uri));
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

            alertDialogBuilder.create();
            alertDialogBuilder.show();
            return true;
        }
        return false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
    }
}
