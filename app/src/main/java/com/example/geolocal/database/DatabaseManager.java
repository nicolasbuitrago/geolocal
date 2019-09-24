package com.example.geolocal.database;

import com.example.geolocal.data.User;

public class DatabaseManager {

    AppDatabase appDatabase;
    IDatabaseManagerCaller caller;

    public DatabaseManager(IDatabaseManagerCaller caller, AppDatabase appDatabase) {
        this.caller = caller;
        this.appDatabase = appDatabase;
    }

    public User getUser(String email, String password){
        User user = appDatabase.UserDao().getUserbyEmail(email);
        if(user!=null && user.password.equals(password)){
            return user;
        }
        return null;
    }


    public void saveUser(String userName, String email, String password){
        User user = new User();
        user.userName = userName;
        user.userEmail = email;
        user.password = password;
        appDatabase.UserDao().insertAll(user);
    }
}
