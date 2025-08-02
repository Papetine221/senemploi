package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.TypeContrat;
import com.mycompany.myapp.repository.TypeContratRepository;
import com.mycompany.myapp.service.criteria.TypeContratCriteria;
import com.mycompany.myapp.service.dto.TypeContratDTO;
import com.mycompany.myapp.service.mapper.TypeContratMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link TypeContrat} entities in the database.
 * The main input is a {@link TypeContratCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TypeContratDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TypeContratQueryService extends QueryService<TypeContrat> {

    private static final Logger LOG = LoggerFactory.getLogger(TypeContratQueryService.class);

    private final TypeContratRepository typeContratRepository;

    private final TypeContratMapper typeContratMapper;

    public TypeContratQueryService(TypeContratRepository typeContratRepository, TypeContratMapper typeContratMapper) {
        this.typeContratRepository = typeContratRepository;
        this.typeContratMapper = typeContratMapper;
    }

    /**
     * Return a {@link List} of {@link TypeContratDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TypeContratDTO> findByCriteria(TypeContratCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<TypeContrat> specification = createSpecification(criteria);
        return typeContratMapper.toDto(typeContratRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TypeContratCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<TypeContrat> specification = createSpecification(criteria);
        return typeContratRepository.count(specification);
    }

    /**
     * Function to convert {@link TypeContratCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TypeContrat> createSpecification(TypeContratCriteria criteria) {
        Specification<TypeContrat> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), TypeContrat_.id),
                buildStringSpecification(criteria.getNom(), TypeContrat_.nom)
            );
        }
        return specification;
    }
}
