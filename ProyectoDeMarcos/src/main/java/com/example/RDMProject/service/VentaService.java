package com.example.RDMProject.service;

import com.example.RDMProject.model.DetalleVenta;
import com.example.RDMProject.model.Producto;
import com.example.RDMProject.model.Venta;
import com.example.RDMProject.repository.DetalleVentaRepository;
import com.example.RDMProject.repository.ProductoRepository;
import com.example.RDMProject.repository.VentaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // --- CARRITO DE COMPRAS (Memoria Temporal Compartida) ---
    private List<DetalleVenta> carrito = new ArrayList<>();

    public List<DetalleVenta> getCarrito() {
        return carrito;
    }

    public void agregarProductoAlCarrito(Producto producto, Integer cantidad) {
        boolean existe = false;
        for (DetalleVenta d : carrito) {
            if (d.getProducto().getId().equals(producto.getId())) {
                d.setCantidad(d.getCantidad() + cantidad);
                d.setSubtotal(d.getCantidad() * d.getPrecioUnitario());
                existe = true;
                break;
            }
        }
        if (!existe) {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProducto(producto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setSubtotal(cantidad * producto.getPrecio());
            carrito.add(detalle);
        }
    }

    public void quitarProductoDelCarrito(Long idProducto) {
        carrito.removeIf(d -> d.getProducto().getId().equals(idProducto));
    }

    public void limpiarCarrito() {
        carrito.clear();
    }

    public int contarItems() {
        return carrito.size();
    }

    // --- LÓGICA DE BASE DE DATOS ---
    
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    @Transactional
    public void guardarVenta(Venta venta) {
        // Guardamos la venta
        Venta ventaGuardada = ventaRepository.save(venta);

        // Procesamos los detalles que estaban en memoria
        for (DetalleVenta detalle : carrito) {
            detalle.setVenta(ventaGuardada);
            
            // Stock
            Producto producto = detalle.getProducto();
            if (producto.getCantidad() < detalle.getCantidad()) {
                throw new RuntimeException("Stock insuficiente: " + producto.getNombre());
            }
            producto.setCantidad(producto.getCantidad() - detalle.getCantidad());
            productoRepository.save(producto);
            
            detalleVentaRepository.save(detalle);
        }
        
        // Limpiamos la memoria al final
        limpiarCarrito();
    }
}