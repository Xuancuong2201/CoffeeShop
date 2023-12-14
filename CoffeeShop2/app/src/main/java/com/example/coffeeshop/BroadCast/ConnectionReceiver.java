package com.example.coffeeshop.BroadCast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

    }
    public static  boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                WifiApp.getInstance().getApplicationContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

}
