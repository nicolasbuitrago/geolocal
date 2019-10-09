package com.example.geolocal;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.example.geolocal.data.model.Message;
import com.example.geolocal.data.model.User;
import com.example.geolocal.network.WebServiceService;
import com.example.geolocal.receiver.IResultReceiverCaller;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class ChatActivity extends AppCompatActivity implements IResultReceiverCaller {

    User user;
    ArrayList<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        messages = intent.getParcelableArrayListExtra("messages");

        final EditText editTextMessage = findViewById(R.id.chat_message);
        final ListView listViewMessages = findViewById(R.id.chat_list_view);
        ArrayAdapter<Message> adapterMessages = new ArrayAdapter<Message>(getApplicationContext(),android.R.layout.simple_list_item_1,messages);

        listViewMessages.setAdapter(adapterMessages);
        adapterMessages.notifyDataSetChanged();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isConnected = isConnected(getApplicationContext());
                if(isConnected){
                    sendMessage(getApplicationContext(),editTextMessage.getText().toString());
                    Snackbar.make(view, "Send message", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }else{
                    Snackbar.make(view, "Does not exist connection", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

            }
        });
    }

    private void sendMessage(Context context, String message){
        final Calendar currentDate = Calendar.getInstance();
        Message newMessage = new Message(currentDate.getTime(),user.userId,message);
        WebServiceService.startActionSend(context,this,WebServiceService.URL,"message",newMessage);
    }

    private boolean isConnected(Context context){
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onReceiveResult(Bundle bundle) {
        String type = bundle.getString(IResultReceiverCaller.EXTRA_TYPE);
        if(type.equals(IResultReceiverCaller.WEBSERVICE)){
            type = bundle.getString(IResultReceiverCaller.TYPE_ACTION_ANSWER);
            if(type.equals(IResultReceiverCaller.TYPE_MESSAGES)){
                ArrayList<Message> mess = bundle.getParcelableArrayList(IResultReceiverCaller.ACTION_ANSWER);
                if(mess!=null) messages = mess;
                else Toast.makeText(this, "Problem in GET MESSAGES WS", Toast.LENGTH_SHORT).show();
            }else {
                String result = bundle.getString(IResultReceiverCaller.ACTION_ANSWER);
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onReceiveError(Exception exception) {
        Toast.makeText(this,exception.getMessage(),Toast.LENGTH_SHORT).show();
    }
}
