package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.CompetenceRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.CompetenceQueryService;
import com.mycompany.myapp.service.CompetenceService;
import com.mycompany.myapp.service.criteria.CompetenceCriteria;
import com.mycompany.myapp.service.dto.CompetenceDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.Competence}.
 */
@RestController
@RequestMapping("/api/competences")
public class CompetenceResource {

    private static final Logger LOG = LoggerFactory.getLogger(CompetenceResource.class);

    private static final String ENTITY_NAME = "competence";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CompetenceService competenceService;

    private final CompetenceRepository competenceRepository;

    private final CompetenceQueryService competenceQueryService;

    public CompetenceResource(
        CompetenceService competenceService,
        CompetenceRepository competenceRepository,
        CompetenceQueryService competenceQueryService
    ) {
        this.competenceService = competenceService;
        this.competenceRepository = competenceRepository;
        this.competenceQueryService = competenceQueryService;
    }

    /**
     * {@code POST  /competences} : Create a new competence.
     *
     * @param competenceDTO the competenceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new competenceDTO, or with status {@code 400 (Bad Request)} if the competence has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.RECRUTEUR + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<CompetenceDTO> createCompetence(@Valid @RequestBody CompetenceDTO competenceDTO) throws URISyntaxException {
        LOG.debug("REST request to save Competence : {}", competenceDTO);
        if (competenceDTO.getId() != null) {
            throw new BadRequestAlertException("A new competence cannot already have an ID", ENTITY_NAME, "idexists");
        }
        competenceDTO = competenceService.save(competenceDTO);
        return ResponseEntity.created(new URI("/api/competences/" + competenceDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, competenceDTO.getId().toString()))
            .body(competenceDTO);
    }

    /**
     * {@code PUT  /competences/:id} : Updates an existing competence.
     *
     * @param id the id of the competenceDTO to save.
     * @param competenceDTO the competenceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated competenceDTO,
     * or with status {@code 400 (Bad Request)} if the competenceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the competenceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.RECRUTEUR + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<CompetenceDTO> updateCompetence(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CompetenceDTO competenceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Competence : {}, {}", id, competenceDTO);
        if (competenceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, competenceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!competenceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        competenceDTO = competenceService.update(competenceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, competenceDTO.getId().toString()))
            .body(competenceDTO);
    }

    /**
     * {@code PATCH  /competences/:id} : Partial updates given fields of an existing competence, field will ignore if it is null
     *
     * @param id the id of the competenceDTO to save.
     * @param competenceDTO the competenceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated competenceDTO,
     * or with status {@code 400 (Bad Request)} if the competenceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the competenceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the competenceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.RECRUTEUR + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<CompetenceDTO> partialUpdateCompetence(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CompetenceDTO competenceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Competence partially : {}, {}", id, competenceDTO);
        if (competenceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, competenceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!competenceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CompetenceDTO> result = competenceService.partialUpdate(competenceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, competenceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /competences} : get all the competences.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of competences in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.CANDIDAT + "', '" + AuthoritiesConstants.RECRUTEUR + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<List<CompetenceDTO>> getAllCompetences(CompetenceCriteria criteria) {
        LOG.debug("REST request to get Competences by criteria: {}", criteria);

        List<CompetenceDTO> entityList = competenceQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /competences/count} : count all the competences.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.CANDIDAT + "', '" + AuthoritiesConstants.RECRUTEUR + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<Long> countCompetences(CompetenceCriteria criteria) {
        LOG.debug("REST request to count Competences by criteria: {}", criteria);
        return ResponseEntity.ok().body(competenceQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /competences/:id} : get the "id" competence.
     *
     * @param id the id of the competenceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the competenceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.CANDIDAT + "', '" + AuthoritiesConstants.RECRUTEUR + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<CompetenceDTO> getCompetence(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Competence : {}", id);
        Optional<CompetenceDTO> competenceDTO = competenceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(competenceDTO);
    }

    /**
     * {@code DELETE  /competences/:id} : delete the "id" competence.
     *
     * @param id the id of the competenceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.RECRUTEUR + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<Void> deleteCompetence(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Competence : {}", id);
        competenceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
