package com.example.geolocal.database;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.example.geolocal.data.model.Coordenada;
import com.example.geolocal.data.model.Message;
import com.example.geolocal.data.model.User;
import com.example.geolocal.receiver.DatabaseResultReceiver;
import com.example.geolocal.receiver.IResultReceiverCaller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * helper methods.
 */
public class DatabaseIntentService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_POPULATE = "com.example.geolocal.database.action.POPULATE";
    private static final String ACTION_SAVE_USER = "com.example.geolocal.database.action.SAVE_USER";
    private static final String ACTION_GET_USER = "com.example.geolocal.database.action.GET_USER";
    private static final String ACTION_GET_USER_FOR_LOGIN = "com.example.geolocal.database.action.GET_USER_FOR_LOGIN";
    private static final String ACTION_GET_COORDENADAS = "com.example.geolocal.database.action.GET_COORDENADAS";
    private static final String ACTION_SAVE_COORDENADA = "com.example.geolocal.database.action.SAVE_COORDENADA";
    private static final String ACTION_SAVE_COORDENADAS = "com.example.geolocal.database.action.SAVE_COORDENADAS";
    private static final String ACTION_GET_MESSAGES = "com.example.geolocal.database.action.GET_MESSAGES";
    private static final String ACTION_GET_MESSAGE = "com.example.geolocal.database.action.GET_MESSAGE";
    private static final String ACTION_SAVE_MESSAGES = "com.example.geolocal.database.action.SAVE_MESSAGES";

    private static final String EXTRA_RECEIVER = "com.example.geolocal.database.extra.RECEIVER";
    private static final String EXTRA_USER = "com.example.geolocal.database.extra.USER";
    private static final String EXTRA_USER_ID = "com.example.geolocal.database.extra.USER_ID";
    private static final String EXTRA_USER_EMAIL = "com.example.geolocal.database.extra.USER_EMAIL";
    private static final String EXTRA_USER_PASSWORD = "com.example.geolocal.database.extra.USER_PASSWORD";
    private static final String EXTRA_USER_USERNAME = "com.example.geolocal.database.extra.USER_USERNAME";
    private static final String EXTRA_COORDENADA = "com.example.geolocal.database.extra.COORDENADA";
    private static final String EXTRA_COORDENADAS = "com.example.geolocal.database.extra.COORDENADAS";
    private static final String EXTRA_DATE_FROM = "com.example.geolocal.database.extra.DATE_FROM";
    private static final String EXTRA_DATE_TO = "com.example.geolocal.database.extra.DATE_TO";
    private static final String EXTRA_MESSAGE = "com.example.geolocal.database.extra.MESSAGE";
    private static final String EXTRA_MESSAGES = "com.example.geolocal.database.extra.MESSAGES";
    private static final String EXTRA_DATE = "com.example.geolocal.database.extra.DATE";

    private AppDatabase appDatabase;

    public DatabaseIntentService() {
        super("DatabaseIntentService");
    }

    public static void startActionPopulate(Context context) {
        Intent intent = new Intent(context, DatabaseIntentService.class);
        intent.setAction(ACTION_POPULATE);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionSaveUser(Context context, IResultReceiverCaller caller, User user) {
        DatabaseResultReceiver receiver = new DatabaseResultReceiver(new Handler());
        receiver.setReceiver(caller);
        Intent intent = new Intent(context, DatabaseIntentService.class);
        intent.setAction(ACTION_SAVE_USER);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        intent.putExtra(EXTRA_USER, user);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action get users with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionGetUser(Context context, IResultReceiverCaller caller, String email) {
        DatabaseResultReceiver receiver = new DatabaseResultReceiver(new Handler());
        receiver.setReceiver(caller);
        Intent intent = new Intent(context, DatabaseIntentService.class);
        intent.setAction(ACTION_GET_USER);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        intent.putExtra(EXTRA_USER_EMAIL, email);
        context.startService(intent);
    }

    public static void startActionGetUserForLogin(Context context, IResultReceiverCaller caller, String email, String password) {
        DatabaseResultReceiver receiver = new DatabaseResultReceiver(new Handler());
        receiver.setReceiver(caller);
        Intent intent = new Intent(context, DatabaseIntentService.class);
        intent.setAction(ACTION_GET_USER_FOR_LOGIN);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        intent.putExtra(EXTRA_USER_EMAIL, email);
        intent.putExtra(EXTRA_USER_PASSWORD, password);
        context.startService(intent);
    }

    /**
     *
     *
     */
    public static void startActionGetCoordenadas(Context context, IResultReceiverCaller caller, int userId, Date from, Date to) {
        DatabaseResultReceiver receiver = new DatabaseResultReceiver(new Handler());
        receiver.setReceiver(caller);
        Intent intent = new Intent(context, DatabaseIntentService.class);
        intent.setAction(ACTION_GET_COORDENADAS);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        intent.putExtra(EXTRA_USER_ID, userId);
        intent.putExtra(EXTRA_DATE_FROM, from);
        intent.putExtra(EXTRA_DATE_TO, to);
        context.startService(intent);
    }

    public static void startActionSaveCoordenada(Context context, IResultReceiverCaller caller, Coordenada coordenada) {
        DatabaseResultReceiver receiver = new DatabaseResultReceiver(new Handler());
        receiver.setReceiver(caller);
        Intent intent = new Intent(context, DatabaseIntentService.class);
        intent.setAction(ACTION_SAVE_COORDENADA);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        intent.putExtra(EXTRA_COORDENADA, coordenada);
        context.startService(intent);
    }

    public static void startActionSaveCoordenadas(Context context, IResultReceiverCaller caller, ArrayList<Coordenada> coordenadas) {
        DatabaseResultReceiver receiver = new DatabaseResultReceiver(new Handler());
        receiver.setReceiver(caller);
        Intent intent = new Intent(context, DatabaseIntentService.class);
        intent.setAction(ACTION_SAVE_COORDENADAS);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        intent.putExtra(EXTRA_COORDENADAS, coordenadas);
        context.startService(intent);
    }

    public static void startActionGetMessages(Context context, IResultReceiverCaller caller) {
        DatabaseResultReceiver receiver = new DatabaseResultReceiver(new Handler());
        receiver.setReceiver(caller);
        Intent intent = new Intent(context, DatabaseIntentService.class);
        intent.setAction(ACTION_GET_MESSAGES);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        context.startService(intent);
    }

    public static void startActionSaveMessages(Context context, IResultReceiverCaller caller, ArrayList<Message> messages) {
        DatabaseResultReceiver receiver = new DatabaseResultReceiver(new Handler());
        receiver.setReceiver(caller);
        Intent intent = new Intent(context, DatabaseIntentService.class);
        intent.setAction(ACTION_SAVE_MESSAGES);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        intent.putExtra(EXTRA_MESSAGES, messages);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            appDatabase = AppDatabase.getDatabaseInstance(this);
            final ResultReceiver receiver =  intent.getParcelableExtra(EXTRA_RECEIVER);
            final String action = intent.getAction();
            Bundle bundle = new Bundle();
            bundle.putString(IResultReceiverCaller.EXTRA_TYPE,IResultReceiverCaller.DATABASE);
            if (ACTION_POPULATE.equals(action)) {
                handleActionPopulate();
            } else if (ACTION_SAVE_USER.equals(action)) {
                final User user = (User) intent.getSerializableExtra(EXTRA_USER);
                handleActionSaveUser(receiver, user, bundle);
            } else if (ACTION_GET_USER.equals(action)) {
                final String email = intent.getStringExtra(EXTRA_USER_EMAIL);
                handleActionGetUser(receiver, email, bundle);
            } else if (ACTION_GET_USER_FOR_LOGIN.equals(action)) {
                final String email = intent.getStringExtra(EXTRA_USER_EMAIL);
                final String password = intent.getStringExtra(EXTRA_USER_PASSWORD);
                handleActionGetUserForLogin(receiver, email, password, bundle);
            } else if(ACTION_GET_COORDENADAS.equals(action)){
                final int userId = intent.getIntExtra(EXTRA_USER_ID,-1);
                final Date from = (Date) intent.getSerializableExtra(EXTRA_DATE_FROM);
                final Date to =(Date) intent.getSerializableExtra(EXTRA_DATE_TO);
                handleActionGetCoordenadas(receiver,userId,from,to,bundle);
            }else if(ACTION_SAVE_COORDENADA.equals(action)){
                final Coordenada coordenada = intent.getParcelableExtra(EXTRA_COORDENADA);
                handleActionSaveCoordenada(receiver, coordenada,bundle);
            }else if(ACTION_SAVE_COORDENADAS.equals(action)){
                final ArrayList<Coordenada> coordenadas = intent.getParcelableArrayListExtra(EXTRA_COORDENADAS);
                handleActionSaveCoordenadas(receiver, coordenadas,bundle);
            }else if(ACTION_GET_MESSAGES.equals(action)){
                handleActionGetMessages(receiver,bundle);
            }else if(ACTION_SAVE_MESSAGES.equals(action)){
                final ArrayList<Message> messages = intent.getParcelableArrayListExtra(EXTRA_MESSAGES);
                handleActionSaveMessages(receiver, messages,bundle);
            }
        }
    }

    private void handleActionPopulate() {
        appDatabase.populateDb();
    }

    /**
     * Handle action Save user in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSaveUser(ResultReceiver receiver, User user, Bundle bundle) {
        appDatabase.UserDao().insertAll(user);
        bundle.putString(IResultReceiverCaller.TYPE_ACTION_ANSWER, IResultReceiverCaller.TYPE_USER);
        bundle.putSerializable(IResultReceiverCaller.ACTION_ANSWER,user);
        receiver.send(IResultReceiverCaller.RESULT_CODE_OK,bundle);
    }

    /**
     * Handle action get user in the provided background thread with the provided
     * parameters.
     */
    private void handleActionGetUser(ResultReceiver receiver, String email, Bundle bundle) {
        User user = appDatabase.UserDao().getUserbyEmail(email);
        if(user!=null) {
            bundle.putString(IResultReceiverCaller.TYPE_ACTION_ANSWER, IResultReceiverCaller.TYPE_USER);
            bundle.putSerializable(IResultReceiverCaller.ACTION_ANSWER, user);
            receiver.send(IResultReceiverCaller.RESULT_CODE_OK, bundle);
        }else{
            bundle.putSerializable(IResultReceiverCaller.PARAM_EXCEPTION, new Exception("Invalid email"));
            receiver.send(IResultReceiverCaller.RESULT_CODE_ERROR, bundle);
        }
    }

    private void handleActionGetUserForLogin(ResultReceiver receiver, String email, String password, Bundle bundle) {
        User user = appDatabase.UserDao().getUserForLogin(email,password);
        if(user!=null) {
            bundle.putString(IResultReceiverCaller.TYPE_ACTION_ANSWER, IResultReceiverCaller.TYPE_USER);
            bundle.putSerializable(IResultReceiverCaller.ACTION_ANSWER, user);
            receiver.send(IResultReceiverCaller.RESULT_CODE_OK, bundle);
        }else{
            bundle.putSerializable(IResultReceiverCaller.PARAM_EXCEPTION, new Exception("Invalid email"));
            receiver.send(IResultReceiverCaller.RESULT_CODE_ERROR, bundle);
//            bundle.putString(IResultReceiverCaller.TYPE_ACTION_ANSWER, IResultReceiverCaller.TYPE_USER);
//            bundle.putString(IResultReceiverCaller.EXTRA_EMAIL,email);
//            bundle.putString(IResultReceiverCaller.EXTRA_PASSWORD,password);
//            receiver.send(IResultReceiverCaller.RESULT_CODE_OK, bundle);
        }
    }

    /**
     * Handle action get coordenadas in the provided background thread with the provided
     * parameters.
     */
    private void handleActionGetCoordenadas(ResultReceiver receiver, int userId, Date from, Date to, Bundle bundle) {
        List<Coordenada> coordenadas= appDatabase.CoordenadaDao().findCoordenadaBetweenDates(userId,from,to);
        //List<Coordenada> coordes= appDatabase.CoordenadaDao().getAll();
        if(coordenadas!=null) {
            ArrayList<Coordenada> cs = new ArrayList<>(coordenadas);
            bundle.putString(IResultReceiverCaller.TYPE_ACTION_ANSWER, IResultReceiverCaller.TYPE_COORDENADAS);
            bundle.putParcelableArrayList(IResultReceiverCaller.ACTION_ANSWER, cs);
            receiver.send(IResultReceiverCaller.RESULT_CODE_OK, bundle);
        }else{
            bundle.putSerializable(IResultReceiverCaller.PARAM_EXCEPTION, new Exception("No hay coordenadas"));
            receiver.send(IResultReceiverCaller.RESULT_CODE_ERROR, bundle);
        }
    }

    private void handleActionSaveCoordenada(ResultReceiver receiver, Coordenada coordenada, Bundle bundle) {
        appDatabase.CoordenadaDao().insertAll(coordenada);
        bundle.putString(IResultReceiverCaller.TYPE_ACTION_ANSWER, IResultReceiverCaller.TYPE_COORDENADA);
        bundle.putParcelable(IResultReceiverCaller.ACTION_ANSWER,coordenada);
        receiver.send(IResultReceiverCaller.RESULT_CODE_OK,bundle);
    }

    private void handleActionSaveCoordenadas(ResultReceiver receiver, ArrayList<Coordenada> coordenadas, Bundle bundle) {
        Object[] array = coordenadas.toArray();
        Coordenada[] cs = Arrays.copyOf(array, array.length, Coordenada[].class);
        appDatabase.CoordenadaDao().insertAll(cs);
        bundle.putString(IResultReceiverCaller.TYPE_ACTION_ANSWER, IResultReceiverCaller.TYPE_COORDENADAS);
        bundle.putParcelableArrayList(IResultReceiverCaller.ACTION_ANSWER,coordenadas);
        receiver.send(IResultReceiverCaller.RESULT_CODE_OK,bundle);
    }

    private void handleActionGetMessages(ResultReceiver receiver, Bundle bundle) {
        List<Message> messages = appDatabase.MessageDao().getAll();
        if(messages!=null){
            ArrayList<Message> ms = new ArrayList<>(messages);
            bundle.putString(IResultReceiverCaller.TYPE_ACTION_ANSWER, IResultReceiverCaller.TYPE_MESSAGES);
            bundle.putParcelableArrayList(IResultReceiverCaller.ACTION_ANSWER,ms);
            receiver.send(IResultReceiverCaller.RESULT_CODE_OK,bundle);
        }else{
            bundle.putSerializable(IResultReceiverCaller.PARAM_EXCEPTION, new Exception("No hay messages"));
            receiver.send(IResultReceiverCaller.RESULT_CODE_ERROR, bundle);
        }
    }

    private void handleActionSaveMessages(ResultReceiver receiver, ArrayList<Message> messages, Bundle bundle) {
        Object[] array = messages.toArray();
        Message[] ms = Arrays.copyOf(array, array.length, Message[].class);
        appDatabase.MessageDao().insertAll(ms);
        bundle.putString(IResultReceiverCaller.TYPE_ACTION_ANSWER, IResultReceiverCaller.TYPE_MESSAGES);
        bundle.putParcelableArrayList(IResultReceiverCaller.ACTION_ANSWER,messages);
        receiver.send(IResultReceiverCaller.RESULT_CODE_OK,bundle);
    }
}
