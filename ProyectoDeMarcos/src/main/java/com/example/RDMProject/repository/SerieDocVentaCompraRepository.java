package com.example.RDMProject.repository;

import com.example.RDMProject.model.SerieDocVentaCompra;
import com.example.RDMProject.model.TipoDocVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SerieDocVentaCompraRepository extends JpaRepository<SerieDocVentaCompra, Long> {
    
    // Buscar por tipo de documento
    List<SerieDocVentaCompra> findByTipoDocVenta(TipoDocVenta tipoDocVenta);
    
    // Buscar por tipo de documento ID
    List<SerieDocVentaCompra> findByTipoDocVenta_IdVentaDoc(Long idVentaDoc);
    
    // Buscar por serie
    Optional<SerieDocVentaCompra> findBySerie(String serie);
    
    // Obtener todas las series activas
    @Query("SELECT s FROM SerieDocVentaCompra s LEFT JOIN FETCH s.tipoDocVenta ORDER BY s.serie")
    List<SerieDocVentaCompra> findAllWithTipoDocumento();
}
