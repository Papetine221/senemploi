package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.Candidature;
import com.mycompany.myapp.repository.CandidatureRepository;
import com.mycompany.myapp.service.criteria.CandidatureCriteria;
import com.mycompany.myapp.service.dto.CandidatureDTO;
import com.mycompany.myapp.service.mapper.CandidatureMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Candidature} entities in the database.
 * The main input is a {@link CandidatureCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CandidatureDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CandidatureQueryService extends QueryService<Candidature> {

    private static final Logger LOG = LoggerFactory.getLogger(CandidatureQueryService.class);

    private final CandidatureRepository candidatureRepository;

    private final CandidatureMapper candidatureMapper;

    public CandidatureQueryService(CandidatureRepository candidatureRepository, CandidatureMapper candidatureMapper) {
        this.candidatureRepository = candidatureRepository;
        this.candidatureMapper = candidatureMapper;
    }

    /**
     * Return a {@link List} of {@link CandidatureDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CandidatureDTO> findByCriteria(CandidatureCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<Candidature> specification = createSpecification(criteria);
        return candidatureMapper.toDto(candidatureRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CandidatureCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Candidature> specification = createSpecification(criteria);
        return candidatureRepository.count(specification);
    }

    /**
     * Function to convert {@link CandidatureCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Candidature> createSpecification(CandidatureCriteria criteria) {
        Specification<Candidature> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Candidature_.id),
                buildRangeSpecification(criteria.getDatePostulation(), Candidature_.datePostulation),
                buildStringSpecification(criteria.getStatut(), Candidature_.statut),
                buildSpecification(criteria.getCandidatId(), root -> root.join(Candidature_.candidat, JoinType.LEFT).get(Candidat_.id)),
                buildSpecification(criteria.getOffreId(), root -> root.join(Candidature_.offre, JoinType.LEFT).get(OffreEmploi_.id))
            );
        }
        return specification;
    }
}
