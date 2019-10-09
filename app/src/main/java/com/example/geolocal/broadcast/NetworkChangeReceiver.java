package com.example.geolocal.broadcast;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastManager {

    public static String CHANNEL_NETWORK = "android.net.conn.CONNECTIVITY_CHANGE";
    public static String TYPE_NETWORK_ONLINE = "com.geolocal.broadcast.TYPE_NETWORK_ONLINE";
    public static String TYPE_NETWORK_OFFLINE = "com.geolocal.broadcast.TYPE_NETWORK_OFFLINE";

    public NetworkChangeReceiver(Context context, IBroadcastManagerCaller caller) {
        super(context,CHANNEL_NETWORK,caller);
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Toast.makeText(context, "Wifi enabled", Toast.LENGTH_LONG).show();
                caller.MessageReceivedThroughBroadcastManager(CHANNEL_NETWORK,TYPE_NETWORK_ONLINE,"Wifi enabled");
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Toast.makeText(context, "Mobile data enabled", Toast.LENGTH_LONG).show();
                caller.MessageReceivedThroughBroadcastManager(CHANNEL_NETWORK,TYPE_NETWORK_ONLINE,"Mobile data enabled");
            }
        } else {
            Toast.makeText(context, "No internet is available", Toast.LENGTH_LONG).show();
            caller.MessageReceivedThroughBroadcastManager(CHANNEL_NETWORK,TYPE_NETWORK_OFFLINE,"No internet is available");
        }
    }
}
