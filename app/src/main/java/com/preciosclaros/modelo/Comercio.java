package com.preciosclaros.modelo;

/**
 * Created by lucas on 22/6/2017.
 */

public class Comercio {
    /*
    "sucursalNombre": "GARIN ",
        "comercioId": 12,
        "banderaDescripcion": "COTO CICSA",
        "banderaId": 1,
        "direccion": "Av. Belgrano 950",
        "localidad": "Garin",
        "provincia": "AR-B"
     */
    private String sucursalNombre;
    private int comercioId;
    private String banderaDescripcion;
    private int banderaId;
    private String direccion;
    private String localidad;
    private String provincia;

    public String getSucursalNombre() {
        return sucursalNombre;
    }

    public void setSucursalNombre(String sucursalNombre) {
        this.sucursalNombre = sucursalNombre;
    }

    public int getComercioId() {
        return comercioId;
    }

    public void setComercioId(int comercioId) {
        this.comercioId = comercioId;
    }

    public String getBanderaDescripcion() {
        return banderaDescripcion;
    }

    public void setBanderaDescripcion(String banderaDescripcion) {
        this.banderaDescripcion = banderaDescripcion;
    }

    public int getBanderaId() {
        return banderaId;
    }

    public void setBanderaId(int banderaId) {
        this.banderaId = banderaId;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }
}
