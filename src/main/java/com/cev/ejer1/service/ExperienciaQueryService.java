package com.cev.ejer1.service;

import com.cev.ejer1.domain.*; // for static metamodels
import com.cev.ejer1.domain.Experiencia;
import com.cev.ejer1.repository.ExperienciaRepository;
import com.cev.ejer1.repository.search.ExperienciaSearchRepository;
import com.cev.ejer1.service.criteria.ExperienciaCriteria;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Servicio para ejecutar consultas complejas para entidades en la base de datos.
 *
 * Devuelve una lista de experiencias  que cumple los criterios.
 */
@Service
@Transactional(readOnly = true)
public class ExperienciaQueryService extends QueryService<Experiencia> {

    private final Logger log = LoggerFactory.getLogger(ExperienciaQueryService.class);

    private final ExperienciaRepository experienciaRepository;

    private final ExperienciaSearchRepository experienciaSearchRepository;

    public ExperienciaQueryService(ExperienciaRepository experienciaRepository, ExperienciaSearchRepository experienciaSearchRepository) {
        this.experienciaRepository = experienciaRepository;
        this.experienciaSearchRepository = experienciaSearchRepository;
    }

    /**
     * Devuelve una lista de Experiencias que coincide con los criterios de la base de datos.
     * return las entidades que coinciden.
     */
    @Transactional(readOnly = true)
    public List<Experiencia> findByCriteria(ExperienciaCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Experiencia> specification = createSpecification(criteria);
        return experienciaRepository.findAll(specification);
    }

    /**
     * Devuelve una lista de Experiencias que coincide con los criterios de la base de datos.
     * return las entidades que coinciden.
     */
    @Transactional(readOnly = true)
    public Page<Experiencia> findByCriteria(ExperienciaCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Experiencia> specification = createSpecification(criteria);
        return experienciaRepository.findAll(specification, page);
    }

    /**
     * Devuelve el número de entidades coincidentes en la base de datos.
     * return el número de entidades coincidentes.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ExperienciaCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Experiencia> specification = createSpecification(criteria);
        return experienciaRepository.count(specification);
    }

    /**
     * Función para convertir {@link ExperienciaCriterios} en un {@link Especificación}
     * return la Especificación que coincide con la entidad.
     */
    protected Specification<Experiencia> createSpecification(ExperienciaCriteria criteria) {
        Specification<Experiencia> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Experiencia_.id));
            }
            if (criteria.getTitulo() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitulo(), Experiencia_.titulo));
            }
            if (criteria.getDescripcion() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescripcion(), Experiencia_.descripcion));
            }
            if (criteria.getLocalizacion() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLocalizacion(), Experiencia_.localizacion));
            }
            if (criteria.getFecha() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getFecha(), Experiencia_.fecha));
            }
        }
        return specification;
    }
}
