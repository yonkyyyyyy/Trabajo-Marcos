package com.example.RDMProject.model;
import jakarta.persistence.*;
import  java.util.List;
@Entity
@Table(name = "tipo_documento")
public class TipoDocumento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTipoDocumento;
    private String IdSunat;
    private String Descripcion;

    @OneToMany(mappedBy = "tipoDocumento",cascade = CascadeType.ALL,orphanRemoval = false)
    private List<ClienteProveedor> clientes;

    public TipoDocumento(){

    }
    public Integer getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(Integer idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

    public String getIdSunat() {
        return IdSunat;
    }

    public void setIdSunat(String idSunat) {
        IdSunat = idSunat;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public List<ClienteProveedor> getClientes() {
        return clientes;
    }

    public void setClientes(List<ClienteProveedor> clientes) {
        this.clientes = clientes;
    }
}
