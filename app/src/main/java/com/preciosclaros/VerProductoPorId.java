package com.preciosclaros;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.preciosclaros.adaptadores.SucursalesAdapter;
import com.preciosclaros.modelo.Producto;
import com.preciosclaros.modelo.Response;
import com.preciosclaros.modelo.Sucursales;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
 * Created by lucas on 23/6/2017.
 */

public class VerProductoPorId extends AppCompatActivity {
    public final String TAG = "";
    //View Objects
    @BindView(R.id.MejorNombre)
    TextView nombreProducto;
    @BindView(R.id.MejorPrecio) TextView precioProducto;
    @BindView(R.id.MejorImgProducto)
    ImageView imgProducto;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    Producto mejorProducto;
    Sucursales mejorSucursal;
    public String id;
    ApiPrecios service;
    public Call<Response> requestCatalog;
    public Context context = this;
    public SharedPreferences sharedPreferences;
    private String actividad;
    private String idProducto;
    private ArrayList<Sucursales> VerSucursales;
    private boolean menuOcultar = false;
    @BindView(R.id.imgErrorVerProductoPorId)ImageView imgError;
    @BindView(R.id.textMensajeErrorBuscar) TextView msgError;
    /*
    @OnClick(R.id.agregarMejorPrecio)public void agregarProducto(){
        if(cargoSelect)
        {
            showPopup();
        }else{
            Toast.makeText(getApplicationContext(),"No se ha podido obtener sus listas",Toast.LENGTH_LONG).show();
            cargarSelect();
        }
    }*/
    @OnClick(R.id.btnReintentarBuscar)public void buscarDeNuevo(){
        findViewById(R.id.msgErrorUbicacion).setVisibility(View.GONE);
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        buscarProducto(idProducto);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.producto_por_codigo);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        actividad = intent.getStringExtra("actividad");
        idProducto = intent.getStringExtra("idProducto");
        buscarProducto(intent.getStringExtra("idProducto"));
        //cargarSelect();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.filter, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(!menuOcultar) {
            switch (item.getItemId()) {
                case R.id.filter:
                    CharSequence colors[] = new CharSequence[] {"Ordenar Por Precio", "Ordenar Por Cercania"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Filtrar");
                    builder.setItems(colors, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // the user clicked on colors[which]
                            switch (which){
                                case 0:
                                    findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                                    Collections.sort(VerSucursales, new Comparator<Sucursales>() {
                                        @Override
                                        public int compare(Sucursales p1, Sucursales p2) {
                                            return (p1.getPreciosProducto().getPrecioLista()).compareTo(p2.getPreciosProducto().getPrecioLista());
                                        }
                                    });
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                    recyclerView.setLayoutManager(linearLayoutManager);
                                    recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                                            getApplicationContext()
                                    ));
                                    SucursalesAdapter adapter = new SucursalesAdapter(VerSucursales, context, mejorSucursal);
                                    // lista =(ListView) findViewById(R.id.listaProductoSucursales);
                                    recyclerView.setAdapter(adapter);
                                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                    break;
                                case 1:
                                    findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                                    Collections.sort(VerSucursales, new Comparator<Sucursales>() {
                                        @Override
                                        public int compare(Sucursales p1, Sucursales p2) {
                                            return (p1.getDistanciaDescripcion()).compareTo(p2.getDistanciaDescripcion());
                                        }
                                    });
                                    linearLayoutManager = new LinearLayoutManager(context);
                                    recyclerView.setLayoutManager(linearLayoutManager);
                                    recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                                            getApplicationContext()
                                    ));
                                    adapter = new SucursalesAdapter(VerSucursales, context, mejorSucursal);
                                    // lista =(ListView) findViewById(R.id.listaProductoSucursales);
                                    recyclerView.setAdapter(adapter);
                                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                    break;
                            }
                        }
                    });
                    builder.show();
                    break;
            }
        }
        return true;
    }
    public void buscarProducto(final String codigo) {

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

        double lati ,lng;
        sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
        if(sharedPreferences.contains("Lat")){
            lati = Double.parseDouble(sharedPreferences.getString("Lat",""));
            lng = Double.parseDouble(sharedPreferences.getString("Longitude",""));
            requestCatalog = service.getProductoC(codigo, lati, lng,10);
            requestCatalog.enqueue(new Callback<com.preciosclaros.modelo.Response>() {
                @Override
                public void onResponse(Call<com.preciosclaros.modelo.Response> call, retrofit2.Response<Response> response) {
                    if (response.isSuccessful()) {
                        Producto received = response.body().getProducto();
                        if (response.body() == null) {

                            if(actividad.equalsIgnoreCase("barcode"))
                            {
                                menuOcultar = true;
                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                imgError.setImageResource(R.drawable.carrito_triste);
                                msgError.setText(R.string.productoSinPrecio);
                                findViewById(R.id.msgErrorUbicacion).setVisibility(View.VISIBLE);
                                /*VerProductoPorId.this.finish();
                                Intent intent = new Intent(VerProductoPorId.this, NoResultFound.class);
                                intent.setFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);*/
                            }
                            else{
                                menuOcultar = true;
                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                imgError.setImageResource(R.drawable.carrito_triste);
                                msgError.setText(R.string.productoSinPrecio);
                                findViewById(R.id.msgErrorUbicacion).setVisibility(View.VISIBLE);
                                /*VerProductoPorId.this.finish();
                                Intent intent = new Intent(VerProductoPorId.this, NoResultSearch.class);
                                intent.setFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);*/
                            }

                        } else {
                            menuOcultar = false;
                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                            VerSucursales = response.body().getProductos();
                            mejorProducto = received;
                            Picasso.with(context).load("https://imagenes.preciosclaros.gob.ar/productos/" + codigo + ".jpg")
                                    .placeholder(R.drawable.image_placeholder)
                                    .error(R.drawable.no_image_aivalable)
                                    .into(imgProducto);
                            precioProducto.setText("$" + response.body().getProductos().get(0).getPreciosProducto().getPrecioLista());
                            nombreProducto.setText(received.getNombre());
                            ArrayList<Sucursales> sucursales = response.body().getProductos();
                            //mejorSucursal = response.body().getMejorPrecio();
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                                    getApplicationContext()
                            ));
                            SucursalesAdapter adapter = new SucursalesAdapter(sucursales, context,mejorSucursal);
                            // lista =(ListView) findViewById(R.id.listaProductoSucursales);
                            recyclerView.setAdapter(adapter);
                            Log.i(TAG, "Artículo descargado: ");
                        }
                    } else {
                        menuOcultar = true;
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        imgError.setImageResource(R.drawable.carrito_triste);
                        msgError.setText(R.string.NotFound);
                        findViewById(R.id.msgErrorUbicacion).setVisibility(View.VISIBLE);
                        /*VerProductoPorId.this.finish();
                        Intent intent = new Intent(VerProductoPorId.this,NoResultFound.class);
                        intent.setFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);*/
                        int code = response.code();
                        String c = String.valueOf(code);
                    }
                }

                @Override
                public void onFailure(Call<com.preciosclaros.modelo.Response> call, Throwable t) {
                    Log.e(TAG, "Error:" + t.getMessage() + t.getCause());
                    menuOcultar = true;
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    imgError.setImageResource(R.drawable.carrito_triste);
                    msgError.setText(R.string.problemaConServidor);
                    findViewById(R.id.msgErrorUbicacion).setVisibility(View.VISIBLE);
                    //Toast.makeText(getApplicationContext(),"Hubo un problema de conexion al servidor",Toast.LENGTH_LONG).show();
                }

            });
        }else {
            menuOcultar = true;
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            findViewById(R.id.msgErrorUbicacion).setVisibility(View.VISIBLE);
        }//-41.1349,-71.409954
        //OBTENER UBICACION
    }

    @Override
    public void onBackPressed() {
        if(actividad.equalsIgnoreCase("barcode"))
        {
            HomeActivity.backVerProducto = true;
        }
        super.onBackPressed();

    }
}