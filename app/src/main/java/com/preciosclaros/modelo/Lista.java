package com.preciosclaros.modelo;

import java.util.ArrayList;

/**
 * Created by lucas on 23/6/2017.
 */

public class Lista {
    /*"id": 9,
  "Nombre": "lista 1",
  "Descripcion": "bebidas de lucas",
  "Items"*/
    private int id;
    private  String Nombre;
    private  String Descripcion;
    ArrayList<Items> Items = new ArrayList<Items>();
    private float TotalLista;
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public float getTotalLista() {
        return TotalLista;
    }

    public void setTotalLista(float totalLista) {
        TotalLista = totalLista;
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

    public ArrayList<com.preciosclaros.modelo.Items> getItems() {
        return Items;
    }

    public void setItems(ArrayList<com.preciosclaros.modelo.Items> items) {
        Items = items;
    }
}
