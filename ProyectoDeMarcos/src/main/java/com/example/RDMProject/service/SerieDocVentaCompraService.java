package com.example.RDMProject.service;

import com.example.RDMProject.model.SerieDocVentaCompra;
import com.example.RDMProject.model.TipoDocVenta;
import com.example.RDMProject.repository.SerieDocVentaCompraRepository;
import com.example.RDMProject.repository.TipoDocVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SerieDocVentaCompraService {

    @Autowired
    private SerieDocVentaCompraRepository serieRepository;
    
    @Autowired
    private TipoDocVentaRepository tipoDocVentaRepository;

    public List<SerieDocVentaCompra> findAll() {
        return serieRepository.findAllWithTipoDocumento();
    }

    public Optional<SerieDocVentaCompra> findById(Long id) {
        return serieRepository.findById(id);
    }

    public List<SerieDocVentaCompra> findByTipoDocumento(Long idTipoDoc) {
        return serieRepository.findByTipoDocVenta_IdVentaDoc(idTipoDoc);
    }

    public Optional<SerieDocVentaCompra> findBySerie(String serie) {
        return serieRepository.findBySerie(serie);
    }

    @Transactional
    public SerieDocVentaCompra save(SerieDocVentaCompra serie) {
        return serieRepository.save(serie);
    }

    @Transactional
    public void deleteById(Long id) {
        serieRepository.deleteById(id);
    }

    /**
     * Incrementa el numerador de una serie y retorna el nuevo valor
     */
    @Transactional
    public Integer incrementarNumerador(Long idDocVentaCompra) {
        SerieDocVentaCompra serie = serieRepository.findById(idDocVentaCompra)
                .orElseThrow(() -> new RuntimeException("Serie no encontrada"));
        
        Integer nuevoNumerador = (serie.getNumerador() != null ? serie.getNumerador() : 0) + 1;
        serie.setNumerador(nuevoNumerador);
        serieRepository.save(serie);
        
        return nuevoNumerador;
    }

    /**
     * Obtiene el siguiente numerador sin incrementar (para preview)
     */
    public Integer getSiguienteNumerador(Long idDocVentaCompra) {
        SerieDocVentaCompra serie = serieRepository.findById(idDocVentaCompra)
                .orElse(null);
        
        if (serie == null) return 1;
        return (serie.getNumerador() != null ? serie.getNumerador() : 0) + 1;
    }
    
    // Métodos para TipoDocVenta
    public List<TipoDocVenta> findAllTiposDocumento() {
        return tipoDocVentaRepository.findAll();
    }
    
    public Optional<TipoDocVenta> findTipoDocumentoById(Long id) {
        return tipoDocVentaRepository.findById(id);
    }
}
