package com.preciosclaros.modelo;

import java.util.ArrayList;

/**
 * Created by lucas on 19/6/2017.
 */

public class Listas {
    private int id;
    private String Nombre;
    private String Descripcion;
    private ArrayList<Sucursales> items = new ArrayList<Sucursales>();

    public ArrayList<Sucursales> getItems() {
        return items;
    }

    public void setItems(ArrayList<Sucursales> items) {
        this.items = items;
    }

    public Listas(String nombre, String descripcion) {
        Nombre = nombre;
        Descripcion = descripcion;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }
}
