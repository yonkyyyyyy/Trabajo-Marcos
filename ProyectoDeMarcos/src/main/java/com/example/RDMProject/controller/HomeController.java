package com.example.RDMProject.controller;

import com.example.RDMProject.model.Producto;
import com.example.RDMProject.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductoService productoService;

    @GetMapping("/")
    public String index(Model model) {
        // 1. Traemos TODOS los productos de la BD
        List<Producto> todosLosProductos = productoService.listarProductos();
        
        // Si no hay productos, mandamos lista vacía y terminamos
        if (todosLosProductos.isEmpty()) {
            model.addAttribute("productosAgrupados", new ArrayList<>());
            return "index";
        }

        // 2. Preparamos la lista de grupos
        List<List<Producto>> productosAgrupados = new ArrayList<>();
        int tamanoGrupo = 3;

        // 3. Lógica de Agrupación con Relleno (Loop Infinito visual)
        for (int i = 0; i < todosLosProductos.size(); i += tamanoGrupo) {
            
            // Creamos un grupo nuevo
            List<Producto> grupoActual = new ArrayList<>();
            
            // Intentamos llenar el grupo con 3 elementos
            for (int j = 0; j < tamanoGrupo; j++) {
                // Calculamos el índice circular (módulo %)
                // Esto hace que si se acaban los productos, vuelva a empezar desde el 0
                int indice = (i + j) % todosLosProductos.size();
                grupoActual.add(todosLosProductos.get(indice));
            }
            
            productosAgrupados.add(grupoActual);
        }

        // 4. Mandamos los grupos "llenos" al HTML
        model.addAttribute("productosAgrupados", productosAgrupados);
        
        return "index";
    }
}