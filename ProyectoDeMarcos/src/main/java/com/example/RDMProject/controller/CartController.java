package com.example.RDMProject.controller;

import com.example.RDMProject.model.*;
import com.example.RDMProject.model.enums.EstadoPedido;
import com.example.RDMProject.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
public class CartController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private VentaService ventaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ClienteProveedorService clienteProveedorService;

    // Ver carrito
    @GetMapping("/cart")
    public String verCarrito(HttpSession session, Model model) {
        Map<Long, ItemCarrito> carrito = obtenerCarrito(session);

        double subtotal = carrito.values().stream()
                .mapToDouble(item -> item.getProducto().getPrecio() * item.getCantidad())
                .sum();

        double igv = subtotal * 0.18;
        double total = subtotal + igv;

        model.addAttribute("carrito", carrito.values());
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("igv", igv);
        model.addAttribute("total", total);
        model.addAttribute("cantidadItems", carrito.size());

        return "carrito";
    }

    // Agregar producto al carrito
    @PostMapping("/cart/add/{id}")
    public String agregarAlCarrito(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int cantidad,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Producto producto = productoService.obtenerPorId(id);

            if (producto == null) {
                redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
                return "redirect:/productos";
            }

            // Validar stock
            if (producto.getCantidad() < cantidad) {
                redirectAttributes.addFlashAttribute("error",
                        "Stock insuficiente. Solo hay " + producto.getCantidad() + " unidades disponibles");
                return "redirect:/productos";
            }

            Map<Long, ItemCarrito> carrito = obtenerCarrito(session);

            // Si el producto ya está en el carrito, aumentar cantidad
            if (carrito.containsKey(id)) {
                ItemCarrito item = carrito.get(id);
                int nuevaCantidad = item.getCantidad() + cantidad;

                // Validar stock total
                if (producto.getCantidad() < nuevaCantidad) {
                    redirectAttributes.addFlashAttribute("error",
                            "No se puede agregar. Stock máximo: " + producto.getCantidad());
                    return "redirect:/productos";
                }

                item.setCantidad(nuevaCantidad);
            } else {
                // Agregar nuevo producto
                ItemCarrito nuevoItem = new ItemCarrito(producto, cantidad);
                carrito.put(id, nuevoItem);
            }

            session.setAttribute("carrito", carrito);
            // Actualizar el contador del carrito para el badge en el header
            actualizarContadorCarrito(session, carrito);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Producto agregado al carrito");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");

        } catch (Exception e) {
            System.err.println("Error al agregar producto al carrito: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error",
                    "Error al agregar producto: " + e.getMessage());
        }

        return "redirect:/productos";
    }

    // Método auxiliar para actualizar el contador del carrito
    private void actualizarContadorCarrito(HttpSession session, Map<Long, ItemCarrito> carrito) {
        int totalItems = carrito.values().stream()
                .mapToInt(ItemCarrito::getCantidad)
                .sum();
        session.setAttribute("carritoCount", totalItems);
    }

    // Actualizar cantidad de un producto
    @PostMapping("/cart/update/{id}")
    public String actualizarCantidad(
            @PathVariable Long id,
            @RequestParam int cantidad,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            if (cantidad <= 0) {
                return eliminarDelCarrito(id, session, redirectAttributes);
            }

            Map<Long, ItemCarrito> carrito = obtenerCarrito(session);
            ItemCarrito item = carrito.get(id);

            if (item != null) {
                Producto producto = item.getProducto();

                // Validar stock
                if (producto.getCantidad() < cantidad) {
                    redirectAttributes.addFlashAttribute("error",
                            "Stock insuficiente. Solo hay " + producto.getCantidad() + " unidades");
                    return "redirect:/cart";
                }

                item.setCantidad(cantidad);
                session.setAttribute("carrito", carrito);
                // Actualizar el contador del carrito
                actualizarContadorCarrito(session, carrito);

                redirectAttributes.addFlashAttribute("mensaje", "Cantidad actualizada");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al actualizar: " + e.getMessage());
        }

        return "redirect:/cart";
    }

    // Eliminar producto del carrito
    @GetMapping("/cart/remove/{id}")
    public String eliminarDelCarrito(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Map<Long, ItemCarrito> carrito = obtenerCarrito(session);
            carrito.remove(id);
            session.setAttribute("carrito", carrito);
            // Actualizar el contador del carrito
            actualizarContadorCarrito(session, carrito);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Producto eliminado del carrito");
            redirectAttributes.addFlashAttribute("tipoMensaje", "warning");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al eliminar: " + e.getMessage());
        }

        return "redirect:/cart";
    }

    // Vaciar carrito
    @GetMapping("/cart/clear")
    public String vaciarCarrito(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute("carrito");
        session.setAttribute("carritoCount", 0);
        redirectAttributes.addFlashAttribute("mensaje", "Carrito vaciado");
        redirectAttributes.addFlashAttribute("tipoMensaje", "info");
        return "redirect:/cart";
    }

    // Proceder al checkout - CREAR PEDIDO DIRECTAMENTE
    @GetMapping("/cart/checkout")
    public String checkout(
            HttpSession session,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Map<Long, ItemCarrito> carrito = obtenerCarrito(session);

            if (carrito.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El carrito está vacío");
                return "redirect:/cart";
            }

            // Obtener el usuario actual
            Usuario usuario = usuarioService.buscarPorUsername(userDetails.getUsername());
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/cart";
            }

            // Crear la venta
            Venta venta = new Venta();
            venta.setUsuario(usuario);
            venta.setEstado(EstadoPedido.PENDIENTE);
            venta.setDescuento(0.0);

            // Buscar un cliente genérico o usar el primero (opcional)
            // Si tienes un cliente por defecto para pedidos online, úsalo aquí
            try {
                var clientes = clienteProveedorService.findClientes();
                if (!clientes.isEmpty()) {
                    venta.setCliente(clientes.get(0)); // Cliente genérico
                }
            } catch (Exception e) {
                // Si no hay clientes, continuar sin cliente
            }

            // Agregar los detalles desde el carrito
            for (ItemCarrito item : carrito.values()) {
                DetalleVenta detalle = new DetalleVenta();
                detalle.setProducto(item.getProducto());
                detalle.setCantidad(item.getCantidad());
                detalle.setPrecioUnitario(item.getProducto().getPrecio());
                detalle.setSubtotal(item.getSubtotal());

                venta.agregarDetalle(detalle);
            }

            // Calcular totales
            venta.calcularTotal();

            // Guardar la venta
            Venta ventaGuardada = ventaService.save(venta);

            // Limpiar el carrito
            session.removeAttribute("carrito");
            session.setAttribute("carritoCount", 0);

            redirectAttributes.addFlashAttribute("mensaje",
                    "¡Pedido creado exitosamente! Número de pedido: " + ventaGuardada.getId());
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");

            // Redirigir a mis pedidos
            return "redirect:/mis-pedidos";

        } catch (Exception e) {
            System.err.println("Error al crear pedido: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error",
                    "Error al crear el pedido: " + e.getMessage());
            return "redirect:/cart";
        }
    }

    // Método auxiliar para obtener el carrito de la sesión
    @SuppressWarnings("unchecked")
    private Map<Long, ItemCarrito> obtenerCarrito(HttpSession session) {
        Map<Long, ItemCarrito> carrito =
                (Map<Long, ItemCarrito>) session.getAttribute("carrito");

        if (carrito == null) {
            carrito = new HashMap<>();
            session.setAttribute("carrito", carrito);
        }

        return carrito;
    }

    // Clase interna para representar un item del carrito
    public static class ItemCarrito {
        private Producto producto;
        private int cantidad;

        public ItemCarrito(Producto producto, int cantidad) {
            this.producto = producto;
            this.cantidad = cantidad;
        }

        public Producto getProducto() {
            return producto;
        }

        public void setProducto(Producto producto) {
            this.producto = producto;
        }

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }

        public double getSubtotal() {
            return producto.getPrecio() * cantidad;
        }
    }
}