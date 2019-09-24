package com.example.geolocal.database;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import androidx.room.Room;

import com.example.geolocal.broadcast.BroadcastManager;
import com.example.geolocal.broadcast.IBroadcastManagerCaller;

public class DatabaseManagerService extends IntentService implements IDatabaseManagerCaller, IBroadcastManagerCaller {

    private AppDatabase appDatabase;
    BroadcastManager broadcastManager;
    DatabaseManager databaseManager;
    public static String DATABASE_SERVICE_CHANNEL="com.example.geolocal.DATABASE_SERVICE_CHANNEL";

    public static final String ACTION_CONNECT = "com.example.geolocal.database.action.ACTION_CONNECT";

    public static final String SAVE_USER = "SAVE_USER";
    public static final String GET_USER = "GET_USER";
    public static final String DELETE_USER = "DELETE_USER";

    public DatabaseManagerService() {
        super("DatabaseManagerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent!=null){
            final String action = intent.getAction();
            if (ACTION_CONNECT.equals(action)) {
                initializeDataBase();
                initializeBroadcastManager();
            }
        }
    }

    public void initializeDataBase(){
        try{
            appDatabase= Room.databaseBuilder(this,AppDatabase.class, "geolocal-database").fallbackToDestructiveMigration().build();
            databaseManager = new DatabaseManager(this,appDatabase);
        }catch (Exception error){
            Toast.makeText(this,error.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public void initializeBroadcastManager(){
        try{
            if(broadcastManager==null){
                broadcastManager=new BroadcastManager(
                        getApplicationContext(),
                        DATABASE_SERVICE_CHANNEL,
                        this);
            }
        }catch (Exception error){
            Toast.makeText(this,error.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void errorFromDatabaseManager(Exception error) {
        Toast.makeText(this,"ERROR DATABASE MANAGER",Toast.LENGTH_LONG).show();
    }

    @Override
    public void MessageReceivedThroughBroadcastManager(String channel, String type, String message) {
        try {
            if (databaseManager != null) {
                String[] info = message.split(";");
                if (type.equals(GET_USER)) {
                    databaseManager.getUser(info[0],info[1]);
                    //TODO

                }else if(type.equals(SAVE_USER)){
                    databaseManager.saveUser(info[0],info[1],info[2]);
                }
            }
        }catch (Exception error){

        }
    }

    @Override
    public void ErrorAtBroadcastManager(Exception error) {
        Toast.makeText(this,"ERROR DATABASE BROADCAST",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        if(broadcastManager!=null){
            broadcastManager.unRegister();
        }
        databaseManager=null;
        broadcastManager=null;
        super.onDestroy();
    }
}
