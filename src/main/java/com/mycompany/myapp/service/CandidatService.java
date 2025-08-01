package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.CandidatDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Candidat}.
 */
public interface CandidatService {
    /**
     * Save a candidat.
     *
     * @param candidatDTO the entity to save.
     * @return the persisted entity.
     */
    CandidatDTO save(CandidatDTO candidatDTO);

    /**
     * Updates a candidat.
     *
     * @param candidatDTO the entity to update.
     * @return the persisted entity.
     */
    CandidatDTO update(CandidatDTO candidatDTO);

    /**
     * Partially updates a candidat.
     *
     * @param candidatDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CandidatDTO> partialUpdate(CandidatDTO candidatDTO);

    /**
     * Get the "id" candidat.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CandidatDTO> findOne(Long id);

    /**
     * Delete the "id" candidat.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
