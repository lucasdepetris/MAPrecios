package com.preciosclaros.modelo;

import java.text.DecimalFormat;

/**
 * Created by lucas on 22/6/2017.
 */

public class Productos {
    /*"producto": {
            "presentacion": "60.0 gr",
            "nombre": "Repelente Off Crema X60Gr",
            "id": "0000077900302",
            "marca": "OFF                           "
          },
          "Cantidad": 1,
          "precioOptimo": 20,
          "precioReal": null
          */
    private Producto producto;
    private int Cantidad;

    public Float getPrecioOptimo() {
        return precioOptimo;
    }

    // private DecimalFormat precioOptimo;
    //private DecimalFormat precioReal;
    private Float precioOptimo;
    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return Cantidad;
    }

    public void setCantidad(int cantidad) {
        Cantidad = cantidad;
    }

    /*public DecimalFormat getPrecioOptimo() {
        return precioOptimo;
    }

    public void setPrecioOptimo(DecimalFormat precioOptimo) {
        this.precioOptimo = precioOptimo;
    }

    public DecimalFormat getPrecioReal() {
        return precioReal;
    }

    public void setPrecioReal(DecimalFormat precioReal) {
        this.precioReal = precioReal;
    }*/
}
