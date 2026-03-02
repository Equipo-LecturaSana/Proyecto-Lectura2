package com.example.LecturaSana.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

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
    @Column(name = "comprador_nombre", length = 100)
    private String compradorNombre;

    @Column(name = "comprador_telefono", length = 20)
    private String compradorTelefono;

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
