package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.*; // for static metamodels
import com.mycompany.myapp.domain.Localisation;
import com.mycompany.myapp.repository.LocalisationRepository;
import com.mycompany.myapp.service.criteria.LocalisationCriteria;
import com.mycompany.myapp.service.dto.LocalisationDTO;
import com.mycompany.myapp.service.mapper.LocalisationMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Localisation} entities in the database.
 * The main input is a {@link LocalisationCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link LocalisationDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class LocalisationQueryService extends QueryService<Localisation> {

    private static final Logger LOG = LoggerFactory.getLogger(LocalisationQueryService.class);

    private final LocalisationRepository localisationRepository;

    private final LocalisationMapper localisationMapper;

    public LocalisationQueryService(LocalisationRepository localisationRepository, LocalisationMapper localisationMapper) {
        this.localisationRepository = localisationRepository;
        this.localisationMapper = localisationMapper;
    }

    /**
     * Return a {@link List} of {@link LocalisationDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<LocalisationDTO> findByCriteria(LocalisationCriteria criteria) {
        LOG.debug("find by criteria : {}", criteria);
        final Specification<Localisation> specification = createSpecification(criteria);
        return localisationMapper.toDto(localisationRepository.findAll(specification));
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(LocalisationCriteria criteria) {
        LOG.debug("count by criteria : {}", criteria);
        final Specification<Localisation> specification = createSpecification(criteria);
        return localisationRepository.count(specification);
    }

    /**
     * Function to convert {@link LocalisationCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Localisation> createSpecification(LocalisationCriteria criteria) {
        Specification<Localisation> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            specification = Specification.allOf(
                Boolean.TRUE.equals(criteria.getDistinct()) ? distinct(criteria.getDistinct()) : null,
                buildRangeSpecification(criteria.getId(), Localisation_.id),
                buildStringSpecification(criteria.getRegion(), Localisation_.region),
                buildStringSpecification(criteria.getDepartement(), Localisation_.departement),
                buildStringSpecification(criteria.getVille(), Localisation_.ville)
            );
        }
        return specification;
    }
}
