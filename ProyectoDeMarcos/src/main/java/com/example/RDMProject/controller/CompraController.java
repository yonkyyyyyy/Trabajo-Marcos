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
import java.util.List;

@Controller
@RequestMapping("/compras")
@PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
public class CompraController {

    @Autowired
    private CompraService compraService;

    @Autowired
    private ClienteProveedorService clienteProveedorService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    // Listar compras
    @GetMapping
    public String listar(
            @RequestParam(required = false) String estado,
            Model model
    ) {
        try {
            List<Compra> compras;

            if (estado != null && !estado.isEmpty()) {
                try {
                    EstadoPedido estadoPedido = EstadoPedido.valueOf(estado);
                    compras = compraService.findByEstado(estadoPedido);
                } catch (IllegalArgumentException e) {
                    compras = compraService.findAll();
                }
            } else {
                compras = compraService.findAll();
            }

            // Calcular estadísticas
            long totalCompras = compras.size();

            double montoTotal = compras.stream()
                    .filter(c -> c.getEstado() != EstadoPedido.CANCELADO)
                    .mapToDouble(c -> c.getTotal() != null ? c.getTotal() : 0.0)
                    .sum();

            double comprasHoy = compras.stream()
                    .filter(c -> c.getFechaCompra() != null &&
                            c.getFechaCompra().equals(LocalDate.now()) &&
                            c.getEstado() != EstadoPedido.CANCELADO)
                    .mapToDouble(c -> c.getTotal() != null ? c.getTotal() : 0.0)
                    .sum();

            long pendientes = compras.stream()
                    .filter(c -> c.getEstado() == EstadoPedido.PENDIENTE)
                    .count();

            model.addAttribute("compras", compras);
            model.addAttribute("estadoFiltro", estado);
            model.addAttribute("totalCompras", totalCompras);
            model.addAttribute("montoTotal", montoTotal);
            model.addAttribute("comprasHoy", comprasHoy);
            model.addAttribute("pendientes", pendientes);

            System.out.println("=== DEBUG LISTA COMPRAS ===");
            System.out.println("Total compras: " + totalCompras);
            System.out.println("Monto total: " + montoTotal);
            System.out.println("Compras hoy: " + comprasHoy);
            System.out.println("Pendientes: " + pendientes);
            System.out.println("Retornando: compra/lista");

            return "compra/lista";

        } catch (Exception e) {
            System.err.println("Error al listar compras: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar las compras: " + e.getMessage());
            model.addAttribute("compras", List.of());
            model.addAttribute("totalCompras", 0L);
            model.addAttribute("montoTotal", 0.0);
            model.addAttribute("comprasHoy", 0.0);
            model.addAttribute("pendientes", 0L);
            return "compra/lista";
        }
    }

