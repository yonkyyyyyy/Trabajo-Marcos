package com.example.RDMProject.controller;

import com.example.RDMProject.model.Usuario;
import com.example.RDMProject.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/miperfil")
@PreAuthorize("isAuthenticated()")
public class ProfileController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String verPerfil(Model model, Authentication authentication) {
        String username = authentication.getName();
        Usuario usuario = usuarioService.findByUsername(username).orElse(null);
        
        if (usuario == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("usuario", usuario);
        return "perfil";
    }

    @PostMapping("/actualizar")
    public String actualizarPerfil(
            @ModelAttribute("usuario") Usuario usuarioActualizado,
            @RequestParam(required = false) String passwordActual,
            @RequestParam(required = false) String passwordNuevo,
            @RequestParam(required = false) String passwordConfirmar,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        String username = authentication.getName();
        Usuario usuarioDB = usuarioService.findByUsername(username).orElse(null);
        
        if (usuarioDB == null) {
            return "redirect:/login";
        }
        
        try {
            // Actualizar datos básicos
            usuarioDB.setNombreCompleto(usuarioActualizado.getNombreCompleto());
            usuarioDB.setEmail(usuarioActualizado.getEmail());
            usuarioDB.setNumeroDocumento(usuarioActualizado.getNumeroDocumento());
            
            // Si quiere cambiar la contraseña
            if (passwordNuevo != null && !passwordNuevo.isEmpty()) {
                // Validar contraseña actual
                if (passwordActual == null || passwordActual.isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", 
                        "Debes ingresar tu contraseña actual");
                    return "redirect:/miperfil";
                }
                
                if (!passwordEncoder.matches(passwordActual, usuarioDB.getContrasena())) {
                    redirectAttributes.addFlashAttribute("error", 
                        "La contraseña actual es incorrecta");
                    return "redirect:/miperfil";
                }
                
                // Validar confirmación
                if (!passwordNuevo.equals(passwordConfirmar)) {
                    redirectAttributes.addFlashAttribute("error", 
                        "Las contraseñas nuevas no coinciden");
                    return "redirect:/miperfil";
                }
                
                // Actualizar contraseña
                usuarioDB.setContrasena(passwordEncoder.encode(passwordNuevo));
            }
            
            usuarioService.save(usuarioDB);
            
            redirectAttributes.addFlashAttribute("mensaje", 
                "Perfil actualizado exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al actualizar perfil: " + e.getMessage());
        }
        
        return "redirect:/miperfil";
    }
}
