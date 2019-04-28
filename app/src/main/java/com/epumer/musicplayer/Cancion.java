package com.epumer.musicplayer;

public class Cancion {

    String urlPortada, urlCancion, titulo, key;

    public Cancion() {}

    public Cancion(String urlPortada, String urlCancion, String titulo) {
        this.urlPortada = urlPortada;
        this.urlCancion = urlCancion;
        this.titulo = titulo;
    }

    public String getUrlPortada() {
        return urlPortada;
    }

    public void setUrlPortada(String urlPortada) {
        this.urlPortada = urlPortada;
    }

    public String getUrlCancion() {
        return urlCancion;
    }

    public void setUrlCancion(String urlCancion) {
        this.urlCancion = urlCancion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getKey() { return key; }

    public void setKey(String key) { this.key = key; }
}
