package com.example.RDMProject.service.impl;

import com.example.RDMProject.model.Categoria;
import com.example.RDMProject.repository.CategoriaRepository;
import com.example.RDMProject.service.CategoriaService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@Transactional
public class CategoriaServiceImpl implements CategoriaService {
    private final CategoriaRepository categoriaRepository;
    public CategoriaServiceImpl(CategoriaRepository categoriaRepository){
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Categoria> findAll(){
        return categoriaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Categoria>findById(Long id){
        return categoriaRepository.findById(id);
    }

    @Override
    @Transactional
    public Categoria save(Categoria categoria){
        return categoriaRepository.save(categoria);
    }

    @Override
    @Transactional
    public void deleteById(Long id){
        if(categoriaRepository.existsById(id)){
            categoriaRepository.deleteById(id);
        }else {
            throw new RuntimeException("Categoria no encontrada con el id : " +id);
        }
    }
}
