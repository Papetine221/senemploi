package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.CandidatureAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Candidat;
import com.mycompany.myapp.domain.Candidature;
import com.mycompany.myapp.domain.OffreEmploi;
import com.mycompany.myapp.repository.CandidatureRepository;
import com.mycompany.myapp.service.dto.CandidatureDTO;
import com.mycompany.myapp.service.mapper.CandidatureMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CandidatureResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CandidatureResourceIT {

    private static final String DEFAULT_LETTRE_MOTIVATION = "AAAAAAAAAA";
    private static final String UPDATED_LETTRE_MOTIVATION = "BBBBBBBBBB";

    private static final Instant DEFAULT_DATE_POSTULATION = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE_POSTULATION = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_STATUT = "AAAAAAAAAA";
    private static final String UPDATED_STATUT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/candidatures";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CandidatureRepository candidatureRepository;

    @Autowired
    private CandidatureMapper candidatureMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCandidatureMockMvc;

    private Candidature candidature;

    private Candidature insertedCandidature;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Candidature createEntity(EntityManager em) {
        Candidature candidature = new Candidature()
            .lettreMotivation(DEFAULT_LETTRE_MOTIVATION)
            .datePostulation(DEFAULT_DATE_POSTULATION)
            .statut(DEFAULT_STATUT);
        // Add required entity
        Candidat candidat;
        if (TestUtil.findAll(em, Candidat.class).isEmpty()) {
            candidat = CandidatResourceIT.createEntity(em);
            em.persist(candidat);
            em.flush();
        } else {
            candidat = TestUtil.findAll(em, Candidat.class).get(0);
        }
        candidature.setCandidat(candidat);
        // Add required entity
        OffreEmploi offreEmploi;
        if (TestUtil.findAll(em, OffreEmploi.class).isEmpty()) {
            offreEmploi = OffreEmploiResourceIT.createEntity(em);
            em.persist(offreEmploi);
            em.flush();
        } else {
            offreEmploi = TestUtil.findAll(em, OffreEmploi.class).get(0);
        }
        candidature.setOffre(offreEmploi);
        return candidature;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Candidature createUpdatedEntity(EntityManager em) {
        Candidature updatedCandidature = new Candidature()
            .lettreMotivation(UPDATED_LETTRE_MOTIVATION)
            .datePostulation(UPDATED_DATE_POSTULATION)
            .statut(UPDATED_STATUT);
        // Add required entity
        Candidat candidat;
        if (TestUtil.findAll(em, Candidat.class).isEmpty()) {
            candidat = CandidatResourceIT.createUpdatedEntity(em);
            em.persist(candidat);
            em.flush();
        } else {
            candidat = TestUtil.findAll(em, Candidat.class).get(0);
        }
        updatedCandidature.setCandidat(candidat);
        // Add required entity
        OffreEmploi offreEmploi;
        if (TestUtil.findAll(em, OffreEmploi.class).isEmpty()) {
            offreEmploi = OffreEmploiResourceIT.createUpdatedEntity(em);
            em.persist(offreEmploi);
            em.flush();
        } else {
            offreEmploi = TestUtil.findAll(em, OffreEmploi.class).get(0);
        }
        updatedCandidature.setOffre(offreEmploi);
        return updatedCandidature;
    }

    @BeforeEach
    void initTest() {
        candidature = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedCandidature != null) {
            candidatureRepository.delete(insertedCandidature);
            insertedCandidature = null;
        }
    }

    @Test
    @Transactional
    void createCandidature() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Candidature
        CandidatureDTO candidatureDTO = candidatureMapper.toDto(candidature);
        var returnedCandidatureDTO = om.readValue(
            restCandidatureMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(candidatureDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CandidatureDTO.class
        );

        // Validate the Candidature in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCandidature = candidatureMapper.toEntity(returnedCandidatureDTO);
        assertCandidatureUpdatableFieldsEquals(returnedCandidature, getPersistedCandidature(returnedCandidature));

        insertedCandidature = returnedCandidature;
    }

    @Test
    @Transactional
    void createCandidatureWithExistingId() throws Exception {
        // Create the Candidature with an existing ID
        candidature.setId(1L);
        CandidatureDTO candidatureDTO = candidatureMapper.toDto(candidature);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCandidatureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(candidatureDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Candidature in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDatePostulationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        candidature.setDatePostulation(null);

        // Create the Candidature, which fails.
        CandidatureDTO candidatureDTO = candidatureMapper.toDto(candidature);

        restCandidatureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(candidatureDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCandidatures() throws Exception {
        // Initialize the database
        insertedCandidature = candidatureRepository.saveAndFlush(candidature);

        // Get all the candidatureList
        restCandidatureMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(candidature.getId().intValue())))
            .andExpect(jsonPath("$.[*].lettreMotivation").value(hasItem(DEFAULT_LETTRE_MOTIVATION)))
            .andExpect(jsonPath("$.[*].datePostulation").value(hasItem(DEFAULT_DATE_POSTULATION.toString())))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT)));
    }

    @Test
    @Transactional
    void getCandidature() throws Exception {
        // Initialize the database
        insertedCandidature = candidatureRepository.saveAndFlush(candidature);

        // Get the candidature
        restCandidatureMockMvc
            .perform(get(ENTITY_API_URL_ID, candidature.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(candidature.getId().intValue()))
            .andExpect(jsonPath("$.lettreMotivation").value(DEFAULT_LETTRE_MOTIVATION))
            .andExpect(jsonPath("$.datePostulation").value(DEFAULT_DATE_POSTULATION.toString()))
            .andExpect(jsonPath("$.statut").value(DEFAULT_STATUT));
    }

    @Test
    @Transactional
    void getCandidaturesByIdFiltering() throws Exception {
        // Initialize the database
        insertedCandidature = candidatureRepository.saveAndFlush(candidature);

        Long id = candidature.getId();

        defaultCandidatureFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCandidatureFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCandidatureFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllCandidaturesByDatePostulationIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCandidature = candidatureRepository.saveAndFlush(candidature);

        // Get all the candidatureList where datePostulation equals to
        defaultCandidatureFiltering(
            "datePostulation.equals=" + DEFAULT_DATE_POSTULATION,
            "datePostulation.equals=" + UPDATED_DATE_POSTULATION
        );
    }

    @Test
    @Transactional
    void getAllCandidaturesByDatePostulationIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCandidature = candidatureRepository.saveAndFlush(candidature);

        // Get all the candidatureList where datePostulation in
        defaultCandidatureFiltering(
            "datePostulation.in=" + DEFAULT_DATE_POSTULATION + "," + UPDATED_DATE_POSTULATION,
            "datePostulation.in=" + UPDATED_DATE_POSTULATION
        );
    }

    @Test
    @Transactional
    void getAllCandidaturesByDatePostulationIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCandidature = candidatureRepository.saveAndFlush(candidature);

        // Get all the candidatureList where datePostulation is not null
        defaultCandidatureFiltering("datePostulation.specified=true", "datePostulation.specified=false");
    }

    @Test
    @Transactional
    void getAllCandidaturesByStatutIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedCandidature = candidatureRepository.saveAndFlush(candidature);

        // Get all the candidatureList where statut equals to
        defaultCandidatureFiltering("statut.equals=" + DEFAULT_STATUT, "statut.equals=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllCandidaturesByStatutIsInShouldWork() throws Exception {
        // Initialize the database
        insertedCandidature = candidatureRepository.saveAndFlush(candidature);

        // Get all the candidatureList where statut in
        defaultCandidatureFiltering("statut.in=" + DEFAULT_STATUT + "," + UPDATED_STATUT, "statut.in=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllCandidaturesByStatutIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedCandidature = candidatureRepository.saveAndFlush(candidature);

        // Get all the candidatureList where statut is not null
        defaultCandidatureFiltering("statut.specified=true", "statut.specified=false");
    }

    @Test
    @Transactional
    void getAllCandidaturesByStatutContainsSomething() throws Exception {
        // Initialize the database
        insertedCandidature = candidatureRepository.saveAndFlush(candidature);

        // Get all the candidatureList where statut contains
        defaultCandidatureFiltering("statut.contains=" + DEFAULT_STATUT, "statut.contains=" + UPDATED_STATUT);
    }

    @Test
    @Transactional
    void getAllCandidaturesByStatutNotContainsSomething() throws Exception {
        // Initialize the database
        insertedCandidature = candidatureRepository.saveAndFlush(candidature);

        // Get all the candidatureList where statut does not contain
        defaultCandidatureFiltering("statut.doesNotContain=" + UPDATED_STATUT, "statut.doesNotContain=" + DEFAULT_STATUT);
    }

    @Test
    @Transactional
    void getAllCandidaturesByCandidatIsEqualToSomething() throws Exception {
        Candidat candidat;
        if (TestUtil.findAll(em, Candidat.class).isEmpty()) {
            candidatureRepository.saveAndFlush(candidature);
            candidat = CandidatResourceIT.createEntity(em);
        } else {
            candidat = TestUtil.findAll(em, Candidat.class).get(0);
        }
        em.persist(candidat);
        em.flush();
        candidature.setCandidat(candidat);
        candidatureRepository.saveAndFlush(candidature);
        Long candidatId = candidat.getId();
        // Get all the candidatureList where candidat equals to candidatId
        defaultCandidatureShouldBeFound("candidatId.equals=" + candidatId);

        // Get all the candidatureList where candidat equals to (candidatId + 1)
        defaultCandidatureShouldNotBeFound("candidatId.equals=" + (candidatId + 1));
    }

    @Test
    @Transactional
    void getAllCandidaturesByOffreIsEqualToSomething() throws Exception {
        OffreEmploi offre;
        if (TestUtil.findAll(em, OffreEmploi.class).isEmpty()) {
            candidatureRepository.saveAndFlush(candidature);
            offre = OffreEmploiResourceIT.createEntity(em);
        } else {
            offre = TestUtil.findAll(em, OffreEmploi.class).get(0);
        }
        em.persist(offre);
        em.flush();
        candidature.setOffre(offre);
        candidatureRepository.saveAndFlush(candidature);
        Long offreId = offre.getId();
        // Get all the candidatureList where offre equals to offreId
        defaultCandidatureShouldBeFound("offreId.equals=" + offreId);

        // Get all the candidatureList where offre equals to (offreId + 1)
        defaultCandidatureShouldNotBeFound("offreId.equals=" + (offreId + 1));
    }

    private void defaultCandidatureFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultCandidatureShouldBeFound(shouldBeFound);
        defaultCandidatureShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCandidatureShouldBeFound(String filter) throws Exception {
        restCandidatureMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(candidature.getId().intValue())))
            .andExpect(jsonPath("$.[*].lettreMotivation").value(hasItem(DEFAULT_LETTRE_MOTIVATION)))
            .andExpect(jsonPath("$.[*].datePostulation").value(hasItem(DEFAULT_DATE_POSTULATION.toString())))
            .andExpect(jsonPath("$.[*].statut").value(hasItem(DEFAULT_STATUT)));

        // Check, that the count call also returns 1
        restCandidatureMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCandidatureShouldNotBeFound(String filter) throws Exception {
        restCandidatureMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCandidatureMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingCandidature() throws Exception {
        // Get the candidature
        restCandidatureMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCandidature() throws Exception {
        // Initialize the database
        insertedCandidature = candidatureRepository.saveAndFlush(candidature);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the candidature
        Candidature updatedCandidature = candidatureRepository.findById(candidature.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCandidature are not directly saved in db
        em.detach(updatedCandidature);
        updatedCandidature.lettreMotivation(UPDATED_LETTRE_MOTIVATION).datePostulation(UPDATED_DATE_POSTULATION).statut(UPDATED_STATUT);
        CandidatureDTO candidatureDTO = candidatureMapper.toDto(updatedCandidature);

        restCandidatureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, candidatureDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(candidatureDTO))
            )
            .andExpect(status().isOk());

        // Validate the Candidature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCandidatureToMatchAllProperties(updatedCandidature);
    }

    @Test
    @Transactional
    void putNonExistingCandidature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidature.setId(longCount.incrementAndGet());

        // Create the Candidature
        CandidatureDTO candidatureDTO = candidatureMapper.toDto(candidature);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCandidatureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, candidatureDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(candidatureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Candidature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCandidature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidature.setId(longCount.incrementAndGet());

        // Create the Candidature
        CandidatureDTO candidatureDTO = candidatureMapper.toDto(candidature);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCandidatureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(candidatureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Candidature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCandidature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidature.setId(longCount.incrementAndGet());

        // Create the Candidature
        CandidatureDTO candidatureDTO = candidatureMapper.toDto(candidature);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCandidatureMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(candidatureDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Candidature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCandidatureWithPatch() throws Exception {
        // Initialize the database
        insertedCandidature = candidatureRepository.saveAndFlush(candidature);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the candidature using partial update
        Candidature partialUpdatedCandidature = new Candidature();
        partialUpdatedCandidature.setId(candidature.getId());

        restCandidatureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCandidature.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCandidature))
            )
            .andExpect(status().isOk());

        // Validate the Candidature in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCandidatureUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCandidature, candidature),
            getPersistedCandidature(candidature)
        );
    }

    @Test
    @Transactional
    void fullUpdateCandidatureWithPatch() throws Exception {
        // Initialize the database
        insertedCandidature = candidatureRepository.saveAndFlush(candidature);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the candidature using partial update
        Candidature partialUpdatedCandidature = new Candidature();
        partialUpdatedCandidature.setId(candidature.getId());

        partialUpdatedCandidature
            .lettreMotivation(UPDATED_LETTRE_MOTIVATION)
            .datePostulation(UPDATED_DATE_POSTULATION)
            .statut(UPDATED_STATUT);

        restCandidatureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCandidature.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCandidature))
            )
            .andExpect(status().isOk());

        // Validate the Candidature in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCandidatureUpdatableFieldsEquals(partialUpdatedCandidature, getPersistedCandidature(partialUpdatedCandidature));
    }

    @Test
    @Transactional
    void patchNonExistingCandidature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidature.setId(longCount.incrementAndGet());

        // Create the Candidature
        CandidatureDTO candidatureDTO = candidatureMapper.toDto(candidature);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCandidatureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, candidatureDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(candidatureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Candidature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCandidature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidature.setId(longCount.incrementAndGet());

        // Create the Candidature
        CandidatureDTO candidatureDTO = candidatureMapper.toDto(candidature);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCandidatureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(candidatureDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Candidature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCandidature() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        candidature.setId(longCount.incrementAndGet());

        // Create the Candidature
        CandidatureDTO candidatureDTO = candidatureMapper.toDto(candidature);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCandidatureMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(candidatureDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Candidature in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCandidature() throws Exception {
        // Initialize the database
        insertedCandidature = candidatureRepository.saveAndFlush(candidature);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the candidature
        restCandidatureMockMvc
            .perform(delete(ENTITY_API_URL_ID, candidature.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return candidatureRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Candidature getPersistedCandidature(Candidature candidature) {
        return candidatureRepository.findById(candidature.getId()).orElseThrow();
    }

    protected void assertPersistedCandidatureToMatchAllProperties(Candidature expectedCandidature) {
        assertCandidatureAllPropertiesEquals(expectedCandidature, getPersistedCandidature(expectedCandidature));
    }

    protected void assertPersistedCandidatureToMatchUpdatableProperties(Candidature expectedCandidature) {
        assertCandidatureAllUpdatablePropertiesEquals(expectedCandidature, getPersistedCandidature(expectedCandidature));
    }
}
