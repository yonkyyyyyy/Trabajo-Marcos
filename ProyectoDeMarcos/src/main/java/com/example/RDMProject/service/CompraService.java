package com.example.RDMProject.service;

import com.example.RDMProject.model.Compra;
import com.example.RDMProject.model.enums.EstadoPedido;
import com.example.RDMProject.repository.CompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;

    public List<Compra> findAll() {
        return compraRepository.findAll();
    }

    public Compra findById(Long id) {
        return compraRepository.findById(id).orElse(null);
    }

    public List<Compra> findByEstado(EstadoPedido estado) {
        return compraRepository.findByEstadoOrderByFechaCompraDesc(estado);
    }

    public List<Compra> findByFechaRange(LocalDate inicio, LocalDate fin) {
        return compraRepository.findByFechaCompraBetween(inicio, fin);
    }

    public Compra save(Compra compra) {
        if (compra.getDetalles() != null && !compra.getDetalles().isEmpty()) {
            calcularTotal(compra);
        }
        return compraRepository.save(compra);
    }

    public Compra cambiarEstado(Long id, EstadoPedido nuevoEstado) {
        Compra compra = findById(id);
        if (compra != null) {
            compra.setEstado(nuevoEstado);
            return save(compra);
        }
        return null;
    }

    public boolean deleteById(Long id) {
        Compra compra = findById(id);
        if (compra != null) {
            EstadoPedido estado = compra.getEstado();
            if (estado == EstadoPedido.PENDIENTE || estado == EstadoPedido.CANCELADO) {
                compraRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }

    private void calcularTotal(Compra compra) {
        double subtotal = compra.getDetalles().stream()
            .mapToDouble(d -> d.getPrecioUnitario() * d.getCantidad())
            .sum();
        
        double descuento = (compra.getDescuento() != null) ? compra.getDescuento() : 0.0;
        double baseImponible = subtotal - descuento;
        double igv = baseImponible * 0.18;
        double total = baseImponible + igv;
        
        compra.setSubtotal(subtotal);
        compra.setIgv(igv);
        compra.setTotal(total);
    }

    public long countPendientes() {
        return compraRepository.countByEstado(EstadoPedido.PENDIENTE);
    }

    public Double getTotalComprasHoy() {
        LocalDate hoy = LocalDate.now();
        List<Compra> comprasHoy = compraRepository.findByFechaCompra(hoy);
        return comprasHoy.stream()
            .filter(c -> c.getEstado() != EstadoPedido.CANCELADO)
            .mapToDouble(Compra::getTotal)
            .sum();
    }
}
