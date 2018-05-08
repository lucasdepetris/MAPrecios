package com.preciosclaros;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.preciosclaros.adaptadores.ListasAdaptador;
import com.preciosclaros.modelo.Lista;
import com.preciosclaros.modelo.Listas;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lucas on 19/6/2017.
 */

public class MisListas extends AppCompatActivity {

    private PopupWindow pw;
    public Button Close,CrearLista;
    @OnClick(R.id.AgregarLista)public void AgregarLista(){
        showPopup();
    }
    @BindView(R.id.recyclerListas)
    RecyclerView recyclerView;
    private EditText nombre ;
    private EditText descripcion ;
    public final String TAG = "";
    public Context ctx = this;
    ApiPrecios service;
    public Call<ArrayList<Listas>> requestCatalog;
    private static final String PREFER_NAME = "Reg";
    private SharedPreferences sharedPreferences;
    int id;
    Listas listaEditar;
    @OnClick(R.id.btnReintentarBuscar)public void reintentarConectar(){
        findViewById(R.id.NoConectoServidor).setVisibility(View.GONE);
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        mostrarMisListas();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_listas);
        ButterKnife.bind(this);
        mostrarMisListas();
    }
    public void mostrarMisListas(){
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
        sharedPreferences = getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
        requestCatalog = service.getListas(sharedPreferences.getInt("id",id));
        requestCatalog.enqueue(new Callback<ArrayList<Listas>>() {
            @Override
            public void onResponse(Call<ArrayList<Listas>> call, retrofit2.Response<ArrayList<Listas>> response) {
                if (response.isSuccessful()) {
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    ArrayList<Listas> listas = response.body();
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ctx);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    ListasAdaptador adapter = new ListasAdaptador(listas,ctx);
                    // lista =(ListView) findViewById(R.id.listaProductoSucursales);
                    recyclerView.setAdapter(adapter);
                    Log.i(TAG, "Artículo descargado: ");
                } else {
                    int code = response.code();
                    String c = String.valueOf(code);
                    Toast.makeText(getApplicationContext(),"Lo sentimos, Ha ocurrido un problema con el servidor",Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<ArrayList<Listas>> call, Throwable t) {
                Log.e(TAG, "Error:" + t.getCause());
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                findViewById(R.id.NoConectoServidor).setVisibility(View.VISIBLE);
            }

        });
    }
    public void showPopup(){
        try {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.agregar_lista_dialog, null);
            dialogBuilder.setView(dialogView);
            final EditText nombreListaNueva = (EditText) dialogView.findViewById(R.id.NombreListaNueva);
            final EditText Descripcion = (EditText) dialogView.findViewById(R.id.DescripcionListaNueva);
            dialogBuilder.setTitle("Crear Lista");
            dialogBuilder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //do something with edt.getText().toString();
                    crearListaNueva(nombreListaNueva,Descripcion);
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
    public void crearListaNueva(EditText nombre,EditText descripcion){
        if(nombre.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "La lista no ha sido creada! Debes ingresar un Nombre", Toast.LENGTH_LONG).show();
        }else{
            sharedPreferences = getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
            Call<Listas> requestCrear = service.putLista(sharedPreferences.getInt("id",id),nombre.getText().toString(),descripcion.getText().toString());
            requestCrear.enqueue(new Callback<Listas>() {
                @Override
                public void onResponse(Call<Listas> call, retrofit2.Response<Listas> response) {
                    if(response.isSuccessful()){
                        sharedPreferences = getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
                        requestCatalog = service.getListas(sharedPreferences.getInt("id",id));
                        requestCatalog.enqueue(new Callback<ArrayList<Listas>>() {
                            @Override
                            public void onResponse(Call<ArrayList<Listas>> call, retrofit2.Response<ArrayList<Listas>> response) {
                                if (response.isSuccessful()) {
                                    ArrayList<Listas> listas = response.body();
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ctx);
                                    recyclerView.setLayoutManager(linearLayoutManager);
                                    ListasAdaptador adapter = new ListasAdaptador(listas,ctx);
                                    // lista =(ListView) findViewById(R.id.listaProductoSucursales);
                                    recyclerView.setAdapter(adapter);
                                    Log.i(TAG, "Artículo descargado: ");
                                } else {
                                    int code = response.code();
                                    String c = String.valueOf(code);
                                }
                            }
                            @Override
                            public void onFailure(Call<ArrayList<Listas>> call, Throwable t) {
                                Log.e(TAG, "Error:" + t.getCause());
                            }
                        });
                    }
                    else {
                        //SI NO CREA CORRECTAMENTE LA LISTA HACEMOS ESTO
                        Toast.makeText(getApplicationContext(),"Lo sentimos,la lista no ha sido creada",Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Listas> call, Throwable t) {
                    Toast.makeText(getApplicationContext(),"Lo sentimos, ha fallado la conexion con el servidor",Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
    public void showPopupEditarLista(Listas lista){
        try {
// We need to get the instance of the LayoutInflater
            listaEditar = lista;
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.agregar_lista_dialog, null);
            dialogBuilder.setView(dialogView);
            final EditText nombreListaNueva = (EditText) dialogView.findViewById(R.id.NombreListaNueva);
            final EditText Descripcion = (EditText) dialogView.findViewById(R.id.DescripcionListaNueva);
            nombreListaNueva.setText(lista.getNombre());
            Descripcion.setText(lista.getDescripcion());
            dialogBuilder.setTitle("Modificar Lista");
            dialogBuilder.setPositiveButton("Modificar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //do something with edt.getText().toString();
                    ModificarLista(nombreListaNueva,Descripcion);
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
    public void ModificarLista(EditText nombre,EditText descripcion){
        if (nombre.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "La lista no ha sido modificada ,Debes ingresar un nombre", Toast.LENGTH_LONG).show();
        } else {
            sharedPreferences = getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
            requestCatalog = service.modificarLista(listaEditar.getId(), nombre.getText().toString(), descripcion.getText().toString(), sharedPreferences.getInt("id", id));
            requestCatalog.enqueue(new Callback<ArrayList<Listas>>() {
                @Override
                public void onResponse(Call<ArrayList<Listas>> call, retrofit2.Response<ArrayList<Listas>> response) {
                    if (response.isSuccessful()) {
                        ArrayList<Listas> listas = response.body();
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ctx);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        ListasAdaptador adapter = new ListasAdaptador(listas, ctx);
                        // lista =(ListView) findViewById(R.id.listaProductoSucursales);
                        recyclerView.setAdapter(adapter);
                        Log.i(TAG, "Artículo descargado: ");
                    } else {
                        int code = response.code();
                        String c = String.valueOf(code);
                        Toast.makeText(getApplicationContext(),"Lo sentimos,la lista no se ha podido modificar",Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<ArrayList<Listas>> call, Throwable t) {
                    Log.e(TAG, "Error:" + t.getCause());
                    Toast.makeText(getApplicationContext(),"Lo sentimos, ha fallado la conexion con el servidor",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void showPopupEliminarLista(Listas lista){
        try {
            listaEditar = lista;
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle(R.string.Eliminar);
            dialogBuilder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //do something with edt.getText().toString();
                    EliminarLista();
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
    public void EliminarLista(){
        sharedPreferences = getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
        requestCatalog = service.eliminarLista(listaEditar.getId(),sharedPreferences.getInt("id",id));
        requestCatalog.enqueue(new Callback<ArrayList<Listas>>() {
            @Override
            public void onResponse(Call<ArrayList<Listas>> call, retrofit2.Response<ArrayList<Listas>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Listas> listas = response.body();
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ctx);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    ListasAdaptador adapter = new ListasAdaptador(listas,ctx);
                    // lista =(ListView) findViewById(R.id.listaProductoSucursales);
                    recyclerView.setAdapter(adapter);
                    Log.i(TAG, "Artículo descargado: ");
                } else {
                    int code = response.code();
                    String c = String.valueOf(code);
                    Toast.makeText(getApplicationContext(),"Lo sentimos,la lista no ha sido eliminada correctamente",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ArrayList<Listas>> call, Throwable t) {
                Log.e(TAG, "Error:" + t.getCause());
                Toast.makeText(getApplicationContext(),"Lo sentimos, ha fallado la conexion con el servidor",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
