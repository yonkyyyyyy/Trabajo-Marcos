package com.example.RDMProject.service;

import com.example.RDMProject.model.Usuario;
import com.example.RDMProject.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    /**
     * Guarda un usuario encriptando su contraseña si es nueva o ha cambiado
     */
    public void save(Usuario usuario) {
        // Si el usuario es nuevo (no tiene ID) o la contraseña ha cambiado
        if (usuario.getIdUsuario() == null) {
            // Usuario nuevo - encriptar contraseña
            usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        } else {
            // Usuario existente - verificar si cambió la contraseña
            Usuario usuarioExistente = usuarioRepository.findById(usuario.getIdUsuario()).orElse(null);
            if (usuarioExistente != null) {
                // Si la contraseña es diferente a la encriptada, encriptarla
                if (!passwordEncoder.matches(usuario.getContrasena(), usuarioExistente.getContrasena())) {
                    // Solo encriptar si no está ya encriptada
                    if (!usuario.getContrasena().startsWith("$2a$")) {
                        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
                    }
                } else {
                    // Mantener la contraseña actual
                    usuario.setContrasena(usuarioExistente.getContrasena());
                }
            }
        }

        // Asegurarse que el rol tenga el prefijo ROLE_
        if (usuario.getRol() != null && !usuario.getRol().startsWith("ROLE_")) {
            usuario.setRol("ROLE_" + usuario.getRol());
        }

        usuarioRepository.save(usuario);
    }

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Valida si un username ya existe
     */
    public boolean existsByUsername(String username) {
        return usuarioRepository.findByUsername(username).isPresent();
    }

    /**
     * Valida si un email ya existe
     */
    public boolean existsByEmail(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username).orElse(null);
    }

}