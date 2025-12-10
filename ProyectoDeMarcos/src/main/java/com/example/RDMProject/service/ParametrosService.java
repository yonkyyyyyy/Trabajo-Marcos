package com.example.RDMProject.service;

import com.example.RDMProject.model.Parametros;
import com.example.RDMProject.repository.ParametrosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ParametrosService {

    @Autowired
    private ParametrosRepository parametrosRepository;

    /**
     * Obtiene los parámetros de la empresa (siempre hay solo uno)
     */
    public Parametros getParametros() {
        return parametrosRepository.findAll().stream()
                .findFirst()
                .orElse(crearParametrosPorDefecto());
    }

    /**
     * Guarda o actualiza los parámetros
     */
    @Transactional
    public Parametros save(Parametros parametros) {
        return parametrosRepository.save(parametros);
    }

    /**
     * Crea parámetros por defecto si no existen
     */
    private Parametros crearParametrosPorDefecto() {
        Parametros parametros = new Parametros();
        parametros.setNombreEmpresa("Mi Empresa");
        parametros.setMoneda("S/.");
        parametros.setIgv(18.0);
        parametros.setTipoCambio(3.70);
        parametros.setPais("Perú");
        return parametrosRepository.save(parametros);
    }
}
