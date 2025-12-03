package com.example.RDMProject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "usuario")
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    // CAMBIO AQUÍ: De 'usuario' a 'username' para evitar conflictos
    @NotBlank(message = "El usuario es obligatorio")
    @Column(name = "username") 
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    private String contrasena;

    @NotBlank(message = "El nombre completo es obligatorio")
    private String nombreCompleto;

    @Email(message = "Formato de correo inválido")
    @NotBlank(message = "El correo es obligatorio")
    private String email;

    private LocalDate fechaIngreso;
    private LocalDate fechaSalida;

    @NotNull(message = "Debe seleccionar un rol")
    private Integer rol; 

    private Integer estado;

    @ManyToOne
    @JoinColumn(name = "id_tipo_documento") 
    private TipoDocumento tipoDocumento;

    public Usuario(){
        this.fechaIngreso = LocalDate.now();
        this.estado = 1; 
    }

    // Getters y Setters
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    // GETTER Y SETTER ACTUALIZADOS
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public LocalDate getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(LocalDate fechaSalida) { this.fechaSalida = fechaSalida; }

    public Integer getRol() { return rol; }
    public void setRol(Integer rol) { this.rol = rol; }

    public Integer getEstado() { return estado; }
    public void setEstado(Integer estado) { this.estado = estado; }

    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }
}