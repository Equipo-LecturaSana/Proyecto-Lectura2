package com.example.LecturaSana.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.LecturaSana.validation.MayorDeEdad;
import jakarta.validation.constraints.Pattern;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 20, message = "El nombre debe tener entre 2 y 20 caracteres")
    @Column(name = "nombre", nullable = false, length = 20)
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(min = 2, max = 50, message = "Los apellidos debe tener entre 2 y 50 caracteres")
    @Column(name = "apellidos", nullable = false, length = 50)
    private String apellidos;

    @NotBlank(message = "El tipo de documento es obligatorio")
    @Size(min = 3, max = 10)
    @Column(name = "tipoDocumento", nullable = false, length = 10)
    private String tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(min = 8, max = 8, message = "El DNI debe tener 8 caracteres")
    @Column(name = "numeroDocumento", nullable = false, length = 8)
    private String numeroDocumento;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(min = 9, max = 9, message = "El teléfono debe tener 9 caracteres")
    @Column(name = "telefono", nullable = false)
    private String telefono;

    @NotBlank(message = "El género es obligatorio")
    @Size(min = 1, max = 10)
    @Column(name = "genero", nullable = false, length = 500)
    private String genero;

    @NotNull(message = "La fecha de cumpleaños es obligatoria")
    @Past(message = "La fecha de cumpleaños debe ser en el pasado")
    @MayorDeEdad
    @DateTimeFormat(pattern = "yyyy-MM-dd") // <-- ESTA LÍNEA ARREGLA EL ERROR DE FECHA
    private LocalDate cumpleanos;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Size(min = 5, max = 50)
    @Column(unique = true)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Pattern(
            // Esta es la versión correcta para JAVA (con dobles barras \\)
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()\\-\\[{}\\]:;',?/*~$^+=<>]).{8,100}$",
            message = "La contraseña debe tener entre 8 y 100 caracteres, incluir al menos una mayúscula, una minúscula, un número y un carácter especial."
    )
    private String password;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_ultimo_acceso")
    private LocalDateTime fechaUltimoAcceso;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id")
    private Rol rol;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pedido> pedidos = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    public Usuario() {
    }

    public Usuario(String apellidos, LocalDate cumpleanos, String email, LocalDateTime fechaCreacion,
            LocalDateTime fechaUltimoAcceso, String genero, Long id, String nombre, String numeroDocumento,
            String password, Rol rol, String telefono, String tipoDocumento) {
        this.apellidos = apellidos;
        this.cumpleanos = cumpleanos;
        this.email = email;
        this.fechaCreacion = fechaCreacion;
        this.fechaUltimoAcceso = fechaUltimoAcceso;
        this.genero = genero;
        this.id = id;
        this.nombre = nombre;
        this.numeroDocumento = numeroDocumento;
        this.password = password;
        this.rol = rol;
        this.telefono = telefono;
        this.tipoDocumento = tipoDocumento;
        this.activo = true;
    }

    // -------------------------------------------------------------------------
    // IMPLEMENTACIÓN DE USER DETAILS
    // -------------------------------------------------------------------------
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (rol != null) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()));
        }
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isEnabled() {
        return activo != null ? activo : true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // -------------------------------------------------------------------------
    // MÉTODOS DE AYUDA (Roles)
    // -------------------------------------------------------------------------
    public boolean esPublicador() {
        return rol != null && "PUBLICADOR".equals(rol.getNombre());
    }

    public boolean esVisor() {
        return rol != null && "VISOR".equals(rol.getNombre());
    }

    // -------------------------------------------------------------------------
    // GETTERS Y SETTERS
    // -------------------------------------------------------------------------
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public LocalDate getCumpleanos() {
        return cumpleanos;
    }

    public void setCumpleanos(LocalDate cumpleanos) {
        this.cumpleanos = cumpleanos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaUltimoAcceso() {
        return fechaUltimoAcceso;
    }

    public void setFechaUltimoAcceso(LocalDateTime fechaUltimoAcceso) {
        this.fechaUltimoAcceso = fechaUltimoAcceso;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }
}
