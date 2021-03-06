package com.preciosclaros.modelo;

import com.preciosclaros.modelo.Producto;
import com.preciosclaros.modelo.Sucursales;

import java.util.ArrayList;

/**
 * Created by lucas on 11/6/2017.
 */

public class Response {

    public int getStatus() {

        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int status;
    private String errorMessage;
    //private  Sucursales mejorPrecio;

   /* public Sucursales getMejorPrecio() {
        return mejorPrecio;
    }*/

   /* public void setMejorPrecio(Sucursales mejorPrecio) {
        this.mejorPrecio = mejorPrecio;
    }*/

    Producto producto;
    ArrayList<Sucursales> sucursales = new ArrayList<Sucursales>();

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Response(Producto producto, ArrayList<Sucursales> sucursales , int status, String errorMessage) {
        this.producto = producto;
        this.sucursales = sucursales;
        this.status = status;
        this.errorMessage = errorMessage;

        //this.mejorPrecio = mejorPrecio;
    }

    public Producto getProducto() {

        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public ArrayList<Sucursales> getProductos() {
        return sucursales;
    }

    public void setProductos(ArrayList<Sucursales> productos) {
        this.sucursales = productos;
    }

}
