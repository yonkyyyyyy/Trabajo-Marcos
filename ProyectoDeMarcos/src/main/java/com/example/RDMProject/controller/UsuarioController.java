package com.example.RDMProject.controller;

import com.example.RDMProject.model.Usuario;
import com.example.RDMProject.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Listar usuarios
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        return "usuario/lista";
    }

    // Formulario Nuevo
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        // CAMBIO CLAVE: "userForm" para evitar choque de nombres
        model.addAttribute("userForm", new Usuario());
        return "usuario/form";
    }

    // Guardar Usuario
    @PostMapping("/guardar")
    // CAMBIO CLAVE: Recibimos "userForm"
    public String guardar(@Valid @ModelAttribute("userForm") Usuario usuario, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "usuario/form";
        }
        usuarioService.save(usuario);
        return "redirect:/usuarios";
    }

    // Editar Usuario
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        // CAMBIO CLAVE: Usamos "userForm" también aquí
        model.addAttribute("userForm", usuarioService.findById(id));
        return "usuario/form";
    }

    // Eliminar Usuario
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return "redirect:/usuarios";
    }
}