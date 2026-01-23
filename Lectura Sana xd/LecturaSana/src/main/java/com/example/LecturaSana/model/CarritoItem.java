package com.example.LecturaSana.model;

import jakarta.persistence.*;

@Entity
@Table(name = "carrito_items")
public class CarritoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "libro_id", nullable = false)
    private Long libroId;

    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo;

    @Column(name = "precio", nullable = false)
    private Double precio;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "imagen", length = 500)
    private String imagen;

    // Session ID para identificar el carrito sin usuario
    @Column(name = "session_id", length = 100)
    private String sessionId;

    // Constructor vacío
    public CarritoItem() {
    }

    // Constructor para uso en el controlador
    public CarritoItem(Long libroId, String titulo, Double precio, Integer cantidad, String imagen, String sessionId) {
        this.libroId = libroId;
        this.titulo = titulo;
        this.precio = precio;
        this.cantidad = cantidad;
        this.imagen = imagen;
        this.sessionId = sessionId;
    }

    // Constructor original (para compatibilidad)
    public CarritoItem(Long id, Long libroId, String titulo, Double precio, Integer cantidad, String imagen) {
        this.id = id;
        this.libroId = libroId;
        this.titulo = titulo;
        this.precio = precio;
        this.cantidad = cantidad;
        this.imagen = imagen;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLibroId() {
        return libroId;
    }

    public void setLibroId(Long libroId) {
        this.libroId = libroId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}