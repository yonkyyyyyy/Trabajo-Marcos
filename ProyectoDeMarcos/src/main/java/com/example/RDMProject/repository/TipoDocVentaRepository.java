package com.example.RDMProject.repository;

import com.example.RDMProject.model.TipoDocVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoDocVentaRepository extends JpaRepository<TipoDocVenta, Long> {
    
    Optional<TipoDocVenta> findByDescripcion(String descripcion);
    
    Optional<TipoDocVenta> findByIdSunat(String idSunat);
}
