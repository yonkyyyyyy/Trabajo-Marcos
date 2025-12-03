package com.example.RDMProject.service;

import com.example.RDMProject.model.ClienteProveedor;
import com.example.RDMProject.repository.ClienteProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClienteProveedorService {

    @Autowired
    private ClienteProveedorRepository repository;

    public List<ClienteProveedor> findAll() {
        return repository.findAll();
    }

    public void save(ClienteProveedor cliente) {
        repository.save(cliente);
    }

    public ClienteProveedor findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}