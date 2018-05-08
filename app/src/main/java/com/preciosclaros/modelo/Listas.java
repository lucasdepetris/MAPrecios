package com.preciosclaros.modelo;

import java.util.ArrayList;

/**
 * Created by lucas on 19/6/2017.
 */

public class Listas {
    private int id;
    private String Nombre;
    private String Descripcion;


    public Listas(int id, String nombre, String descripcion) {
        this.id = id;
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
