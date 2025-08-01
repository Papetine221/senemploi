package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Recruteur;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Recruteur entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RecruteurRepository extends JpaRepository<Recruteur, Long>, JpaSpecificationExecutor<Recruteur> {}
