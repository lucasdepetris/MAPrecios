package com.preciosclaros.modelo;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by lucas on 15/6/2017.
 */

public class PreciosProducto {
    private String precioLista;

    public String getPrecioLista() {
        return precioLista;
    }

    public void setPrecioLista(String precioLista) {
        this.precioLista = precioLista;
    }

    public PreciosProducto(String  precioLista) {

        this.precioLista = precioLista;
    }
}
