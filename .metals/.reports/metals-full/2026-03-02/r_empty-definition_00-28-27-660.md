error id: file:///C:/Users/Usuario/Documents/GitHub/Proyecto-Lectura2/src/main/java/com/example/LecturaSana/model/Pedido.java:jakarta/validation/constraints/Pattern#
file:///C:/Users/Usuario/Documents/GitHub/Proyecto-Lectura2/src/main/java/com/example/LecturaSana/model/Pedido.java
empty definition using pc, found symbol in pc: jakarta/validation/constraints/Pattern#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 246
uri: file:///C:/Users/Usuario/Documents/GitHub/Proyecto-Lectura2/src/main/java/com/example/LecturaSana/model/Pedido.java
text:
```scala
package com.example.LecturaSana.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.@@Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "subtotal", nullable = false)
    private Double subtotal;

    @Column(name = "igv", nullable = false)
    private Double igv;

    @Column(name = "total", nullable = false)
    private Double total;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private List<CarritoItem> items = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "usuario_id") // Permite nulos para invitados
    private Usuario usuario;

    // --- DATOS DE ENVÍO ---
    @NotBlank(message = "El nombre de quien recibe es obligatorio")
    @Column(name = "comprador_nombre", length = 100)
    private String compradorNombre;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^9[0-9]{8}$", message = "El teléfono debe tener 9 dígitos y empezar con 9")
    @Column(name = "comprador_telefono", length = 20)
    private String compradorTelefono;

    @NotBlank(message = "La dirección de envío es obligatoria")
    @Column(name = "direccion_envio", length = 255)
    private String direccionEnvio;

    // --- DATOS DE PAGO CON TARJETA (NO SE GUARDAN EN BD - Solo para validación del formulario) ---
    @Transient
    private String numeroTarjeta;

    @Transient
    private String fechaVencimiento;

    @Transient
    private String cvv;

    // --- MÉTODO DE PAGO ---
    @Column(name = "metodo_pago", length = 20)
    private String metodoPago; // "TARJETA" o "PAYPAL"

    @Column(name = "paypal_order_id", length = 100)
    private String paypalOrderId;

    // Campos opcionales legacy
    @Column(name = "comprador_email", length = 100)
    private String compradorEmail;
    @Column(name = "comprador_dni", length = 20)
    private String compradorDni;

    public Pedido() {
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getIgv() {
        return igv;
    }

    public void setIgv(Double igv) {
        this.igv = igv;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public List<CarritoItem> getItems() {
        return items;
    }

    public void setItems(List<CarritoItem> items) {
        this.items = items;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getCompradorNombre() {
        return compradorNombre;
    }

    public void setCompradorNombre(String compradorNombre) {
        this.compradorNombre = compradorNombre;
    }

    public String getCompradorTelefono() {
        return compradorTelefono;
    }

    public void setCompradorTelefono(String compradorTelefono) {
        this.compradorTelefono = compradorTelefono;
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    // Getters Pago
    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    // Getters Legacy
    public String getCompradorEmail() {
        return compradorEmail;
    }

    public void setCompradorEmail(String compradorEmail) {
        this.compradorEmail = compradorEmail;
    }

    public String getCompradorDni() {
        return compradorDni;
    }

    public void setCompradorDni(String compradorDni) {
        this.compradorDni = compradorDni;
    }

    // Getters/Setters Método de Pago
    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getPaypalOrderId() {
        return paypalOrderId;
    }

    public void setPaypalOrderId(String paypalOrderId) {
        this.paypalOrderId = paypalOrderId;
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: jakarta/validation/constraints/Pattern#