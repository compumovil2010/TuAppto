package negocio;

import com.google.android.gms.maps.model.LatLng;

public class Property {

    int price;
    LatLng ubication;
    String sellOrRent;
    int rooms;
    int area;
    int parking;
    String description;

    public Property() {
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public LatLng getUbication() {
        return ubication;
    }

    public void setUbication(LatLng ubication) {
        this.ubication = ubication;
    }

    public String getSellOrRent() {
        return sellOrRent;
    }

    public void setSellOrRent(String sellOrRent) {
        this.sellOrRent = sellOrRent;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public int getParking() {
        return parking;
    }

    public void setParking(int parking) {
        this.parking = parking;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
