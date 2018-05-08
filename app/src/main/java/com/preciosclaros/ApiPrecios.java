package com.preciosclaros;

import com.preciosclaros.modelo.Lista;
import com.preciosclaros.modelo.Listas;
import com.preciosclaros.modelo.Producto;
import com.preciosclaros.modelo.Response;
import com.preciosclaros.modelo.Usuario;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by lucas on 11/6/2017.
 */

public interface ApiPrecios {
    @Headers("Content-Type: application/json")
    //API DE ELLOS
    @GET("producto")
    Call<Response>getProductoC(@Query ("id_producto") String id , @Query("lat") double latitud, @Query("lng") double longitud, @Query("limit") int limit);
    @GET("productos")
    Call<ArrayList<Producto>> BuscarProductosC(@Query ("string") String buscar , @Query("lat") double latitud, @Query("lng") double longitud, @Query("limit") int limit);

    //REQUEST API NUESTRA MAPRECIOS
    @GET("Productos/ObtenerProductoPorId")
    Call<Response> getProducto(@Query ("codigo") String id , @Query("lat") double latitud, @Query("lng") double longitud);
    @GET("Productos/BuscarProductos")
    Call<ArrayList<Producto>> BuscarProductos(@Query ("buscar") String buscar , @Query("lat") double latitud, @Query("lng") double longitud);
    @GET("Listas/ObtenerListas")
    Call<ArrayList<Listas>> getListas(@Query ("idUsuario") int id );
    @GET("Listas/ObtenerLista")
    Call<Lista> getLista(@Query ("idLista") int id );
    @GET("Listas/GenerarListaCercana")
    Call<Lista> generarListaCercana(@Query ("idLista") int id,@Query("lat") double lat, @Query("lng") double lng );
    @GET("Listas/GenerarListaBarata")
    Call<Lista> generarListaBarata(@Query ("idLista") int id,@Query("lat") double lat, @Query("lng") double lng );
    @POST("Listas/CrearLista")
    //se puede mejorar
    Call<Listas> putLista(@Query ("idUsuario") int id, @Query("nombre") String nombre, @Query("descripcion") String descripcion);
    @GET("Listas/ModificarLista")
    Call<ArrayList<Listas>> modificarLista(@Query ("idLista") int idLista ,@Query("nombre") String nombre, @Query("descripcion") String descripcion, @Query("idUsuario") int idUsuario );
    @GET("Listas/EliminarLista")
    Call<ArrayList<Listas>> eliminarLista(@Query ("idLista") int idLista , @Query("idUsuario") int idUsuario );
    @GET("Listas/ModificarCantidadDeUnProducto")
    Call<Lista> modificarCantidad(@Query("idLista") int idLista, @Query("idArticulo") String idArticulo, @Query("cantidad") int cantidad);
    @POST("Usuarios/ObtenerUsuarioPorIdGoogle")
    Call<Usuario> getUsuario(@Query("idGoogle") String id);
    @POST("Usuarios/Login")
    Call<Usuario> loginUsuario(@Body Usuario user);
    @POST("Listas/AgregarProducto")
    //se puede mejorar
    Call<Listas> AgregarProducto(@Query ("idLista") int id, @Query("idArticulo") String idArticulo, @Query("cantidad") int cantidad,
                                 @Query("precioOptimo") double preciOptimo, @Query("idComercio") String idComercio);
    @GET("Listas/EliminarProducto")
    Call<Lista> EliminarProducto( @Query("idArticulo") String idArticulo,@Query ("idLista") int idLista);

}
