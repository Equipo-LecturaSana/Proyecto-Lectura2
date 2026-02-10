package com.example.LecturaSana.model;

import java.util.List;

public class IndexLibros {

    private List<Libro> carrusel;
    private List<Libro> recomendaciones;
    private List<Libro> estiloFavorito;
    private List<Libro> colecciones;

    public IndexLibros(List<Libro> carrusel, List<Libro> recomendaciones, List<Libro> estiloFavorito, List<Libro> colecciones) {
        this.carrusel = carrusel;
        this.recomendaciones = recomendaciones;
        this.estiloFavorito = estiloFavorito;
        this.colecciones = colecciones;
    }

    public List<Libro> getCarrusel() { return carrusel; }
    public void setCarrusel(List<Libro> carrusel) { this.carrusel = carrusel; }

    public List<Libro> getRecomendaciones() { return recomendaciones; }
    public void setRecomendaciones(List<Libro> recomendaciones) { this.recomendaciones = recomendaciones; }

    public List<Libro> getEstiloFavorito() { return estiloFavorito; }
    public void setEstiloFavorito(List<Libro> estiloFavorito) { this.estiloFavorito = estiloFavorito; }

    public List<Libro> getColecciones() { return colecciones; }
    public void setColecciones(List<Libro> colecciones) { this.colecciones = colecciones; }
}

