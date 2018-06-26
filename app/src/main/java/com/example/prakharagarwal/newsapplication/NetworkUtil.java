package com.example.prakharagarwal.newsapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by prakharagarwal on 23/06/18.
 */

public class NetworkUtil {

    public boolean checkNetwork(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        boolean isWiFi=true;
        if(isConnected)
        isWiFi= activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
//            return isWiFi;
        return  isConnected;
    }
}
