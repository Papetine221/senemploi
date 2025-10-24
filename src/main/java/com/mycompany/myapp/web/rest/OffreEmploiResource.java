package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.repository.OffreEmploiRepository;
import com.mycompany.myapp.security.AuthoritiesConstants;
import com.mycompany.myapp.security.SecurityUtils;
import com.mycompany.myapp.service.OffreEmploiQueryService;
import com.mycompany.myapp.service.OffreEmploiService;
import com.mycompany.myapp.service.criteria.OffreEmploiCriteria;
import com.mycompany.myapp.service.dto.OffreEmploiDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.OffreEmploi}.
 */
@RestController
@RequestMapping("/api/offre-emplois")
public class OffreEmploiResource {

    private static final Logger LOG = LoggerFactory.getLogger(OffreEmploiResource.class);

    private static final String ENTITY_NAME = "offreEmploi";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OffreEmploiService offreEmploiService;

    private final OffreEmploiRepository offreEmploiRepository;

    private final OffreEmploiQueryService offreEmploiQueryService;

    public OffreEmploiResource(
        OffreEmploiService offreEmploiService,
        OffreEmploiRepository offreEmploiRepository,
        OffreEmploiQueryService offreEmploiQueryService
    ) {
        this.offreEmploiService = offreEmploiService;
        this.offreEmploiRepository = offreEmploiRepository;
        this.offreEmploiQueryService = offreEmploiQueryService;
    }

    

    /**
     * {@code POST  /offre-emplois} : Create a new offreEmploi.
     *
     * @param offreEmploiDTO the offreEmploiDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new offreEmploiDTO, or with status {@code 400 (Bad Request)} if the offreEmploi has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.RECRUTEUR + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<OffreEmploiDTO> createOffreEmploi(@Valid @RequestBody OffreEmploiDTO offreEmploiDTO) throws URISyntaxException {
        LOG.debug("REST request to save OffreEmploi : {}", offreEmploiDTO);
        if (offreEmploiDTO.getId() != null) {
            throw new BadRequestAlertException("A new offreEmploi cannot already have an ID", ENTITY_NAME, "idexists");
        }
        offreEmploiDTO = offreEmploiService.save(offreEmploiDTO);
        return ResponseEntity.created(new URI("/api/offre-emplois/" + offreEmploiDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, offreEmploiDTO.getId().toString()))
            .body(offreEmploiDTO);
    }

    /**
     * {@code PUT  /offre-emplois/:id} : Updates an existing offreEmploi.
     *
     * @param id the id of the offreEmploiDTO to save.
     * @param offreEmploiDTO the offreEmploiDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated offreEmploiDTO,
     * or with status {@code 400 (Bad Request)} if the offreEmploiDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the offreEmploiDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.RECRUTEUR + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<OffreEmploiDTO> updateOffreEmploi(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OffreEmploiDTO offreEmploiDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update OffreEmploi : {}, {}", id, offreEmploiDTO);
        if (offreEmploiDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, offreEmploiDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!offreEmploiRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        offreEmploiDTO = offreEmploiService.update(offreEmploiDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, offreEmploiDTO.getId().toString()))
            .body(offreEmploiDTO);
    }

    /**
     * {@code PATCH  /offre-emplois/:id} : Partial updates given fields of an existing offreEmploi, field will ignore if it is null
     *
     * @param id the id of the offreEmploiDTO to save.
     * @param offreEmploiDTO the offreEmploiDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated offreEmploiDTO,
     * or with status {@code 400 (Bad Request)} if the offreEmploiDTO is not valid,
     * or with status {@code 404 (Not Found)} if the offreEmploiDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the offreEmploiDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.RECRUTEUR + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<OffreEmploiDTO> partialUpdateOffreEmploi(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OffreEmploiDTO offreEmploiDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update OffreEmploi partially : {}, {}", id, offreEmploiDTO);
        if (offreEmploiDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, offreEmploiDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!offreEmploiRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<OffreEmploiDTO> result = offreEmploiService.partialUpdate(offreEmploiDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, offreEmploiDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /offre-emplois} : get all the offreEmplois.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of offreEmplois in body.
     */
    @GetMapping("")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.CANDIDAT + "', '" + AuthoritiesConstants.RECRUTEUR + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<List<OffreEmploiDTO>> getAllOffreEmplois(
        OffreEmploiCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to get OffreEmplois by criteria: {}", criteria);

        Page<OffreEmploiDTO> page = offreEmploiQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /offre-emplois/count} : count all the offreEmplois.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.CANDIDAT + "', '" + AuthoritiesConstants.RECRUTEUR + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<Long> countOffreEmplois(OffreEmploiCriteria criteria) {
        LOG.debug("REST request to count OffreEmplois by criteria: {}", criteria);
        return ResponseEntity.ok().body(offreEmploiQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /offre-emplois/:id} : get the "id" offreEmploi.
     *
     * @param id the id of the offreEmploiDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the offreEmploiDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/recruteur/current")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.RECRUTEUR + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<List<OffreEmploiDTO>> getOffresByCurrentRecruteur() {
        String login = SecurityUtils.getCurrentUserLogin().orElse(null);
        if (login == null) {
            return ResponseEntity.badRequest().build();
        }
        List<OffreEmploiDTO> offres = offreEmploiService.findByRecruteurLogin(login);
        return ResponseEntity.ok().body(offres);
    }
    

    /**
     * {@code DELETE  /offre-emplois/:id} : delete the "id" offreEmploi.
     *
     * @param id the id of the offreEmploiDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('" + AuthoritiesConstants.RECRUTEUR + "', '" + AuthoritiesConstants.ADMIN + "')")
    public ResponseEntity<Void> deleteOffreEmploi(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete OffreEmploi : {}", id);
        offreEmploiService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

        /**
     * {@code GET  /offre-emplois/recruteur/:id} : get all offres by recruteur ID.
     *
     * @param id the id of the recruteur whose offers to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of offers in body.
     */
   @GetMapping("/recruteur/mes-offres")
@PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.RECRUTEUR + "\")")
public ResponseEntity<List<OffreEmploiDTO>> getOffresByRecruteurConnecte() {
    String login = SecurityUtils.getCurrentUserLogin().orElseThrow();
    LOG.debug("REST request to get offres for connected recruteur : {}", login);
    List<OffreEmploiDTO> offres = offreEmploiService.findByRecruteurLogin(login);
    return ResponseEntity.ok(offres);
}


}
