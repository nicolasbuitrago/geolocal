package com.example.geolocal.receiver;

import android.os.Bundle;

public interface IResultReceiverCaller {

    String EXTRA_TYPE = "com.example.geolocal.receiver.type.EXTRA_TYPE";
    String DATABASE = "com.example.geolocal.receiver.type.DATABASE";
    String WEBSERVICE = "com.example.geolocal.receiver.type.WEBSERVICE";

    void onReceiveResult(Bundle data);
    void onReceiveError(Exception exception);
}
