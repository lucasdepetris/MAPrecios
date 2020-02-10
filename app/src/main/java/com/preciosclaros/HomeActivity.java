package com.preciosclaros;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.annotation.NonNull;
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
    static  boolean backVerProducto;
    GoogleApiClient mGoogleApiClient;
    private  boolean mapaShow;
    @OnClick({R.id.escanear, R.id.buscar}) public void elejirOpcion(Button btn){
        switch (btn.getId()){
            case R.id.escanear:
                sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
                if(!sharedPreferences.contains("Lat"))
                {
                    elegirUbicacion(2);
                }
                if(sharedPreferences.contains("Lat"))
                {
                    escanear();
                }
                else
                {
                 Toast.makeText(this,"Debe elegir una ubicacion antes de buscar",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.buscar:
                Intent intent3 = new Intent(HomeActivity.this,BuscarProductos.class);
                intent3.setFlags(intent3.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent3);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                break;
        }
    }
    static MenuItem us,ubicacion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
        // get editor to edit in file
        editor = sharedPreferences.edit();
        editor.putBoolean("firstTime",false);
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
        ubicacion.setCheckable(false);
        sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
        if(sharedPreferences.contains("ubicacion"))
        {
            ubicacion.setTitle(sharedPreferences.getString("ubicacion",""));
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.my_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //activityA.finish();
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.mapa, menu);
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
                finish();

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
                                                     intent.putExtra("actividad", "barcode");
                                                     intent.putExtra("idProducto", result.getContents());
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
                    editor.putString("ubicacion",place.getAddress().toString());
                    editor.putString("Lat", String.valueOf(place.getLatLng().latitude));
                    editor.putString("Longitude", String.valueOf(place.getLatLng().longitude));
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
                editor.putString("ubicacion",place.getAddress().toString());
                editor.putString("Lat", String.valueOf(place.getLatLng().latitude));
                editor.putString("Longitude", String.valueOf(place.getLatLng().longitude));
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


