package com.example.RDMProject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*; // Importante para las validaciones

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- CAMPOS CON VALIDACIÓN (Nuestro trabajo) ---

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El precio no puede ser negativo")
    private Double precio;

    private String imagen;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer cantidad; // cantidad para el stock

    // --- CAMPOS NUEVOS (Estructura de tu compañero) ---
    // Los mantenemos para que la base de datos no falle, pero sin validación obligatoria por ahora

    private Double descuento;
    private Integer codigo_barra;
    private Long estado;
    private Double precio_compra;
    private Double peso_bruto;
    private Integer stock_minimo;

    @ManyToOne
    @JoinColumn(name = "idcategoria")
    private Categoria categoria;

    @ManyToOne
    @JoinColumn(name="idunidadmedida")
    private UnidadMedida unidadMedida;

    // --- CONSTRUCTORES ---

    public Producto() {
    }

    // --- GETTERS Y SETTERS (Para TODOS los campos) ---

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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getDescuento() {
        return descuento;
    }

    public void setDescuento(Double descuento) {
        this.descuento = descuento;
    }

    public Integer getCodigo_barra() {
        return codigo_barra;
    }

    public void setCodigo_barra(Integer codigo_barra) {
        this.codigo_barra = codigo_barra;
    }

    public Long getEstado() {
        return estado;
    }

    public void setEstado(Long estado) {
        this.estado = estado;
    }

    public Double getPrecio_compra() {
        return precio_compra;
    }

    public void setPrecio_compra(Double precio_compra) {
        this.precio_compra = precio_compra;
    }

    public Double getPeso_bruto() {
        return peso_bruto;
    }

    public void setPeso_bruto(Double peso_bruto) {
        this.peso_bruto = peso_bruto;
    }

    public Integer getStock_minimo() {
        return stock_minimo;
    }

    public void setStock_minimo(Integer stock_minimo) {
        this.stock_minimo = stock_minimo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(UnidadMedida unidadMedida) {
        this.unidadMedida = unidadMedida;
    }
}