package com.preciosclaros;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.ButterKnife;
import butterknife.OnClick;


import static com.preciosclaros.SignInActivity.activityA;
import static com.preciosclaros.SignInActivity.close;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private IntentIntegrator qrScan;
    private boolean scan = false;
    static boolean backVerProducto;
    GoogleApiClient mGoogleApiClient;
    private boolean mapaShow;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1; // 1 minute
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    // Declaring a Location Manager
    protected LocationManager locationManager;

    @OnClick({R.id.escanear, R.id.buscar})
    public void elejirOpcion(Button btn) {
        switch (btn.getId()) {
            case R.id.escanear:
                sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
                if (!sharedPreferences.contains("Lat")) {
                    elegirUbicacion(2);
                }
                if (sharedPreferences.contains("Lat")) {
                    escanear();
                } else {
                    Toast.makeText(this, "Debe elegir una ubicación antes de buscar", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.buscar:
                Intent intent3 = new Intent(HomeActivity.this, BuscarProductos.class);
                intent3.setFlags(intent3.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent3);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }

    static MenuItem us, ubicacion;
    private final android.location.LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(HomeActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);

        } else {

            System.out.println("Location permissions available, starting location");

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location != null){
                Log.d("LOCATION", String.valueOf(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude()));
                sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
                // get editor to edit in file
                editor = sharedPreferences.edit();
                editor.putString(Constants.LATITUD, String.valueOf(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude()));
                editor.putString(Constants.LONGITUD, String.valueOf(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude()));
                editor.apply();
                editor.commit();
            }

        }


        sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
        // get editor to edit in file
        editor = sharedPreferences.edit();
        editor.putBoolean(Constants.FIRST_TIME, false);
        editor.apply();
        editor.commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        //toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.my_drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ubicacion = navigationView.getMenu().findItem(R.id.ubicacionActual);
        ubicacion.setVisible(false);
        ubicacion.setCheckable(false);
        sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
        if (sharedPreferences.contains(Constants.UBICACION)) {
            ubicacion.setTitle(sharedPreferences.getString(Constants.UBICACION, ""));
        }
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        // displaySelectedScreen(R.id.nav_menu1); SI QUIERO QUE INICIE EN UNA OPCION DEL MENU
       /* AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.agregar_lista_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText nombreListaNueva = (EditText) dialogView.findViewById(R.id.NombreListaNueva);
        final EditText Descripcion = (EditText) dialogView.findViewById(R.id.DescripcionListaNueva);
        dialogBuilder.setTitle("Crear Lista");
        dialogBuilder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }
        });
        dialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
        b.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorSecondary));
        b.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorSecondary));
        */
    }

    @Override

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                System.out.println("Location permissions available, starting location");

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, mLocationListener);
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(location != null){
                    Log.d("LOCATION", String.valueOf(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude()));
                    sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
                    // get editor to edit in file
                    editor = sharedPreferences.edit();
                    editor.putString(Constants.LATITUD, String.valueOf(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLatitude()));
                    editor.putString(Constants.LONGITUD, String.valueOf(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getLongitude()));
                    editor.apply();
                    editor.commit();
                }

            }

        }

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //activityA.finish();
            MapreciosUtils.showAlert(HomeActivity.this, "¿ Deseas salir ?","Cancelar","Salir",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //getMenuInflater().inflate(R.menu.mapa, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_favorite:
                elegirUbicacion(1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        //calling the method displayselectedscreen and passing the id of selected menu
        displaySelectedScreen(item.getItemId());
        //make this method blank
        return true;
    }
    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;
        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_close:
                //fragment = new Menu3();
                close = true;
                sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
                // get editor to edit in file
                editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                //HomeActivity.super.onBackPressed();
                MapreciosUtils.showAlert(HomeActivity.this, "¿ Deseas cerrar sesión ?","Cancelar","Si",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                break;
            case R.id.nav_tuto:
                Intent mainIntent = new Intent(HomeActivity.this,TutorialActivity.class);
                mainIntent.putExtra(Constants.ACTIVITY, Constants.HOME_ACTIVITY);
                HomeActivity.this.startActivity(mainIntent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                //HomeActivity.this.finish();
                break;

        }
        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            if(scan) {
                        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                         //if qrcode has nothing in it
                         if(result != null) {
                                                if (result.getContents() != null) {
                                                    //if qr contains data
                                                     Intent intent = new Intent(HomeActivity.this, VerProductoPorId.class);
                                                     intent.setFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                     intent.putExtra(Constants.ACTIVITY, Constants.BARCODE_ACTIVITY);
                                                     intent.putExtra(Constants.ID_PRODUCTO, result.getContents());
                                                     startActivity(intent);
                                                     overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                                                    //buscarProducto(result.getContents());
                                                     }
                                                }else {super.onActivityResult(requestCode, resultCode, data);}
                    }
            if(requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    Place place = PlacePicker.getPlace(data, this);
                    sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
                    // get editor to edit in file
                    editor = sharedPreferences.edit();
                    HomeActivity.ubicacion.setTitle(place.getAddress());
                    HomeActivity.ubicacion.setCheckable(false);
                    editor.putString(Constants.UBICACION,place.getAddress().toString());
                    editor.putString(Constants.LATITUD, String.valueOf(place.getLatLng().latitude));
                    editor.putString(Constants.LONGITUD, String.valueOf(place.getLatLng().longitude));
                    editor.apply();
                    editor.commit();
                    }
                }
            if(requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
                // get editor to edit in file
                editor = sharedPreferences.edit();
                HomeActivity.ubicacion.setTitle(place.getAddress());
                HomeActivity.ubicacion.setCheckable(false);
                editor.putString(Constants.UBICACION,place.getAddress().toString());
                editor.putString(Constants.LATITUD, String.valueOf(place.getLatLng().latitude));
                editor.putString(Constants.LONGITUD, String.valueOf(place.getLatLng().longitude));
                editor.apply();
                editor.commit();
                escanear();
                }
            }
        mapaShow = false;
    }
    public void elegirUbicacion(int request){
        if(!mapaShow) {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            mapaShow = true;
            try {
                startActivityForResult(builder.build(this), request);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }
    public void escanear(){
        scan = true;
        qrScan = new IntentIntegrator(this);
        qrScan.setBeepEnabled(false);
        //attaching onclick listener
        qrScan.initiateScan();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}


