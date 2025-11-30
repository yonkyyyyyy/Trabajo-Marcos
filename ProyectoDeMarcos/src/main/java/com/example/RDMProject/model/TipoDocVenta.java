package com.example.RDMProject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class TipoDocVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVentaDoc;
    private String idSunat;
    private String descripcion;

    public TipoDocVenta(){

    }
    public TipoDocVenta(Long idVentaDoc, String idSunat, String descripcion) {
        this.idVentaDoc = idVentaDoc;
        this.idSunat = idSunat;
        this.descripcion = descripcion;
    }

    public Long getIdVentaDoc() {
        return idVentaDoc;
    }

    public void setIdVentaDoc(Long idVentaDoc) {
        this.idVentaDoc = idVentaDoc;
    }

    public String getIdSunat() {
        return idSunat;
    }

    public void setIdSunat(String idSunat) {
        this.idSunat = idSunat;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
