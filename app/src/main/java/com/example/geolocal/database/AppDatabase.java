package com.example.geolocal.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.geolocal.dao.CoordenadaDao;
import com.example.geolocal.dao.UserDao;
import com.example.geolocal.data.Converters;
import com.example.geolocal.data.Coordenada;
import com.example.geolocal.data.User;

@Database(entities = {User.class, Coordenada.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao UserDao();
    public abstract CoordenadaDao CoordenadaDao();
}
