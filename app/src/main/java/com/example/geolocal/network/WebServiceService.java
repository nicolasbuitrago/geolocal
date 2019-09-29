package com.example.geolocal.network;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.example.geolocal.receiver.IResultReceiverCaller;
import com.example.geolocal.receiver.WebServiceResultReceiver;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class WebServiceService extends IntentService {

    public static final String URL = "http://192.168.1.55:8080";

    private static final String ACTION_SEND = "com.example.geolocal.network.action.SEND";
    private static final String ACTION_GET = "com.example.geolocal.network.action.GET";

    private static final String EXTRA_RECEIVER = "com.example.geolocal.network.extra.RECEIVER";
    private static final String EXTRA_URL = "com.example.geolocal.network.extra.URL";
    private static final String EXTRA_METHOD = "com.example.geolocal.network.extra.METHOD";
    private static final String EXTRA_RESOURCE_NAME = "com.example.geolocal.network.extra.RESOURCE_NAME";

    public WebServiceService() {
        super("WebServiceService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionSend(Context context, IResultReceiverCaller caller, String url, String resourceName) {
        WebServiceResultReceiver receiver = new WebServiceResultReceiver(new Handler());
        receiver.setReceiver(caller);
        Intent intent = new Intent(context, WebServiceService.class);
        intent.setAction(ACTION_SEND);
        intent.putExtra(EXTRA_RECEIVER, receiver);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_RESOURCE_NAME, resourceName);
        intent.putExtra(EXTRA_METHOD, "POST");
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionGet(Context context, IResultReceiverCaller caller, String url, String resourceName) {
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

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final ResultReceiver receiver =  intent.getParcelableExtra(EXTRA_RECEIVER);
            final String action = intent.getAction();
            Bundle bundle = new Bundle();
            bundle.putString(IResultReceiverCaller.EXTRA_TYPE,IResultReceiverCaller.WEBSERVICE);
            if (ACTION_SEND.equals(action)) {
                final String url = intent.getStringExtra(EXTRA_URL);
                final String resourceName = intent.getStringExtra(EXTRA_RESOURCE_NAME);
                final String method = intent.getStringExtra(EXTRA_METHOD);
                handleActionSend(receiver, url,resourceName,method,bundle);
            } else if (ACTION_GET.equals(action)) {
                final String url = intent.getStringExtra(EXTRA_URL);
                final String resourceName = intent.getStringExtra(EXTRA_RESOURCE_NAME);
                final String method = intent.getStringExtra(EXTRA_METHOD);
                handleActionGet(receiver,url,resourceName,method,bundle);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSend(ResultReceiver receiver, String url, String resourceName, String method, Bundle bundle){
        String result = null;
        try {


        }catch (Exception error) {
            bundle.putSerializable(WebServiceResultReceiver.PARAM_EXCEPTION, new Exception("HTTP error"));
            receiver.send(WebServiceResultReceiver.RESULT_CODE_ERROR,bundle);
        }

        receiver.send(WebServiceResultReceiver.RESULT_CODE_OK,bundle);
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
                bundle.putString(WebServiceResultReceiver.ACTION_ANSWER,result);
                receiver.send(WebServiceResultReceiver.RESULT_CODE_OK,bundle);
            }
        }catch (Exception error) {
            bundle.putSerializable(WebServiceResultReceiver.PARAM_EXCEPTION, new Exception("HTTP error"));
            receiver.send(WebServiceResultReceiver.RESULT_CODE_ERROR,bundle);
        }

        receiver.send(WebServiceResultReceiver.RESULT_CODE_OK,bundle);
    }

    private String http(String url, String resourceName, String method, String operation, String payload)  throws IOException{
        URL urla;
        HttpURLConnection connection = null;
        String result = null;
        try {
            urla=new URL(url+"/"+resourceName+"/"+operation);
            connection= (HttpURLConnection)urla.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod(method);
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
        }finally {
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
