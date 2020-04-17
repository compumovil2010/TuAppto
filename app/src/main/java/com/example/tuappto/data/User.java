package com.example.tuappto.data;

public class User {
    String nombre;
    String apellido;
    String Email;
    String telefono;

    public User() {
    }

    public User(String nombre, String apellido, String email, String telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        Email = email;
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return Email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
