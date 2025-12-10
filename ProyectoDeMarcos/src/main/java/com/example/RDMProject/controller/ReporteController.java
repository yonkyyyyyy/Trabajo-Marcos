package com.example.RDMProject.controller;

import com.example.RDMProject.model.*;
import com.example.RDMProject.model.enums.EstadoPedido;
import com.example.RDMProject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reportes")
@PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
public class ReporteController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ClienteProveedorService clienteProveedorService;

    // ==================== REPORTE DE VENTAS ====================
    @GetMapping("/ventas")
    public String reporteVentas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) String numerador,
            @RequestParam(required = false) String estado,
            Model model
    ) {
        try {
            // Si no hay fechas, usar el mes actual
            if (fechaInicio == null) {
                fechaInicio = LocalDate.now().withDayOfMonth(1);
            }
            if (fechaFin == null) {
                fechaFin = LocalDate.now();
            }

            // Obtener todas las ventas
            List<Venta> ventas = ventaService.findAll();

            // Aplicar filtros
            final LocalDate fInicio = fechaInicio;
            final LocalDate fFin = fechaFin;

            ventas = ventas.stream()
                    .filter(v -> v.getFechaVenta() != null)
                    .filter(v -> !v.getFechaVenta().isBefore(fInicio) && !v.getFechaVenta().isAfter(fFin))
                    .collect(Collectors.toList());

            // Filtrar por usuario (vendedor)
            if (usuarioId != null) {
                ventas = ventas.stream()
                        .filter(v -> v.getUsuario() != null && v.getUsuario().getIdUsuario().equals(usuarioId))
                        .collect(Collectors.toList());
            }

            // Filtrar por cliente (comprador)
            if (clienteId != null) {
                ventas = ventas.stream()
                        .filter(v -> v.getCliente() != null && v.getCliente().getId().equals(clienteId))
                        .collect(Collectors.toList());
            }

            // Filtrar por numerador
            if (numerador != null && !numerador.trim().isEmpty()) {
                final String numBusqueda = numerador.trim();
                ventas = ventas.stream()
                        .filter(v -> v.getNumerador() != null && v.getNumerador().toString().contains(numBusqueda))
                        .collect(Collectors.toList());
            }

            // Filtrar por estado
            if (estado != null && !estado.isEmpty()) {
                try {
                    EstadoPedido estadoPedido = EstadoPedido.valueOf(estado);
                    ventas = ventas.stream()
                            .filter(v -> v.getEstado() == estadoPedido)
                            .collect(Collectors.toList());
                } catch (IllegalArgumentException ignored) {}
            }

            // Calcular estadísticas
            double totalVentas = ventas.stream()
                    .filter(v -> v.getEstado() != EstadoPedido.CANCELADO)
                    .mapToDouble(v -> v.getTotal() != null ? v.getTotal() : 0.0)
                    .sum();

            long cantidadVentas = ventas.size();

            long ventasCompletadas = ventas.stream()
                    .filter(v -> v.getEstado() == EstadoPedido.COMPLETADO)
                    .count();

            long ventasPendientes = ventas.stream()
                    .filter(v -> v.getEstado() == EstadoPedido.PENDIENTE)
                    .count();

            // Cargar datos para los filtros
            List<Usuario> usuarios = usuarioService.findAll();
            List<ClienteProveedor> clientes = clienteProveedorService.findClientes();

            model.addAttribute("ventas", ventas);
            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);
            model.addAttribute("usuarioId", usuarioId);
            model.addAttribute("clienteId", clienteId);
            model.addAttribute("numeradorBusqueda", numerador);
            model.addAttribute("estadoFiltro", estado);
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("clientes", clientes);
            model.addAttribute("estados", Arrays.asList(EstadoPedido.values()));
            model.addAttribute("totalVentas", totalVentas);
            model.addAttribute("cantidadVentas", cantidadVentas);
            model.addAttribute("ventasCompletadas", ventasCompletadas);
            model.addAttribute("ventasPendientes", ventasPendientes);

            return "reportes/ventas";

        } catch (Exception e) {
            System.err.println("Error en reporte de ventas: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al generar el reporte: " + e.getMessage());
            return "reportes/ventas";
        }
    }

    // ==================== REPORTE DE INVENTARIO ====================
    @GetMapping("/inventario")
    public String reporteInventario(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String stockBajo,
            @RequestParam(required = false) String busqueda,
            Model model
    ) {
        try {
            // Obtener todos los productos
            List<Producto> productos = productoService.listarProductos();

            // Filtrar por categoría
            if (categoriaId != null) {
                productos = productos.stream()
                        .filter(p -> p.getCategoria() != null && p.getCategoria().getIdCategoria().equals(categoriaId))
                        .collect(Collectors.toList());
            }

            // Filtrar por stock bajo (productos con cantidad <= stock mínimo o <= 10 si no tiene mínimo)
            if ("true".equals(stockBajo)) {
                productos = productos.stream()
                        .filter(p -> {
                            int minimo = (p.getStock_minimo() != null) ? p.getStock_minimo() : 10;
                            return p.getCantidad() <= minimo;
                        })
                        .collect(Collectors.toList());
            }

            // Filtrar por búsqueda
            if (busqueda != null && !busqueda.trim().isEmpty()) {
                final String busq = busqueda.toLowerCase().trim();
                productos = productos.stream()
                        .filter(p -> p.getNombre().toLowerCase().contains(busq) ||
                                (p.getDescripcion() != null && p.getDescripcion().toLowerCase().contains(busq)))
                        .collect(Collectors.toList());
            }

            // Calcular estadísticas
            long totalProductos = productos.size();

            int totalUnidades = productos.stream()
                    .mapToInt(Producto::getCantidad)
                    .sum();

            double valorInventario = productos.stream()
                    .mapToDouble(p -> p.getCantidad() * p.getPrecio())
                    .sum();

            long productosStockBajo = productos.stream()
                    .filter(p -> {
                        int minimo = (p.getStock_minimo() != null) ? p.getStock_minimo() : 10;
                        return p.getCantidad() <= minimo;
                    })
                    .count();

            long productosSinStock = productos.stream()
                    .filter(p -> p.getCantidad() <= 0)
                    .count();

            // Cargar categorías
            List<Categoria> categorias = productoService.listarCategorias();

            model.addAttribute("productos", productos);
            model.addAttribute("categoriaId", categoriaId);
            model.addAttribute("stockBajo", stockBajo);
            model.addAttribute("busqueda", busqueda);
            model.addAttribute("categorias", categorias);
            model.addAttribute("totalProductos", totalProductos);
            model.addAttribute("totalUnidades", totalUnidades);
            model.addAttribute("valorInventario", valorInventario);
            model.addAttribute("productosStockBajo", productosStockBajo);
            model.addAttribute("productosSinStock", productosSinStock);

            return "reportes/inventario";

        } catch (Exception e) {
            System.err.println("Error en reporte de inventario: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al generar el reporte: " + e.getMessage());
            return "reportes/inventario";
        }
    }
}
