package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.TypeContrat;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TypeContrat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TypeContratRepository extends JpaRepository<TypeContrat, Long>, JpaSpecificationExecutor<TypeContrat> {}
