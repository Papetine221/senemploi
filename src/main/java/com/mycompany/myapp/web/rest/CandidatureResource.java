package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.CandidatureRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.CandidatureQueryService;
import com.mycompany.myapp.service.CandidatureService;
import com.mycompany.myapp.service.criteria.CandidatureCriteria;
import com.mycompany.myapp.service.dto.CandidatureDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.Candidature}.
 */
@RestController
@RequestMapping("/api/candidatures")
public class CandidatureResource {

    private static final Logger LOG = LoggerFactory.getLogger(CandidatureResource.class);

    private static final String ENTITY_NAME = "candidature";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CandidatureService candidatureService;

    private final CandidatureRepository candidatureRepository;

    private final CandidatureQueryService candidatureQueryService;

    public CandidatureResource(
        CandidatureService candidatureService,
        CandidatureRepository candidatureRepository,
        CandidatureQueryService candidatureQueryService
    ) {
        this.candidatureService = candidatureService;
        this.candidatureRepository = candidatureRepository;
        this.candidatureQueryService = candidatureQueryService;
    }

    /**
     * {@code POST  /candidatures} : Create a new candidature.
     *
     * @param candidatureDTO the candidatureDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new candidatureDTO, or with status {@code 400 (Bad Request)} if the candidature has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.CANDIDAT + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<CandidatureDTO> createCandidature(@Valid @RequestBody CandidatureDTO candidatureDTO) throws URISyntaxException {
        LOG.debug("REST request to save Candidature : {}", candidatureDTO);
        if (candidatureDTO.getId() != null) {
            throw new BadRequestAlertException("A new candidature cannot already have an ID", ENTITY_NAME, "idexists");
        }
        candidatureDTO = candidatureService.save(candidatureDTO);
        return ResponseEntity.created(new URI("/api/candidatures/" + candidatureDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, candidatureDTO.getId().toString()))
            .body(candidatureDTO);
    }

    /**
     * {@code PUT  /candidatures/:id} : Updates an existing candidature.
     *
     * @param id the id of the candidatureDTO to save.
     * @param candidatureDTO the candidatureDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated candidatureDTO,
     * or with status {@code 400 (Bad Request)} if the candidatureDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the candidatureDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.CANDIDAT + "', '" + AuthoritiesConstants.RECRUTEUR + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<CandidatureDTO> updateCandidature(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CandidatureDTO candidatureDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Candidature : {}, {}", id, candidatureDTO);
        if (candidatureDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, candidatureDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!candidatureRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        candidatureDTO = candidatureService.update(candidatureDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, candidatureDTO.getId().toString()))
            .body(candidatureDTO);
    }

    /**
     * {@code PATCH  /candidatures/:id} : Partial updates given fields of an existing candidature, field will ignore if it is null
     *
     * @param id the id of the candidatureDTO to save.
     * @param candidatureDTO the candidatureDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated candidatureDTO,
     * or with status {@code 400 (Bad Request)} if the candidatureDTO is not valid,
     * or with status {@code 404 (Not Found)} if the candidatureDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the candidatureDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.CANDIDAT + "', '" + AuthoritiesConstants.RECRUTEUR + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<CandidatureDTO> partialUpdateCandidature(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CandidatureDTO candidatureDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Candidature partially : {}, {}", id, candidatureDTO);
        if (candidatureDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, candidatureDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!candidatureRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CandidatureDTO> result = candidatureService.partialUpdate(candidatureDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, candidatureDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /candidatures} : get all the candidatures.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of candidatures in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.CANDIDAT + "', '" + AuthoritiesConstants.RECRUTEUR + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<List<CandidatureDTO>> getAllCandidatures(CandidatureCriteria criteria) {
        LOG.debug("REST request to get Candidatures by criteria: {}", criteria);

        List<CandidatureDTO> entityList = candidatureQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /candidatures/count} : count all the candidatures.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.CANDIDAT + "', '" + AuthoritiesConstants.RECRUTEUR + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<Long> countCandidatures(CandidatureCriteria criteria) {
        LOG.debug("REST request to count Candidatures by criteria: {}", criteria);
        return ResponseEntity.ok().body(candidatureQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /candidatures/:id} : get the "id" candidature.
     *
     * @param id the id of the candidatureDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the candidatureDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.CANDIDAT + "', '" + AuthoritiesConstants.RECRUTEUR + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<CandidatureDTO> getCandidature(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Candidature : {}", id);
        Optional<CandidatureDTO> candidatureDTO = candidatureService.findOne(id);
        return ResponseUtil.wrapOrNotFound(candidatureDTO);
    }

    /**
     * {@code DELETE  /candidatures/:id} : delete the "id" candidature.
     *
     * @param id the id of the candidatureDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.CANDIDAT + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<Void> deleteCandidature(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Candidature : {}", id);
        candidatureService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
