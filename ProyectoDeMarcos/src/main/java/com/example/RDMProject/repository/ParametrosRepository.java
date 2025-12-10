package com.example.RDMProject.repository;

import com.example.RDMProject.model.Parametros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParametrosRepository extends JpaRepository<Parametros, Long> {
}
