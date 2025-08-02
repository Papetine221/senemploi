package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.OffreEmploi;
import com.mycompany.myapp.repository.OffreEmploiRepository;
import com.mycompany.myapp.service.criteria.OffreEmploiCriteria;
import com.mycompany.myapp.service.dto.OffreEmploiDTO;
import com.mycompany.myapp.service.mapper.OffreEmploiMapper;
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
 * Service for executing complex queries for {@link OffreEmploi} entities in the database.
 * The main input is a {@link OffreEmploiCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link OffreEmploiDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OffreEmploiQueryService extends QueryService<OffreEmploi> {

    private static final Logger LOG = LoggerFactory.getLogger(OffreEmploiQueryService.class);

    private final OffreEmploiRepository offreEmploiRepository;

    private final OffreEmploiMapper offreEmploiMapper;

    public OffreEmploiQueryService(OffreEmploiRepository offreEmploiRepository, OffreEmploiMapper offreEmploiMapper) {
        this.offreEmploiRepository = offreEmploiRepository;
        this.offreEmploiMapper = offreEmploiMapper;
    }

    /**
     * Return a {@link Page} of {@link OffreEmploiDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OffreEmploiDTO> findByCriteria(OffreEmploiCriteria criteria, Pageable page) {
        LOG.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<OffreEmploi> specification = createSpecification(criteria);
        return offreEmploiRepository
            .fetchBagRelationships(offreEmploiRepository.findAll(specification, page))
            .map(offreEmploiMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OffreEmploiCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<OffreEmploi> specification = createSpecification(criteria);
        return offreEmploiRepository.count(specification);
    }

    /**
     * Function to convert {@link OffreEmploiCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<OffreEmploi> createSpecification(OffreEmploiCriteria criteria) {
        Specification<OffreEmploi> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), OffreEmploi_.id),
                buildStringSpecification(criteria.getTitre(), OffreEmploi_.titre),
                buildRangeSpecification(criteria.getSalaire(), OffreEmploi_.salaire),
                buildRangeSpecification(criteria.getDatePublication(), OffreEmploi_.datePublication),
                buildRangeSpecification(criteria.getDateExpiration(), OffreEmploi_.dateExpiration),
                buildSpecification(criteria.getRecruteurId(), root -> root.join(OffreEmploi_.recruteur, JoinType.LEFT).get(Recruteur_.id)),
                buildSpecification(criteria.getTypeContratId(), root ->
                    root.join(OffreEmploi_.typeContrat, JoinType.LEFT).get(TypeContrat_.id)
                ),
                buildSpecification(criteria.getLocalisationId(), root ->
                    root.join(OffreEmploi_.localisation, JoinType.LEFT).get(Localisation_.id)
                ),
                buildSpecification(criteria.getCompetencesId(), root ->
                    root.join(OffreEmploi_.competences, JoinType.LEFT).get(Competence_.id)
                )
            );
        }
        return specification;
    }
}
