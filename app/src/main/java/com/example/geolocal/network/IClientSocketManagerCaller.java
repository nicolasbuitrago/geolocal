package com.example.geolocal.network;

public interface IClientSocketManagerCaller {
    void MessageReceived(String message);
    void ErrorFromSocketManager(Exception error);
}
