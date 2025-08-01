package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.RecruteurDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.Recruteur}.
 */
public interface RecruteurService {
    /**
     * Save a recruteur.
     *
     * @param recruteurDTO the entity to save.
     * @return the persisted entity.
     */
    RecruteurDTO save(RecruteurDTO recruteurDTO);

    /**
     * Updates a recruteur.
     *
     * @param recruteurDTO the entity to update.
     * @return the persisted entity.
     */
    RecruteurDTO update(RecruteurDTO recruteurDTO);

    /**
     * Partially updates a recruteur.
     *
     * @param recruteurDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<RecruteurDTO> partialUpdate(RecruteurDTO recruteurDTO);

    /**
     * Get the "id" recruteur.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<RecruteurDTO> findOne(Long id);

    /**
     * Delete the "id" recruteur.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
