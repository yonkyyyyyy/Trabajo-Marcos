package com.example.RDMProject.repository;

import com.example.RDMProject.model.Venta;
import com.example.RDMProject.model.Usuario;
import com.example.RDMProject.model.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    
    List<Venta> findByUsuarioOrderByFechaVentaDesc(Usuario usuario);
    
    List<Venta> findByEstadoOrderByFechaVentaDesc(EstadoPedido estado);
    
    List<Venta> findByFechaVentaBetweenOrderByFechaVentaDesc(LocalDate inicio, LocalDate fin);
    
    List<Venta> findByFechaVenta(LocalDate fecha);
    
    List<Venta> findByUsuarioAndEstadoOrderByFechaVentaDesc(Usuario usuario, EstadoPedido estado);
    
    long countByEstado(EstadoPedido estado);
    
    long countByUsuario(Usuario usuario);
    
    List<Venta> findTop10ByOrderByFechaVentaDesc();
    
    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.fechaVenta BETWEEN :inicio AND :fin AND v.estado <> 'CANCELADO'")
    Double getTotalVentasByFechaRange(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);
    
    @Query("SELECT v FROM Venta v WHERE v.usuario = :usuario AND v.estado <> 'CANCELADO' ORDER BY v.fechaVenta DESC")
    List<Venta> findVentasActivasByUsuario(@Param("usuario") Usuario usuario);
}
