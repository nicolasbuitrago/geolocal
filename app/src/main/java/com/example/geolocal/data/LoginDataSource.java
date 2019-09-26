package com.example.geolocal.data;

import android.content.Context;
import android.os.Bundle;

import com.example.geolocal.data.model.User;
import com.example.geolocal.database.DatabaseIntentService;
import com.example.geolocal.receiver.DatabaseResultReceiver;
import com.example.geolocal.receiver.IResultReceiverCaller;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource implements IResultReceiverCaller{

    public void login(Context context, String email, String password) {
        try {
            DatabaseIntentService.startActionGetUserForLogin(context,this,email,password);
        } catch (Exception e) {
            LoginRepository.getInstance(this).logged(new Result.Error(new IOException("Error logging in")));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }

    @Override
    public void onReceiveResult(Bundle data) {
        String result = data.getString(DatabaseResultReceiver.PARAM_RESULT);
        if(result.equals(DatabaseResultReceiver.TYPE_USER)) {
            User user = (User) data.getSerializable(DatabaseResultReceiver.ACTION_ANSWER);
            LoginRepository.getInstance(this).logged((user != null) ? new Result.Success<>(user) : new Result.Error(new IOException("Error logging in")));
        }
    }

    @Override
    public void onReceiveError(Exception exception) {
        LoginRepository.getInstance(this).logged(new Result.Error(new IOException("Error logging in")));
    }
}
