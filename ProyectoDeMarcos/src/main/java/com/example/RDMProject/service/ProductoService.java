package com.example.RDMProject.service;

import java.util.List;
import com.example.RDMProject.model.Producto;
import com.example.RDMProject.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;

    // Método para obtener todos los productos de la base de datos
    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }
    //  GUARDAR 
    //  recibe un producto y lo manda a la BD
    public void guardarProducto(Producto producto) {
        productoRepository.save(producto);
    

    }
}