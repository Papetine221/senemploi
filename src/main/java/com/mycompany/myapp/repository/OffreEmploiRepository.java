package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.OffreEmploi;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OffreEmploi entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OffreEmploiRepository extends JpaRepository<OffreEmploi, Long>, JpaSpecificationExecutor<OffreEmploi> {}
