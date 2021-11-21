package com.cev.ejer1.repository;

import com.cev.ejer1.domain.Experiencia;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * El repositorio de Spring data para la Entidad de Experiencia.
 */
@SuppressWarnings("unused")
@Repository
public interface ExperienciaRepository extends JpaRepository<Experiencia, Long>, JpaSpecificationExecutor<Experiencia> {}
