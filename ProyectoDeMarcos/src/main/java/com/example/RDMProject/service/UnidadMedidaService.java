package com.example.RDMProject.service;

import com.example.RDMProject.model.UnidadMedida;
import com.example.RDMProject.repository.UnidadMedidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnidadMedidaService {

    @Autowired
    private UnidadMedidaRepository unidadMedidaRepository;

    public List<UnidadMedida> findAll() {
        return unidadMedidaRepository.findAll();
    }

    public void save(UnidadMedida unidadMedida) {
        unidadMedidaRepository.save(unidadMedida);
    }

    public UnidadMedida findById(Long id) {
        return unidadMedidaRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        unidadMedidaRepository.deleteById(id);
    }
}