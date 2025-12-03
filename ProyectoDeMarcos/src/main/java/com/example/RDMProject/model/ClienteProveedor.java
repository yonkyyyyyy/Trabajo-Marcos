package com.example.RDMProject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size; // Para validar longitud

@Entity
@Table(name = "cliente_proveedor")
public class ClienteProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente") // Mantenemos tu corrección de BD
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(name = "nombre_razon_social")
    private String nombreRazonSocial;

    @NotBlank(message = "El documento es obligatorio")
    @Size(min = 8, max = 15, message = "El documento debe tener entre 8 y 15 caracteres")
    @Column(name = "numero_documento")
    private String numeroDocumento; 

    private String telefono;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El formato del correo no es válido")
    private String correo;

    private String direccion;

    private Integer tipo = 1; 

    @ManyToOne
    @JoinColumn(name = "id_tipo_documento") 
    private TipoDocumento tipoDocumento;

    public ClienteProveedor() {
    }

    // --- Getters y Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreRazonSocial() { return nombreRazonSocial; }
    public void setNombreRazonSocial(String nombreRazonSocial) { this.nombreRazonSocial = nombreRazonSocial; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Integer getTipo() { return tipo; }
    public void setTipo(Integer tipo) { this.tipo = tipo; }

    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }
}