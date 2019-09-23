package com.example.geolocal.gps;

import android.location.Location;

public interface IGPSManagerCaller {

    void needPermissions();
    void locationHasBeenReceived(Location location);
    void gpsErrorHasBeenThrown(Exception error);
}