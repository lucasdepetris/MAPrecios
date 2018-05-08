package com.preciosclaros.modelo;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by lucas on 15/6/2017.
 */

public class PreciosProducto {
    private Double precioLista;

    public Double getPrecioLista() {
        return precioLista;
    }

    public void setPrecioLista(Double precioLista) {
        this.precioLista = precioLista;
    }

    public PreciosProducto(Double precioLista) {

        this.precioLista = precioLista;
    }
}
