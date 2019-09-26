package com.example.geolocal.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.geolocal.dao.CoordenadaDao;
import com.example.geolocal.dao.UserDao;
import com.example.geolocal.data.Converters;
import com.example.geolocal.data.model.Coordenada;
import com.example.geolocal.data.model.User;

import java.util.Date;

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
//                    .addCallback(new RoomDatabase.Callback() {
//                        @Override
//                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
//                            super.onCreate(db);
//                            Log.d("geolocal-database", "populating with data...");
//                            new PopulateDbAsync(mInstance).execute();
//                        }
//                    })
                    .build();
        }
        return mInstance;
    }

    public static void populateDb(){
        final UserDao userDao = mInstance.UserDao();
        final CoordenadaDao coordenadaDao = mInstance.CoordenadaDao();
        userDao.deleteAll();
        User user1 = new User("nicolas","nicolas@email.com","12345678");
        User user2 = new User("darklord","jair@email.com","12345678");

        int u1 = (int)userDao.insert(user1);
        Coordenada c1 = new Coordenada(u1,new Date(1569503534000L),11.0085,-74.7986);
        Coordenada c2 = new Coordenada(u1,new Date(1569503956000L),11.0068,-74.7975);
        Coordenada c3 = new Coordenada(u1,new Date(1569504122000L),11.0045,-74.7964);
        Coordenada c4 = new Coordenada(u1,new Date(1569504230000L),11.0040,-74.7975);
        coordenadaDao.insertAll(c1,c2,c3,c4);

        u1 = (int)userDao.insert(user2);
        c1 = new Coordenada(u1,new Date(1569501634000L),11.010115,-74.827546);
        c2 = new Coordenada(u1,new Date(1569502756000L),11.010760,-74.828669);
        c3 = new Coordenada(u1,new Date(1569503922000L),11.010420,-74.826098);
        c4 = new Coordenada(u1,new Date(1569504230000L),11.010250,-74.829812);
        coordenadaDao.insertAll(c1,c2,c3,c4);
    }

    public static void destroyInstance() {
        mInstance = null;
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {
        private final UserDao userDao;
        private final CoordenadaDao coordenadaDao;

        public PopulateDbAsync(AppDatabase instance) {
            userDao = instance.UserDao();
            coordenadaDao = instance.CoordenadaDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //userDao.deleteAll();
            //coordenadaDao.deleteAll();

            User user1 = new User("nicolas","nicolas@email.com","12345678");
            User user2 = new User("darklord","jair@email.com","12345678");

            int u1 = (int)userDao.insert(user1);
            Coordenada c1 = new Coordenada(u1,new Date(1569503534000L),11.0085,-74.7986);
            Coordenada c2 = new Coordenada(u1,new Date(1569503956000L),11.0068,-74.7975);
            Coordenada c3 = new Coordenada(u1,new Date(1569504122000L),11.0045,-74.7964);
            Coordenada c4 = new Coordenada(u1,new Date(1569504230000L),11.0040,-74.7975);
            coordenadaDao.insertAll(c1,c2,c3,c4);

            u1 = (int)userDao.insert(user2);
            c1 = new Coordenada(u1,new Date(1569501634000L),11.010115,-74.827546);
            c2 = new Coordenada(u1,new Date(1569502756000L),11.010760,-74.828669);
            c3 = new Coordenada(u1,new Date(1569503922000L),11.010420,-74.826098);
            c4 = new Coordenada(u1,new Date(1569504230000L),11.010250,-74.829812);
            coordenadaDao.insertAll(c1,c2,c3,c4);

            return null;
        }
    }
}
