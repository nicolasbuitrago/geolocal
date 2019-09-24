package com.example.geolocal.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
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
}
