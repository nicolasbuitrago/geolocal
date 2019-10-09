package com.example.geolocal.ui.login;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.geolocal.MainActivity;
import com.example.geolocal.R;
import com.example.geolocal.broadcast.IBroadcastManagerCaller;
import com.example.geolocal.broadcast.NetworkChangeReceiver;
import com.example.geolocal.data.LoginRepository;


public class RegisterActivity extends AppCompatActivity implements IBroadcastManagerCaller {

    private LoginViewModel loginViewModel;
    private NetworkChangeReceiver broadcastNetwork;
    Button registerButton;
    private boolean network;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText emailEditText = findViewById(R.id.register_email);
        final EditText userNameEditText = findViewById(R.id.register_user_name);
        final EditText passwordEditText = findViewById(R.id.register_password);
        registerButton = findViewById(R.id.register);
        final ProgressBar loadingProgressBar = findViewById(R.id.register_loading);

        initializeStatusNetwork();
        initializeBroadcastNetwork();

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                registerButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    emailEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                    setResult(Activity.RESULT_OK);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("user", LoginRepository.getInstance(null).getUser());
                    startActivity(intent);

                    //Complete and destroy register activity once successful
                    finish();
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && network) {
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    loginViewModel.register(getApplicationContext(),emailEditText.getText().toString(), userNameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.register(getApplicationContext(),emailEditText.getText().toString(), userNameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });
    }

    private void initializeStatusNetwork(){
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        network = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        registerButton.setEnabled(network);
    }

    public void initializeBroadcastNetwork(){
        broadcastNetwork=new NetworkChangeReceiver(this,this);
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_SHORT).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void MessageReceivedThroughBroadcastManager(String channel, String type, String message) {
        if(channel.equals(NetworkChangeReceiver.CHANNEL_NETWORK)){
            network= type.equals(NetworkChangeReceiver.TYPE_NETWORK_ONLINE);
            registerButton.setEnabled(network);
        }
    }

    @Override
    public void ErrorAtBroadcastManager(final Exception error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder=
                        new AlertDialog.
                                Builder(getApplicationContext());
                builder.setTitle("BM Error")
                        .setMessage(error.getMessage())
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //TODO
                            }
                        });
                builder.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(broadcastNetwork!=null){
            broadcastNetwork.unRegister();
        }
        super.onDestroy();
    }
}
