package com.example.RDMProject.controller;

import com.example.RDMProject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/configuracion")
@PreAuthorize("hasRole('ADMIN')")
public class ConfiguracionController {
/*
    @Autowired(required = false)
    private ParametrosService parametrosService;
    
    @Autowired(required = false)
    private SerieDocVentaCompraService serieDocService;
    
    @Autowired(required = false)
    private TipoDocVentaService tipoDocVentaService;
    */
    @Autowired(required = false)
    private UnidadMedidaService unidadMedidaService;
    
    @Autowired(required = false)
    private CategoriaService categoriaService;

    @GetMapping
    public String dashboard(Model model) {
        /*
        long totalParametros = (parametrosService != null) ? parametrosService.count() : 0;
        long totalSeries = (serieDocService != null) ? serieDocService.count() : 0;
        long totalTiposDoc = (tipoDocVentaService != null) ? tipoDocVentaService.count() : 0;
        */
        long totalUnidades =20; //(unidadMedidaService != null) ? unidadMedidaService.count() : 0;
        long totalCategorias = 25;//(categoriaService != null) ? categoriaService.count() : 0;
        /*
        model.addAttribute("totalParametros", totalParametros);
        model.addAttribute("totalSeries", totalSeries);
        model.addAttribute("totalTiposDoc", totalTiposDoc);
        */

        model.addAttribute("totalUnidades", totalUnidades);
        model.addAttribute("totalCategorias", totalCategorias);
        
        return "configuracion/dashboard";
    }
}
