package com.example.RDMProject.controller;

import com.example.RDMProject.model.Categoria;
import com.example.RDMProject.service.CategoriaService;
import jakarta.validation.Valid; // Importar
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // Importar
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService){
        this.categoriaService = categoriaService;
    }

    // Listar
    @GetMapping
    public String listar(Model model){
        model.addAttribute("categoria", categoriaService.findAll());
        return "categoria/lista";
    }

    // Formulario Nueva
    @GetMapping("/nueva")
    public String nueva(Model model){
        model.addAttribute("categoria", new Categoria());
        return "categoria/form";
    }

    // Guardar (CON VALIDACIÓN)
    @PostMapping
    public String guardar(@Valid @ModelAttribute Categoria categoria, BindingResult result){
        
        // Si hay error (nombre vacío), volvemos al formulario
        if (result.hasErrors()) {
            return "categoria/form";
        }

        categoriaService.save(categoria);
        return "redirect:/categorias"; // Corregido el error de la barra '/'
    }

    // Formulario Editar
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model){
        Categoria categoria = categoriaService.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Categoria No encontrada con el id" + id));
        
        model.addAttribute("categoria", categoria); // Corregido "categora" por "categoria"
        return "categoria/form";
    }

    // Eliminar
    @GetMapping("/eliminar/{id}")
    public String Eliminar(@PathVariable Long id){
        categoriaService.deleteById(id);
        return "redirect:/categorias"; // Corregido el error de la barra '/'
    }
}