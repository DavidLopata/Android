package com.example.campusapp.model;

public class LocationItem {

    private int id;
    private String name;
    private String category;
    private String description;
    private String address;
    private String contact;
    private double latitude;
    private double longitude;
    private boolean favorite;

    public LocationItem(int id, String name,String category, String description,
                        String address, String contact,
                        double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.address = address;
        this.contact = contact;
        this.latitude = latitude;
        this.longitude = longitude;
        this.favorite = false;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() {return category;}
    public String getDescription() { return description; }
    public String getAddress() { return address; }
    public String getContact() { return contact; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean favorite) {this.favorite = favorite;}
}
