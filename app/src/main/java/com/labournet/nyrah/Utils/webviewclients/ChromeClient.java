package com.labournet.nyrah.Utils.webviewclients;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.labournet.nyrah.R;

public class ChromeClient extends WebChromeClient {

    private Activity activity = null;

    public ChromeClient(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View alertTitleView = inflater.inflate(R.layout.custom_alert_title, null);

        AlertDialog dialog = new AlertDialog.Builder(view.getContext(), R.style.MyDialogTheme).
                setIcon(ContextCompat.getDrawable(view.getContext(), R.drawable.alert)).
                setMessage(message).
                setCustomTitle(alertTitleView)
                .create();
        dialog.show();
        return true;
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view1 = inflater.inflate(R.layout.custom_confirm_title, null);

        AlertDialog dialog = new AlertDialog.Builder(view.getContext(), R.style.MyDialogTheme).
                setIcon(ContextCompat.getDrawable(view.getContext(), R.drawable.alert)).
                setCustomTitle(view1).
                setMessage(message).
                setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                }).
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                })
                .create();
        dialog.show();
        return true;
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);

    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        super.onReceivedIcon(view, icon);
    }

    @Override
    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
        super.onReceivedTouchIconUrl(view, url, precomposed);
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        super.onGeolocationPermissionsShowPrompt(origin, callback);
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
        super.onGeolocationPermissionsHidePrompt();
    }

    @Override
    public void onPermissionRequest(PermissionRequest request) {
        super.onPermissionRequest(request);
    }

    @Override
    public void onPermissionRequestCanceled(PermissionRequest request) {
        super.onPermissionRequestCanceled(request);
    }
}
