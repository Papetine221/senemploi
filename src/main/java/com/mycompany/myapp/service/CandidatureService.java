package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.CandidatureDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Candidature}.
 */
public interface CandidatureService {
    /**
     * Save a candidature.
     *
     * @param candidatureDTO the entity to save.
     * @return the persisted entity.
     */
    CandidatureDTO save(CandidatureDTO candidatureDTO);

    /**
     * Updates a candidature.
     *
     * @param candidatureDTO the entity to update.
     * @return the persisted entity.
     */
    CandidatureDTO update(CandidatureDTO candidatureDTO);

    /**
     * Partially updates a candidature.
     *
     * @param candidatureDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CandidatureDTO> partialUpdate(CandidatureDTO candidatureDTO);

    /**
     * Get the "id" candidature.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CandidatureDTO> findOne(Long id);

    /**
     * Delete the "id" candidature.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