    // Formulario nueva compra
    @GetMapping("/nueva")
    public String nuevaCompra(
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

            // Crear nueva compra
            Compra compra = new Compra();
            compra.setUsuario(usuario);
            compra.setFechaCompra(LocalDate.now());
            compra.setEstado(EstadoPedido.PENDIENTE);
            compra.setDescuento(0.0);
            compra.setSubtotal(0.0);
            compra.setIgv(0.0);
            compra.setTotal(0.0);

            // Cargar PROVEEDORES (tipo = 2 o tipo = "2")
            List<ClienteProveedor> proveedores = clienteProveedorService.findProveedores();

            if (proveedores.isEmpty()) {
                redirectAttributes.addFlashAttribute("error",
                        "No hay proveedores registrados. Por favor, registre proveedores primero.");
                return "redirect:/clientes";
            }

            List<Producto> productos = productoService.listarProductos();

            model.addAttribute("compra", compra);
            model.addAttribute("proveedores", proveedores);
            model.addAttribute("productos", productos);

            System.out.println("=== DEBUG NUEVA COMPRA ===");
            System.out.println("Proveedores encontrados: " + proveedores.size());
            proveedores.forEach(p -> System.out.println("- " + p.getNombreRazonSocial() + " (tipo: " + p.getTipo() + ")"));

            return "compra/form";

        } catch (Exception e) {
            System.err.println("Error al crear formulario de compra: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al cargar formulario: " + e.getMessage());
            return "redirect:/compras";
        }
    }

    // Guardar compra
    @PostMapping("/guardar")
    public String guardarCompra(
            @ModelAttribute Compra compra,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            System.out.println("=== GUARDAR COMPRA - DEBUG ===");
            System.out.println("Compra ID: " + compra.getId());
            System.out.println("Proveedor ID: " + (compra.getProveedor() != null ? compra.getProveedor().getId() : "NULL"));
            System.out.println("Usuario ID: " + (compra.getUsuario() != null ? compra.getUsuario().getIdUsuario() : "NULL"));
            System.out.println("Detalles count: " + (compra.getDetalles() != null ? compra.getDetalles().size() : "0"));

            // Validar proveedor seleccionado
            if (compra.getProveedor() == null || compra.getProveedor().getId() == null) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar un proveedor");
                return "redirect:/compras/nueva";
            }

            // Validar detalles
            if (compra.getDetalles() == null || compra.getDetalles().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Debe agregar al menos un producto");
                return "redirect:/compras/nueva";
            }

            // Obtener usuario actual si no viene
            if (compra.getUsuario() == null || compra.getUsuario().getIdUsuario() == null) {
                Usuario usuario = usuarioService.buscarPorUsername(userDetails.getUsername());
                compra.setUsuario(usuario);
            }

            // Cargar proveedor completo desde la BD
            ClienteProveedor proveedor = clienteProveedorService.findById(compra.getProveedor().getId());
            if (proveedor == null) {
                redirectAttributes.addFlashAttribute("error", "Proveedor no encontrado");
                return "redirect:/compras/nueva";
            }

            // Verificar que sea proveedor (tipo = 2 o "2")
            String tipo = proveedor.getTipo();
            if (tipo == null || (!tipo.equals("2") && !tipo.equals("PROVEEDOR"))) {
                redirectAttributes.addFlashAttribute("error",
                        "El cliente seleccionado no es un proveedor. Tipo actual: " + tipo);
                return "redirect:/compras/nueva";
            }

            compra.setProveedor(proveedor);

            // Establecer valores por defecto
            if (compra.getFechaCompra() == null) {
                compra.setFechaCompra(LocalDate.now());
            }

            if (compra.getEstado() == null) {
                compra.setEstado(EstadoPedido.PENDIENTE);
            }

            if (compra.getDescuento() == null) {
                compra.setDescuento(0.0);
            }

            // Procesar detalles
            for (DetalleCompra detalle : compra.getDetalles()) {
                if (detalle.getProducto() != null && detalle.getProducto().getId() != null) {
                    // Cargar producto completo
                    Producto producto = productoService.obtenerPorId(detalle.getProducto().getId());
                    if (producto == null) {
                        redirectAttributes.addFlashAttribute("error",
                                "Producto no encontrado: " + detalle.getProducto().getId());
                        return "redirect:/compras/nueva";
                    }

                    detalle.setProducto(producto);

                    // Establecer precio si no viene (usar precio_compra)
                    if (detalle.getPrecioUnitario() == null || detalle.getPrecioUnitario() == 0) {
                        detalle.setPrecioUnitario(producto.getPrecio_compra());
                    }

                    // Calcular subtotal del detalle
                    if (detalle.getCantidad() != null && detalle.getPrecioUnitario() != null) {
                        detalle.setSubtotal(detalle.getCantidad() * detalle.getPrecioUnitario());
                    }

                    // Asociar detalle con compra
                    detalle.setCompra(compra);
                }
            }

            // Calcular totales
            compra.calcularTotal();

            System.out.println("Subtotal: " + compra.getSubtotal());
            System.out.println("IGV: " + compra.getIgv());
            System.out.println("Total: " + compra.getTotal());

            // Guardar compra
            Compra compraGuardada = compraService.save(compra);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Compra #" + compraGuardada.getId() + " guardada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");

            return "redirect:/compras";

        } catch (Exception e) {
            System.err.println("Error al guardar compra: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error",
                    "Error al guardar compra: " + e.getMessage());
            return "redirect:/compras/nueva";
        }
    }

    // Ver detalle
    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Compra compra = compraService.findById(id);
            if (compra == null) {
                redirectAttributes.addFlashAttribute("error", "Compra no encontrada");
                return "redirect:/compras";
            }

            model.addAttribute("compra", compra);

            System.out.println("=== DEBUG DETALLE COMPRA ===");
            System.out.println("Compra ID: " + compra.getId());
            System.out.println("Proveedor: " + (compra.getProveedor() != null ? compra.getProveedor().getNombreRazonSocial() : "NULL"));
            System.out.println("Retornando: compra/detalle");

            return "compra/detalle";

        } catch (Exception e) {
            System.err.println("Error al ver detalle: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al cargar compra");
            return "redirect:/compras";
        }
    }

    // Cambiar estado
    @PostMapping("/{id}/cambiar-estado")
    public String cambiarEstado(
            @PathVariable Long id,
            @RequestParam String nuevoEstado,
            RedirectAttributes redirectAttributes
    ) {
        try {
            EstadoPedido estado = EstadoPedido.valueOf(nuevoEstado);
            Compra compra = compraService.cambiarEstado(id, estado);

            if (compra != null) {
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

        return "redirect:/compras/" + id;
    }

    // Eliminar compra (solo ADMIN)
    @PostMapping("/{id}/eliminar")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            boolean eliminado = compraService.deleteById(id);

            if (eliminado) {
                redirectAttributes.addFlashAttribute("mensaje", "Compra eliminada exitosamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                redirectAttributes.addFlashAttribute("error",
                        "Solo se pueden eliminar compras en estado PENDIENTE o CANCELADO");
            }

        } catch (Exception e) {
            System.err.println("Error al eliminar compra: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al eliminar compra");
        }

        return "redirect:/compras";
    }
}