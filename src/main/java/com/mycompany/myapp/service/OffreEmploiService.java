package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.OffreEmploiDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.OffreEmploi}.
 */
public interface OffreEmploiService {
    /**
     * Save a offreEmploi.
     *
     * @param offreEmploiDTO the entity to save.
     * @return the persisted entity.
     */
    OffreEmploiDTO save(OffreEmploiDTO offreEmploiDTO);

    /**
     * Updates a offreEmploi.
     *
     * @param offreEmploiDTO the entity to update.
     * @return the persisted entity.
     */
    OffreEmploiDTO update(OffreEmploiDTO offreEmploiDTO);

    /**
     * Partially updates a offreEmploi.
     *
     * @param offreEmploiDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<OffreEmploiDTO> partialUpdate(OffreEmploiDTO offreEmploiDTO);

    /**
     * Get the "id" offreEmploi.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<OffreEmploiDTO> findOne(Long id);

    /**
     * Delete the "id" offreEmploi.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
