package com.example.LecturaSana.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "carrito_items", indexes = {
    @Index(name = "idx_session_id", columnList = "session_id"),
    @Index(name = "idx_session_libro", columnList = "session_id, libro_id")
})
public class CarritoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "libro_id", nullable = false)
    @NotNull(message = "El ID del libro es obligatorio")
    private Long libroId;

    @Column(name = "titulo", nullable = false, length = 255)
    @NotBlank(message = "El título es obligatorio")
    @Size(max = 255, message = "El título no puede exceder 255 caracteres")
    private String titulo;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @Column(name = "cantidad", nullable = false)
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Max(value = 999, message = "La cantidad no puede exceder 999")
    private Integer cantidad;

    @Column(name = "imagen", length = 500)
    @Size(max = 500, message = "La URL de la imagen no puede exceder 500 caracteres")
    private String imagen;

    @Column(name = "session_id", length = 100)
    @NotBlank(message = "El ID de sesión es obligatorio")
    @Size(max = 100, message = "El ID de sesión no puede exceder 100 caracteres")
    private String sessionId;

    public CarritoItem() {
    }

    public CarritoItem(Long libroId, String titulo, BigDecimal precio, Integer cantidad, String imagen, String sessionId) {
        this.libroId = libroId;
        this.titulo = titulo;
        this.precio = precio;
        this.cantidad = cantidad;
        this.imagen = imagen;
        this.sessionId = sessionId;
    }

    public CarritoItem(Long id, Long libroId, String titulo, BigDecimal precio, Integer cantidad, String imagen) {
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

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
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

    /**
     * Calcula el subtotal del item (precio * cantidad)
     */
    public BigDecimal calcularSubtotal() {
        if (precio == null || cantidad == null) {
            return BigDecimal.ZERO;
        }
        return precio.multiply(BigDecimal.valueOf(cantidad))
                    .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Incrementa la cantidad del item
     */
    public void incrementarCantidad() {
        if (this.cantidad == null) {
            this.cantidad = 1;
        } else {
            this.cantidad++;
        }
    }

    /**
     * Decrementa la cantidad del item
     */
    public void decrementarCantidad() {
        if (this.cantidad != null && this.cantidad > 0) {
            this.cantidad--;
        }
    }

    /**
     * Verifica si el item es válido
     */
    public boolean esValido() {
        return libroId != null &&
               titulo != null && !titulo.trim().isEmpty() &&
               precio != null && precio.compareTo(BigDecimal.ZERO) > 0 &&
               cantidad != null && cantidad > 0 &&
               sessionId != null && !sessionId.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "CarritoItem{" +
                "id=" + id +
                ", libroId=" + libroId +
                ", titulo='" + titulo + '\'' +
                ", precio=" + precio +
                ", cantidad=" + cantidad +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}