package com.example.francesco.mapsdemo;

/**
 * Created by Francesco on 09/04/2018.
 */

public class Globals {

    private static String email;
    private static double latitude;
    private static double longitude;

    Globals() {}

    public void setEmail(String s) { email = s; }
    public String getEmail() {
        return email;
    }

    public void setLatitude(double l) { latitude = l; }
    public double getLatitude() { return latitude; }

    public void setLongitude(double lo) { longitude = lo; }
    public double getLongitude() { return longitude; }

}
