package com.example.geolocal.database;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.example.geolocal.data.User;
import com.example.geolocal.receiver.DatabaseResultReceiver;
import com.example.geolocal.receiver.IResultReceiverCaller;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DatabaseIntentService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_SAVE_USER = "com.example.geolocal.database.action.SAVE_USER";
    private static final String ACTION_GET_USER = "com.example.geolocal.database.action.GET_USER";

    public static final String ACTION_ANSWER = "com.example.geolocal.database.action.ACTION_ANSWER";

    private static final String EXTRA_RECEIVER = "com.example.geolocal.database.extra.RECEIVER";
    private static final String EXTRA_USER = "com.example.geolocal.database.extra.USER";
    private static final String EXTRA_USER_EMAIL = "com.example.geolocal.database.extra.USER_EMAIL";
    private static final String EXTRA_USER_PASSWORD = "com.example.geolocal.database.extra.USER_PASSWORD";
    private static final String EXTRA_USER_USERNAME = "com.example.geolocal.database.extra.USER_USERNAME";
    private static final String EXTRA_DATE = "com.example.geolocal.database.extra.DATE";

    private AppDatabase appDatabase;

    public DatabaseIntentService() {
        super("DatabaseIntentService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
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
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionGetUser(Context context, IResultReceiverCaller caller, String email) {
        DatabaseResultReceiver receiver = new DatabaseResultReceiver(new Handler());
        receiver.setReceiver(caller);
        Intent intent = new Intent(context, DatabaseIntentService.class);
        intent.setAction(ACTION_GET_USER);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        intent.putExtra(EXTRA_USER_EMAIL, email);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            appDatabase = AppDatabase.getDatabaseInstance(this);
            final ResultReceiver receiver =  intent.getParcelableExtra(EXTRA_RECEIVER);
            final String action = intent.getAction();
            if (ACTION_SAVE_USER.equals(action)) {
                final User user = (User) intent.getSerializableExtra(EXTRA_USER);
                handleActionSaveUser(receiver, user);
            } else if (ACTION_GET_USER.equals(action)) {
                final String userEmail = intent.getStringExtra(EXTRA_USER_EMAIL);
                final String userPassword = intent.getStringExtra(EXTRA_USER_PASSWORD);
                handleActionGetUser(receiver, userEmail, userPassword);
            }
        }
    }

    /**
     * Handle action Save user in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSaveUser(ResultReceiver receiver, User user) {
        Bundle bundle = new Bundle();
        appDatabase.UserDao().insertAll(user);
        bundle.putSerializable(ACTION_ANSWER,user);
        receiver.send(DatabaseResultReceiver.RESULT_CODE_OK,bundle);
    }

    /**
     * Handle action get user in the provided background thread with the provided
     * parameters.
     */
    private void handleActionGetUser(ResultReceiver receiver, String email, String password) {
        Bundle bundle = new Bundle();
        User user = appDatabase.UserDao().getUserbyEmail(email);
        if(user!=null) {
            bundle.putSerializable(ACTION_ANSWER, user);
            receiver.send(DatabaseResultReceiver.RESULT_CODE_OK, bundle);
        }else{
            bundle.putSerializable(DatabaseResultReceiver.PARAM_EXCEPTION, new Exception("Email invalido"));
            receiver.send(DatabaseResultReceiver.RESULT_CODE_ERROR, bundle);
        }
    }
}
