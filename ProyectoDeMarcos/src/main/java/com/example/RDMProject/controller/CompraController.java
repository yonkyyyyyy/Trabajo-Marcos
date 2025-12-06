package com.example.RDMProject.controller;

import com.example.RDMProject.model.Compra;
import com.example.RDMProject.service.CompraService;
import com.example.RDMProject.service.ProductoService;
import com.example.RDMProject.service.ClienteProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/compras")
public class CompraController {

    @Autowired
    private CompraService compraService;

    @Autowired
    private ProductoService productoService; // Para elegir qué compramos

    @Autowired
    private ClienteProveedorService clienteService; // Para elegir el proveedor

    // 1. Listar Compras (Historial)
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("compras", compraService.listarCompras());
        return "compra/lista";
    }

    // 2. Nueva Compra (Formulario)
    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("compra", new Compra());
        model.addAttribute("proveedores", clienteService.findAll()); // Enviar lista de proveedores
        model.addAttribute("productos", productoService.listarProductos()); // Enviar lista de productos
        return "compra/form";
    }
    
    // El método POST para guardar es más complejo porque requiere recibir la lista de detalles.
    // Lo implementaremos cuando definamos cómo será la vista (Carrito vs Simple).
}