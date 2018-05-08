package com.preciosclaros;

import android.*;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.preciosclaros.adaptadores.SucursalesAdapter;
import com.preciosclaros.modelo.Listas;
import com.preciosclaros.modelo.Producto;
import com.preciosclaros.modelo.Productos;
import com.preciosclaros.modelo.Response;
import com.preciosclaros.modelo.Sucursales;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
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
    private int idLista;
    private PopupWindow pw;
    EditText cantidad;
    Spinner spinner;
    public Button Close,CrearProd;
    public Sucursales sucursalElegida;
    @BindView(R.id.MejorNombre)
    TextView nombreProducto;
    @BindView(R.id.MejorPrecio) TextView precioProducto;
    @BindView(R.id.MejorImgProducto)
    ImageView imgProducto;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    Producto mejorProducto;
    Sucursales mejorSucursal;
    ArrayList<Listas> ls;
    String listElegida;
    int listChoose;
    public String id;
    ApiPrecios service;
    public Call<Response> requestCatalog;
    public Call<ArrayList<Listas>> requestListas;
    Call<Listas> requestListaAdaptador;
    public Context context = this;
    public SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private String actividad;
    private String idProducto;
    private ArrayList<Sucursales> VerSucursales;
    private boolean menuOcultar = false;
    private boolean cargoSelect = false;
    List<String> AirNum = new ArrayList<String>();
    @BindView(R.id.imgErrorVerProductoPorId)ImageView imgError;
    @BindView(R.id.textMensajeErrorBuscar) TextView msgError;
    @OnClick(R.id.agregarMejorPrecio)public void agregarProducto(){
        if(cargoSelect)
        {
            showPopup();
        }else{
            Toast.makeText(getApplicationContext(),"No se ha podido obtener sus listas",Toast.LENGTH_LONG).show();
            cargarSelect();
        }
    }
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
        cargarSelect();
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
                .baseUrl("http://159.203.82.82/api/")
                .build();
        service = retrofit.create(ApiPrecios.class);

        double lati ,lng;
        sharedPreferences = getApplicationContext().getSharedPreferences("Reg", 0);
        if(sharedPreferences.contains("Lat")){
            lati = Double.parseDouble(sharedPreferences.getString("Lat",""));
            lng = Double.parseDouble(sharedPreferences.getString("Longitude",""));
            requestCatalog = service.getProducto(codigo, lati, lng);
            requestCatalog.enqueue(new Callback<com.preciosclaros.modelo.Response>() {
                @Override
                public void onResponse(Call<com.preciosclaros.modelo.Response> call, retrofit2.Response<Response> response) {
                    if (response.isSuccessful()) {
                        Producto received = response.body().getProducto();
                        if (response.body().getMejorPrecio() == null) {

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
                            precioProducto.setText("$" + response.body().getMejorPrecio().getPreciosProducto().getPrecioLista());
                            nombreProducto.setText(received.getNombre());
                            ArrayList<Sucursales> sucursales = response.body().getProductos();
                            mejorSucursal = response.body().getMejorPrecio();
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
                    Log.e(TAG, "Error:" + t.getCause());
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
    public void cargarSelect(){
        int idUser = 0;
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
        sharedPreferences = getApplicationContext().getSharedPreferences("Reg", Context.MODE_PRIVATE);
        int idu = sharedPreferences.getInt("id",idUser);
        requestListas = service.getListas(idu);
        requestListas.enqueue(new Callback<ArrayList<Listas>>() {
            @Override
            public void onResponse(Call<ArrayList<Listas>> call, retrofit2.Response<ArrayList<Listas>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Listas> listas = response.body();
                    ls = listas;
                    for(int i = 0; i<=ls.size()-1;i++)
                    {
                        AirNum.add(i,ls.get(i).getNombre());
                    }
                    Log.i(TAG, "Artículo descargado: ");
                    cargoSelect = true;
                } else {
                    int code = response.code();
                    String c = String.valueOf(code);
                    Toast.makeText(getApplicationContext(),"No se ha podido obtener sus listas",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Listas>> call, Throwable t) {
                Log.e(TAG, "Error:" + t.getCause());
                Toast.makeText(getApplicationContext(),"No se ha podido obtener sus listas",Toast.LENGTH_LONG).show();
            }
        });

    }
    public void showPopup(){
        try {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
            dialogBuilder.setView(dialogView);
            final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
            final Spinner select = (Spinner)dialogView.findViewById(R.id.select1);
            select.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, AirNum));
            select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
                {
                    for(int i=0;i<=ls.size()-1;i++)
                    {
                        listElegida = adapterView.getItemAtPosition(position).toString();
                        if(ls.get(i).getNombre().equalsIgnoreCase(listElegida) )
                        {
                            listChoose = ls.get(i).getId();
                        }
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {
                    // vacio
                }});
            dialogBuilder.setTitle("Agregar Producto");
            dialogBuilder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //do something with edt.getText().toString();
                    agregarProductoAlista(edt);
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
    public  void  agregarProductoAlista(EditText cant){
        if(cant.getText().toString().isEmpty() || cant.getText().toString().equalsIgnoreCase("0")) {
            Toast.makeText(getApplicationContext(), "El producto no ha sido agregado, Debes ingresar una cantidad valida", Toast.LENGTH_LONG).show();
            }else {
            int c = Integer.parseInt(cant.getText().toString());
            double p = mejorSucursal.getPreciosProducto().getPrecioLista().doubleValue();
            Call<Listas> requestLista = service.AgregarProducto(listChoose, mejorProducto.getId().toString(), c,
                    p, mejorSucursal.getComercioId() + "-" + mejorSucursal.getBanderaId() + "-" + mejorSucursal.getId());
            requestLista.enqueue(new Callback<Listas>() {
                @Override
                public void onResponse(Call<Listas> call, retrofit2.Response<Listas> response) {
                    if (response.isSuccessful()) {
                        Listas received = response.body();
                        idLista = received.getId();
                        Log.i(TAG, "Artículo descargado: ");
                        showPopupSeAgregoCorrectamente();
                    } else {
                        int code = response.code();
                        String c = String.valueOf(code);
                        Toast.makeText(getApplicationContext(), "No se ha podido agregar exitosamente", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<Listas> call, Throwable t) {
                    Log.e(TAG, "Error:" + t.getCause());
                    Toast.makeText(getApplicationContext(), "No se ha podido conectar con el servidor", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    public void showPopupAdaptador( Sucursales sucursal){
        if(cargoSelect) {
            try {
                sucursalElegida = sucursal;
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
                dialogBuilder.setView(dialogView);

                final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
                final Spinner select = (Spinner) dialogView.findViewById(R.id.select1);
                select.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, AirNum));
                select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        for (int i = 0; i <= ls.size() - 1; i++) {
                            listElegida = adapterView.getItemAtPosition(position).toString();
                            if (ls.get(i).getNombre().equalsIgnoreCase(listElegida)) {
                                listChoose = ls.get(i).getId();
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // vacio
                    }
                });
                dialogBuilder.setTitle("Agregar Producto");
                dialogBuilder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //do something with edt.getText().toString();
                        agregarProductoAlistaAdaptador(edt);
                    }
                });
                dialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //pass
                    }
                });
                AlertDialog b = dialogBuilder.create();
                b.show();
                b.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSecondary));
                b.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorSecondary));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getApplicationContext(),"No se ha podido obtener sus listas",Toast.LENGTH_LONG).show();
            cargarSelect();
        }
    }
    public void agregarProductoAlistaAdaptador(EditText cant){
        if (cant.getText().toString().isEmpty() || cant.getText().toString().equalsIgnoreCase("0") ){
            Toast.makeText(getApplicationContext(), "El producto no ha sido agregado, Debes ingresar una cantidad valida", Toast.LENGTH_LONG).show();
        }else {
            int c = Integer.parseInt(cant.getText().toString());
            double p = sucursalElegida.getPreciosProducto().getPrecioLista().doubleValue();
            requestListaAdaptador = service.AgregarProducto(listChoose, mejorProducto.getId().toString(), c,
                    p,
                    sucursalElegida.getComercioId() + "-" + sucursalElegida.getBanderaId() + "-" + sucursalElegida.getId());
            requestListaAdaptador.enqueue(new Callback<Listas>() {
                @Override
                public void onResponse(Call<Listas> call, retrofit2.Response<Listas> response) {
                    if (response.isSuccessful()) {
                        Listas received = response.body();
                        idLista = received.getId();
                        Log.i(TAG, "Artículo Descargado: ");
                        showPopupSeAgregoCorrectamente();
                    } else {
                        int code = response.code();
                        String c = String.valueOf(code);
                        Toast.makeText(getApplicationContext(),"No se ha podido agregar exitosamente",Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<Listas> call, Throwable t) {
                    Log.e(TAG, "Error:" + t.getCause());
                    Toast.makeText(getApplicationContext(),"No se ha podido conectar con el servidor",Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    public void showPopupSeAgregoCorrectamente(){
        try {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            dialogBuilder.setTitle("Producto Agregado!");
            dialogBuilder.setPositiveButton("Ver Lista", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //do something with edt.getText().toString();
                    Intent intent = new Intent(VerProductoPorId.this,MostrarLista.class);
                    intent.putExtra("idLista",idLista);
                    startActivity(intent);
                }
            });
            dialogBuilder.setNegativeButton("Seguir Buscando", new DialogInterface.OnClickListener() {
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
    @Override
    public void onBackPressed() {
        if(actividad.equalsIgnoreCase("barcode"))
        {
            HomeActivity.backVerProducto = true;
        }
        super.onBackPressed();

    }
}
