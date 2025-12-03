package com.example.RDMProject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank; // Importante
import java.util.List;

@Entity
@Table(name = "unidad_medida")
public class UnidadMedida {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUNM;

    @NotBlank(message = "El nombre de la unidad es obligatorio")
    private String valor;

    // Relación bidireccional
    @OneToMany(mappedBy = "unidadMedida", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Producto> productos;

    public Long getIdUNM() { return idUNM; }
    public void setIdUNM(Long idUNM) { this.idUNM = idUNM; }

    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }

    public List<Producto> getProductos() { return productos; }
    public void setProductos(List<Producto> productos) { this.productos = productos; }
}