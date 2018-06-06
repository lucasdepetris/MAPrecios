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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.preciosclaros.adaptadores.ProductosAdapter;
import com.preciosclaros.modelo.*;

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
    private IntentIntegrator qrScan;
    private boolean scan = false;
    static  boolean backVerProducto;
    private boolean mapaShow;
    public double latitud =0;
    public void buscarProd(String query) {
        findViewById(R.id.msgErrorUbicacion).setVisibility(View.GONE);
        findViewById(R.id.iconBuscar).setVisibility(View.GONE);
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        buscarProducto(query);
    }
    public void cambiarUbicacion(int request){
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if(scan) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            //if qrcode has nothing in it
            if(result != null) {
                if (result.getContents() != null) {
                    //if qr contains data
                    Intent intent = new Intent(BuscarProductos.this, VerProductoPorId.class);
                    intent.setFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra("actividad", "buscarproducto");
                    intent.putExtra("idProducto", result.getContents());
                    startActivity(intent);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mapa, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_favorite:
                cambiarUbicacion(1);
                break;
            case R.id.action_scan:
                sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
                if(!sharedPreferences.contains("Lat"))
                {
                    cambiarUbicacion(2);
                }
                if(sharedPreferences.contains("Lat"))
                {
                    escanear();
                }
                break;
        }
        return true;
    }

    public void buscarProducto(final String nombre) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor).build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .baseUrl("https://d735s5r2zljbo.cloudfront.net/prod/")
                .build();
        service = retrofit.create(ApiPrecios.class);

        double lati, lng;
        sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
        if (sharedPreferences.contains("Lat") && !sharedPreferences.getString("Lat","").equalsIgnoreCase("vacio") ) {
            lati = Double.parseDouble(sharedPreferences.getString("Lat", ""));
            lng = Double.parseDouble(sharedPreferences.getString("Longitude", ""));
            requestProductos = service.BuscarProductosC(nombre,lati,lng,100);
            requestProductos.enqueue(new Callback<ProductosApi>() {
                @Override
                public void onResponse(Call<ProductosApi> call, retrofit2.Response<ProductosApi> response) {
                    if (response.isSuccessful() && response.body().getErrorMessage() == null) {
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        ProductosApi received = response.body();
                        if(response.body().getProductos().isEmpty())
                        {
                            findViewById(R.id.msgErrorUbicacion).setVisibility(View.VISIBLE);
                            TextView msg = (TextView) findViewById(R.id.textMensajeErrorBuscar);
                            ImageView img = (ImageView) findViewById(R.id.imgMsgError);
                            img.setImageResource(R.drawable.lupa_error);
                            msg.setText(R.string.sinCoincidencias);
                        }
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(linearLayoutManager);
                        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                                getApplicationContext()
                        ));
                        if(!received.getProductos().isEmpty()){
                            ProductosAdapter adapter = new ProductosAdapter(received.getProductos());
                            // lista =(ListView) findViewById(R.id.listaProductoSucursales);
                            recyclerView.setAdapter(adapter);
                            String TAG = null;
                            Log.i(TAG, "Artículo descargado: ");
                        }
                    } else {
                        int code = response.code();
                        String c = String.valueOf(code);
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        findViewById(R.id.msgErrorUbicacion).setVisibility(View.VISIBLE);
                        TextView msg = (TextView) findViewById(R.id.textMensajeErrorBuscar);
                        ImageView img = (ImageView) findViewById(R.id.imgMsgError);
                        img.setImageResource(R.drawable.carrito_triste);
                        msg.setText(R.string.problemaConServidor);
                    }


                }

                @Override
                public void onFailure(Call<ProductosApi> call, Throwable t) {
                    String TAG = null;
                    Log.e(TAG, "Error:" + t.getCause());
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    findViewById(R.id.msgErrorUbicacion).setVisibility(View.VISIBLE);
                    TextView msg = (TextView) findViewById(R.id.textMensajeErrorBuscar);
                    ImageView img = (ImageView) findViewById(R.id.imgMsgError);
                    img.setImageResource(R.drawable.carrito_triste);
                    msg.setText(R.string.noConexion);
                }

            });
        } else {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            findViewById(R.id.msgErrorUbicacion).setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(),"Falla conexion: "+connectionResult.getErrorMessage(),Toast.LENGTH_LONG);
    }

}
