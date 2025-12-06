package com.example.RDMProject.repository;

import com.example.RDMProject.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su username
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Busca un usuario por su email
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica si existe un usuario con ese username
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un usuario con ese email
     */
    boolean existsByEmail(String email);
}