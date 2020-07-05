package com.amilcar.laura.childrentrack.modelosfirebase;

import com.google.firebase.database.Exclude;

public class Posicion {
     @Exclude
    String key;

    public double latitud;
    public double longitud;

    public Posicion(){

    }

    public Posicion(double latitud, double longitud){
        this.latitud = latitud;
        this.longitud = longitud;
    }
}
