package com.example.RDMProject.service;

import com.example.RDMProject.model.Venta;
import com.example.RDMProject.model.Usuario;
import com.example.RDMProject.model.enums.EstadoPedido;
import com.example.RDMProject.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }
    
    public Page<Venta> findAll(Pageable pageable) {
        return ventaRepository.findAll(pageable);
    }

    public List<Venta> findByUsuario(Usuario usuario) {
        return ventaRepository.findByUsuarioOrderByFechaVentaDesc(usuario);
    }
    
    public List<Venta> findByEstado(EstadoPedido estado) {
        return ventaRepository.findByEstadoOrderByFechaVentaDesc(estado);
    }
    
    public List<Venta> findPendientes() {
        return ventaRepository.findByEstadoOrderByFechaVentaDesc(EstadoPedido.PENDIENTE);
    }
    
    public List<Venta> findByFechaRange(LocalDate inicio, LocalDate fin) {
        return ventaRepository.findByFechaVentaBetweenOrderByFechaVentaDesc(inicio, fin);
    }

    public Venta findById(Long id) {
        return ventaRepository.findById(id).orElse(null);
    }

    public Venta save(Venta venta) {
        venta.calcularTotal();
        return ventaRepository.save(venta);
    }
    
    public Venta cambiarEstado(Long id, EstadoPedido nuevoEstado) {
        Venta venta = findById(id);
        if (venta != null) {
            venta.setEstado(nuevoEstado);
            return save(venta);
        }
        return null;
    }
    
    public Venta marcarComoCompletada(Long id) {
        return cambiarEstado(id, EstadoPedido.COMPLETADO);
    }
    
    public Venta marcarComoEntregada(Long id) {
        return cambiarEstado(id, EstadoPedido.ENTREGADO);
    }
    
    public Venta cancelar(Long id) {
        return cambiarEstado(id, EstadoPedido.CANCELADO);
    }

    public boolean deleteById(Long id) {
        Venta venta = findById(id);
        if (venta != null) {
            EstadoPedido estado = venta.getEstado();
            if (estado == EstadoPedido.PENDIENTE || estado == EstadoPedido.CANCELADO) {
                ventaRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }
    
    public Double getTotalVentasHoy() {
        LocalDate hoy = LocalDate.now();
        List<Venta> ventasHoy = ventaRepository.findByFechaVenta(hoy);
        return ventasHoy.stream()
            .filter(v -> v.getEstado() != EstadoPedido.CANCELADO)
            .mapToDouble(Venta::getTotal)
            .sum();
    }
    
    public long countPendientes() {
        return ventaRepository.countByEstado(EstadoPedido.PENDIENTE);
    }
}
