package negocio;

import com.google.android.gms.maps.model.LatLng;

public class Appointment {
    private String owner;
    private String client;
    private String ownerImagePath;
    private String clientImagePath;

    private String clientName;
    private String clientSecondName;
    private String ownerName;
    private String ownerSecondName;
    private long clientPhone;
    private long ownerPhone;

    private LatLng location;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int min;
    private String address;

    public Appointment() {
    }

    public Appointment(String owner, String client, LatLng location, int year, int month, int day, int hour, int min) {
        this.owner = owner;
        this.client = client;
        this.location = location;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.min = min;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientSecondName() {
        return clientSecondName;
    }

    public void setClientSecondName(String clientSecondName) {
        this.clientSecondName = clientSecondName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerSecondName() {
        return ownerSecondName;
    }

    public void setOwnerSecondName(String ownerSecondName) {
        this.ownerSecondName = ownerSecondName;
    }

    public long getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(long clientPhone) {
        this.clientPhone = clientPhone;
    }

    public long getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(long ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getOwnerImagePath() {
        return ownerImagePath;
    }

    public void setOwnerImagePath(String ownerImagePath) {
        this.ownerImagePath = ownerImagePath;
    }

    public String getClientImagePath() {
        return clientImagePath;
    }

    public void setClientImagePath(String clientImagePath) {
        this.clientImagePath = clientImagePath;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }
}
