package com.example.RDMProject.service;

import com.example.RDMProject.model.Usuario;
import com.example.RDMProject.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository repo;

    public CustomUserDetailsService(UsuarioRepository repo){
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username)throws UsernameNotFoundException{
        Usuario user =repo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return  new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getContrasena(),
                List.of(new SimpleGrantedAuthority(user.getRol()))
        );

    }
}
