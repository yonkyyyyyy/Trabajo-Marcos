package com.example.RDMProject.controller;

import com.example.RDMProject.model.Usuario;
import com.example.RDMProject.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    // ============================================
    // LOGIN
    // ============================================

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }

        if (logout != null) {
            model.addAttribute("mensaje", "Has cerrado sesión exitosamente");
        }

        return "login";
    }

    // ============================================
    // REGISTRO
    // ============================================

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @ModelAttribute("usuario") Usuario usuario,
            Model model,
            RedirectAttributes redirectAttributes) {

        System.out.println("=== REGISTRO ===");
        System.out.println("Username: " + usuario.getUsername());
        System.out.println("Email: " + usuario.getEmail());

        // Validar que no exista el username
        if (usuarioService.existsByUsername(usuario.getUsername())) {
            model.addAttribute("error", "El nombre de usuario ya existe");
            return "register";
        }

        // Validar que no exista el email
        if (usuarioService.existsByEmail(usuario.getEmail())) {
            model.addAttribute("error", "El correo electrónico ya está registrado");
            return "register";
        }

        try {
            // Configurar usuario
            usuario.setRol("COMPRADOR"); // El service agregará ROLE_
            usuario.setEstado(1);

            // Guardar (el service encripta la contraseña)
            usuarioService.save(usuario);

            System.out.println("✓ Usuario registrado exitosamente");

            redirectAttributes.addFlashAttribute("mensaje", "¡Registro exitoso! Ya puedes iniciar sesión");
            return "redirect:/login";

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al registrar. Intenta nuevamente.");
            return "register";
        }
    }

    // ============================================
    // ACCESO DENEGADO
    // ============================================

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "acceso-denegado";
    }
}