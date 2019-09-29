package com.example.geolocal.receiver;

import android.os.Bundle;

public interface IResultReceiverCaller {

    String EXTRA_TYPE = "com.example.geolocal.receiver.type.EXTRA_TYPE";
    String DATABASE = "com.example.geolocal.receiver.type.DATABASE";
    String WEBSERVICE = "com.example.geolocal.receiver.type.WEBSERVICE";

    int RESULT_CODE_OK = 200;
    int RESULT_CODE_ERROR = 404;

    String PARAM_EXCEPTION = "com.example.geolocal.receiver.EXCEPTION";
    String PARAM_RESULT = "com.example.geolocal.receiver.PARAM_RESULT";
    String TYPE_ACTION_ANSWER = "com.example.geolocal.receiver.TYPE_ACTION_ANSWER";
    String ACTION_ANSWER = "com.example.geolocal.receiver.ACTION_ANSWER";
    String TYPE_COORDENADA = "com.example.geolocal.receiver.TYPE_COORDENADA";
    String TYPE_COORDENADAS = "com.example.geolocal.receiver.TYPE_COORDENADAS";
    String TYPE_MESSAGE = "com.example.geolocal.receiver.TYPE_MESSAGE";
    String TYPE_MESSAGES = "com.example.geolocal.receiver.TYPE_MESSAGES";
    String TYPE_USER = "com.example.geolocal.receiver.TYPE_USER";
    String TYPE_LIST = "com.example.geolocal.receiver.TYPE_LIST";
    String TYPE_BOOLEAN = "com.example.geolocal.receiver.BOOLEAN";
    String PUSH_OK = "com.example.geolocal.receiver.PUSH_OK";

    String EXTRA_EMAIL = "com.example.geolocal.receiver.EXTRA_EMAIL";
    String EXTRA_PASSWORD = "com.example.geolocal.receiver.EXTRA_PASSWORD";

    void onReceiveResult(Bundle data);
    void onReceiveError(Exception exception);
}
