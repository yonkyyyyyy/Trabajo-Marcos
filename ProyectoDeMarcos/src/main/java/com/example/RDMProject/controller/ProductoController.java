package com.example.RDMProject.controller;

import com.example.RDMProject.model.Producto;
import com.example.RDMProject.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/productos") // Todas las URLs de aquí empezarán con /productos
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // 1. Mostrar el formulario (GET)
    @GetMapping("/nuevo")
    public String mostrarFormularioDeRegistrar(Model model) {
        // Le pasamos un objeto vacío para que el formulario lo llene
        model.addAttribute("producto", new Producto());
        return "producto_formulario"; // Buscaremos este archivo HTML
    }

    // 2. Guardar el producto (POST)
    @PostMapping("/guardar")
    public String guardarProducto(Producto producto) {
        productoService.guardarProducto(producto);
        // Al terminar, nos redirige a la página principal para ver el cambio
        return "redirect:/"; 
    }

    //  Mostrar el Catálogo completo (GET /productos)
    @GetMapping("") // Al estar en la clase @RequestMapping("/productos"), esto responde a "/productos"
    public String listarProductos(Model model) {
        // Obtenemos la lista completa
        var productos = productoService.listarProductos();
        
        // La mandamos a la vista
        model.addAttribute("listaProductos", productos);
        
        // Retornamos el archivo productos.html (que crearemos ahora)
        return "productos"; 
    }
}