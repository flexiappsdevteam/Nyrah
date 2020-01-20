package com.labournet.nyrah.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String status = NetworkUtils.getConnectivityStatus(context);
        if (status.isEmpty()) {
            status = "No internet connection";
        } else if (status.equals("Mobile data enabled") || status.equals("Wifi enabled")) {
            status = "Internet connection is available";
        }
        Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            //should check for null because in airplane mode it will be null
            return networkInfo != null && networkInfo.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
