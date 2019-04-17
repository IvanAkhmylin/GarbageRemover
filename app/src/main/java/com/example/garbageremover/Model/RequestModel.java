package com.example.garbageremover.Model;

public class RequestModel {
    private String CustomerImageUri;
    private String CustomerName;
    private String Description;
    private String Address ;
    private String Payment ;
    private String Latitude;
    private String Longitude;
    public RequestModel(){

    }

    public RequestModel(String customerImageUri, String customerName, String description, String address, String payment, String latitude, String longitude) {
        CustomerImageUri = customerImageUri;
        CustomerName = customerName;
        Description = description;
        Address = address;
        Payment = payment;
        Latitude = latitude;
        Longitude = longitude;
    }

    public String getCustomerImageUri() {
        return CustomerImageUri;
    }

    public void setCustomerImageUri(String customerImageUri) {
        CustomerImageUri = customerImageUri;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPayment() {
        return Payment;
    }

    public void setPayment(String payment) {
        Payment = payment;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }
}
