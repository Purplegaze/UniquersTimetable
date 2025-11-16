package entity;

public class Building {
    //fields
    private String buildingName;
    private String address;
    private double latitude;
    private double longitude;

    //constructor
    public Building(String buildingName, String address, double latitude, double longitude) {
        this.buildingName = buildingName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getAddress() {
        return address;
    }

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
}