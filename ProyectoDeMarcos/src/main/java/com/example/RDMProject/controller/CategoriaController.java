package com.example.RDMProject.controller;

import com.example.RDMProject.model.Categoria;
import com.example.RDMProject.service.CategoriaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {
    private final CategoriaService categoriaService;
    public CategoriaController(CategoriaService categoriaService){
        this.categoriaService = categoriaService;
    }
    @GetMapping
    public String listar(Model model){
        model.addAttribute("categoria",categoriaService.findAll());
        return "categoria/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model){
        model.addAttribute("categoria",new Categoria());
        return "categoria/form";
    }
    @PostMapping
    public String guardar(@ModelAttribute Categoria categoria){
        categoriaService.save(categoria);
        return"redirect:/categorias";
    }
    @GetMapping("/editar/{id]")
    public String editar(@PathVariable Long id,Model model){
        Categoria categoria = categoriaService.findById(id).orElseThrow(()-> new IllegalArgumentException("Categoria No encontrada con el id" + id));
        model.addAttribute("categora",categoria);
        return "categoria/form";
    }

    @GetMapping("eliminar/{id}")
    public String Eliminar(@PathVariable Long id){
        categoriaService.deleteById(id);
        return "redirect:categorias";
    }
}
