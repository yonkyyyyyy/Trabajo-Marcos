package com.example.RDMProject.model;
import jakarta.persistence.*;
@Entity
@Table(name = "SerieDocVentaCompra ")
public class SerieDocVentaCompra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDocVentaCompra;

    private String serie; // Ej: F001, B001

    private Integer numerador = 0;

    @ManyToOne
    @JoinColumn(name = "idVentaDoc")
    private TipoDocVenta tipoDocVenta;
    public SerieDocVentaCompra(){

    }

    public Long getIdDocVentaCompra() {
        return idDocVentaCompra;
    }

    public void setIdDocVentaCompra(Long idDocVentaCompra) {
        this.idDocVentaCompra = idDocVentaCompra;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public Integer getNumerador() {
        return numerador;
    }

    public void setNumerador(Integer numerador) {
        this.numerador = numerador;
    }

    public TipoDocVenta getTipoDocVenta() {
        return tipoDocVenta;
    }

    public void setTipoDocVenta(TipoDocVenta tipoDocVenta) {
        this.tipoDocVenta = tipoDocVenta;
    }
}
