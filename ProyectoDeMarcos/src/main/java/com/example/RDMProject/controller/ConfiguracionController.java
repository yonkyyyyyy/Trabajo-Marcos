package com.example.RDMProject.controller;

import com.example.RDMProject.model.*;
import com.example.RDMProject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/configuracion")
@PreAuthorize("hasRole('ADMIN')")
public class ConfiguracionController {

    @Autowired
    private ParametrosService parametrosService;
    
    @Autowired
    private SerieDocVentaCompraService serieDocService;
    
    @Autowired
    private UnidadMedidaService unidadMedidaService;
    
    @Autowired
    private CategoriaService categoriaService;

    // Dashboard principal de configuración
    @GetMapping
    public String dashboard(Model model) {
        Parametros parametros = parametrosService.getParametros();
        List<SerieDocVentaCompra> series = serieDocService.findAll();
        List<TipoDocVenta> tiposDocumento = serieDocService.findAllTiposDocumento();
        
        model.addAttribute("parametros", parametros);
        model.addAttribute("series", series);
        model.addAttribute("tiposDocumento", tiposDocumento);
        model.addAttribute("totalSeries", series.size());
        
        return "configuracion/dashboard";
    }
    
    // ==================== PARAMETROS ====================
    
    @GetMapping("/parametros")
    public String editarParametros(Model model) {
        Parametros parametros = parametrosService.getParametros();
        model.addAttribute("parametros", parametros);
        return "configuracion/parametros";
    }
    
    @PostMapping("/parametros/guardar")
    public String guardarParametros(@ModelAttribute Parametros parametros, 
                                     RedirectAttributes redirectAttributes) {
        try {
            parametrosService.save(parametros);
            redirectAttributes.addFlashAttribute("mensaje", "Parámetros guardados exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        }
        return "redirect:/configuracion/parametros";
    }
    
    // ==================== SERIES DE DOCUMENTOS ====================
    
    @GetMapping("/series")
    public String listarSeries(Model model) {
        List<SerieDocVentaCompra> series = serieDocService.findAll();
        List<TipoDocVenta> tiposDocumento = serieDocService.findAllTiposDocumento();
        
        model.addAttribute("series", series);
        model.addAttribute("tiposDocumento", tiposDocumento);
        model.addAttribute("nuevaSerie", new SerieDocVentaCompra());
        
        return "configuracion/series";
    }
    
    @PostMapping("/series/guardar")
    public String guardarSerie(@ModelAttribute SerieDocVentaCompra serie,
                               @RequestParam(required = false) Long tipoDocVentaId,
                               RedirectAttributes redirectAttributes) {
        try {
            // Asignar el tipo de documento
            if (tipoDocVentaId != null) {
                TipoDocVenta tipoDoc = serieDocService.findTipoDocumentoById(tipoDocVentaId).orElse(null);
                serie.setTipoDocVenta(tipoDoc);
            }
            
            // Inicializar numerador si es nuevo
            if (serie.getNumerador() == null) {
                serie.setNumerador(0);
            }
            
            serieDocService.save(serie);
            redirectAttributes.addFlashAttribute("mensaje", "Serie guardada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        }
        return "redirect:/configuracion/series";
    }
    
    @GetMapping("/series/editar/{id}")
    public String editarSerie(@PathVariable Long id, Model model) {
        SerieDocVentaCompra serie = serieDocService.findById(id).orElse(null);
        if (serie == null) {
            return "redirect:/configuracion/series";
        }
        
        List<TipoDocVenta> tiposDocumento = serieDocService.findAllTiposDocumento();
        
        model.addAttribute("serie", serie);
        model.addAttribute("tiposDocumento", tiposDocumento);
        
        return "configuracion/serie-form";
    }
    
    @PostMapping("/series/editar")
    public String actualizarSerie(@ModelAttribute SerieDocVentaCompra serie,
                                   @RequestParam(required = false) Long tipoDocVentaId,
                                   RedirectAttributes redirectAttributes) {
        try {
            // Cargar la serie existente
            SerieDocVentaCompra serieExistente = serieDocService.findById(serie.getIdDocVentaCompra()).orElse(null);
            if (serieExistente == null) {
                redirectAttributes.addFlashAttribute("error", "Serie no encontrada");
                return "redirect:/configuracion/series";
            }
            
            // Actualizar campos
            serieExistente.setSerie(serie.getSerie());
            serieExistente.setNumerador(serie.getNumerador());
            
            // Asignar el tipo de documento
            if (tipoDocVentaId != null) {
                TipoDocVenta tipoDoc = serieDocService.findTipoDocumentoById(tipoDocVentaId).orElse(null);
                serieExistente.setTipoDocVenta(tipoDoc);
            }
            
            serieDocService.save(serieExistente);
            redirectAttributes.addFlashAttribute("mensaje", "Serie actualizada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
        }
        return "redirect:/configuracion/series";
    }
    
    @GetMapping("/series/eliminar/{id}")
    public String eliminarSerie(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            serieDocService.deleteById(id);
            redirectAttributes.addFlashAttribute("mensaje", "Serie eliminada exitosamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/configuracion/series";
    }
}
