package com.example.RDMProject.controller;

import com.example.RDMProject.model.ClienteProveedor;
import com.example.RDMProject.service.ClienteProveedorService;
import jakarta.validation.Valid; // Importante
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // Importante
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clientes")
public class ClienteProveedorController {

    @Autowired
    private ClienteProveedorService clienteService;

    // Listar
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.findAll());
        return "cliente/lista";
    }

    // Nuevo
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new ClienteProveedor());
        return "cliente/form";
    }

    // Guardar (CON VALIDACIÓN)
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("cliente") ClienteProveedor cliente, BindingResult result) {
        
        // Si hay errores (DNI vacío, correo mal escrito...), volvemos al formulario
        if (result.hasErrors()) {
            return "cliente/form";
        }

        clienteService.save(cliente);
        return "redirect:/clientes";
    }

    // Editar
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        ClienteProveedor cliente = clienteService.findById(id);
        model.addAttribute("cliente", cliente);
        return "cliente/form";
    }

    // Eliminar
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        clienteService.deleteById(id);
        return "redirect:/clientes";
    }
}