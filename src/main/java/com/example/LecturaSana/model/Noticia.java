package com.example.LecturaSana.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "Noticia")
public class Noticia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Título obligatorio")
    @Column(nullable = false)
    private String titulo;
    @NotBlank(message = "Descripción obligatoria")
    @Column(nullable = false, length = 300)
    private String descripcion;
    @NotBlank(message = "Contenido obligatorio")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenido;
    @NotBlank(message = "Imagen obligatoria")
    @Column(nullable = false, length = 500)
    private String imagen;

    // Este campo se copia de la tarjeta para armar la URL
    @Column(name = "apartado")
    private String apartado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarjeta_id", nullable = false)
    private TarjetaNovedad tarjetaNovedad;

    public Noticia() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getApartado() {
        return apartado;
    }

    public void setApartado(String apartado) {
        this.apartado = apartado;
    }

    public TarjetaNovedad getTarjetaNovedad() {
        return tarjetaNovedad;
    }

    public void setTarjetaNovedad(TarjetaNovedad tarjetaNovedad) {
        this.tarjetaNovedad = tarjetaNovedad;
    }
}
