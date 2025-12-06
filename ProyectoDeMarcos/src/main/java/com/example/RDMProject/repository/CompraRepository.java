package com.example.RDMProject.repository;

import com.example.RDMProject.model.Compra;
import com.example.RDMProject.model.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    
    List<Compra> findByEstadoOrderByFechaCompraDesc(EstadoPedido estado);
    
    List<Compra> findByFechaCompraBetween(LocalDate inicio, LocalDate fin);
    
    List<Compra> findByFechaCompra(LocalDate fecha);
    
    long countByEstado(EstadoPedido estado);
    
    List<Compra> findTop10ByOrderByFechaCompraDesc();
}
