package com.example.geolocal;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.example.geolocal.broadcast.BroadcastManager;
import com.example.geolocal.broadcast.IBroadcastManagerCaller;
import com.example.geolocal.broadcast.NetworkChangeReceiver;
import com.example.geolocal.data.LoginRepository;
import com.example.geolocal.data.model.Coordenada;
import com.example.geolocal.data.model.Message;
import com.example.geolocal.data.model.User;
import com.example.geolocal.data.model.UserConnected;
import com.example.geolocal.database.DatabaseIntentService;
import com.example.geolocal.gps.GPSManager;
import com.example.geolocal.gps.IGPSManagerCaller;
import com.example.geolocal.network.SocketManagementService;
import com.example.geolocal.network.WebServiceService;
import com.example.geolocal.receiver.IResultReceiverCaller;
import com.example.geolocal.ui.login.LoginActivity;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IGPSManagerCaller, IBroadcastManagerCaller, IResultReceiverCaller {

    GPSManager gpsManager;
    private MapView map;
    private User user, userSelected;
    private MyLocationNewOverlay mLocationOverlay;
    BroadcastManager broadcastManagerForSocketIO;
    NetworkChangeReceiver broadcastNetwork;
    ArrayList<String> listOfMessages, listUsers;
    ArrayAdapter<String> adapterMessages, adapterUsers;
    TextView networkStatusTextView;
    boolean socketServiceStarted=false;
    boolean network, needPush;
    private Date from, to;
    Button buttonDateFrom, buttonDateTo;
    ItemizedOverlayWithFocus<OverlayItem> mOverlaysConsulta, mOverlaysUsers;
    private ArrayList<Coordenada> coordenadasToPush;
    private ArrayList<Message> messages;
    private ArrayList<UserConnected> usersConnected;

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
                Snackbar.make(view, "Opening chat", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent intent=new Intent(getApplicationContext(),ChatActivity.class);
                intent.putExtra("messages",messages);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        initializeStatusNetwork();

        coordenadasToPush = new ArrayList<>();
        messages = new ArrayList<>();
        usersConnected = new ArrayList<>();
        listOfMessages=new ArrayList<>();
        listUsers=new ArrayList<>();

        user = (User) getIntent().getSerializableExtra("user");

        //TODO add pull messages
        //WebServiceService.startActionGet(getApplicationContext(),this,WebServiceService.URL,"messages",WebServiceService.MESSAGE);

        ((Button)findViewById(R.id.start_service_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),SocketManagementService.class);
                intent.putExtra("SERVER_HOST",((EditText)findViewById(R.id.server_ip_txt)).getText()+"");
                intent.putExtra("SERVER_PORT",Integer.parseInt(((EditText)findViewById(R.id.server_port_txt)).getText()+""));
                intent.setAction(SocketManagementService.ACTION_CONNECT);
                startService(intent);
                socketServiceStarted=true;
            }
        });

        final Button buttonStartSearchCoordenadas = (Button)findViewById(R.id.start_search_coordenadas);
        buttonDateFrom = (Button)findViewById(R.id.button_date_from);
        buttonDateTo = (Button)findViewById(R.id.button_date_to);

        buttonDateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDate(MainActivity.this, buttonDateFrom,true);
                buttonStartSearchCoordenadas.setEnabled(to!=null);
            }
        });

        buttonDateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDate(MainActivity.this, buttonDateTo,false);
                buttonStartSearchCoordenadas.setEnabled(from!=null);
            }
        });

        final Spinner spinnerUsers = (Spinner) findViewById(R.id.spinner_user);
        // Create an ArrayAdapter using the string array and a default spinner layout
        adapterUsers = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, listUsers);
        // Specify the layout to use when the list of choices appears
        adapterUsers.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerUsers.setAdapter(adapterUsers);
        setListUsers();
        spinnerUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String un = listUsers.get(i);
                for (UserConnected u : usersConnected) {
                    if(u.getUser().userName.equals(un)){
                        userSelected = u.getUser();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final MainActivity ma = this;
        buttonStartSearchCoordenadas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(from!=null && to!=null && userSelected!=null){
                    if(isConnected(getApplicationContext())){
                        WebServiceService.startActionPull(getApplicationContext(),ma, WebServiceService.URL,userSelected.userId);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Please connect to internet.", Toast.LENGTH_SHORT).show();
                        //DatabaseIntentService.startActionGetCoordenadas(getApplicationContext(),ma,user.userId,from,to);
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Please select user and dates.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        initializeGPSManager();
        initializeOSM();
        initializeBroadcastManagerForSocketIO();
        initializeBroadcastNetwork();
        adapterMessages = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listOfMessages);
    }

    private void initializeStatusNetwork(){
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        network = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        networkStatusTextView = ((TextView)findViewById(R.id.status_network_text));
        setNetworkStatus(network);
    }

    public void initializeGPSManager(){
        gpsManager=new GPSManager(this,this);
        gpsManager.initializeLocationManager();
    }

    public void initializeOSM(){
        try{
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
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
        broadcastManagerForSocketIO = new BroadcastManager(this, SocketManagementService.SOCKET_SERVICE_CHANNEL,this);
    }

    public void initializeBroadcastNetwork(){
        broadcastNetwork=new NetworkChangeReceiver(this,this);
    }

    private boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void setMapCenter(Location location){
        IMapController mapController = map.getController();
        mapController.setZoom(14d);
        GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        mapController.setCenter(startPoint);
    }

    private void setUsersInMap(){
        final Context context = getApplicationContext();
        ArrayList<OverlayItem> items = new ArrayList();
        for (UserConnected u:usersConnected) {
            OverlayItem item = new OverlayItem(u.getUser().userName, dateToString(u.getLocation().date), new GeoPoint(u.getLocation().latitud,u.getLocation().longitud));
            item.setMarker(u.isOnline()? getDrawable(R.mipmap.ic_location_on) : getDrawable(R.mipmap.ic_location_off));
            items.add(item);
        }
        if(mOverlaysUsers != null) map.getOverlays().remove(mOverlaysUsers);
        //the overlay
        mOverlaysUsers = new ItemizedOverlayWithFocus<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //Toast.makeText(context,item.getTitle(),Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                }, context);
        mOverlaysUsers.setFocusItemsOnTap(true);
        map.getOverlays().add(mOverlaysUsers);
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
            LoginRepository.getInstance(null).logout();
            setResult(Activity.RESULT_OK);

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);

            //Complete and destroy login activity once successful
            finish();
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
//            DatabaseIntentService.startActionGetUser(getApplicationContext(),this,"nicolas@email.com");
            User u1 = new User("Pedro","pedro@email.com","123456");
            User u2 = new User("Maria","maria@email.com","123456");
            Coordenada c1 = new Coordenada(1,new Date(1569503534000L),11.0085,-74.7986);
            Coordenada c2 = new Coordenada(2,new Date(1569504230000L),11.0040,-74.7975);

            UserConnected uc1 = new UserConnected(u1,c1);
            UserConnected uc2 = new UserConnected(u2,c2);
            usersConnected.add(uc1);
            usersConnected.add(uc2);
            setUsersInMap();
            setListUsers();
        } else if (id == R.id.nav_tools) {
            WebServiceService.startActionGet(getApplicationContext(),this,"http://192.168.1.55:8080","nicolas",WebServiceService.USER);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setNetworkStatus(boolean status){
        network = status;
        if(status){
            networkStatusTextView.setText(R.string.status_online);
            networkStatusTextView.setTextColor(getResources().getColor(R.color.green));
        }else{
            networkStatusTextView.setText(R.string.status_offline);
            networkStatusTextView.setTextColor(getResources().getColor(R.color.red));
        }
    }

    private void setListUsers(){
        listUsers.clear();
        for (UserConnected u:usersConnected) {
            listUsers.add(u.getUser().userName);
        }
        adapterUsers.notifyDataSetChanged();
    }

    private void getDate(final Context context, final Button button, final boolean isFrom) {
        final Calendar date;
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        //Log.v(TAG, "The choosen one " + date.getTime());
                        button.setText(dateToString(date.getTime()));
                        if(isFrom) from = date.getTime();
                        else to = date.getTime();
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    String dateToString(Date date){
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aaa");
        return dateFormat.format(date);
    }

    void addOverLaysConsulta(ArrayList<Coordenada> coordenadas){
        final Context context = getApplicationContext();
        ArrayList<OverlayItem> items = new ArrayList();
        for (Coordenada c : coordenadas) {
            items.add(new OverlayItem(user.userName,dateToString(c.date),new GeoPoint(c.latitud,c.longitud)));
        }
        //items.add(new OverlayItem("Title", "Description", new GeoPoint(11.010115,-74.827546))); // Lat/Lon decimal degrees
        if(mOverlaysConsulta != null) map.getOverlays().remove(mOverlaysConsulta);
        //the overlay
        mOverlaysConsulta = new ItemizedOverlayWithFocus<OverlayItem>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //Toast.makeText(context,item.getTitle(),Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                }, context);
        mOverlaysConsulta.setFocusItemsOnTap(true);
        map.getOverlays().add(mOverlaysConsulta);
    }

    @Override
    public void MessageReceivedThroughBroadcastManager(String channel, String type, final String message) {
        if(channel.equals(SocketManagementService.SOCKET_SERVICE_CHANNEL)){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listOfMessages.add(message);
                    ((ListView)findViewById(R.id.messages_list_view)).setAdapter(adapterMessages);
                    adapterMessages.notifyDataSetChanged();
                }
            });
        }else if(channel.equals(NetworkChangeReceiver.CHANNEL_NETWORK)){
            boolean aux = type.equals(NetworkChangeReceiver.TYPE_NETWORK_ONLINE);
            setNetworkStatus(aux);
            if(needPush && aux){
                WebServiceService.startActionPush(getApplicationContext(),this,WebServiceService.URL,coordenadasToPush);
            }
        }
    }

    @Override
    public void ErrorAtBroadcastManager(final Exception error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder=
                        new AlertDialog.
                                Builder(MainActivity.this);
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
        Coordenada coordenada = new Coordenada(user.userId,Calendar.getInstance().getTime(),location.getLatitude(),location.getLongitude());
        DatabaseIntentService.startActionSaveCoordenada(getApplicationContext(),this,coordenada);
        if (!network) {
            needPush = true;
            coordenadasToPush.add(coordenada);
        }else {
            WebServiceService.startActionSend(getApplicationContext(), this, WebServiceService.URL, "",coordenada);
        }
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
        if(broadcastNetwork!=null){
            broadcastNetwork.unRegister();
        }
        super.onDestroy();
    }

    @Override
    public void onReceiveResult(Bundle bundle) {
        String type = bundle.getString(IResultReceiverCaller.EXTRA_TYPE);
        if(IResultReceiverCaller.WEBSERVICE.equals(type)){
            type = bundle.getString(IResultReceiverCaller.TYPE_ACTION_ANSWER);
            if(IResultReceiverCaller.PUSH_OK.equals(type)){
                coordenadasToPush.clear();
            }else if(IResultReceiverCaller.TYPE_MESSAGES.equals(type)){
                ArrayList<Message> mess = bundle.getParcelableArrayList(IResultReceiverCaller.ACTION_ANSWER);
                if(mess!=null) messages = mess;
                else Toast.makeText(this, "Problem in GET MESSAGES WS", Toast.LENGTH_SHORT).show();
            }else {
                String result = bundle.getString(IResultReceiverCaller.ACTION_ANSWER);
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            }
        }else{
            String result = bundle.getString(IResultReceiverCaller.TYPE_ACTION_ANSWER);
            if(result.equals(IResultReceiverCaller.TYPE_USER)) {
                User user = (User) bundle.getSerializable(IResultReceiverCaller.ACTION_ANSWER);

                Toast.makeText(this, R.string.welcome +" "+ user.userName, Toast.LENGTH_SHORT).show();
            }else if(result.equals(IResultReceiverCaller.TYPE_COORDENADAS)){
                ArrayList<Coordenada> coordenadas = bundle.getParcelableArrayList(IResultReceiverCaller.ACTION_ANSWER);
                addOverLaysConsulta(coordenadas);
                int v = getVelocidadPromedio(coordenadas);
                Toast.makeText(this, "Velocidad: " + v, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onReceiveError(Exception exception) {
        Toast.makeText(this,exception.getMessage(),Toast.LENGTH_SHORT).show();
    }

    private int getVelocidadPromedio(ArrayList<Coordenada> coordenadas){
        int velocidad = 0;
        for(int i = 0; i<coordenadas.size()-1; i++){
            Coordenada c1 = coordenadas.get(i);
            Coordenada c2 = coordenadas.get(i+1);
            int t = Math.toIntExact(c2.date.getTime() - c1.date.getTime() / 1000);
            velocidad += distance(c1.latitud, c2.latitud, c1.longitud, c2.longitud)/ t;
        }
        return velocidad/coordenadas.size();
    }

    public static double distance(double lat1, double lat2, double lon1, double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }
}
