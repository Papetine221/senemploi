package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.LocalisationAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Localisation;
import com.mycompany.myapp.repository.LocalisationRepository;
import com.mycompany.myapp.service.dto.LocalisationDTO;
import com.mycompany.myapp.service.mapper.LocalisationMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link LocalisationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LocalisationResourceIT {

    private static final String DEFAULT_REGION = "AAAAAAAAAA";
    private static final String UPDATED_REGION = "BBBBBBBBBB";

    private static final String DEFAULT_DEPARTEMENT = "AAAAAAAAAA";
    private static final String UPDATED_DEPARTEMENT = "BBBBBBBBBB";

    private static final String DEFAULT_VILLE = "AAAAAAAAAA";
    private static final String UPDATED_VILLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/localisations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LocalisationRepository localisationRepository;

    @Autowired
    private LocalisationMapper localisationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLocalisationMockMvc;

    private Localisation localisation;

    private Localisation insertedLocalisation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Localisation createEntity() {
        return new Localisation().region(DEFAULT_REGION).departement(DEFAULT_DEPARTEMENT).ville(DEFAULT_VILLE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Localisation createUpdatedEntity() {
        return new Localisation().region(UPDATED_REGION).departement(UPDATED_DEPARTEMENT).ville(UPDATED_VILLE);
    }

    @BeforeEach
    void initTest() {
        localisation = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedLocalisation != null) {
            localisationRepository.delete(insertedLocalisation);
            insertedLocalisation = null;
        }
    }

    @Test
    @Transactional
    void createLocalisation() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Localisation
        LocalisationDTO localisationDTO = localisationMapper.toDto(localisation);
        var returnedLocalisationDTO = om.readValue(
            restLocalisationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(localisationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LocalisationDTO.class
        );

        // Validate the Localisation in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLocalisation = localisationMapper.toEntity(returnedLocalisationDTO);
        assertLocalisationUpdatableFieldsEquals(returnedLocalisation, getPersistedLocalisation(returnedLocalisation));

        insertedLocalisation = returnedLocalisation;
    }

    @Test
    @Transactional
    void createLocalisationWithExistingId() throws Exception {
        // Create the Localisation with an existing ID
        localisation.setId(1L);
        LocalisationDTO localisationDTO = localisationMapper.toDto(localisation);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLocalisationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(localisationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Localisation in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkRegionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        localisation.setRegion(null);

        // Create the Localisation, which fails.
        LocalisationDTO localisationDTO = localisationMapper.toDto(localisation);

        restLocalisationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(localisationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkVilleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        localisation.setVille(null);

        // Create the Localisation, which fails.
        LocalisationDTO localisationDTO = localisationMapper.toDto(localisation);

        restLocalisationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(localisationDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLocalisations() throws Exception {
        // Initialize the database
        insertedLocalisation = localisationRepository.saveAndFlush(localisation);

        // Get all the localisationList
        restLocalisationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(localisation.getId().intValue())))
            .andExpect(jsonPath("$.[*].region").value(hasItem(DEFAULT_REGION)))
            .andExpect(jsonPath("$.[*].departement").value(hasItem(DEFAULT_DEPARTEMENT)))
            .andExpect(jsonPath("$.[*].ville").value(hasItem(DEFAULT_VILLE)));
    }

    @Test
    @Transactional
    void getLocalisation() throws Exception {
        // Initialize the database
        insertedLocalisation = localisationRepository.saveAndFlush(localisation);

        // Get the localisation
        restLocalisationMockMvc
            .perform(get(ENTITY_API_URL_ID, localisation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(localisation.getId().intValue()))
            .andExpect(jsonPath("$.region").value(DEFAULT_REGION))
            .andExpect(jsonPath("$.departement").value(DEFAULT_DEPARTEMENT))
            .andExpect(jsonPath("$.ville").value(DEFAULT_VILLE));
    }

    @Test
    @Transactional
    void getLocalisationsByIdFiltering() throws Exception {
        // Initialize the database
        insertedLocalisation = localisationRepository.saveAndFlush(localisation);

        Long id = localisation.getId();

        defaultLocalisationFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultLocalisationFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultLocalisationFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllLocalisationsByRegionIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocalisation = localisationRepository.saveAndFlush(localisation);

        // Get all the localisationList where region equals to
        defaultLocalisationFiltering("region.equals=" + DEFAULT_REGION, "region.equals=" + UPDATED_REGION);
    }

    @Test
    @Transactional
    void getAllLocalisationsByRegionIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocalisation = localisationRepository.saveAndFlush(localisation);

        // Get all the localisationList where region in
        defaultLocalisationFiltering("region.in=" + DEFAULT_REGION + "," + UPDATED_REGION, "region.in=" + UPDATED_REGION);
    }

    @Test
    @Transactional
    void getAllLocalisationsByRegionIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocalisation = localisationRepository.saveAndFlush(localisation);

        // Get all the localisationList where region is not null
        defaultLocalisationFiltering("region.specified=true", "region.specified=false");
    }

    @Test
    @Transactional
    void getAllLocalisationsByRegionContainsSomething() throws Exception {
        // Initialize the database
        insertedLocalisation = localisationRepository.saveAndFlush(localisation);

        // Get all the localisationList where region contains
        defaultLocalisationFiltering("region.contains=" + DEFAULT_REGION, "region.contains=" + UPDATED_REGION);
    }

    @Test
    @Transactional
    void getAllLocalisationsByRegionNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLocalisation = localisationRepository.saveAndFlush(localisation);

        // Get all the localisationList where region does not contain
        defaultLocalisationFiltering("region.doesNotContain=" + UPDATED_REGION, "region.doesNotContain=" + DEFAULT_REGION);
    }

    @Test
    @Transactional
    void getAllLocalisationsByDepartementIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocalisation = localisationRepository.saveAndFlush(localisation);

        // Get all the localisationList where departement equals to
        defaultLocalisationFiltering("departement.equals=" + DEFAULT_DEPARTEMENT, "departement.equals=" + UPDATED_DEPARTEMENT);
    }

    @Test
    @Transactional
    void getAllLocalisationsByDepartementIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocalisation = localisationRepository.saveAndFlush(localisation);

        // Get all the localisationList where departement in
        defaultLocalisationFiltering(
            "departement.in=" + DEFAULT_DEPARTEMENT + "," + UPDATED_DEPARTEMENT,
            "departement.in=" + UPDATED_DEPARTEMENT
        );
    }

    @Test
    @Transactional
    void getAllLocalisationsByDepartementIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocalisation = localisationRepository.saveAndFlush(localisation);

        // Get all the localisationList where departement is not null
        defaultLocalisationFiltering("departement.specified=true", "departement.specified=false");
    }

    @Test
    @Transactional
    void getAllLocalisationsByDepartementContainsSomething() throws Exception {
        // Initialize the database
        insertedLocalisation = localisationRepository.saveAndFlush(localisation);

        // Get all the localisationList where departement contains
        defaultLocalisationFiltering("departement.contains=" + DEFAULT_DEPARTEMENT, "departement.contains=" + UPDATED_DEPARTEMENT);
    }

    @Test
    @Transactional
    void getAllLocalisationsByDepartementNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLocalisation = localisationRepository.saveAndFlush(localisation);

        // Get all the localisationList where departement does not contain
        defaultLocalisationFiltering(
            "departement.doesNotContain=" + UPDATED_DEPARTEMENT,
            "departement.doesNotContain=" + DEFAULT_DEPARTEMENT
        );
    }

    @Test
    @Transactional
    void getAllLocalisationsByVilleIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedLocalisation = localisationRepository.saveAndFlush(localisation);

        // Get all the localisationList where ville equals to
        defaultLocalisationFiltering("ville.equals=" + DEFAULT_VILLE, "ville.equals=" + UPDATED_VILLE);
    }

    @Test
    @Transactional
    void getAllLocalisationsByVilleIsInShouldWork() throws Exception {
        // Initialize the database
        insertedLocalisation = localisationRepository.saveAndFlush(localisation);

        // Get all the localisationList where ville in
        defaultLocalisationFiltering("ville.in=" + DEFAULT_VILLE + "," + UPDATED_VILLE, "ville.in=" + UPDATED_VILLE);
    }

    @Test
    @Transactional
    void getAllLocalisationsByVilleIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedLocalisation = localisationRepository.saveAndFlush(localisation);

        // Get all the localisationList where ville is not null
        defaultLocalisationFiltering("ville.specified=true", "ville.specified=false");
    }

    @Test
    @Transactional
    void getAllLocalisationsByVilleContainsSomething() throws Exception {
        // Initialize the database
        insertedLocalisation = localisationRepository.saveAndFlush(localisation);

        // Get all the localisationList where ville contains
        defaultLocalisationFiltering("ville.contains=" + DEFAULT_VILLE, "ville.contains=" + UPDATED_VILLE);
    }

    @Test
    @Transactional
    void getAllLocalisationsByVilleNotContainsSomething() throws Exception {
        // Initialize the database
        insertedLocalisation = localisationRepository.saveAndFlush(localisation);

        // Get all the localisationList where ville does not contain
        defaultLocalisationFiltering("ville.doesNotContain=" + UPDATED_VILLE, "ville.doesNotContain=" + DEFAULT_VILLE);
    }

    private void defaultLocalisationFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultLocalisationShouldBeFound(shouldBeFound);
        defaultLocalisationShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultLocalisationShouldBeFound(String filter) throws Exception {
        restLocalisationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(localisation.getId().intValue())))
            .andExpect(jsonPath("$.[*].region").value(hasItem(DEFAULT_REGION)))
            .andExpect(jsonPath("$.[*].departement").value(hasItem(DEFAULT_DEPARTEMENT)))
            .andExpect(jsonPath("$.[*].ville").value(hasItem(DEFAULT_VILLE)));

        // Check, that the count call also returns 1
        restLocalisationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultLocalisationShouldNotBeFound(String filter) throws Exception {
        restLocalisationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restLocalisationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingLocalisation() throws Exception {
        // Get the localisation
        restLocalisationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLocalisation() throws Exception {
        // Initialize the database
        insertedLocalisation = localisationRepository.saveAndFlush(localisation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the localisation
        Localisation updatedLocalisation = localisationRepository.findById(localisation.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLocalisation are not directly saved in db
        em.detach(updatedLocalisation);
        updatedLocalisation.region(UPDATED_REGION).departement(UPDATED_DEPARTEMENT).ville(UPDATED_VILLE);
        LocalisationDTO localisationDTO = localisationMapper.toDto(updatedLocalisation);

        restLocalisationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, localisationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(localisationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Localisation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLocalisationToMatchAllProperties(updatedLocalisation);
    }

    @Test
    @Transactional
    void putNonExistingLocalisation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        localisation.setId(longCount.incrementAndGet());

        // Create the Localisation
        LocalisationDTO localisationDTO = localisationMapper.toDto(localisation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLocalisationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, localisationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(localisationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Localisation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLocalisation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        localisation.setId(longCount.incrementAndGet());

        // Create the Localisation
        LocalisationDTO localisationDTO = localisationMapper.toDto(localisation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocalisationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(localisationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Localisation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLocalisation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        localisation.setId(longCount.incrementAndGet());

        // Create the Localisation
        LocalisationDTO localisationDTO = localisationMapper.toDto(localisation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocalisationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(localisationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Localisation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLocalisationWithPatch() throws Exception {
        // Initialize the database
        insertedLocalisation = localisationRepository.saveAndFlush(localisation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the localisation using partial update
        Localisation partialUpdatedLocalisation = new Localisation();
        partialUpdatedLocalisation.setId(localisation.getId());

        partialUpdatedLocalisation.region(UPDATED_REGION).departement(UPDATED_DEPARTEMENT);

        restLocalisationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLocalisation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLocalisation))
            )
            .andExpect(status().isOk());

        // Validate the Localisation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLocalisationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLocalisation, localisation),
            getPersistedLocalisation(localisation)
        );
    }

    @Test
    @Transactional
    void fullUpdateLocalisationWithPatch() throws Exception {
        // Initialize the database
        insertedLocalisation = localisationRepository.saveAndFlush(localisation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the localisation using partial update
        Localisation partialUpdatedLocalisation = new Localisation();
        partialUpdatedLocalisation.setId(localisation.getId());

        partialUpdatedLocalisation.region(UPDATED_REGION).departement(UPDATED_DEPARTEMENT).ville(UPDATED_VILLE);

        restLocalisationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLocalisation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLocalisation))
            )
            .andExpect(status().isOk());

        // Validate the Localisation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLocalisationUpdatableFieldsEquals(partialUpdatedLocalisation, getPersistedLocalisation(partialUpdatedLocalisation));
    }

    @Test
    @Transactional
    void patchNonExistingLocalisation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        localisation.setId(longCount.incrementAndGet());

        // Create the Localisation
        LocalisationDTO localisationDTO = localisationMapper.toDto(localisation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLocalisationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, localisationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(localisationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Localisation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLocalisation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        localisation.setId(longCount.incrementAndGet());

        // Create the Localisation
        LocalisationDTO localisationDTO = localisationMapper.toDto(localisation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocalisationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(localisationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Localisation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLocalisation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        localisation.setId(longCount.incrementAndGet());

        // Create the Localisation
        LocalisationDTO localisationDTO = localisationMapper.toDto(localisation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLocalisationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(localisationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Localisation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLocalisation() throws Exception {
        // Initialize the database
        insertedLocalisation = localisationRepository.saveAndFlush(localisation);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the localisation
        restLocalisationMockMvc
            .perform(delete(ENTITY_API_URL_ID, localisation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return localisationRepository.count();
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

    protected Localisation getPersistedLocalisation(Localisation localisation) {
        return localisationRepository.findById(localisation.getId()).orElseThrow();
    }

    protected void assertPersistedLocalisationToMatchAllProperties(Localisation expectedLocalisation) {
        assertLocalisationAllPropertiesEquals(expectedLocalisation, getPersistedLocalisation(expectedLocalisation));
    }

    protected void assertPersistedLocalisationToMatchUpdatableProperties(Localisation expectedLocalisation) {
        assertLocalisationAllUpdatablePropertiesEquals(expectedLocalisation, getPersistedLocalisation(expectedLocalisation));
    }
}
