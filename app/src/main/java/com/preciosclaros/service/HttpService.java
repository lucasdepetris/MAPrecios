package com.preciosclaros.service;

import android.util.Log;

import com.preciosclaros.modelo.ProductosApi;
import com.preciosclaros.modelo.Response;

import retrofit2.Call;
import retrofit2.Callback;

public class HttpService {

    private static HttpService instance = null;
    private String TAG = "HttpService";
    private String GENERIC_ERROR_MSG = "No se pudo conectar con el servidor";
    private String PARSE_JSON_ERROR = "No se pudo procesar la respuesta del servidor";


    private HttpService() {
    }

    public static HttpService getInstance() {
        if (instance == null) {
            instance = new HttpService();
        }
        return instance;
    }

    public interface CustomCallListener<T> {
        public void getResult(T object);
    }

    public void BuscarProductos(String nombre, double lati, double lng, int limit, final CustomCallListener<ProductosApi> listener) {
        Call<ProductosApi> requestProductos;

        requestProductos = Api.getInstance().getApiService().BuscarProductosC(nombre, lati, lng, limit);
        requestProductos.enqueue(new Callback<ProductosApi>() {
            @Override
            public void onResponse(Call<ProductosApi> call, retrofit2.Response<ProductosApi> response) {
                if (response.isSuccessful() && response.body().getErrorMessage() == null && response.body().getErrorDescription() == null) {
                    ProductosApi received = response.body();
                    listener.getResult(received);
                } else {
                    listener.getResult(null);
                }
            }

            @Override
            public void onFailure(Call<ProductosApi> call, Throwable t) {
                String TAG = null;
                Log.e(TAG, "Error:" + t.getCause());
                listener.getResult(null);
            }

        });
    }

    public void GetProductoByID(String codigo, double lati, double lng,int limit,final CustomCallListener<retrofit2.Response<Response>> listener){
        Call<Response> requestCatalog;
        requestCatalog = Api.getInstance().getApiService().getProductoC(codigo, lati, lng,20);
        requestCatalog.enqueue(new Callback<com.preciosclaros.modelo.Response>() {
            @Override
            public void onResponse(Call<com.preciosclaros.modelo.Response> call, retrofit2.Response<Response> response) {
                if (response.isSuccessful() && response.body().getErrorMessage() == null) {
                    listener.getResult(response);
                }else{
                    listener.getResult(null);
                }

            }

            @Override
            public void onFailure(Call<com.preciosclaros.modelo.Response> call, Throwable t) {
                Log.e(TAG, "Error:" + t.getMessage() + t.getCause());
                listener.getResult(null);
                //Toast.makeText(getApplicationContext(),"Hubo un problema de conexion al servidor",Toast.LENGTH_LONG).show();
            }

        });
    }
}
