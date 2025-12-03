package com.example.RDMProject.repository;

import com.example.RDMProject.model.Producto;
import com.example.RDMProject.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // Método mágico: Spring crea el SQL automáticamente
    List<Producto> findByCategoria(Categoria categoria);
}