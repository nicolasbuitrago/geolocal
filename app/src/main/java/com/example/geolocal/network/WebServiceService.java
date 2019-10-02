package com.example.geolocal.network;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.example.geolocal.data.DateSerializer;
import com.example.geolocal.data.model.Coordenada;
import com.example.geolocal.data.model.Message;
import com.example.geolocal.data.model.User;
import com.example.geolocal.receiver.IResultReceiverCaller;
import com.example.geolocal.receiver.WebServiceResultReceiver;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class WebServiceService extends IntentService {

    public static final String URL = "http://10.20.29.37:8080/restweb/webresources";

    private static final String ACTION_SEND = "com.example.geolocal.network.action.SEND";
    private static final String ACTION_GET = "com.example.geolocal.network.action.GET";
    private static final String ACTION_LOGIN = "com.example.geolocal.network.action.LOGIN";
    private static final String ACTION_REGISTER = "com.example.geolocal.network.action.REGISTER";
    private static final String ACTION_PUSH = "com.example.geolocal.network.action.PUSH";
    private static final String ACTION_PULL = "com.example.geolocal.network.action.PULL";

    public static final String USER = "com.example.geolocal.network.action.GET.USER";
    public static final String COORDENADA = "com.example.geolocal.network.action.GET.COORDENADA";
    public static final String MESSAGE = "com.example.geolocal.network.action.GET.MESSAGE";

    private static final String EXTRA_RECEIVER = "com.example.geolocal.network.extra.RECEIVER";
    private static final String EXTRA_URL = "com.example.geolocal.network.extra.URL";
    private static final String EXTRA_METHOD = "com.example.geolocal.network.extra.METHOD";
    private static final String EXTRA_RESOURCE_NAME = "com.example.geolocal.network.extra.RESOURCE_NAME";
    private static final String EXTRA_OPERATION = "com.example.geolocal.network.extra.OPERATION";
    private static final String EXTRA_USER = "com.example.geolocal.network.extra.USER";
    private static final String EXTRA_USER_ID = "com.example.geolocal.network.extra.USER_ID";
    private static final String EXTRA_EMAIL = "com.example.geolocal.network.extra.USER";
    private static final String EXTRA_PASSWORD = "com.example.geolocal.network.extra.PASSWORD";
    private static final String EXTRA_COORDENADA = "com.example.geolocal.network.extra.COORDENADA";
    private static final String EXTRA_MESSAGE = "com.example.geolocal.network.extra.EXTRA_MESSAGE";

    public WebServiceService() {
        super("WebServiceService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionSend(Context context, IResultReceiverCaller caller, String url, String resourceName, User user) {
        WebServiceResultReceiver receiver = new WebServiceResultReceiver(new Handler());
        receiver.setReceiver(caller);
        Intent intent = new Intent(context, WebServiceService.class);
        intent.setAction(ACTION_SEND);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_RESOURCE_NAME, resourceName);
        intent.putExtra(EXTRA_METHOD, "POST");
        intent.putExtra(EXTRA_USER, user);
        context.startService(intent);
    }

    public static void startActionSend(Context context, IResultReceiverCaller caller, String url, String resourceName, Coordenada coordenada) {
        WebServiceResultReceiver receiver = new WebServiceResultReceiver(new Handler());
        receiver.setReceiver(caller);
        Intent intent = new Intent(context, WebServiceService.class);
        intent.setAction(ACTION_SEND);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_RESOURCE_NAME, resourceName);
        intent.putExtra(EXTRA_METHOD, "POST");
        intent.putExtra(EXTRA_COORDENADA, coordenada);
        context.startService(intent);
    }

    public static void startActionSend(Context context, IResultReceiverCaller caller, String url, String resourceName, Message message) {
        WebServiceResultReceiver receiver = new WebServiceResultReceiver(new Handler());
        receiver.setReceiver(caller);
        Intent intent = new Intent(context, WebServiceService.class);
        intent.setAction(ACTION_SEND);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_RESOURCE_NAME, resourceName);
        intent.putExtra(EXTRA_METHOD, "POST");
        intent.putExtra(EXTRA_MESSAGE, message);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionGet(Context context, IResultReceiverCaller caller, String url, String resourceName, String operation) {
        WebServiceResultReceiver receiver = new WebServiceResultReceiver(new Handler());
        receiver.setReceiver(caller);
        Intent intent = new Intent(context, WebServiceService.class);
        intent.setAction(ACTION_GET);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_RESOURCE_NAME, resourceName);
        intent.putExtra(EXTRA_METHOD, "GET");
        context.startService(intent);
    }

    public static void startActionLogin(Context context, IResultReceiverCaller caller, String url, String email, String password) {
        WebServiceResultReceiver receiver = new WebServiceResultReceiver(new Handler());
        receiver.setReceiver(caller);
        Intent intent = new Intent(context, WebServiceService.class);
        intent.setAction(ACTION_LOGIN);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_EMAIL, email);
        intent.putExtra(EXTRA_PASSWORD, password);
        intent.putExtra(EXTRA_METHOD, "POST");
        context.startService(intent);
    }

    public static void startActionRegister(Context context, IResultReceiverCaller caller, String url, User user) {
        WebServiceResultReceiver receiver = new WebServiceResultReceiver(new Handler());
        receiver.setReceiver(caller);
        Intent intent = new Intent(context, WebServiceService.class);
        intent.setAction(ACTION_REGISTER);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_USER, user);
        intent.putExtra(EXTRA_METHOD, "POST");
        context.startService(intent);
    }

    public static void startActionPush(Context context, IResultReceiverCaller caller, String url, ArrayList<Coordenada> coordenadas) {
        WebServiceResultReceiver receiver = new WebServiceResultReceiver(new Handler());
        receiver.setReceiver(caller);
        Intent intent = new Intent(context, WebServiceService.class);
        intent.setAction(ACTION_PUSH);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(IResultReceiverCaller.TYPE_COORDENADAS, coordenadas);
        context.startService(intent);
    }

    public static void startActionPull(Context context, IResultReceiverCaller caller, String url, int userId) {
        WebServiceResultReceiver receiver = new WebServiceResultReceiver(new Handler());
        receiver.setReceiver(caller);
        Intent intent = new Intent(context, WebServiceService.class);
        intent.setAction(ACTION_PULL);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_USER_ID, userId);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final ResultReceiver receiver =  intent.getParcelableExtra(EXTRA_RECEIVER);
            final String action = intent.getAction();
            Bundle bundle = new Bundle();
            bundle.putString(IResultReceiverCaller.EXTRA_TYPE,IResultReceiverCaller.WEBSERVICE);
            final String url = intent.getStringExtra(EXTRA_URL);
            final String method = intent.getStringExtra(EXTRA_METHOD);
            if (ACTION_SEND.equals(action)) {
                final String resourceName = intent.getStringExtra(EXTRA_RESOURCE_NAME);
                final User user = (User) intent.getSerializableExtra(EXTRA_USER);
                final Coordenada coordenada = (Coordenada) intent.getSerializableExtra(EXTRA_COORDENADA);
                final Message message = (Message) intent.getSerializableExtra(EXTRA_MESSAGE);

                if(user != null) handleActionSend(receiver, url,resourceName,method, user, bundle);
                else if (coordenada!= null) handleActionSend(receiver, url,resourceName,method, coordenada, bundle);
                else handleActionSend(receiver, url,resourceName,method, message, bundle);
            } else if (ACTION_GET.equals(action)) {
                final String resourceName = intent.getStringExtra(EXTRA_RESOURCE_NAME);
                handleActionGet(receiver,url,resourceName,method,bundle);
            } else if(ACTION_LOGIN.equals(action)){
                final String email = intent.getStringExtra(EXTRA_EMAIL);
                final String password = intent.getStringExtra(EXTRA_PASSWORD);
                handleActionLogin(receiver,url,email,password,bundle);
            } else if(ACTION_REGISTER.equals(action)){
                final User user = (User) intent.getSerializableExtra(EXTRA_USER);
                handleActionRegister(receiver,url,user,bundle);
            } else if(ACTION_PUSH.equals(action)){
                final ArrayList<Coordenada> coordenadas = intent.getParcelableArrayListExtra(IResultReceiverCaller.TYPE_COORDENADAS);
                handleActionPush(receiver,url,coordenadas,bundle);
            } else if(ACTION_PULL.equals(action)){
                final int userId = intent.getIntExtra(EXTRA_USER_ID,-1);
                handleActionPull(receiver,url,userId,bundle);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSend(ResultReceiver receiver, String url, String resourceName, String method, User user, Bundle bundle){
        String result = null;
        try {


        }catch (Exception error) {
            bundle.putSerializable(IResultReceiverCaller.PARAM_EXCEPTION, new Exception("HTTP error"));
            receiver.send(IResultReceiverCaller.RESULT_CODE_ERROR,bundle);
        }

        receiver.send(IResultReceiverCaller.RESULT_CODE_OK,bundle);
    }

    private void handleActionSend(ResultReceiver receiver, String url, String resourceName, String method, Coordenada coordenada, Bundle bundle){
        String result = null;
        try {


        }catch (Exception error) {
            bundle.putSerializable(IResultReceiverCaller.PARAM_EXCEPTION, new Exception("HTTP error"));
            receiver.send(IResultReceiverCaller.RESULT_CODE_ERROR,bundle);
        }

        receiver.send(IResultReceiverCaller.RESULT_CODE_OK,bundle);
    }

    private void handleActionSend(ResultReceiver receiver, String url, String resourceName, String method, Message message, Bundle bundle){
        String result = null;
        try {


        }catch (Exception error) {
            bundle.putSerializable(IResultReceiverCaller.PARAM_EXCEPTION, new Exception("HTTP error"));
            receiver.send(IResultReceiverCaller.RESULT_CODE_ERROR,bundle);
        }
        receiver.send(IResultReceiverCaller.RESULT_CODE_OK,bundle);
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionGet(ResultReceiver receiver, String url, String resourceName, String method, Bundle bundle){
        String result;
        try {
            result = http(url,resourceName,method,"operation","");
            if(result != null){
                bundle.putString(IResultReceiverCaller.ACTION_ANSWER,result);
            }
        }catch (Exception error) {
            bundle.putSerializable(IResultReceiverCaller.PARAM_EXCEPTION, new Exception("HTTP error"));
            receiver.send(IResultReceiverCaller.RESULT_CODE_ERROR,bundle);
        }

        receiver.send(IResultReceiverCaller.RESULT_CODE_OK,bundle);
    }

    private void handleActionLogin(ResultReceiver receiver,String url,String email,String password, Bundle bundle){
        String result = null;
        try {
            Gson gson = new Gson();
            User user = new User("",email,password);
            String payload = gson.toJson(user);
            result = http(url,"users","POST","login",payload);
            user = gson.fromJson(result,User.class);
            bundle.putString(IResultReceiverCaller.TYPE_ACTION_ANSWER,IResultReceiverCaller.TYPE_USER);
            bundle.putSerializable(IResultReceiverCaller.ACTION_ANSWER, user);
        }catch (Exception error) {
            bundle.putSerializable(IResultReceiverCaller.PARAM_EXCEPTION, new Exception("HTTP error"));
            receiver.send(IResultReceiverCaller.RESULT_CODE_ERROR,bundle);
        }
        receiver.send(IResultReceiverCaller.RESULT_CODE_OK,bundle);
    }

    private void handleActionRegister(ResultReceiver receiver,String url,User user, Bundle bundle){
        String result = null;
        try {
            Gson gson = new Gson();
            String payload = gson.toJson(user);
            result = http(url,"users","POST","register",payload);
            if("OK".equals(result)) {
                bundle.putString(IResultReceiverCaller.TYPE_ACTION_ANSWER, IResultReceiverCaller.TYPE_USER);
                bundle.putSerializable(IResultReceiverCaller.ACTION_ANSWER, user);
                receiver.send(IResultReceiverCaller.RESULT_CODE_OK, bundle);
            }else{
                bundle.putSerializable(IResultReceiverCaller.PARAM_EXCEPTION, new Exception("HTTP error"));
                receiver.send(IResultReceiverCaller.RESULT_CODE_ERROR,bundle);
            }
        }catch (Exception error) {
            bundle.putSerializable(IResultReceiverCaller.PARAM_EXCEPTION, new Exception("HTTP error"));
            receiver.send(IResultReceiverCaller.RESULT_CODE_ERROR,bundle);
        }
    }

    private void handleActionPush(ResultReceiver receiver, String url, ArrayList<Coordenada> coordenadas, Bundle bundle){
        String result = null;
        try {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
            Coordenada[] cs = (Coordenada[]) coordenadas.toArray();
            String playload = gson.toJson(cs);

            result = http(url,"location","POST","positions",playload);
            if("OK".equals(result)) bundle.putString(IResultReceiverCaller.TYPE_ACTION_ANSWER,IResultReceiverCaller.PUSH_OK);
            else {
                bundle.putSerializable(IResultReceiverCaller.PARAM_EXCEPTION, new Exception("HTTP error"));
                receiver.send(IResultReceiverCaller.RESULT_CODE_ERROR,bundle);
            }
        }catch (Exception error) {
            bundle.putSerializable(IResultReceiverCaller.PARAM_EXCEPTION, new Exception("HTTP error"));
            receiver.send(IResultReceiverCaller.RESULT_CODE_ERROR,bundle);
        }
        receiver.send(IResultReceiverCaller.RESULT_CODE_OK,bundle);
    }

    private void handleActionPull(ResultReceiver receiver, String url, int userId, Bundle bundle){
        String result = null;
        try {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();

            result = http(url,"locations","GET","user/"+userId,"");
            if(result!=null) {
                Coordenada[] cs = gson.fromJson(result, Coordenada[].class);
                ArrayList<Coordenada> coordenadas = new ArrayList<>(Arrays.asList(cs));
                bundle.putString(IResultReceiverCaller.TYPE_ACTION_ANSWER, IResultReceiverCaller.TYPE_COORDENADAS);
                bundle.putParcelableArrayList(IResultReceiverCaller.TYPE_COORDENADAS, coordenadas);
            }else{
                bundle.putSerializable(IResultReceiverCaller.PARAM_EXCEPTION, new Exception("HTTP error"));
                receiver.send(IResultReceiverCaller.RESULT_CODE_ERROR,bundle);
            }
        }catch (Exception error) {
            bundle.putSerializable(IResultReceiverCaller.PARAM_EXCEPTION, new Exception("HTTP error"));
            receiver.send(IResultReceiverCaller.RESULT_CODE_ERROR,bundle);
        }
        receiver.send(IResultReceiverCaller.RESULT_CODE_OK,bundle);
    }

    private String http(String url, String resourceName, String method, String operation, String payload)  throws IOException{
        URL urla;
        HttpURLConnection connection = null;
        String result = null;
        try {
            String link = url+"/"+resourceName+"/"+operation;
            urla=new URL(link);
            connection= (HttpURLConnection)urla.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.getOutputStream().write(payload.getBytes());
            int responseCode=connection.getResponseCode();
            if(responseCode==HttpURLConnection.HTTP_OK){
                InputStream in=connection.getInputStream();
                StringBuffer stringBuffer=new StringBuffer();
                int charIn=0;
                while((charIn=in.read())!=-1){
                    stringBuffer.append((char)charIn);
                }
                result = stringBuffer.toString();
            }
        }catch(Exception e){
            String error = e.getMessage();

        } finally{
            // Close Stream and disconnect HTTPS connection.

            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    private String httpGET(String url, String resourceName)  throws IOException{
        URL urla;
        InputStream stream = null;
        HttpsURLConnection connection = null;
        String result = null;
        try {
            urla =new URL(url+"/"+resourceName);
            connection = (HttpsURLConnection) urla.openConnection();
            // Timeout for reading InputStream arbitrarily set to 3000ms.
            connection.setReadTimeout(3000);
            // Timeout for connection.connect() arbitrarily set to 3000ms.
            connection.setConnectTimeout(3000);
            // For this use case, set HTTP method to GET.
            connection.setRequestMethod("GET");
            // Already true by default but setting just in case; needs to be true since this request
            // is carrying an input (response) body.
            connection.setDoInput(true);
            // Open communications link (network traffic occurs here).
            connection.connect();
            //publishProgress(DownloadCallback.Progress.CONNECT_SUCCESS);
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as an InputStream.
            stream = connection.getInputStream();
            //publishProgress(DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS, 0);
            if (stream != null) {
                // Converts Stream to String with max length of 500.
                result = readStream(stream, 500);
            }
        }finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    /**
     * Converts the contents of an InputStream to a String.
     */
    private String readStream(InputStream stream, int maxLength) throws IOException {
        String result = null;
        // Read InputStream using the UTF-8 charset.
        InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
        // Create temporary buffer to hold Stream data with specified max length.
        char[] buffer = new char[maxLength];
        // Populate temporary buffer with Stream data.
        int numChars = 0;
        int readSize = 0;
        while (numChars < maxLength && readSize != -1) {
            numChars += readSize;
//            int pct = (100 * numChars) / maxLength;
//            publishProgress(DownloadCallback.Progress.PROCESS_INPUT_STREAM_IN_PROGRESS, pct);
            readSize = reader.read(buffer, numChars, buffer.length - numChars);
        }
        if (numChars != -1) {
            // The stream was not empty.
            // Create String that is actual length of response body if actual length was less than
            // max length.
            numChars = Math.min(numChars, maxLength);
            result = new String(buffer, 0, numChars);
        }
        return result;
    }
}
