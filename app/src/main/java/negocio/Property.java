package negocio;

import com.google.android.gms.maps.model.LatLng;

public class Property {
    String owner;
    int price;
    LatLng ubication;
    Boolean rent;
    Boolean sell;
    int rooms;
    int area;
    int parking;
    String description;

    public Property() {
    }

    public Property(String owner, int price, LatLng ubication, Boolean rent, Boolean sell, int rooms, int area, int parking, String description) {
        this.owner = owner;
        this.price = price;
        this.ubication = ubication;
        this.rent = rent;
        this.sell = sell;
        this.rooms = rooms;
        this.area = area;
        this.parking = parking;
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public int getPrice() {
        return price;
    }

    public LatLng getUbication() {
        return ubication;
    }

    public Boolean getRent() {
        return rent;
    }

    public Boolean getSell() {
        return sell;
    }

    public int getRooms() {
        return rooms;
    }

    public int getArea() {
        return area;
    }

    public int getParking() {
        return parking;
    }

    public String getDescription() {
        return description;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setUbication(LatLng ubication) {
        this.ubication = ubication;
    }

    public void setRent(Boolean rent) {
        this.rent = rent;
    }

    public void setSell(Boolean sell) {
        this.sell = sell;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public void setParking(int parking) {
        this.parking = parking;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
