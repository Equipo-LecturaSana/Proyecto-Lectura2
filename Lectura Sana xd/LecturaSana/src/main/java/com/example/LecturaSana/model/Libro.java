package com.example.LecturaSana.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 1, max = 255, message = "El título debe tener entre 1 y 255 caracteres")
    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo;

    @NotBlank(message = "El autor es obligatorio")
    @Size(min = 1, max = 255, message = "El autor debe tener entre 1 y 255 caracteres")
    @Column(name = "autor", nullable = false, length = 255)
    private String autor;

    @NotBlank(message = "La categoría es obligatoria")
    @Column(name = "categoria", nullable = false, length = 50)
    private String categoria;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El precio debe tener máximo 2 decimales")
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @NotBlank(message = "La imagen es obligatoria")
    @Column(name = "imagen", nullable = false, length = 500)
    private String imagen;

    @NotBlank(message = "La sinopsis es obligatoria")
    @Size(min = 10, max = 2000, message = "La sinopsis debe tener entre 10 y 2000 caracteres")
    @Column(name = "sinopsis", columnDefinition = "TEXT", nullable = false)
    private String sinopsis;

    @NotBlank(message = "El género es obligatorio")
    @Column(name = "genero", nullable = false, length = 50)
    private String genero = "General";

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(name = "stock", nullable = false)
    private Integer stock = 50;

    @Column(name = "novedad", nullable = false)
    private Boolean novedad = false;

    // Constructores
    public Libro() {}

    public Libro(String titulo, String autor, String categoria, 
                BigDecimal precio, String imagen, String sinopsis, 
                String genero, Integer stock, Boolean novedad) {
        this.titulo = titulo;
        this.autor = autor;
        this.categoria = categoria;
        this.precio = precio;
        this.imagen = imagen;
        this.sinopsis = sinopsis;
        this.genero = genero;
        this.stock = stock;
        this.novedad = novedad;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    
    public String getSinopsis() { return sinopsis; }
    public void setSinopsis(String sinopsis) { this.sinopsis = sinopsis; }
    
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    
    public Boolean getNovedad() { return novedad; }
    public void setNovedad(Boolean novedad) { this.novedad = novedad; }

    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", categoria='" + categoria + '\'' +
                ", precio=" + precio +
                ", stock=" + stock +
                ", novedad=" + novedad +
                '}';
    }
}