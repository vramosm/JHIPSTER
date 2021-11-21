package com.cev.ejer1.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.cev.ejer1.IntegrationTest;
import com.cev.ejer1.domain.Experiencia;
import com.cev.ejer1.repository.ExperienciaRepository;
import com.cev.ejer1.repository.search.ExperienciaSearchRepository;
import com.cev.ejer1.service.criteria.ExperienciaCriteria;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ExperienciaResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ExperienciaResourceIT {

    private static final String DEFAULT_TITULO = "AAAAAAAAAA";
    private static final String UPDATED_TITULO = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPCION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPCION = "BBBBBBBBBB";

    private static final String DEFAULT_LOCALIZACION = "AAAAAAAAAA";
    private static final String UPDATED_LOCALIZACION = "BBBBBBBBBB";

    private static final Instant DEFAULT_FECHA = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FECHA = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/experiencias";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/experiencias";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ExperienciaRepository experienciaRepository;

    /**
     * This repository is mocked in the com.cev.ejer1.repository.search test package.
     *
     * @see com.cev.ejer1.repository.search.ExperienciaSearchRepositoryMockConfiguration
     */
    @Autowired
    private ExperienciaSearchRepository mockExperienciaSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExperienciaMockMvc;

    private Experiencia experiencia;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Experiencia createEntity(EntityManager em) {
        Experiencia experiencia = new Experiencia()
            .titulo(DEFAULT_TITULO)
            .descripcion(DEFAULT_DESCRIPCION)
            .localizacion(DEFAULT_LOCALIZACION)
            .fecha(DEFAULT_FECHA);
        return experiencia;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Experiencia createUpdatedEntity(EntityManager em) {
        Experiencia experiencia = new Experiencia()
            .titulo(UPDATED_TITULO)
            .descripcion(UPDATED_DESCRIPCION)
            .localizacion(UPDATED_LOCALIZACION)
            .fecha(UPDATED_FECHA);
        return experiencia;
    }

    @BeforeEach
    public void initTest() {
        experiencia = createEntity(em);
    }

    @Test
    @Transactional
    void createExperiencia() throws Exception {
        int databaseSizeBeforeCreate = experienciaRepository.findAll().size();
        // Create the Experiencia
        restExperienciaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(experiencia)))
            .andExpect(status().isCreated());

        // Validate the Experiencia in the database
        List<Experiencia> experienciaList = experienciaRepository.findAll();
        assertThat(experienciaList).hasSize(databaseSizeBeforeCreate + 1);
        Experiencia testExperiencia = experienciaList.get(experienciaList.size() - 1);
        assertThat(testExperiencia.getTitulo()).isEqualTo(DEFAULT_TITULO);
        assertThat(testExperiencia.getDescripcion()).isEqualTo(DEFAULT_DESCRIPCION);
        assertThat(testExperiencia.getLocalizacion()).isEqualTo(DEFAULT_LOCALIZACION);
        assertThat(testExperiencia.getFecha()).isEqualTo(DEFAULT_FECHA);

        // Validate the Experiencia in Elasticsearch
        verify(mockExperienciaSearchRepository, times(1)).save(testExperiencia);
    }

    @Test
    @Transactional
    void createExperienciaWithExistingId() throws Exception {
        // Create the Experiencia with an existing ID
        experiencia.setId(1L);

        int databaseSizeBeforeCreate = experienciaRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restExperienciaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(experiencia)))
            .andExpect(status().isBadRequest());

        // Validate the Experiencia in the database
        List<Experiencia> experienciaList = experienciaRepository.findAll();
        assertThat(experienciaList).hasSize(databaseSizeBeforeCreate);

        // Validate the Experiencia in Elasticsearch
        verify(mockExperienciaSearchRepository, times(0)).save(experiencia);
    }

    @Test
    @Transactional
    void checkTituloIsRequired() throws Exception {
        int databaseSizeBeforeTest = experienciaRepository.findAll().size();
        // set the field null
        experiencia.setTitulo(null);

        // Create the Experiencia, which fails.

        restExperienciaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(experiencia)))
            .andExpect(status().isBadRequest());

        List<Experiencia> experienciaList = experienciaRepository.findAll();
        assertThat(experienciaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFechaIsRequired() throws Exception {
        int databaseSizeBeforeTest = experienciaRepository.findAll().size();
        // set the field null
        experiencia.setFecha(null);

        // Create the Experiencia, which fails.

        restExperienciaMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(experiencia)))
            .andExpect(status().isBadRequest());

        List<Experiencia> experienciaList = experienciaRepository.findAll();
        assertThat(experienciaList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllExperiencias() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        // Get all the experienciaList
        restExperienciaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(experiencia.getId().intValue())))
            .andExpect(jsonPath("$.[*].titulo").value(hasItem(DEFAULT_TITULO)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].localizacion").value(hasItem(DEFAULT_LOCALIZACION)))
            .andExpect(jsonPath("$.[*].fecha").value(hasItem(DEFAULT_FECHA.toString())));
    }

    @Test
    @Transactional
    void getExperiencia() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        // Get the experiencia
        restExperienciaMockMvc
            .perform(get(ENTITY_API_URL_ID, experiencia.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(experiencia.getId().intValue()))
            .andExpect(jsonPath("$.titulo").value(DEFAULT_TITULO))
            .andExpect(jsonPath("$.descripcion").value(DEFAULT_DESCRIPCION))
            .andExpect(jsonPath("$.localizacion").value(DEFAULT_LOCALIZACION))
            .andExpect(jsonPath("$.fecha").value(DEFAULT_FECHA.toString()));
    }

    @Test
    @Transactional
    void getExperienciasByIdFiltering() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        Long id = experiencia.getId();

        defaultExperienciaShouldBeFound("id.equals=" + id);
        defaultExperienciaShouldNotBeFound("id.notEquals=" + id);

        defaultExperienciaShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultExperienciaShouldNotBeFound("id.greaterThan=" + id);

        defaultExperienciaShouldBeFound("id.lessThanOrEqual=" + id);
        defaultExperienciaShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllExperienciasByTituloIsEqualToSomething() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        // Get all the experienciaList where titulo equals to DEFAULT_TITULO
        defaultExperienciaShouldBeFound("titulo.equals=" + DEFAULT_TITULO);

        // Get all the experienciaList where titulo equals to UPDATED_TITULO
        defaultExperienciaShouldNotBeFound("titulo.equals=" + UPDATED_TITULO);
    }

    @Test
    @Transactional
    void getAllExperienciasByTituloIsNotEqualToSomething() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        // Get all the experienciaList where titulo not equals to DEFAULT_TITULO
        defaultExperienciaShouldNotBeFound("titulo.notEquals=" + DEFAULT_TITULO);

        // Get all the experienciaList where titulo not equals to UPDATED_TITULO
        defaultExperienciaShouldBeFound("titulo.notEquals=" + UPDATED_TITULO);
    }

    @Test
    @Transactional
    void getAllExperienciasByTituloIsInShouldWork() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        // Get all the experienciaList where titulo in DEFAULT_TITULO or UPDATED_TITULO
        defaultExperienciaShouldBeFound("titulo.in=" + DEFAULT_TITULO + "," + UPDATED_TITULO);

        // Get all the experienciaList where titulo equals to UPDATED_TITULO
        defaultExperienciaShouldNotBeFound("titulo.in=" + UPDATED_TITULO);
    }

    @Test
    @Transactional
    void getAllExperienciasByTituloIsNullOrNotNull() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        // Get all the experienciaList where titulo is not null
        defaultExperienciaShouldBeFound("titulo.specified=true");

        // Get all the experienciaList where titulo is null
        defaultExperienciaShouldNotBeFound("titulo.specified=false");
    }

    @Test
    @Transactional
    void getAllExperienciasByTituloContainsSomething() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        // Get all the experienciaList where titulo contains DEFAULT_TITULO
        defaultExperienciaShouldBeFound("titulo.contains=" + DEFAULT_TITULO);

        // Get all the experienciaList where titulo contains UPDATED_TITULO
        defaultExperienciaShouldNotBeFound("titulo.contains=" + UPDATED_TITULO);
    }

    @Test
    @Transactional
    void getAllExperienciasByTituloNotContainsSomething() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        // Get all the experienciaList where titulo does not contain DEFAULT_TITULO
        defaultExperienciaShouldNotBeFound("titulo.doesNotContain=" + DEFAULT_TITULO);

        // Get all the experienciaList where titulo does not contain UPDATED_TITULO
        defaultExperienciaShouldBeFound("titulo.doesNotContain=" + UPDATED_TITULO);
    }

    @Test
    @Transactional
    void getAllExperienciasByDescripcionIsEqualToSomething() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        // Get all the experienciaList where descripcion equals to DEFAULT_DESCRIPCION
        defaultExperienciaShouldBeFound("descripcion.equals=" + DEFAULT_DESCRIPCION);

        // Get all the experienciaList where descripcion equals to UPDATED_DESCRIPCION
        defaultExperienciaShouldNotBeFound("descripcion.equals=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllExperienciasByDescripcionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        // Get all the experienciaList where descripcion not equals to DEFAULT_DESCRIPCION
        defaultExperienciaShouldNotBeFound("descripcion.notEquals=" + DEFAULT_DESCRIPCION);

        // Get all the experienciaList where descripcion not equals to UPDATED_DESCRIPCION
        defaultExperienciaShouldBeFound("descripcion.notEquals=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllExperienciasByDescripcionIsInShouldWork() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        // Get all the experienciaList where descripcion in DEFAULT_DESCRIPCION or UPDATED_DESCRIPCION
        defaultExperienciaShouldBeFound("descripcion.in=" + DEFAULT_DESCRIPCION + "," + UPDATED_DESCRIPCION);

        // Get all the experienciaList where descripcion equals to UPDATED_DESCRIPCION
        defaultExperienciaShouldNotBeFound("descripcion.in=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllExperienciasByDescripcionIsNullOrNotNull() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        // Get all the experienciaList where descripcion is not null
        defaultExperienciaShouldBeFound("descripcion.specified=true");

        // Get all the experienciaList where descripcion is null
        defaultExperienciaShouldNotBeFound("descripcion.specified=false");
    }

    @Test
    @Transactional
    void getAllExperienciasByDescripcionContainsSomething() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        // Get all the experienciaList where descripcion contains DEFAULT_DESCRIPCION
        defaultExperienciaShouldBeFound("descripcion.contains=" + DEFAULT_DESCRIPCION);

        // Get all the experienciaList where descripcion contains UPDATED_DESCRIPCION
        defaultExperienciaShouldNotBeFound("descripcion.contains=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllExperienciasByDescripcionNotContainsSomething() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        // Get all the experienciaList where descripcion does not contain DEFAULT_DESCRIPCION
        defaultExperienciaShouldNotBeFound("descripcion.doesNotContain=" + DEFAULT_DESCRIPCION);

        // Get all the experienciaList where descripcion does not contain UPDATED_DESCRIPCION
        defaultExperienciaShouldBeFound("descripcion.doesNotContain=" + UPDATED_DESCRIPCION);
    }

    @Test
    @Transactional
    void getAllExperienciasByLocalizacionIsEqualToSomething() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        // Get all the experienciaList where localizacion equals to DEFAULT_LOCALIZACION
        defaultExperienciaShouldBeFound("localizacion.equals=" + DEFAULT_LOCALIZACION);

        // Get all the experienciaList where localizacion equals to UPDATED_LOCALIZACION
        defaultExperienciaShouldNotBeFound("localizacion.equals=" + UPDATED_LOCALIZACION);
    }

    @Test
    @Transactional
    void getAllExperienciasByLocalizacionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        // Get all the experienciaList where localizacion not equals to DEFAULT_LOCALIZACION
        defaultExperienciaShouldNotBeFound("localizacion.notEquals=" + DEFAULT_LOCALIZACION);

        // Get all the experienciaList where localizacion not equals to UPDATED_LOCALIZACION
        defaultExperienciaShouldBeFound("localizacion.notEquals=" + UPDATED_LOCALIZACION);
    }

    @Test
    @Transactional
    void getAllExperienciasByLocalizacionIsInShouldWork() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        // Get all the experienciaList where localizacion in DEFAULT_LOCALIZACION or UPDATED_LOCALIZACION
        defaultExperienciaShouldBeFound("localizacion.in=" + DEFAULT_LOCALIZACION + "," + UPDATED_LOCALIZACION);

        // Get all the experienciaList where localizacion equals to UPDATED_LOCALIZACION
        defaultExperienciaShouldNotBeFound("localizacion.in=" + UPDATED_LOCALIZACION);
    }

    @Test
    @Transactional
    void getAllExperienciasByLocalizacionIsNullOrNotNull() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        // Get all the experienciaList where localizacion is not null
        defaultExperienciaShouldBeFound("localizacion.specified=true");

        // Get all the experienciaList where localizacion is null
        defaultExperienciaShouldNotBeFound("localizacion.specified=false");
    }

    @Test
    @Transactional
    void getAllExperienciasByLocalizacionContainsSomething() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        // Get all the experienciaList where localizacion contains DEFAULT_LOCALIZACION
        defaultExperienciaShouldBeFound("localizacion.contains=" + DEFAULT_LOCALIZACION);

        // Get all the experienciaList where localizacion contains UPDATED_LOCALIZACION
        defaultExperienciaShouldNotBeFound("localizacion.contains=" + UPDATED_LOCALIZACION);
    }

    @Test
    @Transactional
    void getAllExperienciasByLocalizacionNotContainsSomething() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        // Get all the experienciaList where localizacion does not contain DEFAULT_LOCALIZACION
        defaultExperienciaShouldNotBeFound("localizacion.doesNotContain=" + DEFAULT_LOCALIZACION);

        // Get all the experienciaList where localizacion does not contain UPDATED_LOCALIZACION
        defaultExperienciaShouldBeFound("localizacion.doesNotContain=" + UPDATED_LOCALIZACION);
    }

    @Test
    @Transactional
    void getAllExperienciasByFechaIsEqualToSomething() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        // Get all the experienciaList where fecha equals to DEFAULT_FECHA
        defaultExperienciaShouldBeFound("fecha.equals=" + DEFAULT_FECHA);

        // Get all the experienciaList where fecha equals to UPDATED_FECHA
        defaultExperienciaShouldNotBeFound("fecha.equals=" + UPDATED_FECHA);
    }

    @Test
    @Transactional
    void getAllExperienciasByFechaIsNotEqualToSomething() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        // Get all the experienciaList where fecha not equals to DEFAULT_FECHA
        defaultExperienciaShouldNotBeFound("fecha.notEquals=" + DEFAULT_FECHA);

        // Get all the experienciaList where fecha not equals to UPDATED_FECHA
        defaultExperienciaShouldBeFound("fecha.notEquals=" + UPDATED_FECHA);
    }

    @Test
    @Transactional
    void getAllExperienciasByFechaIsInShouldWork() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        // Get all the experienciaList where fecha in DEFAULT_FECHA or UPDATED_FECHA
        defaultExperienciaShouldBeFound("fecha.in=" + DEFAULT_FECHA + "," + UPDATED_FECHA);

        // Get all the experienciaList where fecha equals to UPDATED_FECHA
        defaultExperienciaShouldNotBeFound("fecha.in=" + UPDATED_FECHA);
    }

    @Test
    @Transactional
    void getAllExperienciasByFechaIsNullOrNotNull() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        // Get all the experienciaList where fecha is not null
        defaultExperienciaShouldBeFound("fecha.specified=true");

        // Get all the experienciaList where fecha is null
        defaultExperienciaShouldNotBeFound("fecha.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultExperienciaShouldBeFound(String filter) throws Exception {
        restExperienciaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(experiencia.getId().intValue())))
            .andExpect(jsonPath("$.[*].titulo").value(hasItem(DEFAULT_TITULO)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].localizacion").value(hasItem(DEFAULT_LOCALIZACION)))
            .andExpect(jsonPath("$.[*].fecha").value(hasItem(DEFAULT_FECHA.toString())));

        // Check, that the count call also returns 1
        restExperienciaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultExperienciaShouldNotBeFound(String filter) throws Exception {
        restExperienciaMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restExperienciaMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingExperiencia() throws Exception {
        // Get the experiencia
        restExperienciaMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewExperiencia() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        int databaseSizeBeforeUpdate = experienciaRepository.findAll().size();

        // Update the experiencia
        Experiencia updatedExperiencia = experienciaRepository.findById(experiencia.getId()).get();
        // Disconnect from session so that the updates on updatedExperiencia are not directly saved in db
        em.detach(updatedExperiencia);
        updatedExperiencia.titulo(UPDATED_TITULO).descripcion(UPDATED_DESCRIPCION).localizacion(UPDATED_LOCALIZACION).fecha(UPDATED_FECHA);

        restExperienciaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedExperiencia.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedExperiencia))
            )
            .andExpect(status().isOk());

        // Validate the Experiencia in the database
        List<Experiencia> experienciaList = experienciaRepository.findAll();
        assertThat(experienciaList).hasSize(databaseSizeBeforeUpdate);
        Experiencia testExperiencia = experienciaList.get(experienciaList.size() - 1);
        assertThat(testExperiencia.getTitulo()).isEqualTo(UPDATED_TITULO);
        assertThat(testExperiencia.getDescripcion()).isEqualTo(UPDATED_DESCRIPCION);
        assertThat(testExperiencia.getLocalizacion()).isEqualTo(UPDATED_LOCALIZACION);
        assertThat(testExperiencia.getFecha()).isEqualTo(UPDATED_FECHA);

        // Validate the Experiencia in Elasticsearch
        verify(mockExperienciaSearchRepository).save(testExperiencia);
    }

    @Test
    @Transactional
    void putNonExistingExperiencia() throws Exception {
        int databaseSizeBeforeUpdate = experienciaRepository.findAll().size();
        experiencia.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExperienciaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, experiencia.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(experiencia))
            )
            .andExpect(status().isBadRequest());

        // Validate the Experiencia in the database
        List<Experiencia> experienciaList = experienciaRepository.findAll();
        assertThat(experienciaList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Experiencia in Elasticsearch
        verify(mockExperienciaSearchRepository, times(0)).save(experiencia);
    }

    @Test
    @Transactional
    void putWithIdMismatchExperiencia() throws Exception {
        int databaseSizeBeforeUpdate = experienciaRepository.findAll().size();
        experiencia.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExperienciaMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(experiencia))
            )
            .andExpect(status().isBadRequest());

        // Validate the Experiencia in the database
        List<Experiencia> experienciaList = experienciaRepository.findAll();
        assertThat(experienciaList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Experiencia in Elasticsearch
        verify(mockExperienciaSearchRepository, times(0)).save(experiencia);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExperiencia() throws Exception {
        int databaseSizeBeforeUpdate = experienciaRepository.findAll().size();
        experiencia.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExperienciaMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(experiencia)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Experiencia in the database
        List<Experiencia> experienciaList = experienciaRepository.findAll();
        assertThat(experienciaList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Experiencia in Elasticsearch
        verify(mockExperienciaSearchRepository, times(0)).save(experiencia);
    }

    @Test
    @Transactional
    void partialUpdateExperienciaWithPatch() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        int databaseSizeBeforeUpdate = experienciaRepository.findAll().size();

        // Update the experiencia using partial update
        Experiencia partialUpdatedExperiencia = new Experiencia();
        partialUpdatedExperiencia.setId(experiencia.getId());

        partialUpdatedExperiencia.localizacion(UPDATED_LOCALIZACION).fecha(UPDATED_FECHA);

        restExperienciaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExperiencia.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExperiencia))
            )
            .andExpect(status().isOk());

        // Validate the Experiencia in the database
        List<Experiencia> experienciaList = experienciaRepository.findAll();
        assertThat(experienciaList).hasSize(databaseSizeBeforeUpdate);
        Experiencia testExperiencia = experienciaList.get(experienciaList.size() - 1);
        assertThat(testExperiencia.getTitulo()).isEqualTo(DEFAULT_TITULO);
        assertThat(testExperiencia.getDescripcion()).isEqualTo(DEFAULT_DESCRIPCION);
        assertThat(testExperiencia.getLocalizacion()).isEqualTo(UPDATED_LOCALIZACION);
        assertThat(testExperiencia.getFecha()).isEqualTo(UPDATED_FECHA);
    }

    @Test
    @Transactional
    void fullUpdateExperienciaWithPatch() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        int databaseSizeBeforeUpdate = experienciaRepository.findAll().size();

        // Update the experiencia using partial update
        Experiencia partialUpdatedExperiencia = new Experiencia();
        partialUpdatedExperiencia.setId(experiencia.getId());

        partialUpdatedExperiencia
            .titulo(UPDATED_TITULO)
            .descripcion(UPDATED_DESCRIPCION)
            .localizacion(UPDATED_LOCALIZACION)
            .fecha(UPDATED_FECHA);

        restExperienciaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExperiencia.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExperiencia))
            )
            .andExpect(status().isOk());

        // Validate the Experiencia in the database
        List<Experiencia> experienciaList = experienciaRepository.findAll();
        assertThat(experienciaList).hasSize(databaseSizeBeforeUpdate);
        Experiencia testExperiencia = experienciaList.get(experienciaList.size() - 1);
        assertThat(testExperiencia.getTitulo()).isEqualTo(UPDATED_TITULO);
        assertThat(testExperiencia.getDescripcion()).isEqualTo(UPDATED_DESCRIPCION);
        assertThat(testExperiencia.getLocalizacion()).isEqualTo(UPDATED_LOCALIZACION);
        assertThat(testExperiencia.getFecha()).isEqualTo(UPDATED_FECHA);
    }

    @Test
    @Transactional
    void patchNonExistingExperiencia() throws Exception {
        int databaseSizeBeforeUpdate = experienciaRepository.findAll().size();
        experiencia.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExperienciaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, experiencia.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(experiencia))
            )
            .andExpect(status().isBadRequest());

        // Validate the Experiencia in the database
        List<Experiencia> experienciaList = experienciaRepository.findAll();
        assertThat(experienciaList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Experiencia in Elasticsearch
        verify(mockExperienciaSearchRepository, times(0)).save(experiencia);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExperiencia() throws Exception {
        int databaseSizeBeforeUpdate = experienciaRepository.findAll().size();
        experiencia.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExperienciaMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(experiencia))
            )
            .andExpect(status().isBadRequest());

        // Validate the Experiencia in the database
        List<Experiencia> experienciaList = experienciaRepository.findAll();
        assertThat(experienciaList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Experiencia in Elasticsearch
        verify(mockExperienciaSearchRepository, times(0)).save(experiencia);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExperiencia() throws Exception {
        int databaseSizeBeforeUpdate = experienciaRepository.findAll().size();
        experiencia.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExperienciaMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(experiencia))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Experiencia in the database
        List<Experiencia> experienciaList = experienciaRepository.findAll();
        assertThat(experienciaList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Experiencia in Elasticsearch
        verify(mockExperienciaSearchRepository, times(0)).save(experiencia);
    }

    @Test
    @Transactional
    void deleteExperiencia() throws Exception {
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);

        int databaseSizeBeforeDelete = experienciaRepository.findAll().size();

        // Delete the experiencia
        restExperienciaMockMvc
            .perform(delete(ENTITY_API_URL_ID, experiencia.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Experiencia> experienciaList = experienciaRepository.findAll();
        assertThat(experienciaList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Experiencia in Elasticsearch
        verify(mockExperienciaSearchRepository, times(1)).deleteById(experiencia.getId());
    }

    @Test
    @Transactional
    void searchExperiencia() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        experienciaRepository.saveAndFlush(experiencia);
        when(mockExperienciaSearchRepository.search("id:" + experiencia.getId(), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(experiencia), PageRequest.of(0, 1), 1));

        // Search the experiencia
        restExperienciaMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + experiencia.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(experiencia.getId().intValue())))
            .andExpect(jsonPath("$.[*].titulo").value(hasItem(DEFAULT_TITULO)))
            .andExpect(jsonPath("$.[*].descripcion").value(hasItem(DEFAULT_DESCRIPCION)))
            .andExpect(jsonPath("$.[*].localizacion").value(hasItem(DEFAULT_LOCALIZACION)))
            .andExpect(jsonPath("$.[*].fecha").value(hasItem(DEFAULT_FECHA.toString())));
    }
}
