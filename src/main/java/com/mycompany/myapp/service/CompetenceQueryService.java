package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.Competence;
import com.mycompany.myapp.repository.CompetenceRepository;
import com.mycompany.myapp.service.criteria.CompetenceCriteria;
import com.mycompany.myapp.service.dto.CompetenceDTO;
import com.mycompany.myapp.service.mapper.CompetenceMapper;
import jakarta.persistence.criteria.JoinType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Competence} entities in the database.
 * The main input is a {@link CompetenceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CompetenceDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CompetenceQueryService extends QueryService<Competence> {

    private static final Logger LOG = LoggerFactory.getLogger(CompetenceQueryService.class);

    private final CompetenceRepository competenceRepository;

    private final CompetenceMapper competenceMapper;

    public CompetenceQueryService(CompetenceRepository competenceRepository, CompetenceMapper competenceMapper) {
        this.competenceRepository = competenceRepository;
        this.competenceMapper = competenceMapper;
    }

    /**
     * Return a {@link List} of {@link CompetenceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CompetenceDTO> findByCriteria(CompetenceCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<Competence> specification = createSpecification(criteria);
        return competenceMapper.toDto(competenceRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CompetenceCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Competence> specification = createSpecification(criteria);
        return competenceRepository.count(specification);
    }

    /**
     * Function to convert {@link CompetenceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Competence> createSpecification(CompetenceCriteria criteria) {
        Specification<Competence> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Competence_.id),
                buildStringSpecification(criteria.getNom(), Competence_.nom),
                buildSpecification(criteria.getOffreEmploiId(), root ->
                    root.join(Competence_.offreEmplois, JoinType.LEFT).get(OffreEmploi_.id)
                )
            );
        }
        return specification;
    }
}
