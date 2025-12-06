package com.example.RDMProject.service;

import com.example.RDMProject.model.Compra;
import com.example.RDMProject.model.DetalleCompra;
import com.example.RDMProject.model.Producto;
import com.example.RDMProject.repository.CompraRepository;
import com.example.RDMProject.repository.DetalleCompraRepository;
import com.example.RDMProject.repository.ProductoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private DetalleCompraRepository detalleCompraRepository;

    @Autowired
    private ProductoRepository productoRepository; // Necesario para actualizar stock

    public List<Compra> listarCompras() {
        return compraRepository.findAll();
    }

    // --- TRANSACCIÓN MAESTRA ---
    @Transactional // Si algo falla, deshace todo (no guarda medias compras)
    public void guardarCompra(Compra compra, List<DetalleCompra> detalles) {
        
        // 1. Guardar la Cabecera de la Compra
        Compra compraGuardada = compraRepository.save(compra);

        // 2. Recorrer los detalles para guardarlos y actualizar stock
        for (DetalleCompra det : detalles) {
            det.setCompra(compraGuardada); // Vincular detalle a la compra
            
            // --- ACTUALIZAR STOCK ---
            Producto producto = det.getProducto();
            // Sumamos lo que había + lo que compramos
            int nuevoStock = producto.getCantidad() + det.getCantidad();
            producto.setCantidad(nuevoStock);
            
            // Guardamos el producto con el nuevo stock
            productoRepository.save(producto);
            
            // Guardamos el detalle
            detalleCompraRepository.save(det);
        }
    }
}