package com.example.RDMProject.controller;

import com.example.RDMProject.model.Usuario;
import com.example.RDMProject.model.Venta;
import com.example.RDMProject.model.enums.EstadoPedido;
import com.example.RDMProject.service.UsuarioService;
import com.example.RDMProject.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/mis-pedidos")
@PreAuthorize("isAuthenticated()")
public class MisPedidosController {

    @Autowired
    private VentaService ventaService;
    
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String index(
            @RequestParam(required = false) EstadoPedido estado,
            Model model,
            Authentication authentication
    ) {
        String username = authentication.getName();
        Usuario usuario = usuarioService.findByUsername(username).orElse(null);
        
        if (usuario == null) {
            return "redirect:/login";
        }
        
        List<Venta> misPedidos;
        
        if (estado != null) {
            misPedidos = ventaService.findByUsuario(usuario).stream()
                .filter(v -> v.getEstado() == estado)
                .toList();
            model.addAttribute("estadoFiltro", estado);
        } else {
            misPedidos = ventaService.findByUsuario(usuario);
        }
        
        long totalPedidos = misPedidos.size();
        long pendientes = misPedidos.stream()
            .filter(v -> v.getEstado() == EstadoPedido.PENDIENTE)
            .count();
        long enProceso = misPedidos.stream()
            .filter(v -> v.getEstado() == EstadoPedido.EN_PROCESO)
            .count();
        long completados = misPedidos.stream()
            .filter(v -> v.getEstado() == EstadoPedido.COMPLETADO || v.getEstado() == EstadoPedido.ENTREGADO)
            .count();
        
        double totalGastado = misPedidos.stream()
            .filter(v -> v.getEstado() != EstadoPedido.CANCELADO)
            .mapToDouble(Venta::getTotal)
            .sum();
        
        model.addAttribute("pedidos", misPedidos);
        model.addAttribute("estados", EstadoPedido.values());
        model.addAttribute("totalPedidos", totalPedidos);
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("enProceso", enProceso);
        model.addAttribute("completados", completados);
        model.addAttribute("totalGastado", totalGastado);
        
        return "mis-pedidos/index";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id) {
        return "redirect:/ventas/" + id;
    }

    @GetMapping("/pendientes")
    public String pendientes(Model model, Authentication authentication) {
        String username = authentication.getName();
        Usuario usuario = usuarioService.findByUsername(username).orElse(null);
        
        if (usuario == null) {
            return "redirect:/login";
        }
        
        List<Venta> pendientes = ventaService.findByUsuario(usuario).stream()
            .filter(v -> v.getEstado() == EstadoPedido.PENDIENTE)
            .toList();
        
        model.addAttribute("pedidos", pendientes);
        model.addAttribute("titulo", "Pedidos Pendientes");
        
        return "mis-pedidos/index";
    }

    @GetMapping("/en-proceso")
    public String enProceso(Model model, Authentication authentication) {
        String username = authentication.getName();
        Usuario usuario = usuarioService.findByUsername(username).orElse(null);
        
        if (usuario == null) {
            return "redirect:/login";
        }
        
        List<Venta> enProceso = ventaService.findByUsuario(usuario).stream()
            .filter(v -> v.getEstado() == EstadoPedido.EN_PROCESO)
            .toList();
        
        model.addAttribute("pedidos", enProceso);
        model.addAttribute("titulo", "Pedidos en Proceso");
        
        return "mis-pedidos/index";
    }

    @GetMapping("/historial")
    public String historial(Model model, Authentication authentication) {
        String username = authentication.getName();
        Usuario usuario = usuarioService.findByUsername(username).orElse(null);
        
        if (usuario == null) {
            return "redirect:/login";
        }
        
        List<Venta> historial = ventaService.findByUsuario(usuario).stream()
            .filter(v -> v.getEstado() == EstadoPedido.COMPLETADO || 
                        v.getEstado() == EstadoPedido.ENTREGADO)
            .toList();
        
        model.addAttribute("pedidos", historial);
        model.addAttribute("titulo", "Historial de Pedidos");
        
        return "mis-pedidos/index";
    }
}
