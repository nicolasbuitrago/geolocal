package com.example.geolocal.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.geolocal.dao.UserDao;
import com.example.geolocal.data.User;

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao UserDao();
}
