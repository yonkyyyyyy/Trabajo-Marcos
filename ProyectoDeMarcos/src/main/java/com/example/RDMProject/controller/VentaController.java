package com.example.RDMProject.controller;

import com.example.RDMProject.model.*;
import com.example.RDMProject.model.enums.EstadoPedido;
import com.example.RDMProject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ClienteProveedorService clienteProveedorService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    // Listar ventas (ADMIN y VENDEDOR)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public String listar(
            @RequestParam(required = false) String estado,
            Model model
    ) {
        try {
            List<Venta> ventas;

            if (estado != null && !estado.isEmpty()) {
                EstadoPedido estadoPedido = EstadoPedido.valueOf(estado);
                ventas = ventaService.findByEstado(estadoPedido);
            } else {
                ventas = ventaService.findAll();
            }

            model.addAttribute("ventas", ventas);
            model.addAttribute("estadoFiltro", estado);
            model.addAttribute("estados", Arrays.asList(EstadoPedido.values())); // AGREGADO

            return "venta/lista";

        } catch (Exception e) {
            System.err.println("Error al listar ventas: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar las ventas");
            return "venta/lista";
        }
    }

    // Formulario nueva venta
    @GetMapping("/nueva")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'COMPRADOR')")
    public String nuevaVenta(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Obtener usuario actual
            Usuario usuario = usuarioService.buscarPorUsername(userDetails.getUsername());
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/login";
            }

            // Crear nueva venta
            Venta venta = new Venta();
            venta.setUsuario(usuario);
            venta.setFechaVenta(LocalDate.now());
            venta.setEstado(EstadoPedido.PENDIENTE);
            venta.setDescuento(0.0);
            venta.setSubtotal(0.0);
            venta.setIgv(0.0);
            venta.setTotal(0.0);

            // Cargar datos para el formulario
            List<ClienteProveedor> clientes = clienteProveedorService.findClientes();
            List<Producto> productos = productoService.listarProductos();

            model.addAttribute("venta", venta);
            model.addAttribute("clientes", clientes);
            model.addAttribute("productos", productos);

            return "venta/form";

        } catch (Exception e) {
            System.err.println("Error al crear formulario de venta: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al cargar formulario");
            return "redirect:/ventas";
        }
    }

    // Guardar venta
    @PostMapping("/guardar")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'COMPRADOR')")
    public String guardarVenta(
            @ModelAttribute Venta venta,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            System.out.println("=== GUARDAR VENTA - DEBUG ===");
            System.out.println("Venta ID: " + venta.getId());
            System.out.println("Cliente ID: " + (venta.getCliente() != null ? venta.getCliente().getId() : "NULL"));
            System.out.println("Usuario ID: " + (venta.getUsuario() != null ? venta.getUsuario().getIdUsuario() : "NULL"));
            System.out.println("Detalles count: " + (venta.getDetalles() != null ? venta.getDetalles().size() : "0"));

            // Validaciones básicas
            if (venta.getCliente() == null || venta.getCliente().getId() == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un cliente");
                return "redirect:/ventas/nueva";
            }

            if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Debe agregar al menos un producto");
                return "redirect:/ventas/nueva";
            }

            // Obtener usuario actual si no viene en el objeto
            if (venta.getUsuario() == null || venta.getUsuario().getIdUsuario() == null) {
                Usuario usuario = usuarioService.buscarPorUsername(userDetails.getUsername());
                venta.setUsuario(usuario);
            }

            // Cargar el cliente completo desde la BD
            ClienteProveedor cliente = clienteProveedorService.findById(venta.getCliente().getId());
            if (cliente == null) {
                redirectAttributes.addFlashAttribute("error", "Cliente no encontrado");
                return "redirect:/ventas/nueva";
            }
            venta.setCliente(cliente);

            // Establecer valores por defecto si faltan
            if (venta.getFechaVenta() == null) {
                venta.setFechaVenta(LocalDate.now());
            }

            if (venta.getEstado() == null) {
                venta.setEstado(EstadoPedido.PENDIENTE);
            }

            if (venta.getDescuento() == null) {
                venta.setDescuento(0.0);
            }

            // Procesar detalles
            for (DetalleVenta detalle : venta.getDetalles()) {
                // Cargar producto completo
                if (detalle.getProducto() != null && detalle.getProducto().getId() != null) {
                    Producto producto = productoService.obtenerPorId(detalle.getProducto().getId());
                    if (producto == null) {
                        redirectAttributes.addFlashAttribute("error", "Producto no encontrado: " + detalle.getProducto().getId());
                        return "redirect:/ventas/nueva";
                    }

                    detalle.setProducto(producto);

                    // Establecer precio si no viene
                    if (detalle.getPrecioUnitario() == null || detalle.getPrecioUnitario() == 0) {
                        detalle.setPrecioUnitario(producto.getPrecio());
                    }

                    // Calcular subtotal del detalle
                    if (detalle.getCantidad() != null && detalle.getPrecioUnitario() != null) {
                        detalle.setSubtotal(detalle.getCantidad() * detalle.getPrecioUnitario());
                    }

                    // Asociar detalle con venta
                    detalle.setVenta(venta);
                }
            }

            // Calcular totales
            venta.calcularTotal();

            System.out.println("Subtotal: " + venta.getSubtotal());
            System.out.println("IGV: " + venta.getIgv());
            System.out.println("Total: " + venta.getTotal());

            // Guardar venta
            Venta ventaGuardada = ventaService.save(venta);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Venta #" + ventaGuardada.getId() + " guardada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");

            return "redirect:/ventas";

        } catch (Exception e) {
            System.err.println("Error al guardar venta: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error",
                    "Error al guardar venta: " + e.getMessage());
            return "redirect:/ventas/nueva";
        }
    }

    // Ver detalle - CORREGIDO AQUÍ
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public String verDetalle(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Venta venta = ventaService.findById(id);
            if (venta == null) {
                redirectAttributes.addFlashAttribute("error", "Venta no encontrada");
                return "redirect:/ventas";
            }

            model.addAttribute("venta", venta);
            // AGREGAR LISTA DE ESTADOS DISPONIBLES
            model.addAttribute("estados", Arrays.asList(EstadoPedido.values()));

            return "venta/detalle";

        } catch (Exception e) {
            System.err.println("Error al ver detalle: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al cargar venta");
            return "redirect:/ventas";
        }
    }

    // Cambiar estado
    @PostMapping("/{id}/cambiar-estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public String cambiarEstado(
            @PathVariable Long id,
            @RequestParam String nuevoEstado,
            RedirectAttributes redirectAttributes
    ) {
        try {
            EstadoPedido estado = EstadoPedido.valueOf(nuevoEstado);
            Venta venta = ventaService.cambiarEstado(id, estado);

            if (venta != null) {
                redirectAttributes.addFlashAttribute("mensaje", "Estado actualizado a " + estado);
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("error", "No se pudo cambiar el estado");
            }

        } catch (Exception e) {
            System.err.println("Error al cambiar estado: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al cambiar estado");
        }

        return "redirect:/ventas/" + id;
    }

    // Eliminar venta
    @PostMapping("/{id}/eliminar")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            boolean eliminado = ventaService.deleteById(id);

            if (eliminado) {
                redirectAttributes.addFlashAttribute("mensaje", "Venta eliminada exitosamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("error",
                        "Solo se pueden eliminar ventas en estado PENDIENTE o CANCELADO");
            }

        } catch (Exception e) {
            System.err.println("Error al eliminar venta: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al eliminar venta");
        }

        return "redirect:/ventas";
    }
}