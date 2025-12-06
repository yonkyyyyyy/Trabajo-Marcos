package com.example.RDMProject.controller;

import com.example.RDMProject.model.*;
import com.example.RDMProject.model.enums.EstadoPedido;
import com.example.RDMProject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/compras")
@PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
public class CompraController {

    @Autowired
    private CompraService compraService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private ClienteProveedorService proveedorService;
    
    @Autowired
    private ProductoService productoService;

    @GetMapping
    public String listar(@RequestParam(required = false) String estado, Model model) {
        List<Compra> compras;
        
        if (estado != null && !estado.isEmpty()) {
            try {
                EstadoPedido estadoPedido = EstadoPedido.valueOf(estado);
                compras = compraService.findByEstado(estadoPedido);
                model.addAttribute("estadoFiltro", estado);
            } catch (IllegalArgumentException e) {
                compras = compraService.findAll();
            }
        } else {
            compras = compraService.findAll();
        }
        
        model.addAttribute("compras", compras);
        model.addAttribute("estados", EstadoPedido.values());
        model.addAttribute("titulo", "Gestión de Compras");
        
        return "compra/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model, Authentication authentication) {
        Compra compra = new Compra();
        compra.setFechaCompra(LocalDate.now());
        
        String username = authentication.getName();
        Usuario usuario = usuarioService.findByUsername(username).orElse(null);
        compra.setUsuario(usuario);
        
        model.addAttribute("compra", compra);
        model.addAttribute("proveedores", proveedorService.findProveedores());
        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("estados", EstadoPedido.values());
        
        return "compra/form";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Compra compra = compraService.findById(id);
        
        if (compra == null) {
            return "redirect:/compras";
        }
        
        model.addAttribute("compra", compra);
        model.addAttribute("estados", EstadoPedido.values());
        
        return "compra/detalle";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Compra compra = compraService.findById(id);
        
        if (compra == null) {
            return "redirect:/compras";
        }
        
        if (compra.getEstado() != null && compra.getEstado() != EstadoPedido.PENDIENTE) {
            return "redirect:/compras/" + id + "?error=No se puede modificar esta compra";
        }
        
        model.addAttribute("compra", compra);
        model.addAttribute("proveedores", proveedorService.findProveedores());
        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("estados", EstadoPedido.values());
        
        return "compra/form";
    }

    @PostMapping("/guardar")
    public String guardar(
            @ModelAttribute("compra") Compra compra,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (compra.getId() == null) {
                String username = authentication.getName();
                Usuario usuario = usuarioService.findByUsername(username).orElse(null);
                compra.setUsuario(usuario);
                compra.setEstado(EstadoPedido.PENDIENTE);
            }
            
            Compra compraGuardada = compraService.save(compra);
            
            redirectAttributes.addFlashAttribute("mensaje", 
                "Compra guardada exitosamente. Total: S/ " + compraGuardada.getTotal());
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            
            return "redirect:/compras/" + compraGuardada.getId();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al guardar la compra: " + e.getMessage());
            return "redirect:/compras/nueva";
        }
    }

    @PostMapping("/{id}/cambiar-estado")
    @PreAuthorize("hasRole('ADMIN')")
    public String cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoPedido nuevoEstado,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Compra compra = compraService.cambiarEstado(id, nuevoEstado);
            
            if (compra != null) {
                redirectAttributes.addFlashAttribute("mensaje", 
                    "Estado actualizado a: " + nuevoEstado.getDescripcion());
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("error", 
                    "No se pudo cambiar el estado");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al cambiar estado: " + e.getMessage());
        }
        
        return "redirect:/compras/" + id;
    }

    @PostMapping("/{id}/eliminar")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            boolean eliminada = compraService.deleteById(id);
            
            if (eliminada) {
                redirectAttributes.addFlashAttribute("mensaje", "Compra eliminada exitosamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("error", 
                    "No se puede eliminar. Solo se pueden eliminar compras PENDIENTES o CANCELADAS");
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al eliminar: " + e.getMessage());
        }
        
        return "redirect:/compras";
    }
}
