package com.preciosclaros.modelo;

/**
 * Created by lucas on 14/6/2017.
 */

public class Sucursales {
    private String comercioRazonSocial;
    private double distanciaNumero ;
    private String distanciaDescripcion;
    private int banderaId ;
    private boolean actualizadoHoy ;
    private String lat ;
    private String lng ;
    private String sucursalNombre ;
    private String id ;
    private String sucursalTipo ;
    private String provincia ;
    private String comercioId;
    private PreciosProducto preciosProducto;

    public PreciosProducto getPreciosProducto() {
        return preciosProducto;
    }

    public void setPreciosProducto(PreciosProducto preciosProducto) {
        this.preciosProducto = preciosProducto;
    }

    public String getComercioId() {
        return comercioId;
    }

    public void setComercioId(String comercioId) {
        this.comercioId = comercioId;
    }

    //private PreciosProducto preciosProducto ;
    private String direccion ;

    private String banderaDescripcion ;
    private String localidad ;

    public String getComercioRazonSocial() {
        return comercioRazonSocial;
    }

    public void setComercioRazonSocial(String comercioRazonSocial) {
        this.comercioRazonSocial = comercioRazonSocial;
    }

    public double getDistanciaNumero() {
        return distanciaNumero;
    }

    public void setDistanciaNumero(double distanciaNumero) {
        this.distanciaNumero = distanciaNumero;
    }

    public String getDistanciaDescripcion() {
        return distanciaDescripcion;
    }

    public void setDistanciaDescripcion(String distanciaDescripcion) {
        this.distanciaDescripcion = distanciaDescripcion;
    }

    public int getBanderaId() {
        return banderaId;
    }

    public void setBanderaId(int banderaId) {
        this.banderaId = banderaId;
    }

    public boolean isActualizadoHoy() {
        return actualizadoHoy;
    }

    public void setActualizadoHoy(boolean actualizadoHoy) {
        this.actualizadoHoy = actualizadoHoy;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getSucursalNombre() {
        return sucursalNombre;
    }

    public void setSucursalNombre(String sucursalNombre) {
        this.sucursalNombre = sucursalNombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSucursalTipo() {
        return sucursalTipo;
    }

    public void setSucursalTipo(String sucursalTipo) {
        this.sucursalTipo = sucursalTipo;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getBanderaDescripcion() {
        return banderaDescripcion;
    }

    public void setBanderaDescripcion(String banderaDescripcion) {
        this.banderaDescripcion = banderaDescripcion;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public Sucursales(String comercioRazonSocial, double distanciaNumero, String distanciaDescripcion,
                      int banderaId, boolean actualizadoHoy, String lat, String lng, String sucursalNombre,
                      String id, String sucursalTipo, String provincia, String direccion, String banderaDescripcion, String localidad,String comercioId,PreciosProducto preciosProducto)
    {
        this.comercioRazonSocial = comercioRazonSocial;
        this.distanciaNumero = distanciaNumero;
        this.distanciaDescripcion = distanciaDescripcion;
        this.banderaId = banderaId;
        this.actualizadoHoy = actualizadoHoy;
        this.lat = lat;
        this.lng = lng;
        this.sucursalNombre = sucursalNombre;
        this.id = id;
        this.comercioId = comercioId;
        this.sucursalTipo = sucursalTipo;
        this.provincia = provincia;
        this.direccion = direccion;
        this.banderaDescripcion = banderaDescripcion;
        this.localidad = localidad;
        this.preciosProducto = preciosProducto;
    }
}
