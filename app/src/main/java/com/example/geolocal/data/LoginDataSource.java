package com.example.geolocal.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.example.geolocal.data.model.User;
import com.example.geolocal.database.DatabaseIntentService;
import com.example.geolocal.network.WebServiceService;
import com.example.geolocal.receiver.IResultReceiverCaller;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource implements IResultReceiverCaller{

    private Context context;

    public void login(Context context, String email, String password) {
        this.context = context;
        try{
            if(!isConnected(context)){ //TODO quitar la negación de este if
                //WebServiceService.startActionSend(context,this,WebServiceService.URL,email);
            }else {
                DatabaseIntentService.startActionGetUserForLogin(context, this, email, password);
            }
        } catch (Exception e) {
            LoginRepository.getInstance(this).logged(new Result.Error(new IOException("Error logging in")));
        }
    }

    public void register(Context context, String email, String userName, String password) {
        this.context = context;
        try {
            if(isConnected(context)){
                //WebServiceService.startActionSend(context,this,WebServiceService.URL,email);
                DatabaseIntentService.startActionGetUserForLogin(context,this,email,password);
            }else{
                LoginRepository.getInstance(this).logged(new Result.Error(new IOException("No network")));
            }
        } catch (Exception e) {
            LoginRepository.getInstance(this).logged(new Result.Error(new IOException("Error in Register")));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }

    private boolean isConnected(Context context){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onReceiveResult(Bundle data) {
        String type = data.getString(IResultReceiverCaller.EXTRA_TYPE);
        if(type.equals(IResultReceiverCaller.DATABASE)){
            String result = data.getString(IResultReceiverCaller.TYPE_ACTION_ANSWER);
            if(result.equals(IResultReceiverCaller.TYPE_USER)) {
                User user = (User) data.getSerializable(IResultReceiverCaller.ACTION_ANSWER);
                if(user != null){
                    LoginRepository.getInstance(this).logged( new Result.Success<>(user) );
                } else{
                    LoginRepository.getInstance(this).logged(new Result.Error(new IOException("Error inLogin")));
                }
            }
        }else{
            String result = data.getString(IResultReceiverCaller.TYPE_ACTION_ANSWER);
            if(result.equals(IResultReceiverCaller.TYPE_USER)) {
                User user = (User) data.getSerializable(IResultReceiverCaller.ACTION_ANSWER);
                LoginRepository.getInstance(this).logged((user != null) ? new Result.Success<>(user) : new Result.Error(new IOException("Error logging in")));
            }
        }
    }

    @Override
    public void onReceiveError(Exception exception) {
        LoginRepository.getInstance(this).logged(new Result.Error(new IOException("Error logging in")));
    }
}
