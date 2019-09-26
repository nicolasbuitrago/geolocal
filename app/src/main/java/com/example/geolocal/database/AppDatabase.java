package com.example.geolocal.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.geolocal.dao.CoordenadaDao;
import com.example.geolocal.dao.UserDao;
import com.example.geolocal.data.Converters;
import com.example.geolocal.data.model.Coordenada;
import com.example.geolocal.data.model.User;

@Database(entities = {User.class, Coordenada.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase mInstance;
    public abstract UserDao UserDao();
    public abstract CoordenadaDao CoordenadaDao();

    public synchronized static AppDatabase getDatabaseInstance(Context context) {
        if (mInstance == null) {
            mInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "geolocal-database")
                    //.allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return mInstance;
    }

    public static void destroyInstance() {
        mInstance = null;
    }
}
