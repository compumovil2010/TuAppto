package com.example.tuappto.data;

public class Inmueble {
    int antiguedad;
    double area;
    boolean arriendo;
    boolean venta;
    String Descripcion;
    int Estrato;
    int Habitaciones;
    int Num_Banios;
    boolean Parqueadero;
    double Precio;

    public Inmueble() {
    }

    public Inmueble(int antiguedad, double area, boolean arriendo, boolean venta, String descripcion, int estrato, int habitaciones, int num_Banios, boolean parqueadero, double precio) {
        this.antiguedad = antiguedad;
        this.area = area;
        this.arriendo = arriendo;
        this.venta = venta;
        Descripcion = descripcion;
        Estrato = estrato;
        Habitaciones = habitaciones;
        Num_Banios = num_Banios;
        Parqueadero = parqueadero;
        Precio = precio;
    }

    public int getAntiguedad() {
        return antiguedad;
    }

    public double getArea() {
        return area;
    }

    public boolean isArriendo() {
        return arriendo;
    }

    public boolean isVenta() {
        return venta;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public int getEstrato() {
        return Estrato;
    }

    public int getHabitaciones() {
        return Habitaciones;
    }

    public int getNum_Banios() {
        return Num_Banios;
    }

    public boolean isParqueadero() {
        return Parqueadero;
    }

    public double getPrecio() {
        return Precio;
    }

    public void setAntiguedad(int antiguedad) {
        this.antiguedad = antiguedad;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public void setArriendo(boolean arriendo) {
        this.arriendo = arriendo;
    }

    public void setVenta(boolean venta) {
        this.venta = venta;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public void setEstrato(int estrato) {
        Estrato = estrato;
    }

    public void setHabitaciones(int habitaciones) {
        Habitaciones = habitaciones;
    }

    public void setNum_Banios(int num_Banios) {
        Num_Banios = num_Banios;
    }

    public void setParqueadero(boolean parqueadero) {
        Parqueadero = parqueadero;
    }

    public void setPrecio(double precio) {
        Precio = precio;
    }
}
