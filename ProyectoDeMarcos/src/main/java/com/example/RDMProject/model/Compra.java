package com.example.RDMProject.model;

import com.example.RDMProject.model.enums.EstadoPedido;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "compra")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_compra")
    private LocalDate fechaCompra;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    private String observaciones;
    private Double total;
    private Double igv;
    private Double descuento;
    private Double subtotal;
    private Integer numerador;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    private EstadoPedido estado;

    // Relación con Usuario
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    // IMPORTANTE: Relación con PROVEEDOR (no cliente)
    @ManyToOne
    @JoinColumn(name = "id_proveedor")
    private ClienteProveedor proveedor;

    @ManyToOne
    @JoinColumn(name = "id_doc_venta_compra")
    private SerieDocVentaCompra documento;

    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCompra> detalles = new ArrayList<>();

    public Compra() {
        this.fechaCompra = LocalDate.now();
        this.fechaCreacion = LocalDateTime.now();
        this.total = 0.0;
        this.subtotal = 0.0;
        this.igv = 0.0;
        this.descuento = 0.0;
        this.estado = EstadoPedido.PENDIENTE;
    }

    @PrePersist
    public void prePersist() {
        this.fechaCompra = LocalDate.now();
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoPedido.PENDIENTE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void calcularTotal() {
        this.subtotal = detalles.stream()
                .mapToDouble(d -> d.getPrecioUnitario() * d.getCantidad())
                .sum();

        double descuentoTotal = (this.descuento != null) ? this.descuento : 0.0;
        double baseImponible = this.subtotal - descuentoTotal;
        this.igv = baseImponible * 0.18;
        this.total = baseImponible + this.igv;
    }

    public void agregarDetalle(DetalleCompra detalle) {
        detalles.add(detalle);
        detalle.setCompra(this);
        calcularTotal();
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(LocalDate fechaCompra) { this.fechaCompra = fechaCompra; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }

    public Double getIgv() { return igv; }
    public void setIgv(Double igv) { this.igv = igv; }

    public Double getDescuento() { return descuento; }
    public void setDescuento(Double descuento) { this.descuento = descuento; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }

    public Integer getNumerador() { return numerador; }
    public void setNumerador(Integer numerador) { this.numerador = numerador; }

    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    // IMPORTANTE: Getter y Setter de PROVEEDOR
    public ClienteProveedor getProveedor() { return proveedor; }
    public void setProveedor(ClienteProveedor proveedor) { this.proveedor = proveedor; }

    public SerieDocVentaCompra getDocumento() { return documento; }
    public void setDocumento(SerieDocVentaCompra documento) { this.documento = documento; }

    public List<DetalleCompra> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleCompra> detalles) { this.detalles = detalles; }
}