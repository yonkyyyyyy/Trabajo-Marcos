package com.example.RDMProject.service;

import com.example.RDMProject.model.Categoria;
import com.example.RDMProject.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // <--- ¡ESTO ES VITAL! Convierte la clase en un Bean de Spring
public class CategoriaService { // <--- Cambiado de 'interface' a 'class'

    @Autowired
    private CategoriaRepository categoriaRepository;

    // Implementación real de los métodos
    public List<Categoria> findAll() {
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> findById(Long id) {
        return categoriaRepository.findById(id);
    }

    public Categoria save(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public void deleteById(Long id) {
        categoriaRepository.deleteById(id);
    }
}