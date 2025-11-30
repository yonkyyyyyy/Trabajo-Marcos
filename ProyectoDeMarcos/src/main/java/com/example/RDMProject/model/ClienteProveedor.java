package com.example.RDMProject.model;
import jakarta.persistence.*;
@Entity
@Table(name = "cliente_proveedor")
public class ClienteProveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCliente;
    private Integer nro_documento;
    private String nombre_cliente;
    private String direccion;
    private String email;
    private Integer celular;
    private Integer estado;
    private String DatosAdicionales;
    private String genero;
    private Integer tipoCliente;// 1 cliente 2 proveedor 0 ambos
    @ManyToOne
    @JoinColumn(name = "tipoDocumento")
    private TipoDocumento tipoDocumento;

    public ClienteProveedor(){
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public Integer getNro_documento() {
        return nro_documento;
    }

    public void setNro_documento(Integer nro_documento) {
        this.nro_documento = nro_documento;
    }

    public String getNombre_cliente() {
        return nombre_cliente;
    }

    public void setNombre_cliente(String nombre_cliente) {
        this.nombre_cliente = nombre_cliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getCelular() {
        return celular;
    }

    public void setCelular(Integer celular) {
        this.celular = celular;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getDatosAdicionales() {
        return DatosAdicionales;
    }

    public void setDatosAdicionales(String datosAdicionales) {
        DatosAdicionales = datosAdicionales;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Integer getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(Integer tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
}
