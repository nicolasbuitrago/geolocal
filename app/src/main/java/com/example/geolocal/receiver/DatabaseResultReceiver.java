package com.example.geolocal.receiver;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class DatabaseResultReceiver extends ResultReceiver {
    public static final int RESULT_CODE_OK = 200;
    public static final int RESULT_CODE_ERROR = 404;
    public static final String PARAM_EXCEPTION = "com.example.geolocal.receiver.exception";
    public static final String PARAM_RESULT = "com.example.geolocal.receiver.result";
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
