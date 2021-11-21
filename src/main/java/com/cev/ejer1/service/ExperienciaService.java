package com.cev.ejer1.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.cev.ejer1.domain.Experiencia;
import com.cev.ejer1.repository.ExperienciaRepository;
import com.cev.ejer1.repository.search.ExperienciaSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Experiencia}.
 */
@Service
@Transactional
public class ExperienciaService {

    private final Logger log = LoggerFactory.getLogger(ExperienciaService.class);

    private final ExperienciaRepository experienciaRepository;

    private final ExperienciaSearchRepository experienciaSearchRepository;

    public ExperienciaService(ExperienciaRepository experienciaRepository, ExperienciaSearchRepository experienciaSearchRepository) {
        this.experienciaRepository = experienciaRepository;
        this.experienciaSearchRepository = experienciaSearchRepository;
    }

    /**
     * Guarda experiencia.
     *
     * @return la experiencia guardada.
     */
    public Experiencia save(Experiencia experiencia) {
        log.debug("Request to save Experiencia : {}", experiencia);
        Experiencia result = experienciaRepository.save(experiencia);
        experienciaSearchRepository.save(result);
        return result;
    }

    /**
     * Actualizar parcialmente una experiencia.
     *
     * return la entidad actualizada.
     */
    public Optional<Experiencia> partialUpdate(Experiencia experiencia) {
        log.debug("Request to partially update Experiencia : {}", experiencia);

        return experienciaRepository
            .findById(experiencia.getId())
            .map(existingExperiencia -> {
                if (experiencia.getTitulo() != null) {
                    existingExperiencia.setTitulo(experiencia.getTitulo());
                }
                if (experiencia.getDescripcion() != null) {
                    existingExperiencia.setDescripcion(experiencia.getDescripcion());
                }
                if (experiencia.getLocalizacion() != null) {
                    existingExperiencia.setLocalizacion(experiencia.getLocalizacion());
                }
                if (experiencia.getFecha() != null) {
                    existingExperiencia.setFecha(experiencia.getFecha());
                }

                return existingExperiencia;
            })
            .map(experienciaRepository::save)
            .map(savedExperiencia -> {
                experienciaSearchRepository.save(savedExperiencia);

                return savedExperiencia;
            });
    }

    /**
     * Consigue todas las experiencias.
     *
     * @return la lista de entidades.
     */
    @Transactional(readOnly = true)
    public Page<Experiencia> findAll(Pageable pageable) {
        log.debug("Request to get all Experiencias");
        return experienciaRepository.findAll(pageable);
    }

    /**
     * Consigue una experiencia por id.
     *
     * @return la entidad.
     */
    @Transactional(readOnly = true)
    public Optional<Experiencia> findOne(Long id) {
        log.debug("Request to get Experiencia : {}", id);
        return experienciaRepository.findById(id);
    }

    /**
     * Borra una experiencia por id.
     *
     */
    public void delete(Long id) {
        log.debug("Request to delete Experiencia : {}", id);
        experienciaRepository.deleteById(id);
        experienciaSearchRepository.deleteById(id);
    }

    /**
     * Busca la experiencia correspondiente a la consulta.
     *
     * @return la lista de entidades.
     */
    @Transactional(readOnly = true)
    public Page<Experiencia> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Experiencias for query {}", query);
        return experienciaSearchRepository.search(query, pageable);
    }
}
