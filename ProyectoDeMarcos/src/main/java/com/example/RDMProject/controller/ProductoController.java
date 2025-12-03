package com.example.RDMProject.controller;

import com.example.RDMProject.model.Producto;
import com.example.RDMProject.service.ProductoService;
import com.example.RDMProject.service.CategoriaService;
import com.example.RDMProject.service.UnidadMedidaService;
import com.example.RDMProject.repository.ProductoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // Importante
import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private UnidadMedidaService unidadMedidaService;

    // 1. Mostrar formulario (CON LISTAS)
    @GetMapping("/nuevo")
    public String mostrarFormularioDeRegistrar(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", categoriaService.findAll());
        model.addAttribute("unidades", unidadMedidaService.findAll());
        return "producto_formulario";
    }

    // 2. Guardar (CON VALIDACIÓN)
    @PostMapping("/guardar")
    public String guardarProducto(@Valid Producto producto, BindingResult result, Model model) {
        
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.findAll());
            model.addAttribute("unidades", unidadMedidaService.findAll());
            return "producto_formulario"; 
        }

        productoService.guardarProducto(producto);
        return "redirect:/productos"; 
    }

    // 3. Mostrar Catálogo (CON FILTRO OPCIONAL)
    @GetMapping("") 
    public String listarProductos(@RequestParam(required = false) Long categoriaId, Model model) {
        
        List<Producto> productos;

        if (categoriaId != null) {
            // Lógica de filtrado
            var categoriaBuscada = categoriaService.findById(categoriaId).orElse(null);
            
            if (categoriaBuscada != null) {
                productos = productoService.listarPorCategoria(categoriaBuscada);
                model.addAttribute("categoriaActiva", categoriaId); // Para pintar el botón
            } else {
                productos = productoService.listarProductos();
            }
        } else {
            // Si no hay filtro, mostrar todo
            productos = productoService.listarProductos();
        }

        model.addAttribute("listaProductos", productos);
        
        // Enviamos las categorías para pintar los botones de filtro
        model.addAttribute("categorias", categoriaService.findAll());
        
        return "productos"; 
    }

    // 4. Editar
    @GetMapping("/editar/{id}")
    public String editarProducto(@PathVariable Long id, Model model) {
        Producto producto = productoService.obtenerPorId(id);
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.findAll());
        model.addAttribute("unidades", unidadMedidaService.findAll());
        return "producto_formulario";
    }

    // 5. Eliminar
    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return "redirect:/productos";
    }
}