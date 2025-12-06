package com.example.RDMProject.service;

import com.example.RDMProject.model.ClienteProveedor;
import com.example.RDMProject.repository.ClienteProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClienteProveedorService {

    @Autowired
    private ClienteProveedorRepository repository;

    public List<ClienteProveedor> findAll() {
        return repository.findAll();
    }

    public ClienteProveedor findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public ClienteProveedor save(ClienteProveedor clienteProveedor) {
        return repository.save(clienteProveedor);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public List<ClienteProveedor> findClientes() {
        return repository.findByTipo("1");
    }

    public List<ClienteProveedor> findProveedores() {
        return repository.findByTipo("2");
    }

    public long count() {
        return repository.count();
    }
}
