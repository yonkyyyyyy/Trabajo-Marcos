package com.example.RDMProject.model.enums;

/**
 * Estados posibles de una venta/pedido
 */
public enum EstadoPedido {
    PENDIENTE("Pendiente", "warning"),
    EN_PROCESO("En Proceso", "info"),
    COMPLETADO("Completado", "success"),
    CANCELADO("Cancelado", "danger"),
    ENTREGADO("Entregado", "primary");
    
    private final String descripcion;
    private final String badgeColor; // Color para Bootstrap badges
    
    EstadoPedido(String descripcion, String badgeColor) {
        this.descripcion = descripcion;
        this.badgeColor = badgeColor;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public String getBadgeColor() {
        return badgeColor;
    }
}
