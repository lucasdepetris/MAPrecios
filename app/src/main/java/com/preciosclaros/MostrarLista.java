package com.preciosclaros;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
import com.preciosclaros.adaptadores.ListasAdaptador;
import com.preciosclaros.adaptadores.MiListaAdaptador;
import com.preciosclaros.modelo.Items;
import com.preciosclaros.modelo.Lista;
import com.preciosclaros.modelo.Listas;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lucas on 22/6/2017.
 */

public class MostrarLista extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    public Context ctx = this;
    ApiPrecios service;
    private PopupWindow pw;
    public Button Close,Aceptar;
    private EditText cantidad ;
    private String idArticulo;
    private int id;
    private int CantidadAnt;
    public SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private  Lista listaCabecera;
    private int PLACE_PICKER_REQUEST = 1;
    private GoogleApiClient mGoogleApiClient;
    private boolean original,cercana,barata,mapaShow;
    @BindView(R.id.perdida) TextView txtPerdidas;
    @BindView(R.id.ahorro)TextView txtAhorro;
    @BindView(R.id.BtnListaOriginal)Button btnListaOriginal;
    @BindView(R.id.BtnListaCercana)Button btnListaCercana;
    @BindView(R.id.BtnListaBarata)Button btnListaBarata;
    @OnClick(R.id.btnReintentarBuscar)public void reintentarConectar(){
        findViewById(R.id.NoConectoServidor).setVisibility(View.GONE);
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        MostrarLista(id);
    }
    @OnClick({R.id.BtnListaOriginal,R.id.BtnListaCercana,R.id.BtnListaBarata})public void optimizarLista(Button btn){
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        txtAhorro.setVisibility(View.GONE);
        txtPerdidas.setVisibility(View.GONE);
        switch (btn.getId()){
            case R.id.BtnListaOriginal:
                btnListaOriginal.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_dark));
                original = true;
                cercana = false;
                barata = false;
                btnListaCercana.setPressed(false);
                btnListaBarata.setPressed(false);
                btnListaOriginal.setPressed(true);
                btnListaOriginal.setTextColor(ContextCompat.getColor(this,R.color.white));
                btnListaCercana.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
                btnListaCercana.setTextColor(ContextCompat.getColor(this,R.color.md_black_1000));
                btnListaBarata.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
                btnListaBarata.setTextColor(ContextCompat.getColor(this,R.color.md_black_1000));
                MostrarLista(id);
                break;
            case R.id.BtnListaCercana:
                sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
                if(!sharedPreferences.contains("Lat"))
                {
                    elegirUbicacion(2);
                }
                MostrarListaCercana();
                break;
            case R.id.BtnListaBarata:
                sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
                if(!sharedPreferences.contains("Lat") )
                {
                    elegirUbicacion(3);
                }
                MostrarListaEconomica();
                break;
        }
    }

    @BindView(R.id.ReciclerContenidoLista) RecyclerView recicler;
    @BindView(R.id.VerNombreLista)TextView txt;
    @BindView(R.id.VerDescripcionLista)TextView txt2;
    @BindView(R.id.TotalDeListaDeclarado) TextView totalLista;
    public Call<Lista> requestCatalog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mostrar_lista);
        ButterKnife.bind(this);
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        Intent intent = getIntent();
        int i = 0;
        id = intent.getIntExtra("idLista",i);
        btnListaOriginal.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_dark));
        btnListaOriginal.setTextColor(ContextCompat.getColor(this,R.color.white));
        MostrarLista(id);
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
                elegirUbicacion(PLACE_PICKER_REQUEST);
                break;
        }
        return true;
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                HomeActivity.ubicacion.setTitle(place.getAddress());
                HomeActivity.ubicacion.setCheckable(false);
                sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
                // get editor to edit in file
                editor = sharedPreferences.edit();
                editor.putString("ubicacion",place.getAddress().toString());
                editor.putString("Lat", String.valueOf(place.getLatLng().latitude));
                editor.putString("Longitude", String.valueOf(place.getLatLng().longitude));
                editor.apply();
                editor.commit();
                if(cercana)
                {
                    MostrarListaCercana();
                }
                if(barata)
                {
                    MostrarListaEconomica();
                }
            }
        }
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                HomeActivity.ubicacion.setTitle(place.getAddress());
                HomeActivity.ubicacion.setCheckable(false);
                sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
                // get editor to edit in file
                editor = sharedPreferences.edit();
                editor.putString("ubicacion",place.getAddress().toString());
                editor.putString("Lat", String.valueOf(place.getLatLng().latitude));
                editor.putString("Longitude", String.valueOf(place.getLatLng().longitude));
                editor.apply();
                editor.commit();
                MostrarListaCercana();
            }
        }
        if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                HomeActivity.ubicacion.setTitle(place.getAddress());
                HomeActivity.ubicacion.setCheckable(false);
                sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
                // get editor to edit in file
                editor = sharedPreferences.edit();
                editor.putString("ubicacion",place.getAddress().toString());
                editor.putString("Lat", String.valueOf(place.getLatLng().latitude));
                editor.putString("Longitude", String.valueOf(place.getLatLng().longitude));
                editor.apply();
                editor.commit();
                MostrarListaEconomica();
            }
        }
        mapaShow = false;
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void MostrarLista(int id){
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
                .baseUrl("http://159.203.82.82/api/")
                .build();
        service = retrofit.create(ApiPrecios.class);

        requestCatalog = service.getLista(id);
        requestCatalog.enqueue(new Callback<Lista>() {
            @Override
            public void onResponse(Call<Lista> call, retrofit2.Response<Lista> response) {
                if (response.isSuccessful()) {
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    Lista lista = response.body();
                    if(lista.getItems().isEmpty())
                    {
                        findViewById(R.id.botonesOptimizar).setVisibility(View.GONE);
                        findViewById(R.id.listaVacia).setVisibility(View.VISIBLE);
                        TextView msg = (TextView) findViewById(R.id.msgListaVacia);
                        msg.setText("Su lista "+lista.getNombre()+" está vacía, busque productos y agreguelos.");
                    }
                    listaCabecera = lista;
                    ArrayList<Items> items = lista.getItems();
                    txt.setText(lista.getNombre());
                    txt2.setText(lista.getDescripcion());
                    totalLista.setText("$"+lista.getTotalLista());
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ctx);
                    recicler.setLayoutManager(linearLayoutManager);
                    recicler.addItemDecoration(new SimpleDividerItemDecoration(
                            getApplicationContext()
                    ));
                    MiListaAdaptador adapter = new MiListaAdaptador(items,ctx,false);
                    // lista =(ListView) findViewById(R.id.listaProductoSucursales);
                    recicler.setAdapter(adapter);
                    String TAG = null;
                    Log.i(TAG, "Artículo descargado: ");
                } else {
                    Toast.makeText(getApplicationContext(),"Lo sentimos, Ha ocurrido un problema con el servidor",Toast.LENGTH_SHORT).show();
                    int code = response.code();
                    String c = String.valueOf(code);
                }
            }
            @Override
            public void onFailure(Call<Lista> call, Throwable t) {
                String TAG = null;
                Log.e(TAG, "Error:" + t.getCause());
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                findViewById(R.id.NoConectoServidor).setVisibility(View.VISIBLE);
            }
        });
    }
    public void MostrarListaCercana(){
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        double lati ,lng;
        if(sharedPreferences.contains("Lat")){
            btnListaCercana.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_dark));
            original = false;
            cercana = true;
            barata = false;
            btnListaCercana.setPressed(true);
            btnListaBarata.setPressed(false);
            btnListaOriginal.setPressed(false);
            btnListaCercana.setTextColor(ContextCompat.getColor(this,R.color.white));
            btnListaOriginal.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
            btnListaOriginal.setTextColor(ContextCompat.getColor(this,R.color.md_black_1000));
            btnListaBarata.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
            btnListaBarata.setTextColor(ContextCompat.getColor(this,R.color.md_black_1000));
            lati = Double.parseDouble(sharedPreferences.getString("Lat",""));
            lng = Double.parseDouble(sharedPreferences.getString("Longitude",""));
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
                    .baseUrl("http://159.203.82.82/api/")
                    .build();
            service = retrofit.create(ApiPrecios.class);

            requestCatalog = service.generarListaCercana(id,lati,lng);
            requestCatalog.enqueue(new Callback<Lista>() {
                @Override
                public void onResponse(Call<Lista> call, retrofit2.Response<Lista> response) {
                    if (response.isSuccessful()) {
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        Lista lista = response.body();
                        if(lista.getError() != null)
                        {
                            findViewById(R.id.msgErrorListaOptima).setVisibility(View.VISIBLE);
                        }
                        ArrayList<Items> items = lista.getItems();
                        txt.setText(lista.getNombre());
                        txt2.setText(lista.getDescripcion());
                        if(lista.getTotalLista() > 0.0)
                        {
                            totalLista.setText("$"+lista.getTotalLista());
                        }
                        if(listaCabecera.getTotalLista() < lista.getTotalLista())
                        {
                            float ahorrado = lista.getTotalLista() - listaCabecera.getTotalLista();
                            txtPerdidas.setText("+$"+String.format("%.2f", ahorrado));
                            txtPerdidas.setVisibility(View.VISIBLE);
                        }
                        if(listaCabecera.getTotalLista() > lista.getTotalLista())
                        {
                            float ahorrado = listaCabecera.getTotalLista() - lista.getTotalLista();
                            txtAhorro.setText("-$"+String.format("%.2f", ahorrado));
                            txtAhorro.setVisibility(View.VISIBLE);
                        }
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ctx);
                        recicler.setLayoutManager(linearLayoutManager);
                        recicler.addItemDecoration(new SimpleDividerItemDecoration(
                                getApplicationContext()
                        ));
                        MiListaAdaptador adapter = new MiListaAdaptador(items,ctx,true);
                        // lista =(ListView) findViewById(R.id.listaProductoSucursales);
                        recicler.setAdapter(adapter);
                        String TAG = null;
                        Log.i(TAG, "Artículo descargado: ");
                    } else {
                        int code = response.code();
                        String c = String.valueOf(code);
                        Toast.makeText(getApplicationContext(),"Lo sentimos, Ha ocurrido un problema con el servidor",Toast.LENGTH_SHORT).show();
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        findViewById(R.id.msgErrorListaOptima).setVisibility(View.VISIBLE);
                    }


                }

                @Override
                public void onFailure(Call<Lista> call, Throwable t) {
                    String TAG = null;
                    Log.e(TAG, "Error:" + t.getCause());
                    Toast.makeText(getApplicationContext(),"Lo sentimos, ha fallado la conexión con el servidor",Toast.LENGTH_SHORT).show();
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    findViewById(R.id.msgErrorListaOptima).setVisibility(View.VISIBLE);
                }

            });
        }
        else {
                    /* findViewById(R.id.msgErrorListaOptima).setVisibility(View.VISIBLE);
                    TextView msg = (TextView) findViewById(R.id.textMensajeErrorLista);
                    msg.setText("No se ha podido obtener su ubicacion, intente nuevamente eligiendo una ubicacion");
                    ImageView img = (ImageView) findViewById(R.id.imgMsgError);
                    img.setImageResource(R.drawable.mapa_error);*/
            Toast.makeText(getApplicationContext(),"No se ha podido obtener su ubicación, intente nuevamente eligiendo una ubicación",Toast.LENGTH_SHORT).show();
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }
    }
    public void MostrarListaEconomica(){
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
        double lati ,lng;
        if(sharedPreferences.contains("Lat")){
            btnListaBarata.setBackgroundColor(ContextCompat.getColor(this, R.color.primary_dark));
            original = false;
            cercana = false;
            barata = true;
            btnListaBarata.setPressed(true);
            btnListaCercana.setPressed(false);
            btnListaOriginal.setPressed(false);
            btnListaBarata.setTextColor(ContextCompat.getColor(this,R.color.white));
            btnListaOriginal.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
            btnListaOriginal.setTextColor(ContextCompat.getColor(this,R.color.md_black_1000));
            btnListaCercana.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
            btnListaCercana.setTextColor(ContextCompat.getColor(this,R.color.md_black_1000));
            lati = Double.parseDouble(sharedPreferences.getString("Lat",""));
            lng = Double.parseDouble(sharedPreferences.getString("Longitude",""));
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
                    .baseUrl("http://maprecios.azurewebsites.net/")
                    .build();
            service = retrofit.create(ApiPrecios.class);

            requestCatalog = service.generarListaBarata(id,lati,lng);
            requestCatalog.enqueue(new Callback<Lista>() {
                @Override
                public void onResponse(Call<Lista> call, retrofit2.Response<Lista> response) {
                    if (response.isSuccessful()) {
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        Lista lista = response.body();
                        if(lista.getError() != null)
                        {
                            findViewById(R.id.msgErrorListaOptima).setVisibility(View.VISIBLE);
                        }
                        ArrayList<Items> items = lista.getItems();
                        txt.setText(listaCabecera.getNombre());
                        txt2.setText(listaCabecera.getDescripcion());
                        totalLista.setText("$"+lista.getTotalLista());
                        if(listaCabecera.getTotalLista() < lista.getTotalLista())
                        {
                            float ahorrado = lista.getTotalLista() - listaCabecera.getTotalLista();
                            txtPerdidas.setText("+$"+String.format("%.2f", ahorrado));
                            txtPerdidas.setVisibility(View.VISIBLE);
                        }
                        if(listaCabecera.getTotalLista() > lista.getTotalLista())
                        {
                            float ahorrado = listaCabecera.getTotalLista() - lista.getTotalLista();
                            txtAhorro.setText("-$"+String.format("%.2f", ahorrado));
                            txtAhorro.setVisibility(View.VISIBLE);
                        }
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ctx);
                        recicler.setLayoutManager(linearLayoutManager);
                        recicler.addItemDecoration(new SimpleDividerItemDecoration(
                                getApplicationContext()
                        ));
                        MiListaAdaptador adapter = new MiListaAdaptador(items,ctx,true);
                        // lista =(ListView) findViewById(R.id.listaProductoSucursales);
                        recicler.setAdapter(adapter);
                        String TAG = null;
                        Log.i(TAG, "Artículo descargado: ");
                    } else {
                        int code = response.code();
                        String c = String.valueOf(code);
                        Toast.makeText(getApplicationContext(),"Lo sentimos, Ha ocurrido un problema con el servidor",Toast.LENGTH_SHORT).show();
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        findViewById(R.id.msgErrorListaOptima).setVisibility(View.VISIBLE);
                    }


                }

                @Override
                public void onFailure(Call<Lista> call, Throwable t) {
                    String TAG = null;
                    Log.e(TAG, "Error:" + t.getCause());
                    Toast.makeText(getApplicationContext(),"Lo sentimos, ha fallado la conexión con el servidor",Toast.LENGTH_SHORT).show();
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    findViewById(R.id.msgErrorListaOptima).setVisibility(View.VISIBLE);
                }

            });
        }
        else {
                   /* findViewById(R.id.msgErrorListaOptima).setVisibility(View.VISIBLE);
                    TextView msg = (TextView) findViewById(R.id.textMensajeErrorLista);
                    msg.setText("No se ha podido obtener su ubicacion, intente nuevamente eligiendo una ubicacion");
                    ImageView img = (ImageView) findViewById(R.id.imgMsgError);
                    img.setImageResource(R.drawable.mapa_error);*/
            Toast.makeText(getApplicationContext(),"No se ha podido obtener su ubicación, intente nuevamente eligiendo una ubicación",Toast.LENGTH_SHORT).show();
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }
    }
    public void showPopup(String cantidadAnt, String idArticulo){
        try {
            // We need to get the instance of the LayoutInflater
            this.idArticulo = idArticulo;
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.modificar_cantidad_dialog, null);
            final EditText cant = (EditText)dialogView.findViewById(R.id.Cantidad);
            cant.setText(cantidadAnt);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setTitle("Modificar Cantidad");
            dialogBuilder.setPositiveButton("Modificar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //do something with edt.getText().toString();
                    ModificarCantidadProducto(cant);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void ModificarCantidadProducto(EditText cant){
        if (cant.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "La cantidad no ha sido modificada, ingresa un valor valido", Toast.LENGTH_LONG).show();
        }else {
            int c = Integer.parseInt(cant.getText().toString());
            requestCatalog = service.modificarCantidad(id, idArticulo, c);
            requestCatalog.enqueue(new Callback<Lista>() {
                @Override
                public void onResponse(Call<Lista> call, retrofit2.Response<Lista> response) {
                    if (response.isSuccessful()) {
                        Lista lista = response.body();
                        listaCabecera = lista;
                        ArrayList<Items> items = lista.getItems();
                        txt.setText(lista.getNombre());
                        txt2.setText(lista.getDescripcion());
                        totalLista.setText("Total:$"+lista.getTotalLista());
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ctx);
                        recicler.setLayoutManager(linearLayoutManager);
                        recicler.addItemDecoration(new SimpleDividerItemDecoration(
                                getApplicationContext()
                        ));
                        MiListaAdaptador adapter = new MiListaAdaptador(items, ctx,false);
                        // lista =(ListView) findViewById(R.id.listaProductoSucursales);
                        recicler.setAdapter(adapter);
                        String TAG = null;
                        Log.i(TAG, "Artículo descargado: ");
                    } else {
                        int code = response.code();
                        String c = String.valueOf(code);
                        Toast.makeText(getApplicationContext(),"Lo sentimos, no se ha podido modificar la cantidad",Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Lista> call, Throwable t) {
                    String TAG = null;
                    Log.e(TAG, "Error:" + t.getCause());
                    Toast.makeText(getApplicationContext(),"Lo sentimos, ha fallado la conexión con el servidor",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void showPopupEliminarProducto(String idArticulo){
        try {
            this.idArticulo = idArticulo;
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle(R.string.Eliminar);
            dialogBuilder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //do something with edt.getText().toString();
                  EliminarProducto();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void EliminarProducto(){
        requestCatalog = service.EliminarProducto(idArticulo,id);
        requestCatalog.enqueue(new Callback<Lista>() {
            @Override
            public void onResponse(Call<Lista> call, retrofit2.Response<Lista> response) {
                if (response.isSuccessful()) {
                    Lista lista = response.body();
                    listaCabecera = lista;
                    ArrayList<Items> items = lista.getItems();
                    if(lista.getItems().isEmpty())
                    {
                        findViewById(R.id.botonesOptimizar).setVisibility(View.GONE);
                        findViewById(R.id.listaVacia).setVisibility(View.VISIBLE);
                        TextView msg = (TextView) findViewById(R.id.msgListaVacia);
                        msg.setText("Su lista "+lista.getNombre()+" está vacía, busque productos y agreguelos.");
                    }
                    txt.setText(lista.getNombre());
                    txt2.setText(lista.getDescripcion());
                    totalLista.setText("Total:$"+lista.getTotalLista());
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ctx);
                    recicler.setLayoutManager(linearLayoutManager);
                    recicler.addItemDecoration(new SimpleDividerItemDecoration(
                            getApplicationContext()
                    ));
                    MiListaAdaptador adapter = new MiListaAdaptador(items,ctx,false);
                    // lista =(ListView) findViewById(R.id.listaProductoSucursales);
                    recicler.setAdapter(adapter);
                    String TAG = null;
                    Log.i(TAG, "Artículo descargado: ");
                } else {
                    int code = response.code();
                    String c = String.valueOf(code);
                    Toast.makeText(getApplicationContext(),"Lo sentimos, la lista no se ha podido eliminar",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Lista> call, Throwable t) {
                String TAG = null;
                Log.e(TAG, "Error:" + t.getCause());
                Toast.makeText(getApplicationContext(),"Lo sentimos, ha fallado la conexión con el servidor",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
