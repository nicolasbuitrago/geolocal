package com.example.geolocal.network;

public interface IWebServiceCaller {

    void receivedTroughtWebService(String message);
    void errorWebService(Exception error);
}
