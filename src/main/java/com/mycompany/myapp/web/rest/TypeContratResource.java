package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.TypeContratRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.service.TypeContratQueryService;
import com.mycompany.myapp.service.TypeContratService;
import com.mycompany.myapp.service.criteria.TypeContratCriteria;
import com.mycompany.myapp.service.dto.TypeContratDTO;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.TypeContrat}.
 */
@RestController
@RequestMapping("/api/type-contrats")
public class TypeContratResource {

    private static final Logger LOG = LoggerFactory.getLogger(TypeContratResource.class);

    private static final String ENTITY_NAME = "typeContrat";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TypeContratService typeContratService;

    private final TypeContratRepository typeContratRepository;

    private final TypeContratQueryService typeContratQueryService;

    public TypeContratResource(
        TypeContratService typeContratService,
        TypeContratRepository typeContratRepository,
        TypeContratQueryService typeContratQueryService
    ) {
        this.typeContratService = typeContratService;
        this.typeContratRepository = typeContratRepository;
        this.typeContratQueryService = typeContratQueryService;
    }

    /**
     * {@code POST  /type-contrats} : Create a new typeContrat.
     *
     * @param typeContratDTO the typeContratDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new typeContratDTO, or with status {@code 400 (Bad Request)} if the typeContrat has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<TypeContratDTO> createTypeContrat(@Valid @RequestBody TypeContratDTO typeContratDTO) throws URISyntaxException {
        LOG.debug("REST request to save TypeContrat : {}", typeContratDTO);
        if (typeContratDTO.getId() != null) {
            throw new BadRequestAlertException("A new typeContrat cannot already have an ID", ENTITY_NAME, "idexists");
        }
        typeContratDTO = typeContratService.save(typeContratDTO);
        return ResponseEntity.created(new URI("/api/type-contrats/" + typeContratDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, typeContratDTO.getId().toString()))
            .body(typeContratDTO);
    }

    /**
     * {@code PUT  /type-contrats/:id} : Updates an existing typeContrat.
     *
     * @param id the id of the typeContratDTO to save.
     * @param typeContratDTO the typeContratDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated typeContratDTO,
     * or with status {@code 400 (Bad Request)} if the typeContratDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the typeContratDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<TypeContratDTO> updateTypeContrat(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TypeContratDTO typeContratDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TypeContrat : {}, {}", id, typeContratDTO);
        if (typeContratDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, typeContratDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!typeContratRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        typeContratDTO = typeContratService.update(typeContratDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, typeContratDTO.getId().toString()))
            .body(typeContratDTO);
    }

    /**
     * {@code PATCH  /type-contrats/:id} : Partial updates given fields of an existing typeContrat, field will ignore if it is null
     *
     * @param id the id of the typeContratDTO to save.
     * @param typeContratDTO the typeContratDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated typeContratDTO,
     * or with status {@code 400 (Bad Request)} if the typeContratDTO is not valid,
     * or with status {@code 404 (Not Found)} if the typeContratDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the typeContratDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<TypeContratDTO> partialUpdateTypeContrat(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TypeContratDTO typeContratDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TypeContrat partially : {}, {}", id, typeContratDTO);
        if (typeContratDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, typeContratDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!typeContratRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TypeContratDTO> result = typeContratService.partialUpdate(typeContratDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, typeContratDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /type-contrats} : get all the typeContrats.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of typeContrats in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.CANDIDAT + "', '" + AuthoritiesConstants.RECRUTEUR + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<List<TypeContratDTO>> getAllTypeContrats(TypeContratCriteria criteria) {
        LOG.debug("REST request to get TypeContrats by criteria: {}", criteria);

        List<TypeContratDTO> entityList = typeContratQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /type-contrats/count} : count all the typeContrats.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.CANDIDAT + "', '" + AuthoritiesConstants.RECRUTEUR + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<Long> countTypeContrats(TypeContratCriteria criteria) {
        LOG.debug("REST request to count TypeContrats by criteria: {}", criteria);
        return ResponseEntity.ok().body(typeContratQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /type-contrats/:id} : get the "id" typeContrat.
     *
     * @param id the id of the typeContratDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the typeContratDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.CANDIDAT + "', '" + AuthoritiesConstants.RECRUTEUR + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<TypeContratDTO> getTypeContrat(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TypeContrat : {}", id);
        Optional<TypeContratDTO> typeContratDTO = typeContratService.findOne(id);
        return ResponseUtil.wrapOrNotFound(typeContratDTO);
    }

    /**
     * {@code DELETE  /type-contrats/:id} : delete the "id" typeContrat.
     *
     * @param id the id of the typeContratDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<Void> deleteTypeContrat(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TypeContrat : {}", id);
        typeContratService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
