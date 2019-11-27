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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.preciosclaros.adaptadores.ProductosAdapter;
import com.preciosclaros.adaptadores.SucursalesAdapter;
import com.preciosclaros.modelo.Producto;
import com.preciosclaros.modelo.ProductosApi;
import com.preciosclaros.modelo.Response;
import com.preciosclaros.modelo.Sucursales;
import com.preciosclaros.service.HttpService;
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
    @BindView(R.id.MejorPrecio)
    TextView precioProducto;
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
    @BindView(R.id.imgErrorVerProductoPorId)
    ImageView imgError;
    @BindView(R.id.textMensajeErrorBuscar)
    TextView msgError;

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
    @OnClick(R.id.btnReintentarBuscar)
    public void buscarDeNuevo() {
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
        if (!menuOcultar) {
            switch (item.getItemId()) {
                case R.id.filter:
                    CharSequence colors[] = new CharSequence[]{"Ordenar Por Precio", "Ordenar Por Cercania"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Filtrar");
                    builder.setItems(colors, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // the user clicked on colors[which]
                            switch (which) {
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
                                            return Double.compare(p1.getDistanciaNumero(), p2.getDistanciaNumero());
                                        }
                                    });
                                    linearLayoutManager = new LinearLayoutManager(context);
                                    recyclerView.setLayoutManager(linearLayoutManager);

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

        double lati, lng;
        sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
        if (sharedPreferences.contains("Lat")) {
            lati = Double.parseDouble(sharedPreferences.getString("Lat", ""));
            lng = Double.parseDouble(sharedPreferences.getString("Longitude", ""));
            HttpService.getInstance().GetProductoByID(codigo, lati, lng, 100, new HttpService.CustomCallListener<retrofit2.Response<Response>>() {
                @Override
                public void getResult(retrofit2.Response<Response> response) {
                    if (null != response) {
                        Producto received = response.body().getProducto();
                        if (response.body().getProductos() == null) {

                            if (actividad.equalsIgnoreCase("barcode")) {
                                menuOcultar = true;
                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                imgError.setImageResource(R.drawable.carrito_triste);
                                msgError.setText(R.string.productoSinPrecio);
                                findViewById(R.id.msgErrorUbicacion).setVisibility(View.VISIBLE);
                            } else {
                                menuOcultar = true;
                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                imgError.setImageResource(R.drawable.carrito_triste);
                                msgError.setText(R.string.productoSinPrecio);
                                findViewById(R.id.msgErrorUbicacion).setVisibility(View.VISIBLE);
                            }

                        } else {
                            menuOcultar = false;
                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                            VerSucursales = response.body().getProductos();
                            Collections.sort(response.body().getProductos(), new Comparator<Sucursales>() {
                                @Override
                                public int compare(Sucursales p1, Sucursales p2) {
                                    return (p1.getPreciosProducto().getPrecioLista()).compareTo(p2.getPreciosProducto().getPrecioLista());
                                }
                            });
                            mejorProducto = received;
                            Picasso.with(context).load("https://imagenes.preciosclaros.gob.ar/productos/" + codigo + ".jpg")
                                    .placeholder(R.drawable.image_placeholder)
                                    .error(R.drawable.no_image_aivalable)
                                    .into(imgProducto);
                            String mejorPrecio = response.body().getProductos().get(0).getPreciosProducto().getPrecioLista();
                            int i = 0;
                            while (mejorPrecio.isEmpty()) {
                                mejorPrecio = response.body().getProductos().get(i + 1).getPreciosProducto().getPrecioLista();
                                i++;
                            }
                            precioProducto.setText("$" + mejorPrecio);
                            nombreProducto.setText(received.getNombre());
                            ArrayList<Sucursales> sucursales = response.body().getProductos();
                            //mejorSucursal = response.body().getMejorPrecio();
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                            recyclerView.setLayoutManager(linearLayoutManager);

                            if (!sucursales.isEmpty()) {
                                SucursalesAdapter adapter = new SucursalesAdapter(sucursales, context, mejorSucursal);
                                // lista =(ListView) findViewById(R.id.listaProductoSucursales);
                                recyclerView.setAdapter(adapter);
                                Log.i(TAG, "Artículo descargado: ");
                            }
                        }
                    } else {
                        menuOcultar = true;
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        imgError.setImageResource(R.drawable.carrito_triste);
                        msgError.setText(R.string.problemaConServidor);
                        findViewById(R.id.msgErrorUbicacion).setVisibility(View.VISIBLE);
                    }
                }
            });

        } else {
            menuOcultar = true;
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            findViewById(R.id.msgErrorUbicacion).setVisibility(View.VISIBLE);
        }//-41.1349,-71.409954
        //OBTENER UBICACION
    }

    @Override
    public void onBackPressed() {
        if (actividad.equalsIgnoreCase("barcode")) {
            HomeActivity.backVerProducto = true;
        }
        super.onBackPressed();

    }
}