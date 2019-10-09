package com.example.geolocal.data.model;

public class UserConnected {

    private User user;
    private Coordenada location;
    private boolean isOnline;

    public UserConnected(User user, Coordenada location) {
        this.user = user;
        this.location = location;
        this.isOnline = false;
    }

    public User getUser() {
        return user;
    }

    public Coordenada getLocation() {
        return location;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setLocation(Coordenada location) {
        this.location = location;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
