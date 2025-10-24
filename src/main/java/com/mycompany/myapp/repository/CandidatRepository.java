package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Candidat;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Candidat entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CandidatRepository extends JpaRepository<Candidat, Long>, JpaSpecificationExecutor<Candidat> {
    /**
     * Find a candidat by user login.
     *
     * @param login the login of the user.
     * @return the candidat if found.
     */
    Optional<Candidat> findByUserLogin(String login);
}
