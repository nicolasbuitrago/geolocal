package com.example.geolocal.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class User implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    @NonNull
    public int userId;

    @ColumnInfo(name = "user_name")
    @NonNull
    public String userName;

    @ColumnInfo(name = "email")
    @NonNull
    public String userEmail;

    @ColumnInfo(name = "password")
    @NonNull
    public String password;

    public User(@NonNull String userName, @NonNull String userEmail, @NonNull String password) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.password = password;
    }
}
