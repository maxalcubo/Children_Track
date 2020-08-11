package com.amilcar.laura.childrentrack.models.firebase;

public class Acontecimiento {
    public String titulo;
    public String descripcion;
    public long fecha;
    public String urlMedia;

    public Acontecimiento(){

    }

    public Acontecimiento(String titulo, String descripcion, long fecha, String urlMedia) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.urlMedia = urlMedia;
    }
}
