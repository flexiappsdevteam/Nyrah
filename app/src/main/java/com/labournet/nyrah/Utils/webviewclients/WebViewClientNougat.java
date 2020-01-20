package com.labournet.nyrah.Utils.webviewclients;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import static com.labournet.nyrah.Utils.BaseActivity.LoadingSpinner;

public class WebViewClientNougat extends WebViewClient {

    private Activity activity = null;
    public Dialog progress_spinner;

    public WebViewClientNougat(Activity activity) {
        this.activity = activity;
        progress_spinner = LoadingSpinner(activity);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        final Uri uri = request.getUrl();
        Log.d("UrlLoading", "shouldOverrideUrlLoading");

        if (uri.toString().startsWith("tel:")) {
            activity.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(uri.toString())));
            return true;
        }
        return false;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        progress_spinner.show();

    }


    @Override
    public void onPageCommitVisible(WebView view, String url) {
        super.onPageCommitVisible(view, url);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
    }

    @Override
    public void onPageFinished(WebView view, String url) {

        progress_spinner.hide();
        //animateWebView(view);
        //  view.setVisibility(View.VISIBLE);
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);
    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

        return super.shouldInterceptRequest(view, request);
    }

}
