package com.example.RDMProject.controller;

import com.example.RDMProject.model.UnidadMedida;
import com.example.RDMProject.service.UnidadMedidaService;
import jakarta.validation.Valid; // Importar
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // Importar
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/unidades")
public class UnidadMedidaController {

    @Autowired
    private UnidadMedidaService unidadMedidaService;

    // Listar
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("unidades", unidadMedidaService.findAll());
        return "unidadmedida/lista";
    }

    // Formulario Nueva
    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("unidadMedida", new UnidadMedida());
        return "unidadmedida/form";
    }

    // Guardar (CON VALIDACIÓN)
    @PostMapping
    public String guardar(@Valid @ModelAttribute UnidadMedida unidadMedida, BindingResult result) {
        
        // Si hay error (nombre vacío), volvemos al formulario
        if (result.hasErrors()) {
            return "unidadmedida/form";
        }

        unidadMedidaService.save(unidadMedida);
        return "redirect:/unidades";
    }

    // Formulario Editar
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        UnidadMedida unidad = unidadMedidaService.findById(id);
        model.addAttribute("unidadMedida", unidad);
        return "unidadmedida/form";
    }

    // Eliminar
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        unidadMedidaService.deleteById(id);
        return "redirect:/unidades";
    }
}