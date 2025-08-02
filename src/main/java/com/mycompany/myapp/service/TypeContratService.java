package com.mycompany.myapp.service;

import com.mycompany.myapp.service.dto.TypeContratDTO;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.mycompany.myapp.domain.TypeContrat}.
 */
public interface TypeContratService {
    /**
     * Save a typeContrat.
     *
     * @param typeContratDTO the entity to save.
     * @return the persisted entity.
     */
    TypeContratDTO save(TypeContratDTO typeContratDTO);

    /**
     * Updates a typeContrat.
     *
     * @param typeContratDTO the entity to update.
     * @return the persisted entity.
     */
    TypeContratDTO update(TypeContratDTO typeContratDTO);

    /**
     * Partially updates a typeContrat.
     *
     * @param typeContratDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<TypeContratDTO> partialUpdate(TypeContratDTO typeContratDTO);

    /**
     * Get the "id" typeContrat.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<TypeContratDTO> findOne(Long id);

    /**
     * Delete the "id" typeContrat.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
