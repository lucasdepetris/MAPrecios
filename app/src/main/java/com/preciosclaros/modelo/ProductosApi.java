package com.preciosclaros.modelo;

import java.util.ArrayList;

/**
 * Created by desarrollo on 8/5/18.
 */

public class ProductosApi {
    /*
    "status": 200,
    "total": 45,
    "maxLimitPermitido": 100,
    "maxCantSucursalesPermitido": 50,
    productos,
    "totalPagina": 10
     */
    private int status;
    private int total;
    private int totalPagina;
    ArrayList<Producto> productos = new ArrayList<Producto>();
    private String errorMessage;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalPagina() {
        return totalPagina;
    }

    public void setTotalPagina(int totalPagina) {
        this.totalPagina = totalPagina;
    }

    public ArrayList<Producto> getProductos() {
        return productos;
    }

    public void setProductos(ArrayList<Producto> productos) {
        this.productos = productos;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ProductosApi(int status, int total, int totalPagina, ArrayList<Producto> productos, String errorMessage) {

        this.status = status;
        this.total = total;
        this.totalPagina = totalPagina;
        this.productos = productos;
        this.errorMessage = errorMessage;
    }
}
