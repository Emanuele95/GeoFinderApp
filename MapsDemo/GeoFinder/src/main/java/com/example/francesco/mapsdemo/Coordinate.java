package com.example.francesco.mapsdemo;

/**
 * Created by Francesco on 06/04/2018.
 */

public class Coordinate {

    private double lat;
    private double lng;
    private String nomeLuogo;
    private int idUser;



    public Coordinate(double lat, double lng, String nome, int idUser) {
        this.lat = lat;
        this.lng = lng;
        this.nomeLuogo = nome;
        this.idUser = idUser;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() { return lng; }

    public String getNomeLuogo() { return nomeLuogo; }

    public int getIdUser() { return idUser; }





}
