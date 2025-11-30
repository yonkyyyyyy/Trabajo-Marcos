package com.example.RDMProject.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String observaciones;
    private LocalDate fechaCompra;
    private Double total;
    private String imagenUrl;

    private Integer numerador;

    @ManyToOne
    @JoinColumn(name = "idDocVentaCompra")
    private SerieDocVentaCompra serieDocVentaCompra;

    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "idCliente")
    private ClienteProveedor proveedor;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCompra> detalles;

}
