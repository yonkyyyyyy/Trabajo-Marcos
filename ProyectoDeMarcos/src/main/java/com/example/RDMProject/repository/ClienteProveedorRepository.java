package com.example.RDMProject.repository;

import com.example.RDMProject.model.ClienteProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteProveedorRepository extends JpaRepository<ClienteProveedor, Long> {
    List<ClienteProveedor> findByTipo(String tipo);
}
