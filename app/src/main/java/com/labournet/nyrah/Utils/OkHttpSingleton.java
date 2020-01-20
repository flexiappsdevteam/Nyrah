package com.labournet.nyrah.Utils;

import okhttp3.OkHttpClient;

public class OkHttpSingleton {

    private static OkHttpSingleton singletonInstance;
    private OkHttpClient client;

    private OkHttpSingleton() {
        client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .build();
    }

    public static OkHttpSingleton getInstance() {
        if (singletonInstance == null) {
            singletonInstance = new OkHttpSingleton();
        }
        return singletonInstance;
    }

    public void closeConnections() {
        client.dispatcher().cancelAll();
    }

    // In case if a unique OkHttpClient instance needed.
    public OkHttpClient getClient() {
        return client;
    }

}
