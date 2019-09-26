package com.example.geolocal;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.example.geolocal.broadcast.BroadcastManager;
import com.example.geolocal.broadcast.IBroadcastManagerCaller;
import com.example.geolocal.data.model.Coordenada;
import com.example.geolocal.data.model.User;
import com.example.geolocal.database.AppDatabase;
import com.example.geolocal.database.DatabaseIntentService;
import com.example.geolocal.gps.GPSManager;
import com.example.geolocal.gps.IGPSManagerCaller;
import com.example.geolocal.network.SocketManagementService;
import com.example.geolocal.receiver.DatabaseResultReceiver;
import com.example.geolocal.receiver.IResultReceiverCaller;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.preference.PreferenceManager;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IGPSManagerCaller, IBroadcastManagerCaller, IResultReceiverCaller {

    GPSManager gpsManager;
    private MapView map;
    private User user;
    private MyLocationNewOverlay mLocationOverlay;
    BroadcastManager broadcastManagerForSocketIO;
    ArrayList<String> listOfMessages=new ArrayList<>();
    ArrayAdapter<String> adapter ;
    boolean socketServiceStarted=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        user = (User) getIntent().getSerializableExtra("user");

        /*String user=getIntent().getExtras().
                getString("user_name");
        Toast.makeText(
                this,
                "Welcome "+user,Toast.LENGTH_LONG).
                show();*/
        ((Button)findViewById(R.id.start_service_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(
                        getApplicationContext(),SocketManagementService.class);
                intent.putExtra("SERVER_HOST",((EditText)findViewById(R.id.server_ip_txt)).getText()+"");
                intent.putExtra("SERVER_PORT",Integer.parseInt(((EditText)findViewById(R.id.server_port_txt)).getText()+""));
                intent.setAction(SocketManagementService.ACTION_CONNECT);
                startService(intent);
                socketServiceStarted=true;
            }
        });

        initializeGPSManager();
        initializeOSM();
        initializeBroadcastManagerForSocketIO();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listOfMessages);
    }

    public void initializeGPSManager(){
        gpsManager=new GPSManager(this,this);
        gpsManager.initializeLocationManager();
    }

    public void initializeOSM(){
        try{
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1002);
                return;
            }
            Context ctx = getApplicationContext();
            Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
            map = (MapView) findViewById(R.id.map);
            map.setTileSource(TileSourceFactory.MAPNIK);
            this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this),map);
            this.mLocationOverlay.enableMyLocation();
            map.getOverlays().add(this.mLocationOverlay);
        }catch (Exception error){
            Toast.makeText(this,error.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public void initializeBroadcastManagerForSocketIO(){
        broadcastManagerForSocketIO=new BroadcastManager(this,
                SocketManagementService.
                        SOCKET_SERVICE_CHANNEL,this);
    }

    public void setMapCenter(Location location){
        IMapController mapController =
                map.getController();
        mapController.setZoom(9.5);
        GeoPoint startPoint = new GeoPoint(
                location.getLatitude(), location.getLongitude());
        mapController.setCenter(startPoint);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            DatabaseIntentService.startActionGetCoordenadas(getApplicationContext(),this,user.userId,new Date(1569503534000L), new Date(1569504122000L));
        } else if (id == R.id.nav_slideshow) {
            DatabaseIntentService.startActionGetUser(getApplicationContext(),this,"nicolas@email.com");
        } else if (id == R.id.nav_tools) {


        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void MessageReceivedThroughBroadcastManager(String channel, String type, final String message) {
        if(channel.equals(SocketManagementService.SOCKET_SERVICE_CHANNEL)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listOfMessages.add(message);
                    ((ListView)findViewById(R.id.messages_list_view)).setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            });
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
    public void needPermissions() {
        this.requestPermissions(new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
    }

    @Override
    public void locationHasBeenReceived(final Location location) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView)findViewById(R.id.latitude_text_view)).setText(location.getLatitude()+"");
                ((TextView)findViewById(R.id.longitude_text_view)).setText(location.getLongitude()+"");
                if(map!=null)
                    setMapCenter(location);

            }
        });

        if(socketServiceStarted)
            if(broadcastManagerForSocketIO!=null){
                broadcastManagerForSocketIO.sendBroadcast(
                        SocketManagementService.CLIENT_TO_SERVER_MESSAGE,
                        location.getLatitude()+" / "+location.getLongitude());
            }
    }

    @Override
    public void gpsErrorHasBeenThrown(final Exception error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder= new AlertDialog.Builder(getApplicationContext());
                builder.setTitle("GPS Error").setMessage(error.getMessage()).setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
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
        if(broadcastManagerForSocketIO!=null){
            broadcastManagerForSocketIO.unRegister();
        }
        super.onDestroy();
    }

    @Override
    public void onReceiveResult(Bundle bundle) {
        String result = bundle.getString(DatabaseResultReceiver.PARAM_RESULT);
        if(result.equals(DatabaseResultReceiver.TYPE_USER)) {
            User user = (User) bundle.getSerializable(DatabaseResultReceiver.ACTION_ANSWER);

            Toast.makeText(this, R.string.welcome +" "+ user.userName, Toast.LENGTH_SHORT).show();
        }else if(result.equals(DatabaseResultReceiver.TYPE_COORDENADAS)){
            ArrayList<Coordenada> coordenadas = bundle.getParcelableArrayList(DatabaseResultReceiver.ACTION_ANSWER);
            Toast.makeText(this, "Coordenadas: " + coordenadas.size(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onReceiveError(Exception exception) {
        Toast.makeText(this,exception.getMessage(),Toast.LENGTH_SHORT).show();
    }
}
