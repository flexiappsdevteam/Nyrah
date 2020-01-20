package com.labournet.nyrah.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class NyrahSingleton {
    private static NyrahSingleton mInstance;
    Context context;

    private static final String SHARED_PREF_NAME = "Nyrah_AppData";

    public NyrahSingleton(Context context) {
        this.context = context;
    }

    public static synchronized NyrahSingleton getmInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NyrahSingleton(context);
        }
        return mInstance;
    }

    public boolean checkNetworkConnection() {
        ConnectivityManager connMgr = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void FlashMessage(final String error) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(
                new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context.getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

}
