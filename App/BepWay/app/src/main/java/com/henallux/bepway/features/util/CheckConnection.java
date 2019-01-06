package com.henallux.bepway.features.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;

public final class CheckConnection {

    public static Boolean haveConnection(Context context){
        return (isWifiConnected(context) || is3GConnected(context));
    }

    public static boolean isWifiConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        Boolean hasWifi =  (networkCapabilities != null && networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
        return  hasWifi;
    }

    public static boolean is3GConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        Boolean has3G =  (networkCapabilities != null && networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        return  has3G;
    }
}
