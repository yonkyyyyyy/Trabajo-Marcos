package com.example.RDMProject.controller;

import com.example.RDMProject.model.Usuario;
import com.example.RDMProject.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/miperfil")
    public String verPerfil(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (userDetails == null) {
                redirectAttributes.addFlashAttribute("error", "Sesión no válida");
                return "redirect:/login";
            }

            Usuario usuario = usuarioService.buscarPorUsername(userDetails.getUsername());

            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/login";
            }

            model.addAttribute("usuario", usuario);
            return "miperfil";

        } catch (Exception e) {
            System.err.println("Error en ver perfil: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al cargar perfil");
            return "redirect:/";
        }
    }

    @PostMapping("/miperfil/actualizar")
    public String actualizarPerfil(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String nombreCompleto,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String numeroDocumento,
            @RequestParam(required = false) String passwordActual,
            @RequestParam(required = false) String passwordNuevo,
            @RequestParam(required = false) String passwordConfirmar,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (userDetails == null) {
                redirectAttributes.addFlashAttribute("error", "Sesión no válida");
                return "redirect:/login";
            }

            Usuario usuario = usuarioService.buscarPorUsername(userDetails.getUsername());

            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/login";
            }

            // Actualizar datos básicos solo si se proporcionan
            if (nombreCompleto != null && !nombreCompleto.trim().isEmpty()) {
                usuario.setNombreCompleto(nombreCompleto.trim());
            }

            if (email != null && !email.trim().isEmpty()) {
                usuario.setEmail(email.trim());
            }

            if (numeroDocumento != null && !numeroDocumento.trim().isEmpty()) {
                usuario.setNumeroDocumento(numeroDocumento.trim());
            }

            // Cambiar contraseña solo si se proporcionan los campos
            if (passwordActual != null && !passwordActual.isEmpty() &&
                    passwordNuevo != null && !passwordNuevo.isEmpty()) {

                // Validar contraseña actual
                if (!passwordEncoder.matches(passwordActual, usuario.getContrasena())) {
                    redirectAttributes.addFlashAttribute("error", "Contraseña actual incorrecta");
                    return "redirect:/miperfil";
                }

                // Validar que las nuevas coincidan
                if (!passwordNuevo.equals(passwordConfirmar)) {
                    redirectAttributes.addFlashAttribute("error", "Las contraseñas nuevas no coinciden");
                    return "redirect:/miperfil";
                }

                // Validar longitud mínima
                if (passwordNuevo.length() < 6) {
                    redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres");
                    return "redirect:/miperfil";
                }

                // Actualizar contraseña
                usuario.setContrasena(passwordEncoder.encode(passwordNuevo));
            }

            // Guardar cambios
            usuarioService.save(usuario);

            redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");

        } catch (Exception e) {
            System.err.println("Error al actualizar perfil: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al actualizar perfil: " + e.getMessage());
        }

        return "redirect:/miperfil";
    }
}