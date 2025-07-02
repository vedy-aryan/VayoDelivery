package com.vayonics.model;

public class TrackableOrder {
    private String orderId;
    private String foodItem;
    private String droneId;
    private double lat;
    private double lng;

    // Getters and Setters
    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }

    public TrackableOrder() {} // For Firebase

    public TrackableOrder(String orderId, String foodItem, String droneId) {
        this.orderId = orderId;
        this.foodItem = foodItem;
        this.droneId = droneId;
    }

    public String getOrderId() { return orderId; }
    public String getFoodItem() { return foodItem; }
    public String getDroneId() { return droneId; }
}
