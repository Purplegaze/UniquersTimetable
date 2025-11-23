package entity;

public class Building {
    //fields
    private String code;
    private String name;
    private String address;


    //constructor
    public Building(String code, String name, String address) {
        this.code = code;
        this.name= name;
        this.address = address;
    }

    public String getBuildingCode() {
        return code;
    }
    public void setBuildingCode(String buildingName) {
        this.code = code;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }


    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

}