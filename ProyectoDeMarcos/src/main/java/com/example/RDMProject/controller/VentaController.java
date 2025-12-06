package com.example.RDMProject.controller;

import com.example.RDMProject.model.*;
import com.example.RDMProject.model.enums.EstadoPedido;
import com.example.RDMProject.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/ventas")
@PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'COMPRADOR')")
public class VentaController {

    @Autowired
    private VentaService ventaService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private ClienteProveedorService clienteService;
    
    @Autowired
    private ProductoService productoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public String listar(@RequestParam(required = false) EstadoPedido estado, Model model) {
        List<Venta> ventas;
        
        if (estado != null) {
            ventas = ventaService.findByEstado(estado);
            model.addAttribute("estadoFiltro", estado);
        } else {
            ventas = ventaService.findAll();
        }
        
        model.addAttribute("ventas", ventas);
        model.addAttribute("estados", EstadoPedido.values());
        model.addAttribute("ventasPendientes", ventaService.countPendientes());
        
        return "venta/lista";
    }

    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public String pendientes(Model model) {
        List<Venta> pendientes = ventaService.findPendientes();
        model.addAttribute("ventas", pendientes);
        model.addAttribute("total", pendientes.size());
        return "venta/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model, Authentication authentication) {
        Venta venta = new Venta();
        
        String username = authentication.getName();
        Usuario usuario = usuarioService.findByUsername(username).orElse(null);
        venta.setUsuario(usuario);
        
        model.addAttribute("venta", venta);
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("estados", EstadoPedido.values());
        
        return "venta/form";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model, Authentication authentication) {
        Venta venta = ventaService.findById(id);
        
        if (venta == null) {
            return "redirect:/ventas";
        }
        
        String username = authentication.getName();
        Usuario usuarioActual = usuarioService.findByUsername(username).orElse(null);
        
        if (usuarioActual != null) {
            String rol = usuarioActual.getRol();
            
            if (rol.equals("ROLE_COMPRADOR")) {
                if (!venta.getUsuario().equals(usuarioActual)) {
                    return "redirect:/acceso-denegado";
                }
            }
        }
        
        model.addAttribute("venta", venta);
        model.addAttribute("puedeModificar", venta.puedeModificar(usuarioActual));
        model.addAttribute("estados", EstadoPedido.values());
        
        return "venta/detalle";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, Authentication authentication) {
        Venta venta = ventaService.findById(id);
        
        if (venta == null) {
            return "redirect:/ventas";
        }
        
        String username = authentication.getName();
        Usuario usuarioActual = usuarioService.findByUsername(username).orElse(null);
        
        if (!venta.puedeModificar(usuarioActual)) {
            return "redirect:/ventas/" + id + "?error=No se puede modificar esta venta";
        }
        
        model.addAttribute("venta", venta);
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("productos", productoService.listarProductos());
        model.addAttribute("estados", EstadoPedido.values());
        
        return "venta/form";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("venta") Venta venta,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("clientes", clienteService.findAll());
            model.addAttribute("productos", productoService.listarProductos());
            return "venta/form";
        }
        
        try {
            if (venta.getId() == null) {
                String username = authentication.getName();
                Usuario usuario = usuarioService.findByUsername(username).orElse(null);
                venta.setUsuario(usuario);
                venta.setEstado(EstadoPedido.PENDIENTE);
            }
            
            Venta ventaGuardada = ventaService.save(venta);
            
            redirectAttributes.addFlashAttribute("mensaje", 
                "Venta guardada exitosamente. Total: S/ " + ventaGuardada.getTotal());
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            
            return "redirect:/ventas/" + ventaGuardada.getId();
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al guardar la venta: " + e.getMessage());
            return "redirect:/ventas/nueva";
        }
    }

    @PostMapping("/{id}/cambiar-estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public String cambiarEstado(
            @PathVariable Long id,
            @RequestParam EstadoPedido nuevoEstado,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Venta venta = ventaService.cambiarEstado(id, nuevoEstado);
            
            if (venta != null) {
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
        
        return "redirect:/ventas/" + id;
    }

    @PostMapping("/{id}/completar")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public String completar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        ventaService.marcarComoCompletada(id);
        redirectAttributes.addFlashAttribute("mensaje", "Venta marcada como completada");
        return "redirect:/ventas/" + id;
    }

    @PostMapping("/{id}/entregar")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public String entregar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        ventaService.marcarComoEntregada(id);
        redirectAttributes.addFlashAttribute("mensaje", "Venta marcada como entregada");
        return "redirect:/ventas/" + id;
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        ventaService.cancelar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Venta cancelada");
        redirectAttributes.addFlashAttribute("tipoMensaje", "warning");
        return "redirect:/ventas/" + id;
    }

    @PostMapping("/{id}/eliminar")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean eliminada = ventaService.deleteById(id);
        
        if (eliminada) {
            redirectAttributes.addFlashAttribute("mensaje", "Venta eliminada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } else {
            redirectAttributes.addFlashAttribute("error", 
                "No se puede eliminar. Solo se pueden eliminar ventas PENDIENTES o CANCELADAS");
        }
        
        return "redirect:/ventas";
    }
}
