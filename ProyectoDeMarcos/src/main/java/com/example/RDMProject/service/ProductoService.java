package com.example.RDMProject.service;

import com.example.RDMProject.model.Producto;
import com.example.RDMProject.model.Categoria;
import com.example.RDMProject.repository.ProductoRepository;
import com.example.RDMProject.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private CategoriaRepository categoriaRepository;

    // 1. LISTAR TODOS
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    // 2. GUARDAR
    public void guardarProducto(Producto producto) {
        productoRepository.save(producto);
    }

    // 3. BUSCAR POR ID (Para editar)
    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    // 4. ELIMINAR
    public void eliminarProducto(Long id) {
        productoRepository.deleteById(id);
    }

    // 5. BUSCAR POR CATEGORÍA (NUEVO)
    public List<Producto> listarPorCategoria(Categoria categoria) {
        return productoRepository.findByCategoria(categoria);
    }
    
    // 6. LISTAR CATEGORÍAS
    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }
}