package com.example.RDMProject.repository;

import com.example.RDMProject.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // CAMBIO AQUÍ: Buscar por username
    Usuario findByUsername(String username);
}