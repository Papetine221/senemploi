package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Candidat;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Candidat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CandidatRepository extends JpaRepository<Candidat, Long>, JpaSpecificationExecutor<Candidat> {}
