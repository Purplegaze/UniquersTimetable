package entity;

public class Building {
    //fields

    private String buildingCode;
    private String address;
    private double latitude;
    private double longitude;

    //constructor
    public Building(String buildingCode, String address, double latitude, double longitude) {
        this.buildingCode = buildingCode;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getBuildingCode() {return buildingCode;}

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getTimeTo(Building other) {
        // TODO: Implement this once DistanceMatrix API stuff is figured out
        return -1;
    }
}