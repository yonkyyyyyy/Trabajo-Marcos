package com.example.RDMProject.controller;

import com.example.RDMProject.model.Venta;
import com.example.RDMProject.service.ProductoService;
import com.example.RDMProject.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Importante para la notificación

@Controller
@RequestMapping("/")
public class VentaController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private VentaService ventaService;

    // 1. Añadir al carrito (CON NOTIFICACIÓN)
    @PostMapping("/cart/add/{id}")
    public String addCart(@PathVariable Long id, @RequestParam Integer cantidad, RedirectAttributes flash) {
        
        var producto = productoService.obtenerPorId(id);
        if (producto != null) {
            ventaService.agregarProductoAlCarrito(producto, cantidad);
            // Mensaje Flash: Solo dura una recarga
            flash.addFlashAttribute("success", "¡" + producto.getNombre() + " agregado al carrito!");
        }
        
        return "redirect:/productos";
    }

    // 2. Ver Carrito
    @GetMapping("/cart")
    public String getCart(Model model) {
        var carrito = ventaService.getCarrito();
        double total = carrito.stream().mapToDouble(d -> d.getSubtotal()).sum();

        model.addAttribute("carrito", carrito);
        model.addAttribute("total", total);
        
        return "carrito";
    }

    // 3. Eliminar del carrito
    @GetMapping("/cart/delete/{id}")
    public String deleteFromCart(@PathVariable Long id) {
        ventaService.quitarProductoDelCarrito(id);
        return "redirect:/cart";
    }

    // 4. Guardar Venta
    @GetMapping("/order/save")
    public String saveOrder() {
        Venta venta = new Venta();
        // Aquí podrías setear el usuario/cliente más adelante
        venta.setTotal(ventaService.getCarrito().stream().mapToDouble(d -> d.getSubtotal()).sum());
        
        ventaService.guardarVenta(venta);
        return "redirect:/";
    }
}