package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Competence;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Competence entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CompetenceRepository extends JpaRepository<Competence, Long>, JpaSpecificationExecutor<Competence> {}
