package com.example.tuappto.data;

public class User {
    String nombre;
    String apellido;
    String Email;
    String contraseña;
    String telefono;

    public User() {
    }

    public User(String nombre, String apellido, String email, String contraseña, String telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        Email = email;
        this.contraseña = contraseña;
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

    public String getContraseña() {
        return contraseña;
    }

    public String getTelefono() {
        return telefono;
    }
}
