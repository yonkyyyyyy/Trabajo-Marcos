package com.example.RDMProject.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "compra")
public class Compra {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String observaciones;
    private LocalDate fechaCompra;
    private Double total;
    private String imagenUrl;
    private Integer numerador; // Número de factura/boleta

    // Relación con Serie (Si te da error, comenta estas 3 líneas por ahora)
    @ManyToOne
    @JoinColumn(name = "idDocVentaCompra")
    private SerieDocVentaCompra serieDocVentaCompra;

    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "idCliente") // Ojo: Asegúrate que en la BD sea este nombre
    private ClienteProveedor proveedor;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCompra> detalles;

    public Compra() {
        this.fechaCompra = LocalDate.now();
        this.total = 0.0;
    }

    // --- MAGIA: Poner fecha automática antes de guardar ---
    @PrePersist
    public void prePersist() {
        this.fechaCompra = LocalDate.now();
    }

    // --- GETTERS Y SETTERS ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDate getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(LocalDate fechaCompra) { this.fechaCompra = fechaCompra; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public Integer getNumerador() { return numerador; }
    public void setNumerador(Integer numerador) { this.numerador = numerador; }

    public SerieDocVentaCompra getSerieDocVentaCompra() { return serieDocVentaCompra; }
    public void setSerieDocVentaCompra(SerieDocVentaCompra serieDocVentaCompra) { this.serieDocVentaCompra = serieDocVentaCompra; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public ClienteProveedor getProveedor() { return proveedor; }
    public void setProveedor(ClienteProveedor proveedor) { this.proveedor = proveedor; }

    public List<DetalleCompra> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleCompra> detalles) { this.detalles = detalles; }
}