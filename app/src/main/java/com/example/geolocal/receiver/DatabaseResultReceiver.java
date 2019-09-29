package com.example.geolocal.receiver;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class DatabaseResultReceiver extends ResultReceiver {
    public static final int RESULT_CODE_OK = 200;
    public static final int RESULT_CODE_ERROR = 404;
    public static final String PARAM_EXCEPTION = "com.example.geolocal.receiver.EXCEPTION";
    public static final String TYPE_ACTION_ANSWER = "com.example.geolocal.receiver.TYPE_ACTION_ANSWER";
    public static final String ACTION_ANSWER = "com.example.geolocal.receiver.ACTION_ANSWER";
    public static final String TYPE_COORDENADA = "com.example.geolocal.receiver.TYPE_COORDENADA";
    public static final String TYPE_COORDENADAS = "com.example.geolocal.receiver.TYPE_COORDENADAS";
    public static final String TYPE_USER = "com.example.geolocal.receiver.TYPE_USER";
    public static final String TYPE_BOOLEAN = "com.example.geolocal.receiver.TYPE_USER";

    private IResultReceiverCaller caller;

    public DatabaseResultReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(IResultReceiverCaller receiver) {
        caller = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        if (caller != null) {
            if(resultCode == RESULT_CODE_OK){
                caller.onReceiveResult(resultData);
            } else {
                caller.onReceiveError((Exception) resultData.getSerializable(PARAM_EXCEPTION));

            }
        }
    }

}
