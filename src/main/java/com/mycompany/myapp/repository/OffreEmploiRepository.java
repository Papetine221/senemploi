package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.OffreEmploi;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OffreEmploi entity.
 *
 * When extending this class, extend OffreEmploiRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface OffreEmploiRepository
    extends OffreEmploiRepositoryWithBagRelationships, JpaRepository<OffreEmploi, Long>, JpaSpecificationExecutor<OffreEmploi> {

    // 🔹 Méthode personnalisée pour récupérer les offres d’un recruteur donné
    List<OffreEmploi> findByRecruteurId(Long recruteurId);

    // 🔹 Méthodes par défaut générées par JHipster (à garder)
    default Optional<OffreEmploi> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<OffreEmploi> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<OffreEmploi> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
