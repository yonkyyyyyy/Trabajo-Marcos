package com.example.RDMProject.repository;

import com.example.RDMProject.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
    public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
