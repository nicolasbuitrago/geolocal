package com.example.geolocal.receiver;

import android.os.Bundle;

public interface IResultReceiverCaller {
    public void onReceiveResult(Bundle data);
    public void onReceiveError(Exception exception);
}
