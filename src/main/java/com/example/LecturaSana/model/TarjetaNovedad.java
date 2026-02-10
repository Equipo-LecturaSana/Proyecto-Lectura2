package com.example.LecturaSana.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Entity
@Table(name = "tarjetaNovedad")
public class TarjetaNovedad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Título obligatorio")
    @Column(nullable = false)
    private String titulo;
    @NotBlank(message = "Descripción obligatoria")
    @Column(nullable = false)
    private String descripcion;
    @NotBlank(message = "Imagen obligatoria")
    @Column(nullable = false, length = 500)
    private String imagen;
    @NotBlank(message = "Apartado obligatorio")
    @Column(nullable = false, unique = true)
    private String apartado;

    @OneToMany(mappedBy = "tarjetaNovedad", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Noticia> noticias;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "libro_id")
    private Libro libro;

    public TarjetaNovedad() {
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

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public List<Noticia> getNoticias() {
        return noticias;
    }

    public void setNoticias(List<Noticia> noticias) {
        this.noticias = noticias;
    }
}
