package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.Recruteur;
import com.mycompany.myapp.repository.RecruteurRepository;
import com.mycompany.myapp.service.criteria.RecruteurCriteria;
import com.mycompany.myapp.service.dto.RecruteurDTO;
import com.mycompany.myapp.service.mapper.RecruteurMapper;
import jakarta.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Recruteur} entities in the database.
 * The main input is a {@link RecruteurCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link RecruteurDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RecruteurQueryService extends QueryService<Recruteur> {

    private static final Logger LOG = LoggerFactory.getLogger(RecruteurQueryService.class);

    private final RecruteurRepository recruteurRepository;

    private final RecruteurMapper recruteurMapper;

    public RecruteurQueryService(RecruteurRepository recruteurRepository, RecruteurMapper recruteurMapper) {
        this.recruteurRepository = recruteurRepository;
        this.recruteurMapper = recruteurMapper;
    }

    /**
     * Return a {@link Page} of {@link RecruteurDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RecruteurDTO> findByCriteria(RecruteurCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Recruteur> specification = createSpecification(criteria);
        return recruteurRepository.findAll(specification, page).map(recruteurMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RecruteurCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Recruteur> specification = createSpecification(criteria);
        return recruteurRepository.count(specification);
    }

    /**
     * Function to convert {@link RecruteurCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Recruteur> createSpecification(RecruteurCriteria criteria) {
        Specification<Recruteur> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Recruteur_.id),
                buildStringSpecification(criteria.getNomEntreprise(), Recruteur_.nomEntreprise),
                buildStringSpecification(criteria.getSecteur(), Recruteur_.secteur),
                buildSpecification(criteria.getUserId(), root -> root.join(Recruteur_.user, JoinType.LEFT).get(User_.id))
            );
        }
        return specification;
    }
}
