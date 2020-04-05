package com.preciosclaros;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.Gson;
import com.preciosclaros.adaptadores.ProductosAdapter;
import com.preciosclaros.modelo.*;
import com.preciosclaros.service.Api;
import com.preciosclaros.service.HttpService;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lucas on 22/6/2017.
 */

public class BuscarProductos extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    int PLACE_PICKER_REQUEST = 1;
    public Context context = this;
    public SharedPreferences sharedPreferences;
    ApiPrecios service;
    public Call<ProductosApi> requestProductos;
    SharedPreferences.Editor editor;
    @BindView(R.id.ReciclerProductos)
    RecyclerView recyclerView;
    @BindView(R.id.searchProducto)
    SearchView buscador;
    private boolean mapaShow;
    public double latitud =0;
    public void buscarProd(String query) {
        findViewById(R.id.msgErrorUbicacion).setVisibility(View.GONE);
        findViewById(R.id.iconBuscar).setVisibility(View.GONE);
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        buscarProducto(query);
    }
    public void cambiarUbicacion(){
        if(!mapaShow) {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            mapaShow = true;
            try {
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.buscar_productos);
        ButterKnife.bind(this);
        buscador.setIconifiedByDefault(false);
        buscador.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                buscarProd(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        int PLACE_PICKER_REQUEST = 1;
        sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
        if(!sharedPreferences.contains("Lat")) {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                HomeActivity.ubicacion.setTitle(place.getAddress());
                HomeActivity.ubicacion.setCheckable(false);
                sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
                // get editor to edit in file
                editor = sharedPreferences.edit();
                editor.putString(Constants.UBICACION,place.getAddress().toString());
                editor.putString(Constants.LATITUD, String.valueOf(place.getLatLng().latitude));
                editor.putString(Constants.LONGITUD, String.valueOf(place.getLatLng().longitude));
                editor.apply();
                editor.commit();
            }
        }
        mapaShow = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mapa, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_favorite:
                cambiarUbicacion();
                break;
            case android.R.id.home:
                finish();
                //return true;
                break;
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void buscarProducto(final String nombre) {

        double lati, lng;
        sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
        if (sharedPreferences.contains(Constants.LATITUD) && !sharedPreferences.getString(Constants.LATITUD,"").equalsIgnoreCase("vacio") ) {
            lati = Double.parseDouble(sharedPreferences.getString(Constants.LATITUD, ""));
            lng = Double.parseDouble(sharedPreferences.getString(Constants.LONGITUD, ""));
            HttpService.getInstance().BuscarProductos(nombre,lati,lng,100, new HttpService.CustomCallListener<ProductosApi>() {
                @Override
                public void getResult(ProductosApi object) {
                    if (null != object){
                        // check objects and do whatever...
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        if(object.getProductos().isEmpty())
                            {
                                findViewById(R.id.msgErrorUbicacion).setVisibility(View.VISIBLE);
                                TextView msg = (TextView) findViewById(R.id.textMensajeErrorBuscar);
                                ImageView img = (ImageView) findViewById(R.id.imgMsgError);
                                img.setImageResource(R.drawable.lupa_error);
                                msg.setText(R.string.sinCoincidencias);
                            }
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(linearLayoutManager);

                        if(!object.getProductos().isEmpty()){
                            ProductosAdapter adapter = new ProductosAdapter(object.getProductos());
                            // lista =(ListView) findViewById(R.id.listaProductoSucursales);
                            recyclerView.setAdapter(adapter);
                            String TAG = null;
                            Log.i(TAG, "Artículo descargado: ");
                            }
                        }
                    else {
                        // ... do other stuff .. handle failure codes etc.
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        findViewById(R.id.msgErrorUbicacion).setVisibility(View.VISIBLE);
                        TextView msg = (TextView) findViewById(R.id.textMensajeErrorBuscar);
                        ImageView img = (ImageView) findViewById(R.id.imgMsgError);
                        img.setImageResource(R.drawable.carrito_triste);
                        msg.setText(R.string.problemaConServidor);
                    }
                }
            });
        } else {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            findViewById(R.id.msgErrorUbicacion).setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),"Falla conexion: "+connectionResult.getErrorMessage(),Toast.LENGTH_LONG).show();
    }

}
