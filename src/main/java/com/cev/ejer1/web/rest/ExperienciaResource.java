package com.cev.ejer1.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.cev.ejer1.domain.Experiencia;
import com.cev.ejer1.repository.ExperienciaRepository;
import com.cev.ejer1.service.ExperienciaQueryService;
import com.cev.ejer1.service.ExperienciaService;
import com.cev.ejer1.service.criteria.ExperienciaCriteria;
import com.cev.ejer1.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller para gestionar {@link com.cev.ejer1.domain.Experiencia}.
 */
@RestController
@RequestMapping("/api")
public class ExperienciaResource {

    private final Logger log = LoggerFactory.getLogger(ExperienciaResource.class);

    private static final String ENTITY_NAME = "experiencia";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExperienciaService experienciaService; // Se vincula el service

    private final ExperienciaRepository experienciaRepository; // se vincula con el repository

    private final ExperienciaQueryService experienciaQueryService; // se vincula con el servicio de hacer consultas

    public ExperienciaResource(
        ExperienciaService experienciaService,
        ExperienciaRepository experienciaRepository,
        ExperienciaQueryService experienciaQueryService
    ) {
        this.experienciaService = experienciaService;
        this.experienciaRepository = experienciaRepository;
        this.experienciaQueryService = experienciaQueryService;
    }

    /**
     * {@code POST  /experiencias} : Crear una nueva experiencia.
     *
     * @return la entidad con el estado {@código 201 (Creado)} y con el cuerpo la nueva experiencia, o con el estado {@código 400 (Solicitud incorrecta)} si la experiencia ya tiene un ID.
     * @throws URISyntaxException si la sintaxis del es incorrecta.
     */
    @PostMapping("/experiencias")
    public ResponseEntity<Experiencia> createExperiencia(@Valid @RequestBody Experiencia experiencia) throws URISyntaxException {
        log.debug("REST request to save Experiencia : {}", experiencia);
        if (experiencia.getId() != null) {
            throw new BadRequestAlertException("A new experiencia cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Experiencia result = experienciaService.save(experiencia);
        return ResponseEntity
            .created(new URI("/api/experiencias/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /experiencias/:id} : Actualizar una experiencia ya creada.
     *
     * @return el link con estado {@código 200 (OK)} y con cuerpo la experiencia actualizada,
     * o con el estado {@code 400 (Bad Request)} si la experiencia no es válida,
     * o con estado {@code 500 (Internal Server Error)} si la experiencia no ha podido ser actualizada.
     * @throws URISyntaxException si la sintaxis del Location URI es incorrecta.
     */
    @PutMapping("/experiencias/{id}")
    public ResponseEntity<Experiencia> updateExperiencia(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Experiencia experiencia
    ) throws URISyntaxException {
        log.debug("REST request to update Experiencia : {}, {}", id, experiencia);
        if (experiencia.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, experiencia.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!experienciaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Experiencia result = experienciaService.save(experiencia);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, experiencia.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /experiencias/:id} : Actualiza parcialmente los campos dados de una experiencia existente, el campo será ignorado si es nulo
     *
     * @param id de la experiencia a guardar.
     * @param la experiencia a actualizar.
     * @returne la {@link ResponseEntity} con estado {@código 200 (OK)} y con cuerpo la experiencia actualizada,
     * o con el estado {@code 400 (Bad Request)} si la experiencia no es válida,
     * o con el estado {@code 404 (Not Found)} si la experiencia no se encuentra,
     * o con el estado {@code 500 (Internal Server Error)} si la experiencia no ha podido ser actualizada.
     * @throws URISyntaxException si la sintaxis del Location URI es incorrecta.
     */
    @PatchMapping(value = "/experiencias/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Experiencia> partialUpdateExperiencia(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Experiencia experiencia
    ) throws URISyntaxException {
        log.debug("REST request to partial update Experiencia partially : {}, {}", id, experiencia);
        if (experiencia.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, experiencia.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!experienciaRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Experiencia> result = experienciaService.partialUpdate(experiencia);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, experiencia.getId().toString())
        );
    }

    /**
     * {@code GET  /experiencias} : consigue todas las experiencias.
     *
     * @param la información de paginación.
     * @param los criterios con los que deben coincidir las entidades solicitadas.
     * @return la {@link ResponseEntity} con el estado {@code 200 (OK)} y la lista de experiencias en el cuerpo.
     */
    @GetMapping("/experiencias")
    public ResponseEntity<List<Experiencia>> getAllExperiencias(ExperienciaCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Experiencias by criteria: {}", criteria);
        Page<Experiencia> page = experienciaQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /experiencias/count} : cuantifica las experiencias.
     *
     * @return la {@link ResponseEntity} con el estado {@code 200 (OK)} y el recuento en el cuerpo.
     */
    @GetMapping("/experiencias/count")
    public ResponseEntity<Long> countExperiencias(ExperienciaCriteria criteria) {
        log.debug("REST request to count Experiencias by criteria: {}", criteria);
        return ResponseEntity.ok().body(experienciaQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /experiencias/:id} : consulta el "id" experiencia.
     *
     * @param id de la experiencia a recuperar.
     * @return la {@link ResponseEntity} con estado {@código 200 (OK)} y con cuerpo la experiencia, o con estado {@código 404 (Not Found)}.
     */
    @GetMapping("/experiencias/{id}")
    public ResponseEntity<Experiencia> getExperiencia(@PathVariable Long id) {
        log.debug("REST request to get Experiencia : {}", id);
        Optional<Experiencia> experiencia = experienciaService.findOne(id);
        return ResponseUtil.wrapOrNotFound(experiencia);
    }

    /**
     * {@code DELETE  /experiencias/:id} : borra "id" experiencia.
     *
     * @param el id de la experiencia .
     * @return el link con el estado  {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/experiencias/{id}")
    public ResponseEntity<Void> deleteExperiencia(@PathVariable Long id) {
        log.debug("REST request to delete Experiencia : {}", id);
        experienciaService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/experiencias?query=:query} : Busca la experiencia correspondiente en la consulta
     *
     * @return el resultado de la búsqueda.
     */
    @GetMapping("/_search/experiencias")
    public ResponseEntity<List<Experiencia>> searchExperiencias(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Experiencias for query {}", query);
        Page<Experiencia> page = experienciaService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
