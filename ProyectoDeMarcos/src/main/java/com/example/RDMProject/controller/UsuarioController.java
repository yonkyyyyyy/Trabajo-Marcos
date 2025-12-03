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

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        return "usuario/lista";
    }

    // NUEVO: Aquí le ponemos el apodo "usuarioForm"
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuarioForm", new Usuario()); 
        return "usuario/form";
    }

    // GUARDAR: Recibimos el apodo "usuarioForm"
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("usuarioForm") Usuario usuario, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "usuario/form";
        }
        usuarioService.save(usuario);
        return "redirect:/usuarios";
    }

    // EDITAR: También usamos el apodo aquí
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("usuarioForm", usuarioService.findById(id));
        return "usuario/form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        usuarioService.deleteById(id);
        return "redirect:/usuarios";
    }
}