package com.preciosclaros.modelo;

import java.util.Date;

/**
 * Created by lucas on 19/6/2017.
 */

public class Usuario {
    private int id;
    private String idGogle;
    private String Nombre, apellido, Email;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdGoogle() {
        return idGogle;
    }

    public void setIdGoogle(String idGoogle) {
        this.idGogle = idGoogle;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        this.Nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

   /* public Usuario(int id, String idGoogle, Date fechaRegistro, String nombre, String apellido, String email) {

        this.id = id;
        this.idGogle = idGoogle;
        this.fechaRegistro = fechaRegistro;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }*/
}
